package com.taskbot.model;

import java.time.LocalDateTime;

public class Subtask {
    private int id;
    private int taskId;
    private String title;
    private boolean done;
    private LocalDateTime createdAt;

    public Subtask(int id, int taskId, String title, boolean done, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
