package com.taskbot.model;

public class TaskStats {
    private final int pendingTasks;
    private final int completedTasks;
    private final int overdueTasks;
    private final int highPriorityPendingTasks;
    private final int completedThisWeek;

    public TaskStats(int pendingTasks, int completedTasks, int overdueTasks,
                     int highPriorityPendingTasks, int completedThisWeek) {
        this.pendingTasks = pendingTasks;
        this.completedTasks = completedTasks;
        this.overdueTasks = overdueTasks;
        this.highPriorityPendingTasks = highPriorityPendingTasks;
        this.completedThisWeek = completedThisWeek;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getOverdueTasks() {
        return overdueTasks;
    }

    public int getHighPriorityPendingTasks() {
        return highPriorityPendingTasks;
    }

    public int getCompletedThisWeek() {
        return completedThisWeek;
    }
}
