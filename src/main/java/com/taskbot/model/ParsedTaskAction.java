package com.taskbot.model;

import java.time.LocalDateTime;

public class ParsedTaskAction {
    private String action;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer priority;
    private String clarificationMessage;

    public static ParsedTaskAction askClarification(String message) {
        ParsedTaskAction parsed = new ParsedTaskAction();
        parsed.action = "ask_clarification";
        parsed.clarificationMessage = message;
        return parsed;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getClarificationMessage() {
        return clarificationMessage;
    }

    public void setClarificationMessage(String clarificationMessage) {
        this.clarificationMessage = clarificationMessage;
    }
}
