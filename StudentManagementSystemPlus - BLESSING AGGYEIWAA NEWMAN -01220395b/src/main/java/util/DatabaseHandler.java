package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:aj_management.db";
    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL);
                createTableIfNotExists();
            }
        } catch (SQLException e) {
            AppLogger.logError("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY, " +
                "full_name TEXT NOT NULL, " +
                "programme TEXT, " +
                "level INTEGER, " +
                "gpa REAL, " +
                "email TEXT, " +
                "phone TEXT, " +
                "date_added TEXT, " +
                "status TEXT)";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}