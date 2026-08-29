package communityconnect;

import javax.swing.*;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            // Check if connection is closed or null
            if (connection == null || connection.isClosed()) {
                reconnect();
            }

            // Additional check to ensure connection is still valid
            if (!connection.isValid(2)) { // 2 second timeout
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection check failed: " + e.getMessage());
            reconnect();
        }
        return connection;
    }

    private static void reconnect() {
        closeConnection(); // Close existing connection if any

        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input == null) {
                throw new IllegalStateException(
                        "database.properties not found on classpath. " +
                                "Create resources/database.properties with db.url, db.username, db.password.");
            }
            props.load(input);
            input.close();

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            // Add autoReconnect parameters if not already present
            if (!url.contains("autoReconnect")) {
                url += "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connected successfully!");

        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Database Connection Failed!\n\n" +
                            "Please check:\n" +
                            "1. MySQL Server is running\n" +
                            "2. Database 'communityconnect' exists\n" +
                            "3. Update resources/database.properties with your credentials\n\n" +
                            "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Test connection failed: " + e.getMessage());
            return false;
        }
    }
}
