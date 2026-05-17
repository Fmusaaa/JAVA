package com.taskbot.model;

import java.time.LocalDateTime;

public class RecurringTask {
    private int id;
    private int taskId;
    private String recurrenceType;
    private int intervalValue;
    private LocalDateTime nextDueDate;

    public RecurringTask(int id, int taskId, String recurrenceType, int intervalValue, LocalDateTime nextDueDate) {
        this.id = id;
        this.taskId = taskId;
        this.recurrenceType = recurrenceType;
        this.intervalValue = intervalValue;
        this.nextDueDate = nextDueDate;
    }

    public int getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public int getIntervalValue() {
        return intervalValue;
    }

    public LocalDateTime getNextDueDate() {
        return nextDueDate;
    }
}
