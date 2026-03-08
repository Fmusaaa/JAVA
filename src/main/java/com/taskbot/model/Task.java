package com.taskbot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String userId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int priority; // 1=Low, 2=Medium, 3=High
    private boolean completed;
    private LocalDateTime createdAt;

    public Task(String userId, String title, String description, LocalDateTime dueDate, int priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public Task(int id, String userId, String title, String description, LocalDateTime dueDate,
                int priority, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
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
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPriorityLabel() {
        return switch (priority) {
            case 1 -> "🟢 Low";
            case 2 -> "🟡 Medium";
            case 3 -> "🔴 High";
            default -> "Unknown";
        };
    }

    public String formatForDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dueStr = dueDate != null ? dueDate.format(formatter) : "No due date";
        String status = completed ? "✅ DONE" : "⏳ PENDING";

        return String.format("ID: %d | %s\n📝 %s\n%s\n📅 %s\n%s",
            id, status, title, description, dueStr, getPriorityLabel());
    }
}
