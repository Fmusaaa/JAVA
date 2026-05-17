package com.taskbot.repository;

import com.taskbot.database.DatabaseManager;
import com.taskbot.model.RecurringTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RecurringTaskRepository {
    private final DatabaseManager databaseManager;

    public RecurringTaskRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public RecurringTask findByTaskId(int taskId) {
        String sql = "SELECT * FROM recurring_tasks WHERE task_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil recurring task", e);
        }
        return null;
    }

    public RecurringTask save(int taskId, String recurrenceType, int intervalValue, LocalDateTime nextDueDate) {
        RecurringTask existing = findByTaskId(taskId);
        if (existing != null) {
            update(taskId, recurrenceType, intervalValue, nextDueDate);
            return findByTaskId(taskId);
        }
        return insert(taskId, recurrenceType, intervalValue, nextDueDate);
    }

    private RecurringTask insert(int taskId, String recurrenceType, int intervalValue, LocalDateTime nextDueDate) {
        String sql = "INSERT INTO recurring_tasks (task_id, recurrence_type, interval_value, next_due_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setString(2, recurrenceType);
            statement.setInt(3, intervalValue);
            databaseManager.setDateTime(statement, 4, nextDueDate);
            statement.executeUpdate();
            databaseManager.getLastInsertId(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membuat recurring task", e);
        }
        return findByTaskId(taskId);
    }

    private void update(int taskId, String recurrenceType, int intervalValue, LocalDateTime nextDueDate) {
        String sql = "UPDATE recurring_tasks SET recurrence_type = ?, interval_value = ?, next_due_date = ? WHERE task_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, recurrenceType);
            statement.setInt(2, intervalValue);
            databaseManager.setDateTime(statement, 3, nextDueDate);
            statement.setInt(4, taskId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui recurring task", e);
        }
    }

    private RecurringTask map(ResultSet resultSet) throws SQLException {
        return new RecurringTask(
                resultSet.getInt("id"),
                resultSet.getInt("task_id"),
                resultSet.getString("recurrence_type"),
                resultSet.getInt("interval_value"),
                databaseManager.getDateTime(resultSet, "next_due_date")
        );
    }
}
