package UI.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.List;

import database.DatabaseManager;
import database.DatabaseManager.Report;  // ← FIXED: Use Report from DatabaseManager
import database.ResidentDAO;
import UI.dialogs.Reportdialog;
import UI.dialogs.SettleReportDialog;
import UI.dialogs.ViewReportDialog;


public class ReportsPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel settledCountLabel;
    private JLabel unsettledCountLabel;
    private JLabel scheduledCountLabel;
    private JLabel pendingCountLabel;
    private String currentFilter = "All"; // Track current filter

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= HEADER =================
        GradientPanel headerPanel = new GradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(100, 110));

        JLabel subtitle = new JLabel("REPORTS", SwingConstants.CENTER);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Tahoma", Font.BOLD, 28));

        headerPanel.add(subtitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(230, 230, 230));
        add(mainPanel, BorderLayout.CENTER);

        // ================= LEFT PANEL =================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(500, 200));

        // Create cards with click handlers
        JButton settledCard = createCard("Settled Cases", "/img/CheckIcon.png", new Color(102, 170, 51));
        settledCountLabel = getValueLabel(settledCard);
        settledCard.addActionListener(e -> filterReportsByStatus("Settled"));
        cardsPanel.add(settledCard);

        JButton unsettledCard = createCard("Unsettled Cases", "/img/clockicon.png", new Color(200, 150, 50));
        unsettledCountLabel = getValueLabel(unsettledCard);
        unsettledCard.addActionListener(e -> filterReportsByStatus("Unsettled"));
        cardsPanel.add(unsettledCard);

        JButton scheduledCard = createCard("Scheduled Cases", "/img/calendaricon.png", new Color(100, 150, 200));
        scheduledCountLabel = getValueLabel(scheduledCard);
        scheduledCard.addActionListener(e -> filterReportsByStatus("Scheduled"));
        cardsPanel.add(scheduledCard);

        JButton pendingCard = createCard("Pending Cases", "/img/hourglassicon.png", new Color(180, 140, 70));
        pendingCountLabel = getValueLabel(pendingCard);
        pendingCard.addActionListener(e -> filterReportsByStatus("Pending"));
        cardsPanel.add(pendingCard);

        leftPanel.add(cardsPanel);
        leftPanel.add(Box.createVerticalStrut(20));

        JPanel monthlyPanel = new JPanel();
        monthlyPanel.setBackground(Color.WHITE);
        monthlyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Monthly Reports",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 14)
        ));
        monthlyPanel.setPreferredSize(new Dimension(500, 300));
        monthlyPanel.setMaximumSize(new Dimension(500, 300));

        leftPanel.add(monthlyPanel);
        mainPanel.add(leftPanel);

        // ================= RIGHT PANEL (FULL SIZE RECENT RECORDS) =================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Button panel at the top
        JPanel btnpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnpanel.setBackground(Color.WHITE);
        
        JButton btnAddReport = createStyledButton("Add Report", new Color(102, 170, 51));
        btnAddReport.addActionListener(e -> showAddReportDialog());
        btnpanel.add(btnAddReport);
        
        JButton btnSettleReport = createStyledButton("Settle Report", new Color(70, 130, 180));
        btnSettleReport.addActionListener(e -> showSettleReportDialog());
        btnpanel.add(btnSettleReport);
        
        JButton btnEditReport = createStyledButton("Edit Report", new Color(255, 165, 0));
        btnEditReport.addActionListener(e -> showEditReportDialog());
        btnpanel.add(btnEditReport);
        
        JButton btnViewReport = createStyledButton("View Report", new Color(100, 100, 200));
        btnViewReport.addActionListener(e -> showViewReportDialog());
        btnpanel.add(btnViewReport);
        
        rightPanel.add(btnpanel, BorderLayout.NORTH);

        // Recent Records Panel that fills the remaining space
        JPanel recentRecordsPanel = new JPanel(new BorderLayout());
        recentRecordsPanel.setBackground(Color.WHITE);
        recentRecordsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Header with "Recent Records" and "View All" button
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(102, 170, 51));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel recentLabel = new JLabel("Recent Records");
        recentLabel.setForeground(Color.WHITE);
        recentLabel.setFont(new Font("Tahoma", Font.BOLD, 18));

        JButton viewAll = new JButton("View All");
        viewAll.setForeground(Color.WHITE);
        viewAll.setFont(new Font("Tahoma", Font.BOLD, 13));
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.setFocusPainted(false);
        viewAll.setBorderPainted(false);
        viewAll.setContentAreaFilled(false);
        viewAll.setOpaque(false);
        viewAll.addActionListener(e -> filterReportsByStatus("All"));

        header.add(recentLabel, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);
        recentRecordsPanel.add(header, BorderLayout.NORTH);
        
        // Table that takes the remaining space
        String[] columns = {"ID", "Title", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths proportionally
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Add double-click listener to view report
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showViewReportDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        recentRecordsPanel.add(scrollPane, BorderLayout.CENTER);
        
        rightPanel.add(recentRecordsPanel, BorderLayout.CENTER);

        mainPanel.add(rightPanel);
        
        // Load initial data
        loadReports();
        updateCardCounts();
    }

    // ================= CREATE STYLED BUTTON =================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }

    // ================= LOAD REPORTS =================
    private void loadReports() {
        try {
            List<Report> reports;
            if (currentFilter.equals("All")) {
                reports = DatabaseManager.getAllReports();
            } else {
                reports = DatabaseManager.getReportsByStatus(currentFilter);
            }
            
            // Clear existing rows
            tableModel.setRowCount(0);
            
            // Add all reports to table
            for (Report report : reports) {
                String displayTitle = report.getTitle() != null && !report.getTitle().isEmpty() ? 
                                     report.getTitle() : "Untitled Report";
                tableModel.addRow(new Object[]{
                    report.getId(),
                    displayTitle,
                    report.getStatus(),
                    report.getIncidentDate()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading reports: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= UPDATE CARD COUNTS =================
    private void updateCardCounts() {
        try {
            int settledCount = DatabaseManager.getReportCountByStatus("Settled");
            int unsettledCount = DatabaseManager.getReportCountByStatus("Unsettled");
            int scheduledCount = DatabaseManager.getReportCountByStatus("Scheduled");
            int pendingCount = DatabaseManager.getReportCountByStatus("Pending");
            
            settledCountLabel.setText(String.valueOf(settledCount));
            unsettledCountLabel.setText(String.valueOf(unsettledCount));
            scheduledCountLabel.setText(String.valueOf(scheduledCount));
            pendingCountLabel.setText(String.valueOf(pendingCount));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= FILTER REPORTS BY STATUS =================
    private void filterReportsByStatus(String status) {
        currentFilter = status;
        loadReports();
    }

    // ================= SHOW ADD REPORT DIALOG =================
    private void showAddReportDialog() {
        Reportdialog dialog = new Reportdialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadReports();
            updateCardCounts();
        }
    }

    // ================= SHOW EDIT REPORT DIALOG =================
    private void showEditReportDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a report to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int reportId = (int) tableModel.getValueAt(selectedRow, 0);
            Report report = DatabaseManager.getReportById(reportId);
            
            if (report != null) {
                Reportdialog dialog = new Reportdialog((Frame) SwingUtilities.getWindowAncestor(this), report);
                dialog.setVisible(true);
                
                if (dialog.isSaved()) {
                    loadReports();
                    updateCardCounts();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SHOW VIEW REPORT DIALOG =================
    private void showViewReportDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a report to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int reportId = (int) tableModel.getValueAt(selectedRow, 0);
            Report report = DatabaseManager.getReportById(reportId);
            
            if (report != null) {
                ViewReportDialog dialog = new ViewReportDialog((Frame) SwingUtilities.getWindowAncestor(this), report);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SHOW SETTLE REPORT DIALOG =================
    private void showSettleReportDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a report to settle.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int reportId = (int) tableModel.getValueAt(selectedRow, 0);
            Report report = DatabaseManager.getReportById(reportId);
            
            if (report != null) {
                if ("Settled".equals(report.getStatus())) {
                    JOptionPane.showMessageDialog(this,
                        "This report is already settled.",
                        "Already Settled",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                SettleReportDialog dialog = new SettleReportDialog((Frame) SwingUtilities.getWindowAncestor(this), report);
                dialog.setVisible(true);
                
                if (dialog.isSettled()) {
                    loadReports();
                    updateCardCounts();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= GET VALUE LABEL FROM CARD =================
    private JLabel getValueLabel(JButton card) {
        // Search for the value label in the card's components
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComponents = panel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getFont().getSize() == 32) { // This is the value label
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }

    // ================= CREATE CARD =================
    private JButton createCard(String title, String resourcePath, Color iconColor) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setFocusPainted(false);
        card.setBorderPainted(false);
        card.setContentAreaFilled(true);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(180, 90));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ICON
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
            Image scaled = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.out.println("Could not load icon: " + resourcePath);
            iconLabel.setIcon(createColoredCircleIcon(iconColor, 40));
        }
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.WEST);

        // TEXT
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        valueLabel.setForeground(new Color(50, 50, 50));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);

        // HOVER EFFECT
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    // Helper method to create colored circle icon
    private ImageIcon createColoredCircleIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(2, 2, size - 4, size - 4);
        g2d.dispose();
        return new ImageIcon(image);
    }
}