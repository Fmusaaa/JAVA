package com.taskbot.model;

public class ReminderNotification {
    private final long telegramId;
    private final int taskId;
    private final String message;
    private final boolean overdue;

    public ReminderNotification(long telegramId, int taskId, String message, boolean overdue) {
        this.telegramId = telegramId;
        this.taskId = taskId;
        this.message = message;
        this.overdue = overdue;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOverdue() {
        return overdue;
    }
}
