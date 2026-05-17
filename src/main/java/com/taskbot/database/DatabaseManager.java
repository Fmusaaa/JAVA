package com.taskbot.database;

import com.taskbot.config.AppConfig;
import com.taskbot.config.DatabaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private final AppConfig config;

    public DatabaseManager(AppConfig config) {
        this.config = config;
        loadJdbcDriver();
        initializeDatabase();
    }

    public Connection getConnection() throws SQLException {
        Connection connection;
        if (config.getDatabaseType() == DatabaseType.MYSQL) {
            connection = DriverManager.getConnection(config.getDatabaseUrl(),
                    config.getDatabaseUsername(), config.getDatabasePassword());
        } else {
            connection = DriverManager.getConnection(config.getDatabaseUrl());
        }

        if (config.getDatabaseType() == DatabaseType.SQLITE) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    public boolean isMySql() {
        return config.getDatabaseType() == DatabaseType.MYSQL;
    }

    public void setDateTime(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    public LocalDateTime getDateTime(ResultSet resultSet, String columnName) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public int getLastInsertId(Connection connection) throws SQLException {
        String sql = isMySql() ? "SELECT LAST_INSERT_ID()" : "SELECT last_insert_rowid()";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    private void initializeDatabase() {
        try (Connection connection = getConnection()) {
            if (isMySql()) {
                createMySqlSchema(connection);
            } else {
                migrateLegacySqliteIfNeeded(connection);
                createSqliteSchema(connection);
            }
            logger.info("Database initialized using {}", config.getDatabaseType());
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }

    private void loadJdbcDriver() {
        String driverClass = isMySql() ? "com.mysql.cj.jdbc.Driver" : "org.sqlite.JDBC";
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver tidak ditemukan: " + driverClass, e);
        }
    }

    private void createMySqlSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        telegram_id BIGINT UNIQUE NOT NULL,
                        username VARCHAR(100),
                        first_name VARCHAR(100),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        description TEXT NULL,
                        due_date DATETIME NULL,
                        priority INT DEFAULT 1,
                        status VARCHAR(20) DEFAULT 'pending',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        completed_at DATETIME NULL,
                        reminder_sent BOOLEAN DEFAULT FALSE,
                        overdue_notified BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS subtasks (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        task_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        is_done BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS recurring_tasks (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        task_id INT NOT NULL,
                        recurrence_type VARCHAR(20) NOT NULL,
                        interval_value INT DEFAULT 1,
                        next_due_date DATETIME NULL,
                        FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
                    )
                    """);
        }
    }

    private void createSqliteSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        telegram_id INTEGER UNIQUE NOT NULL,
                        username TEXT,
                        first_name TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        due_date TIMESTAMP NULL,
                        priority INTEGER DEFAULT 1,
                        status TEXT DEFAULT 'pending',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        completed_at TIMESTAMP NULL,
                        reminder_sent INTEGER DEFAULT 0,
                        overdue_notified INTEGER DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS subtasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        task_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        is_done INTEGER DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS recurring_tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        task_id INTEGER NOT NULL,
                        recurrence_type TEXT NOT NULL,
                        interval_value INTEGER DEFAULT 1,
                        next_due_date TIMESTAMP NULL,
                        FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
                    )
                    """);
        }
    }

    private void migrateLegacySqliteIfNeeded(Connection connection) throws SQLException {
        if (!tableExists(connection, "tasks") || columnExists(connection, "tasks", "status")) {
            return;
        }

        logger.info("Migrating legacy SQLite tasks table to OOP schema");
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE tasks RENAME TO tasks_legacy");
        }
        createSqliteSchema(connection);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT OR IGNORE INTO users (telegram_id, username, first_name, created_at)
                    SELECT CAST(user_id AS INTEGER), NULL, NULL, MIN(created_at)
                    FROM tasks_legacy
                    GROUP BY user_id
                    """);
            statement.execute("""
                    INSERT INTO tasks (id, user_id, title, description, due_date, priority, status, created_at,
                                       completed_at, reminder_sent, overdue_notified)
                    SELECT old.id, users.id, old.title, old.description, old.due_date, old.priority,
                           CASE WHEN old.completed = 1 THEN 'completed' ELSE 'pending' END,
                           old.created_at, NULL, 0, 0
                    FROM tasks_legacy old
                    JOIN users ON users.telegram_id = CAST(old.user_id AS INTEGER)
                    """);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
