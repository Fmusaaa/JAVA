package com.taskbot.repository;

import com.taskbot.database.DatabaseManager;
import com.taskbot.model.ReminderNotification;
import com.taskbot.model.Task;
import com.taskbot.model.TaskStats;
import com.taskbot.util.DateTimeUtil;
import com.taskbot.util.MessageFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final DatabaseManager databaseManager;

    public TaskRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Task addTask(int userId, String title, String description, LocalDateTime dueDate, int priority) {
        String sql = "INSERT INTO tasks (user_id, title, description, due_date, priority, status) VALUES (?, ?, ?, ?, ?, 'pending')";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, title);
            statement.setString(3, description);
            databaseManager.setDateTime(statement, 4, dueDate);
            statement.setInt(5, priority);
            statement.executeUpdate();
            int taskId = databaseManager.getLastInsertId(connection);
            return getTask(userId, taskId);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menambah task", e);
        }
    }

    public Task getTask(int userId, int taskId) {
        String sql = baseTaskSelect() + " WHERE t.id = ? AND t.user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTask(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil task", e);
        }
        return null;
    }

    public List<Task> getUserTasks(int userId) {
        String sql = baseTaskSelect() + " WHERE t.user_id = ? ORDER BY t.status DESC, t.priority DESC, t.due_date ASC";
        return listTasks(sql, userId);
    }

    public List<Task> findDueBetween(int userId, LocalDateTime start, LocalDateTime end) {
        String sql = baseTaskSelect() + " WHERE t.user_id = ? AND t.status = 'pending' AND t.due_date BETWEEN ? AND ? ORDER BY t.due_date ASC";
        return listTasks(sql, userId, start, end);
    }

    public List<Task> findOverdue(int userId, LocalDateTime now) {
        String sql = baseTaskSelect() + " WHERE t.user_id = ? AND t.status = 'pending' AND t.due_date < ? ORDER BY t.due_date ASC";
        return listTasks(sql, userId, now);
    }

    public List<Task> findHighPriorityPending(int userId) {
        String sql = baseTaskSelect() + " WHERE t.user_id = ? AND t.status = 'pending' AND t.priority = 3 ORDER BY t.due_date ASC";
        return listTasks(sql, userId);
    }

    public List<Task> search(int userId, String keyword) {
        String sql = baseTaskSelect() + """
                WHERE t.user_id = ?
                  AND (LOWER(t.title) LIKE ? OR LOWER(COALESCE(t.description, '')) LIKE ?)
                ORDER BY t.priority DESC, t.due_date ASC
                """;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            statement.setInt(1, userId);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapTasks(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mencari task", e);
        }
    }

    public boolean updateTask(int userId, int taskId, String title, String description,
                              LocalDateTime dueDate, int priority) {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ?, reminder_sent = 0, overdue_notified = 0 WHERE id = ? AND user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, description);
            databaseManager.setDateTime(statement, 3, dueDate);
            statement.setInt(4, priority);
            statement.setInt(5, taskId);
            statement.setInt(6, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui task", e);
        }
    }

    public boolean completeTask(int userId, int taskId, LocalDateTime completedAt) {
        String sql = "UPDATE tasks SET status = 'completed', completed_at = ? WHERE id = ? AND user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            databaseManager.setDateTime(statement, 1, completedAt);
            statement.setInt(2, taskId);
            statement.setInt(3, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyelesaikan task", e);
        }
    }

    public boolean deleteTask(int userId, int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menghapus task", e);
        }
    }

    public TaskStats getStats(int userId, LocalDateTime now) {
        LocalDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        return new TaskStats(
                count(userId, "status = 'pending'", null, null),
                count(userId, "status = 'completed'", null, null),
                count(userId, "status = 'pending' AND due_date < ?", now, null),
                count(userId, "status = 'pending' AND priority = 3", null, null),
                count(userId, "status = 'completed' AND completed_at >= ? AND completed_at <= ?", weekStart, now)
        );
    }

    public List<ReminderNotification> findDueReminderNotifications(LocalDateTime now, LocalDateTime until) {
        String sql = """
                SELECT u.telegram_id, t.*
                FROM tasks t
                JOIN users u ON u.id = t.user_id
                WHERE t.status = 'pending'
                  AND t.due_date BETWEEN ? AND ?
                  AND t.reminder_sent = 0
                ORDER BY t.due_date ASC
                """;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            databaseManager.setDateTime(statement, 1, now);
            databaseManager.setDateTime(statement, 2, until);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ReminderNotification> reminders = new ArrayList<>();
                while (resultSet.next()) {
                    Task task = mapTaskWithoutSubtasks(resultSet);
                    String message = "⏰ Reminder: task akan jatuh tempo dalam 1 jam.\n\n" + MessageFormatter.formatTask(task);
                    reminders.add(new ReminderNotification(resultSet.getLong("telegram_id"), task.getId(), message, false));
                }
                return reminders;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil reminder", e);
        }
    }

    public List<ReminderNotification> findOverdueNotifications(LocalDateTime now) {
        String sql = """
                SELECT u.telegram_id, t.*
                FROM tasks t
                JOIN users u ON u.id = t.user_id
                WHERE t.status = 'pending'
                  AND t.due_date < ?
                  AND t.overdue_notified = 0
                ORDER BY t.due_date ASC
                """;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            databaseManager.setDateTime(statement, 1, now);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ReminderNotification> reminders = new ArrayList<>();
                while (resultSet.next()) {
                    Task task = mapTaskWithoutSubtasks(resultSet);
                    String message = "⚠️ Task overdue.\n\n" + MessageFormatter.formatTask(task);
                    reminders.add(new ReminderNotification(resultSet.getLong("telegram_id"), task.getId(), message, true));
                }
                return reminders;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil overdue reminder", e);
        }
    }

    public void markReminderSent(int taskId) {
        updateFlag(taskId, "reminder_sent");
    }

    public void markOverdueNotified(int taskId) {
        updateFlag(taskId, "overdue_notified");
    }

    private void updateFlag(int taskId, String column) {
        String sql = "UPDATE tasks SET " + column + " = 1 WHERE id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui flag task", e);
        }
    }

    private int count(int userId, String condition, LocalDateTime firstDate, LocalDateTime secondDate) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND " + condition;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            if (firstDate != null) {
                databaseManager.setDateTime(statement, 2, firstDate);
            }
            if (secondDate != null) {
                databaseManager.setDateTime(statement, 3, secondDate);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menghitung statistik", e);
        }
    }

    private List<Task> listTasks(String sql, Object... params) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof LocalDateTime dateTime) {
                    databaseManager.setDateTime(statement, i + 1, dateTime);
                } else {
                    statement.setObject(i + 1, param);
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapTasks(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil daftar task", e);
        }
    }

    private List<Task> mapTasks(ResultSet resultSet) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        while (resultSet.next()) {
            tasks.add(mapTask(resultSet));
        }
        return tasks;
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        Task task = mapTaskWithoutSubtasks(resultSet);
        task.setCompletedSubtasks(resultSet.getInt("completed_subtasks"));
        task.setTotalSubtasks(resultSet.getInt("total_subtasks"));
        return task;
    }

    private Task mapTaskWithoutSubtasks(ResultSet resultSet) throws SQLException {
        return new Task(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                databaseManager.getDateTime(resultSet, "due_date"),
                resultSet.getInt("priority"),
                resultSet.getString("status"),
                databaseManager.getDateTime(resultSet, "created_at"),
                databaseManager.getDateTime(resultSet, "completed_at"),
                resultSet.getBoolean("reminder_sent"),
                resultSet.getBoolean("overdue_notified")
        );
    }

    private String baseTaskSelect() {
        return """
                SELECT t.*,
                       (SELECT COUNT(*) FROM subtasks s WHERE s.task_id = t.id AND s.is_done = 1) AS completed_subtasks,
                       (SELECT COUNT(*) FROM subtasks s WHERE s.task_id = t.id) AS total_subtasks
                FROM tasks t
                """;
    }
}
