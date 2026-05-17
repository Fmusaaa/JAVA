package com.taskbot;

import com.taskbot.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TaskBotApplication {
    private static final Logger logger = LoggerFactory.getLogger(TaskBotApplication.class);

    public static void main(String[] args) {
        try {
            AppConfig config = AppConfig.fromEnvironment();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TaskManagerBot bot = new TaskManagerBot(config);
            botsApi.registerBot(bot);

            logger.info("Bot started successfully!");
            System.out.println("Task Manager Bot is running.");
            System.out.println("Bot username: " + config.getBotUsername());
            System.out.println("Database: " + config.getDatabaseType());
            System.out.println("Press Ctrl+C to stop the bot.");
        } catch (IllegalStateException e) {
            logger.error("Configuration error", e);
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        } catch (TelegramApiException e) {
            logger.error("Failed to start bot", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
