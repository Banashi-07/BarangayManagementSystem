package UI;

import UI.panels.ContentPanel;
import UI.panels.HomePanel;
import UI.panels.MainPanel;
import database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class MainFrame extends JFrame {

	private HomePanel homePanel = new HomePanel();
	
    public MainFrame() {
        // 1. Initialize database connection using getConnection()
        try {
            DatabaseManager.getConnection();
            System.out.println("Database connected successfully");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog to user
            int response = JOptionPane.showConfirmDialog(
                null,
                "Failed to connect to the database.\n\nError: " + e.getMessage() + 
                "\n\nDo you want to retry?\nClick 'No' to exit the application.",
                "Database Connection Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                // Retry connection
                try {
                    DatabaseManager.getConnection();
                } catch (SQLException ex) {
                    System.err.println("Retry failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(
                        null,
                        "Cannot connect to database. Application will exit.\nError: " + ex.getMessage(),
                        "Fatal Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            } else {
                System.exit(1);
            }
        }

        setTitle("Barangay Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Create main panel
        ContentPanel content = new ContentPanel(homePanel);
        MainPanel header = new MainPanel(content);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 150);
                this.trackColor = new Color(220, 220, 220);
            }
        });

        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    DatabaseManager.close();
                    System.out.println("Database connection closed");
                } catch (SQLException ex) {
                    System.err.println("Error closing database: " + ex.getMessage());
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            homePanel.refreshStatistics();
        });
        
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }
            
            new MainFrame();
        });
    }
}