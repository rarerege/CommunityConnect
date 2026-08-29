package communityconnect;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel - CORRECTED VERSION
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Starting CommunityConnect Application...");
        System.out.println("A Resource Matcher for Social Good");
        System.out.println("===================================");

        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ Database connected successfully!");
        } else {
            System.out.println("❌ Database connection failed!");
            JOptionPane.showMessageDialog(null,
                    "Database connection failed!\n\n" +
                            "Please check:\n" +
                            "1. MySQL Server is running\n" +
                            "2. Update resources/database.properties with your credentials\n" +
                            "3. Execute resources/schema.sql in MySQL Workbench",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }
}
