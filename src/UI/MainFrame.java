package UI;

import UI.panels.ContentPanel;
import UI.panels.HomePanel;
import UI.panels.MainPanel;
import database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private HomePanel homePanel = new HomePanel();
    private static final int NORMAL_WIDTH = 1280;
    private static final int NORMAL_HEIGHT = 1100;
    
    public MainFrame() {
        // Initialize database connection
        try {
            DatabaseManager.getConnection();
            System.out.println("Database connected successfully");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            
            int response = JOptionPane.showConfirmDialog(
                null,
                "Failed to connect to the database.\n\nError: " + e.getMessage() + 
                "\n\nDo you want to retry?\nClick 'No' to exit the application.",
                "Database Connection Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
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
        
        // Set the normal size first (for when restored down)
        setSize(NORMAL_WIDTH, NORMAL_HEIGHT);
        
        // Then maximize to full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Create main panel
        ContentPanel content = new ContentPanel(homePanel);
        MainPanel header = new MainPanel(content);

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        
        // Customize scroll bar appearance
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 150);
                this.trackColor = new Color(220, 220, 220);
            }
        });

        // Add components to frame
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Handle window state changes (maximize/restore)
        addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == Frame.NORMAL) {
                    // Window restored from maximized to normal size
                    SwingUtilities.invokeLater(() -> {
                        // Ensure window is at the correct normal size
                        setSize(NORMAL_WIDTH, NORMAL_HEIGHT);
                        setLocationRelativeTo(null);
                        homePanel.resizeComponents();
                        homePanel.revalidate();
                        homePanel.repaint();
                        scrollPane.getViewport().setViewPosition(new Point(0, 0));
                    });
                } else if (e.getNewState() == Frame.MAXIMIZED_BOTH) {
                    // Window maximized
                    SwingUtilities.invokeLater(() -> {
                        homePanel.resizeComponents();
                        homePanel.revalidate();
                        homePanel.repaint();
                        scrollPane.getViewport().setViewPosition(new Point(0, 0));
                    });
                }
            }
        });

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
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
        });
        
        setVisible(true);
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