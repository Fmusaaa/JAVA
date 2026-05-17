package com.taskbot.model;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int priority; // 1=Low, 2=Medium, 3=High
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private boolean reminderSent;
    private boolean overdueNotified;
    private int completedSubtasks;
    private int totalSubtasks;

    public Task(int userId, String title, String description, LocalDateTime dueDate, int priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
    }

    public Task(int id, int userId, String title, String description, LocalDateTime dueDate,
                int priority, String status, LocalDateTime createdAt, LocalDateTime completedAt,
                boolean reminderSent, boolean overdueNotified) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.reminderSent = reminderSent;
        this.overdueNotified = overdueNotified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    public void setCompleted(boolean completed) {
        this.status = completed ? "completed" : "pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public boolean isOverdueNotified() {
        return overdueNotified;
    }

    public void setOverdueNotified(boolean overdueNotified) {
        this.overdueNotified = overdueNotified;
    }

    public int getCompletedSubtasks() {
        return completedSubtasks;
    }

    public void setCompletedSubtasks(int completedSubtasks) {
        this.completedSubtasks = completedSubtasks;
    }

    public int getTotalSubtasks() {
        return totalSubtasks;
    }

    public void setTotalSubtasks(int totalSubtasks) {
        this.totalSubtasks = totalSubtasks;
    }

    public String getPriorityLabel() {
        return switch (priority) {
            case 1 -> "🟢 Low";
            case 2 -> "🟡 Medium";
            case 3 -> "🔴 High";
            default -> "Unknown";
        };
    }
}
