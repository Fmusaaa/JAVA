package com.taskbot.util;

import com.taskbot.model.Subtask;
import com.taskbot.model.Task;
import com.taskbot.model.TaskStats;

import java.util.List;

public final class MessageFormatter {
    private MessageFormatter() {
    }

    public static String formatTask(Task task) {
        String status = task.isCompleted() ? "DONE" : "PENDING";
        StringBuilder sb = new StringBuilder();
        sb.append("Task #").append(task.getId()).append(" - ").append(task.getTitle()).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Priority: ").append(task.getPriorityLabel()).append("\n");
        sb.append("Due: ").append(DateTimeUtil.formatUserDateTime(task.getDueDate())).append("\n");
        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            sb.append("Deskripsi: ").append(task.getDescription()).append("\n");
        }
        if (task.getTotalSubtasks() > 0) {
            sb.append("Subtasks: ").append(task.getCompletedSubtasks())
                    .append("/").append(task.getTotalSubtasks()).append(" done\n");
        }
        return sb.toString().trim();
    }

    public static String formatTaskList(String title, List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "📭 Tidak ada task untuk kategori ini.";
        }

        StringBuilder sb = new StringBuilder(title).append("\n\n");
        for (Task task : tasks) {
            sb.append(formatTask(task)).append("\n\n");
            sb.append("------------------------------").append("\n\n");
        }
        return sb.toString().trim();
    }

    public static String formatSubtasks(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            return "Belum ada subtask untuk task ini.";
        }

        StringBuilder sb = new StringBuilder("Checklist:\n\n");
        for (Subtask subtask : subtasks) {
            sb.append(subtask.isDone() ? "[x] " : "[ ] ");
            sb.append("#").append(subtask.getId()).append(" ");
            sb.append(subtask.getTitle()).append("\n");
        }
        return sb.toString().trim();
    }

    public static String formatStats(TaskStats stats) {
        return "📊 Statistik Produktivitas\n\n" +
                "Pending tasks: " + stats.getPendingTasks() + "\n" +
                "Completed tasks: " + stats.getCompletedTasks() + "\n" +
                "Overdue tasks: " + stats.getOverdueTasks() + "\n" +
                "High priority pending: " + stats.getHighPriorityPendingTasks() + "\n" +
                "Completed minggu ini: " + stats.getCompletedThisWeek();
    }
}
