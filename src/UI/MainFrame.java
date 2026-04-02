package UI;

import UI.panels.ContentPanel;
import UI.panels.HomePanel;
import UI.panels.MainPanel;
import database.DatabaseManager;
import database.DatabaseSeeder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

	private HomePanel homePanel = new HomePanel();
	
    public MainFrame() {
        // 1. Connect to database
        DatabaseManager.connect();

        // 2. Seed sample data (skips if tables already have data)
        DatabaseSeeder.seed();

        setTitle("Barangay Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
                DatabaseManager.close();
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
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}