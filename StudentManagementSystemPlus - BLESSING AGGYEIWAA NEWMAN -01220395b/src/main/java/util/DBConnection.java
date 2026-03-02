package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
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
                "date TEXT, " +
                "status TEXT)";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NEW METHOD: Call this to erase all data
    public static void resetDatabase() {
        String sql = "DROP TABLE IF EXISTS students";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql); // Delete the table
            AppLogger.logInfo("Database has been reset (erased).");

            // Recreate the empty table immediately
            createTableIfNotExists();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}