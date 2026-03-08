package com.taskbot.handler;

import com.taskbot.database.DatabaseManager;
import com.taskbot.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private final DatabaseManager db;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CommandHandler(DatabaseManager db) {
        this.db = db;
    }

    public String handleHelp() {
        return "📋 Task Manager Bot Commands\n\n" +
                "Task Management:\n" +
                "/add_task - Add a new task\n" +
                "/list_tasks - View all your tasks\n" +
                "/complete_task <id> - Mark task as done\n" +
                "/edit_task <id> - Edit existing task\n" +
                "/delete_task <id> - Delete a task\n\n" +
                "Available Priorities:\n" +
                "1 - 🟢 Low\n" +
                "2 - 🟡 Medium\n" +
                "3 - 🔴 High\n\n" +
                "Date Format: dd/MM/yyyy HH:mm\n" +
                "Example: 15/03/2025 14:30\n\n" +
                "Use /help to see this message anytime.";
    }

    public String initAddTask() {
        return "📝 Let's add a new task!\n\n" +
                "Please provide the task details in this format:\n" +
                "<title> | <description> | <due_date> | <priority>\n\n" +
                "Example:\n" +
                "Finish project report | Complete Q1 analysis | 15/03/2025 14:30 | 3\n\n" +
                "Priority levels:\n" +
                "1 = Low, 2 = Medium, 3 = High\n\n" +
                "You can omit due date and priority:\n" +
                "Finish project report | Complete Q1 analysis";
    }

    public String addTask(String userId, String input) {
        try {
            String[] parts = input.split("\\|");
            if (parts.length < 1) {
                return "❌ Invalid format! Use: <title> | <description> | <due_date> | <priority>";
            }

            String title = parts[0].trim();
            String description = parts.length > 1 ? parts[1].trim() : "";
            LocalDateTime dueDate = null;
            int priority = 2; // Default medium

            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                try {
                    dueDate = LocalDateTime.parse(parts[2].trim(), DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    return "❌ Invalid date format! Use: dd/MM/yyyy HH:mm";
                }
            }

            if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                try {
                    priority = Integer.parseInt(parts[3].trim());
                    if (priority < 1 || priority > 3) {
                        return "❌ Priority must be 1 (Low), 2 (Medium), or 3 (High)";
                    }
                } catch (NumberFormatException e) {
                    return "❌ Priority must be a number (1, 2, or 3)";
                }
            }

            Task task = db.addTask(userId, title, description, dueDate, priority);
            if (task != null) {
                return "✅ Task Added Successfully!\n\n" + task.formatForDisplay();
            } else {
                return "❌ Error adding task. Please try again.";
            }
        } catch (Exception e) {
            logger.error("Error in addTask", e);
            return "❌ Error processing your request. Please try again.";
        }
    }

    public String listTasks(String userId) {
        List<Task> tasks = db.getUserTasks(userId);

        if (tasks.isEmpty()) {
            return "📭 You have no tasks yet!\n\nUse /add_task to create one.";
        }

        StringBuilder sb = new StringBuilder("📋 *Your Tasks*\n\n");
        for (Task task : tasks) {
            sb.append(task.formatForDisplay()).append("\n\n" + "—".repeat(40) + "\n\n");
        }
        return sb.toString();
    }

    public String initEditTask() {
        return "✏️ Edit Task\n\n" +
                "Please provide the task ID and new details:\n" +
                "<task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>\n\n" +
                "Example:\n" +
                "1 | Updated Title | New description | 20/03/2025 10:00 | 2";
    }

    public String editTask(String userId, String input) {
        try {
            String[] parts = input.split("\\|");
            if (parts.length < 2) {
                return "❌ Invalid format! Use: <task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>";
            }

            int taskId;
            try {
                taskId = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                return "❌ Task ID must be a number!";
            }

            Task existingTask = db.getTask(userId, taskId);
            if (existingTask == null) {
                return "❌ Task not found!";
            }

            String newTitle = parts.length > 1 && !parts[1].trim().isEmpty() ? parts[1].trim() : existingTask.getTitle();
            String newDescription = parts.length > 2 ? parts[2].trim() : existingTask.getDescription();
            LocalDateTime newDueDate = existingTask.getDueDate();
            int newPriority = existingTask.getPriority();

            if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                try {
                    newDueDate = LocalDateTime.parse(parts[3].trim(), DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    return "❌ Invalid date format! Use: dd/MM/yyyy HH:mm";
                }
            }

            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                try {
                    newPriority = Integer.parseInt(parts[4].trim());
                    if (newPriority < 1 || newPriority > 3) {
                        return "❌ Priority must be 1, 2, or 3";
                    }
                } catch (NumberFormatException e) {
                    return "❌ Priority must be a number!";
                }
            }

            if (db.updateTask(userId, taskId, newTitle, newDescription, newDueDate, newPriority)) {
                Task updatedTask = db.getTask(userId, taskId);
                return "✅ Task Updated!\n\n" + updatedTask.formatForDisplay();
            } else {
                return "❌ Error updating task. Please try again.";
            }
        } catch (Exception e) {
            logger.error("Error in editTask", e);
            return "❌ Error processing your request.";
        }
    }

    public String completeTask(String userId, String taskIdStr) {
        try {
            int taskId = Integer.parseInt(taskIdStr.trim());
            Task task = db.getTask(userId, taskId);

            if (task == null) {
                return "❌ Task not found!";
            }

            if (db.completeTask(userId, taskId)) {
                return "✅ Task Completed!\n\n" + db.getTask(userId, taskId).formatForDisplay();
            } else {
                return "❌ Error completing task.";
            }
        } catch (NumberFormatException e) {
            return "❌ Please provide a valid task ID!";
        }
    }

    public String deleteTask(String userId, String taskIdStr) {
        try {
            int taskId = Integer.parseInt(taskIdStr.trim());
            Task task = db.getTask(userId, taskId);

            if (task == null) {
                return "❌ Task not found!";
            }

            if (db.deleteTask(userId, taskId)) {
                return "🗑️ Task Deleted!\n\nTask ID: " + taskId + " has been removed.";
            } else {
                return "❌ Error deleting task.";
            }
        } catch (NumberFormatException e) {
            return "❌ Please provide a valid task ID!";
        }
    }
}
