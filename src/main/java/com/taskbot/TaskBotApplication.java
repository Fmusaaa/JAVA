package com.taskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TaskBotApplication {
    private static final Logger logger = LoggerFactory.getLogger(TaskBotApplication.class);

    public static void main(String[] args) {
        // Get credentials from environment variables
        String BOT_USERNAME = System.getenv("BOT_USERNAME");
        String BOT_TOKEN = System.getenv("BOT_TOKEN");

        // Validate credentials
        if (BOT_USERNAME == null || BOT_USERNAME.isEmpty()) {
            logger.error("BOT_USERNAME environment variable not set!");
            System.err.println("Error: BOT_USERNAME environment variable is not set.");
            System.err.println("Please set your bot username and try again.");
            System.exit(1);
        }

        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            logger.error("BOT_TOKEN environment variable not set!");
            System.err.println("Error: BOT_TOKEN environment variable is not set.");
            System.err.println("Please set your bot token and try again.");
            System.exit(1);
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TaskManagerBot bot = new TaskManagerBot(BOT_USERNAME, BOT_TOKEN);
            botsApi.registerBot(bot);

            logger.info("Bot started successfully!");
            System.out.println("✅ Task Manager Bot is running!");
            System.out.println("Bot username: " + BOT_USERNAME);
            System.out.println("Press Ctrl+C to stop the bot.");
        } catch (TelegramApiException e) {
            logger.error("Failed to start bot", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
