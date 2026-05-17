package com.taskbot.repository;

import com.taskbot.database.DatabaseManager;
import com.taskbot.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final DatabaseManager databaseManager;

    public UserRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User findOrCreate(long telegramId, String username, String firstName) {
        User existing = findByTelegramId(telegramId);
        if (existing != null) {
            updateProfile(existing.getId(), username, firstName);
            return findByTelegramId(telegramId);
        }
        return create(telegramId, username, firstName);
    }

    public User findByTelegramId(long telegramId) {
        String sql = "SELECT * FROM users WHERE telegram_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, telegramId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil user", e);
        }
        return null;
    }

    private User create(long telegramId, String username, String firstName) {
        String sql = "INSERT INTO users (telegram_id, username, first_name) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, telegramId);
            statement.setString(2, username);
            statement.setString(3, firstName);
            statement.executeUpdate();
            databaseManager.getLastInsertId(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal membuat user", e);
        }
        return findByTelegramId(telegramId);
    }

    private void updateProfile(int userId, String username, String firstName) {
        String sql = "UPDATE users SET username = ?, first_name = ? WHERE id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui user", e);
        }
    }

    private User map(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getLong("telegram_id"),
                resultSet.getString("username"),
                resultSet.getString("first_name"),
                databaseManager.getDateTime(resultSet, "created_at")
        );
    }
}
