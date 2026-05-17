package com.taskbot.repository;

import com.taskbot.database.DatabaseManager;
import com.taskbot.model.Subtask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubtaskRepository {
    private final DatabaseManager databaseManager;

    public SubtaskRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Subtask addSubtask(int taskId, String title) {
        String sql = "INSERT INTO subtasks (task_id, title) VALUES (?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setString(2, title);
            statement.executeUpdate();
            int subtaskId = databaseManager.getLastInsertId(connection);
            return findById(subtaskId);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menambah subtask", e);
        }
    }

    public List<Subtask> findByTaskId(int taskId) {
        String sql = "SELECT * FROM subtasks WHERE task_id = ? ORDER BY id ASC";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Subtask> subtasks = new ArrayList<>();
                while (resultSet.next()) {
                    subtasks.add(map(resultSet));
                }
                return subtasks;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil subtask", e);
        }
    }

    public boolean markDone(int userId, int subtaskId) {
        String sql = """
                UPDATE subtasks
                SET is_done = 1
                WHERE id = ?
                  AND task_id IN (SELECT id FROM tasks WHERE user_id = ?)
                """;
        return executeOwnedUpdate(sql, subtaskId, userId);
    }

    public boolean delete(int userId, int subtaskId) {
        String sql = """
                DELETE FROM subtasks
                WHERE id = ?
                  AND task_id IN (SELECT id FROM tasks WHERE user_id = ?)
                """;
        return executeOwnedUpdate(sql, subtaskId, userId);
    }

    private Subtask findById(int subtaskId) {
        String sql = "SELECT * FROM subtasks WHERE id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, subtaskId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil subtask", e);
        }
        return null;
    }

    private boolean executeOwnedUpdate(String sql, int subtaskId, int userId) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, subtaskId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui subtask", e);
        }
    }

    private Subtask map(ResultSet resultSet) throws SQLException {
        return new Subtask(
                resultSet.getInt("id"),
                resultSet.getInt("task_id"),
                resultSet.getString("title"),
                resultSet.getBoolean("is_done"),
                databaseManager.getDateTime(resultSet, "created_at")
        );
    }
}
