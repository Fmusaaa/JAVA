package com.taskbot.config;

public class AppConfig {
    private final String botUsername;
    private final String botToken;
    private final DatabaseType databaseType;
    private final String databaseUrl;
    private final String databaseUsername;
    private final String databasePassword;
    private final int reminderIntervalMinutes;
    private final String aiProvider;
    private final String geminiApiKey;
    private final String aiModel;

    private AppConfig(String botUsername, String botToken, DatabaseType databaseType,
                      String databaseUrl, String databaseUsername, String databasePassword,
                      int reminderIntervalMinutes, String aiProvider, String geminiApiKey, String aiModel) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.databaseType = databaseType;
        this.databaseUrl = databaseUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.reminderIntervalMinutes = reminderIntervalMinutes;
        this.aiProvider = aiProvider;
        this.geminiApiKey = geminiApiKey;
        this.aiModel = aiModel;
    }

    public static AppConfig fromEnvironment() {
        String botUsername = requiredEnv("BOT_USERNAME");
        String botToken = requiredEnv("BOT_TOKEN");
        DatabaseType databaseType = parseDatabaseType(getEnvOrDefault("DB_TYPE", "SQLITE"));

        String defaultUrl = databaseType == DatabaseType.MYSQL
                ? "jdbc:mysql://localhost:3306/taskbot?useSSL=false&serverTimezone=Asia/Jakarta"
                : "jdbc:sqlite:taskbot.db";

        String databaseUrl = getEnvOrDefault("DB_URL", defaultUrl);
        String databaseUsername = System.getenv("DB_USERNAME");
        String databasePassword = System.getenv("DB_PASSWORD");
        int reminderIntervalMinutes = parsePositiveInt(getEnvOrDefault("REMINDER_INTERVAL_MINUTES", "1"), 1);
        String aiProvider = getEnvOrDefault("AI_PROVIDER", "STUB").trim().toUpperCase();
        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        String aiModel = getEnvOrDefault("AI_MODEL", "gemini-2.5-flash-lite");

        return new AppConfig(botUsername, botToken, databaseType, databaseUrl,
                databaseUsername, databasePassword, reminderIntervalMinutes,
                aiProvider, geminiApiKey, aiModel);
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " environment variable is not set.");
        }
        return value;
    }

    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static DatabaseType parseDatabaseType(String value) {
        try {
            return DatabaseType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DatabaseType.SQLITE;
        }
    }

    private static int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public int getReminderIntervalMinutes() {
        return reminderIntervalMinutes;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public String getAiModel() {
        return aiModel;
    }
}
