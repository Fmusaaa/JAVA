package com.taskbot.database;

import com.taskbot.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:taskbot.db";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id TEXT NOT NULL," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "due_date TEXT," +
                    "priority INTEGER DEFAULT 2," +
                    "completed BOOLEAN DEFAULT 0," +
                    "created_at TEXT NOT NULL" +
                    ")";

            stmt.execute(createTasksTable);
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    public Task addTask(String userId, String title, String description, LocalDateTime dueDate, int priority) {
        String sql = "INSERT INTO tasks (user_id, title, description, due_date, priority, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, dueDate != null ? dueDate.format(DATETIME_FORMATTER) : null);
            pstmt.setInt(5, priority);
            pstmt.setString(6, LocalDateTime.now().format(DATETIME_FORMATTER));

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int taskId = generatedKeys.getInt(1);
                    return getTask(userId, taskId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding task", e);
        }
        return null;
    }

    public Task getTask(String userId, int taskId) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setString(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTask(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting task", e);
        }
        return null;
    }

    public List<Task> getUserTasks(String userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY priority DESC, due_date ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user tasks", e);
        }
        return tasks;
    }

    public boolean updateTask(String userId, int taskId, String title, String description,
                             LocalDateTime dueDate, int priority) {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ? " +
                "WHERE id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, dueDate != null ? dueDate.format(DATETIME_FORMATTER) : null);
            pstmt.setInt(4, priority);
            pstmt.setInt(5, taskId);
            pstmt.setString(6, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating task", e);
        }
        return false;
    }

    public boolean completeTask(String userId, int taskId) {
        String sql = "UPDATE tasks SET completed = 1 WHERE id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setString(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error completing task", e);
        }
        return false;
    }

    public boolean deleteTask(String userId, int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.setString(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting task", e);
        }
        return false;
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String userId = rs.getString("user_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String dueDateStr = rs.getString("due_date");
        int priority = rs.getInt("priority");
        boolean completed = rs.getBoolean("completed");
        String createdAtStr = rs.getString("created_at");

        LocalDateTime dueDate = dueDateStr != null ? LocalDateTime.parse(dueDateStr, DATETIME_FORMATTER) : null;
        LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, DATETIME_FORMATTER);

        return new Task(id, userId, title, description, dueDate, priority, completed, createdAt);
    }
}
