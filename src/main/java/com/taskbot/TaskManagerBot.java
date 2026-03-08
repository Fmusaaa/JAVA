package com.taskbot;

import com.taskbot.database.DatabaseManager;
import com.taskbot.handler.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TaskManagerBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagerBot.class);
    private final DatabaseManager db;
    private final CommandHandler commandHandler;
    private final String botUsername;
    private final String botToken;

    // State management untuk tracking user context
    private enum UserState {
        IDLE, ADDING_TASK, EDITING_TASK
    }

    private final java.util.Map<Long, UserState> userStates = new java.util.HashMap<>();
    private final java.util.Map<Long, String> userInputBuffer = new java.util.HashMap<>();

    public TaskManagerBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.db = new DatabaseManager();
        this.commandHandler = new CommandHandler(db);
        logger.info("TaskManagerBot initialized");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userId = update.getMessage().getFrom().getId().toString();

        logger.info("Message from user {}: {}", userId, messageText);

        try {
            handleMessage(chatId, userId, messageText);
        } catch (TelegramApiException e) {
            logger.error("Error handling message", e);
            try {
                sendMessage(chatId, "❌ An error occurred. Please try again.");
            } catch (TelegramApiException sendError) {
                logger.error("Failed to send error message", sendError);
            }
        }
    }

    private void handleMessage(long chatId, String userId, String messageText) throws TelegramApiException {
        UserState currentState = userStates.getOrDefault(chatId, UserState.IDLE);

        // Handle commands
        if (messageText.startsWith("/")) {
            userStates.put(chatId, UserState.IDLE);
            userInputBuffer.remove(chatId);
            handleCommand(chatId, userId, messageText);
        } else {
            // Handle input based on current state
            switch (currentState) {
                case ADDING_TASK:
                    String addResult = commandHandler.addTask(userId, messageText);
                    sendMessage(chatId, addResult);
                    userStates.put(chatId, UserState.IDLE);
                    userInputBuffer.remove(chatId);
                    break;

                case EDITING_TASK:
                    String editResult = commandHandler.editTask(userId, messageText);
                    sendMessage(chatId, editResult);
                    userStates.put(chatId, UserState.IDLE);
                    userInputBuffer.remove(chatId);
                    break;

                case IDLE:
                default:
                    sendMessage(chatId, "📝 Please use a command first. Type /help to see available commands.");
                    break;
            }
        }
    }

    private void handleCommand(long chatId, String userId, String command) throws TelegramApiException {
        if (command.equals("/start")) {
            handleStart(chatId);
        } else if (command.equals("/help")) {
            sendMessage(chatId, commandHandler.handleHelp());
        } else if (command.equals("/list_tasks")) {
            sendMessage(chatId, commandHandler.listTasks(userId));
        } else if (command.equals("/add_task")) {
            userStates.put(chatId, UserState.ADDING_TASK);
            sendMessage(chatId, commandHandler.initAddTask());
        } else if (command.equals("/edit_task")) {
            userStates.put(chatId, UserState.EDITING_TASK);
            sendMessage(chatId, commandHandler.initEditTask());
        } else if (command.startsWith("/complete_task ")) {
            String taskId = command.substring("/complete_task ".length()).trim();
            sendMessage(chatId, commandHandler.completeTask(userId, taskId));
        } else if (command.startsWith("/delete_task ")) {
            String taskId = command.substring("/delete_task ".length()).trim();
            sendMessage(chatId, commandHandler.deleteTask(userId, taskId));
        } else {
            sendMessage(chatId, "❓ Unknown command! Type /help to see available commands.");
        }
    }

    private void handleStart(long chatId) throws TelegramApiException {
        String welcomeMessage = "👋 Welcome to Task Manager Bot!\n\n" +
                "I'm here to help you manage your tasks efficiently.\n\n" +
                "Quick Start:\n" +
                "/add_task - Create a new task\n" +
                "/list_tasks - View all your tasks\n" +
                "/help - See all commands\n\n" +
                "Let's get productive! 🚀";
        sendMessage(chatId, welcomeMessage);
    }

    private void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        execute(message);
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
