package com.taskbot.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private long telegramId;
    private String username;
    private String firstName;
    private LocalDateTime createdAt;

    public User(int id, long telegramId, String username, String firstName, LocalDateTime createdAt) {
        this.id = id;
        this.telegramId = telegramId;
        this.username = username;
        this.firstName = firstName;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
