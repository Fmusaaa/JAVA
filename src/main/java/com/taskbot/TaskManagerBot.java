package com.taskbot;

import com.taskbot.ai.AiAssistant;
import com.taskbot.ai.AiTaskParser;
import com.taskbot.ai.GeminiAiAssistant;
import com.taskbot.ai.GeminiAiTaskParser;
import com.taskbot.ai.StubAiAssistant;
import com.taskbot.ai.StubAiTaskParser;
import com.taskbot.config.AppConfig;
import com.taskbot.database.DatabaseManager;
import com.taskbot.handler.CommandHandler;
import com.taskbot.handler.CommandValidator;
import com.taskbot.model.ParsedTaskAction;
import com.taskbot.model.ReminderNotification;
import com.taskbot.repository.RecurringTaskRepository;
import com.taskbot.repository.SubtaskRepository;
import com.taskbot.repository.TaskRepository;
import com.taskbot.repository.UserRepository;
import com.taskbot.service.ReminderScheduler;
import com.taskbot.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskManagerBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagerBot.class);

    private final CommandHandler commandHandler;
    private final CommandValidator commandValidator;
    private final ReminderScheduler reminderScheduler;
    private final AiTaskParser aiTaskParser;
    private final AiAssistant aiAssistant;
    private final String botUsername;
    private final String botToken;

    private enum UserState {
        IDLE, ADDING_TASK, EDITING_TASK
    }

    private final Map<String, UserState> userStates = new HashMap<>();
    private final Map<String, String> userInputBuffer = new HashMap<>();

    public TaskManagerBot(AppConfig config) {
        this.botUsername = config.getBotUsername();
        this.botToken = config.getBotToken();

        DatabaseManager databaseManager = new DatabaseManager(config);
        UserRepository userRepository = new UserRepository(databaseManager);
        TaskRepository taskRepository = new TaskRepository(databaseManager);
        SubtaskRepository subtaskRepository = new SubtaskRepository(databaseManager);
        RecurringTaskRepository recurringTaskRepository = new RecurringTaskRepository(databaseManager);
        TaskService taskService = new TaskService(userRepository, taskRepository, subtaskRepository, recurringTaskRepository);

        this.commandHandler = new CommandHandler(taskService);
        this.commandValidator = new CommandValidator();
        this.aiTaskParser = createAiTaskParser(config);
        this.aiAssistant = createAiAssistant(config);
        this.reminderScheduler = new ReminderScheduler(taskService, this::sendReminder, config.getReminderIntervalMinutes());
        this.reminderScheduler.start();
        Runtime.getRuntime().addShutdownHook(new Thread(reminderScheduler::stop));

        logger.info("TaskManagerBot initialized");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText() || update.getMessage().getFrom() == null) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        User telegramUser = update.getMessage().getFrom();
        long telegramId = telegramUser.getId();

        commandHandler.registerUser(telegramId, telegramUser.getUserName(), telegramUser.getFirstName());
        logger.info("Message from user {}: {}", telegramId, messageText);

        try {
            handleMessage(chatId, telegramId, messageText);
        } catch (TelegramApiException e) {
            logger.error("Error handling message", e);
            try {
                sendMessage(chatId, "Terjadi error. Coba lagi ya.");
            } catch (TelegramApiException sendError) {
                logger.error("Failed to send error message", sendError);
            }
        }
    }

    private void handleMessage(long chatId, long telegramId, String messageText) throws TelegramApiException {
        String stateKey = stateKey(chatId, telegramId);
        UserState currentState = userStates.getOrDefault(stateKey, UserState.IDLE);

        if (messageText.startsWith("/")) {
            userStates.put(stateKey, UserState.IDLE);
            userInputBuffer.remove(stateKey);
            handleCommand(chatId, telegramId, stateKey, messageText);
            return;
        }

        switch (currentState) {
            case ADDING_TASK -> {
                sendMessage(chatId, commandHandler.addTask(telegramId, messageText));
                clearState(stateKey);
            }
            case EDITING_TASK -> {
                String bufferedTaskId = userInputBuffer.get(stateKey);
                String editInput = bufferedTaskId == null ? messageText : bufferedTaskId + " | " + messageText;
                sendMessage(chatId, commandHandler.editTask(telegramId, editInput));
                clearState(stateKey);
            }
            case IDLE -> {
                ParsedTaskAction parsed = aiTaskParser.parse(messageText);
                if (parsed != null && "add_task".equalsIgnoreCase(parsed.getAction())) {
                    sendMessage(chatId, commandHandler.addParsedTask(telegramId, parsed));
                } else {
                    String clarification = parsed != null && parsed.getClarificationMessage() != null
                            ? parsed.getClarificationMessage()
                            : "Gunakan command dulu. Ketik /help untuk melihat daftar command.";
                    sendMessage(chatId, clarification);
                }
            }
        }
    }

    private AiTaskParser createAiTaskParser(AppConfig config) {
        if ("GEMINI".equalsIgnoreCase(config.getAiProvider())) {
            logger.info("Gemini AI parser enabled with model {}", config.getAiModel());
            return new GeminiAiTaskParser(config.getGeminiApiKey(), config.getAiModel());
        }
        logger.info("AI parser running in stub mode");
        return new StubAiTaskParser();
    }

    private AiAssistant createAiAssistant(AppConfig config) {
        if ("GEMINI".equalsIgnoreCase(config.getAiProvider())) {
            logger.info("Gemini AI assistant enabled with model {}", config.getAiModel());
            return new GeminiAiAssistant(config.getGeminiApiKey(), config.getAiModel());
        }
        logger.info("AI assistant running in stub mode");
        return new StubAiAssistant();
    }

    private void handleCommand(long chatId, long telegramId, String stateKey, String rawCommand) throws TelegramApiException {
        String trimmed = rawCommand.trim();
        String commandName = extractCommandName(trimmed);
        String args = extractArgs(trimmed);

        switch (commandName) {
            case "/start" -> handleStart(chatId);
            case "/help" -> sendMessage(chatId, commandHandler.handleHelp());
            case "/list_tasks" -> sendMessage(chatId, commandHandler.listTasks(telegramId));
            case "/add_task" -> {
                userStates.put(stateKey, UserState.ADDING_TASK);
                sendMessage(chatId, commandHandler.initAddTask());
            }
            case "/edit_task" -> handleEditCommand(chatId, telegramId, stateKey, args);
            case "/complete_task" -> sendMessage(chatId, requireArgs(args, "/complete_task <id>")
                    ? commandHandler.completeTask(telegramId, args)
                    : "Gunakan format: /complete_task <id>");
            case "/delete_task" -> sendMessage(chatId, requireArgs(args, "/delete_task <id>")
                    ? commandHandler.deleteTask(telegramId, args)
                    : "Gunakan format: /delete_task <id>");
            case "/today" -> sendMessage(chatId, commandHandler.listToday(telegramId));
            case "/tomorrow" -> sendMessage(chatId, commandHandler.listTomorrow(telegramId));
            case "/overdue" -> sendMessage(chatId, commandHandler.listOverdue(telegramId));
            case "/high_priority" -> sendMessage(chatId, commandHandler.listHighPriority(telegramId));
            case "/search" -> sendMessage(chatId, commandHandler.search(telegramId, args));
            case "/stats" -> sendMessage(chatId, commandHandler.stats(telegramId));
            case "/ask" -> sendMessage(chatId, aiAssistant.answer(args));
            case "/add_subtask" -> sendMessage(chatId, commandHandler.addSubtask(telegramId, args));
            case "/list_subtasks" -> sendMessage(chatId, commandHandler.listSubtasks(telegramId, args));
            case "/done_subtask" -> sendMessage(chatId, commandHandler.doneSubtask(telegramId, args));
            case "/delete_subtask" -> sendMessage(chatId, commandHandler.deleteSubtask(telegramId, args));
            case "/repeat" -> sendMessage(chatId, commandHandler.repeatTask(telegramId, args));
            default -> sendMessage(chatId, unknownCommandMessage(commandName));
        }
    }

    private void handleEditCommand(long chatId, long telegramId, String stateKey, String args) throws TelegramApiException {
        if (args.isBlank()) {
            userStates.put(stateKey, UserState.EDITING_TASK);
            sendMessage(chatId, commandHandler.initEditTask());
            return;
        }

        if (args.contains("|")) {
            sendMessage(chatId, commandHandler.editTask(telegramId, args));
            return;
        }

        userStates.put(stateKey, UserState.EDITING_TASK);
        userInputBuffer.put(stateKey, args.trim());
        sendMessage(chatId, commandHandler.initEditTaskWithId(args.trim()));
    }

    private String unknownCommandMessage(String commandName) {
        String suggestion = commandValidator.getSuggestion(commandName);
        if (suggestion != null) {
            return "Command tidak dikenal. Maksud kamu " + suggestion + "?\n\n" +
                    "Ketik /help untuk melihat semua command.";
        }
        return commandValidator.availableCommandsMessage();
    }

    private boolean requireArgs(String args, String usage) {
        return args != null && !args.isBlank() && usage != null;
    }

    private String extractCommandName(String rawCommand) {
        String firstToken = rawCommand.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        int botMentionIndex = firstToken.indexOf('@');
        return botMentionIndex >= 0 ? firstToken.substring(0, botMentionIndex) : firstToken;
    }

    private String extractArgs(String rawCommand) {
        String[] parts = rawCommand.split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    private void handleStart(long chatId) throws TelegramApiException {
        String welcomeMessage = "👋 Selamat datang di Task Manager Bot!\n\n" +
                "Bot ini membantu kamu mengelola task, subtask, reminder, dan recurring task.\n\n" +
                "Mulai cepat:\n" +
                "/add_task - Buat task baru\n" +
                "/list_tasks - Lihat task\n" +
                "/today - Lihat task hari ini\n" +
                "/ask - Tanya AI assistant\n" +
                "/help - Lihat semua command";
        sendMessage(chatId, welcomeMessage);
    }

    private void sendReminder(ReminderNotification notification) throws TelegramApiException {
        sendMessage(notification.getTelegramId(), notification.getMessage());
    }

    private void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        execute(message);
    }

    private String stateKey(long chatId, long telegramId) {
        return chatId + ":" + telegramId;
    }

    private void clearState(String stateKey) {
        userStates.put(stateKey, UserState.IDLE);
        userInputBuffer.remove(stateKey);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
