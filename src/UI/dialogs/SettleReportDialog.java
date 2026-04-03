package UI.dialogs;

import database.Report;
import database.ResidentDAO;
import service.Reportservice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;

public class SettleReportDialog extends JDialog {
    private JTextArea settlementDescriptionArea;
    private boolean settled = false;
    private Report report;
    private ResidentDAO.ResidentRow complainant;
    private ResidentDAO.ResidentRow complainee;
    
    public SettleReportDialog(Frame parent, Report report) {
        super(parent, "Settle Report", true);
        this.report = report;
        loadParties();
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void loadParties() {
        if (report.getComplainantId() > 0) {
            complainant = ResidentDAO.getResidentById(report.getComplainantId());
        }
        if (report.getComplaineeId() > 0) {
            complainee = ResidentDAO.getResidentById(report.getComplaineeId());
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setSize(700, 750);
        setMinimumSize(new Dimension(650, 700));
        
        // Header Panel (NORTH)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(102, 170, 51));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Settle Report");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Panel (CENTER)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Report Information Panel (NORTH of mainPanel)
        JPanel infoPanel = createInfoPanel();
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, infoPanel.getPreferredSize().height));
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Settlement Panel (CENTER of mainPanel)
        JPanel settlementPanel = createSettlementPanel();
        mainPanel.add(settlementPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button Panel (SOUTH)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton settleButton = createStyledButton("✓ Settle Report", new Color(102, 170, 51));
        settleButton.addActionListener(e -> settleReport());
        
        JButton cancelButton = createStyledButton("✗ Cancel", new Color(200, 200, 200));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(settleButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(102, 170, 51), 2),
                "REPORT INFORMATION",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 16),
                new Color(102, 170, 51)
            ),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Report Title
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setBackground(new Color(248, 248, 248));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel titleIcon = new JLabel("📋 ");
        titleIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel titleLabel2 = new JLabel("Report Title:");
        titleLabel2.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        String reportTitle = report.getTitle() != null && !report.getTitle().isEmpty() ? 
                            report.getTitle() : "Untitled Report";
        JLabel titleValue = new JLabel(reportTitle);
        titleValue.setFont(new Font("Tahoma", Font.PLAIN, 14));
        titleValue.setForeground(new Color(70, 70, 70));
        
        JPanel titleLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleLeftPanel.setBackground(new Color(248, 248, 248));
        titleLeftPanel.add(titleIcon);
        titleLeftPanel.add(titleLabel2);
        
        titlePanel.add(titleLeftPanel, BorderLayout.WEST);
        titlePanel.add(titleValue, BorderLayout.CENTER);
        
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Parties Information
        JPanel partiesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        partiesPanel.setBackground(new Color(248, 248, 248));
        
        // Complainant Panel
        JPanel complainantPanel = createComplainantPanel();
        partiesPanel.add(complainantPanel);
        
        // Complainee Panel
        JPanel complaineePanel = createComplaineePanel();
        partiesPanel.add(complaineePanel);
        
        panel.add(partiesPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Report Details
        JPanel detailsPanel = createDetailsPanel();
        panel.add(detailsPanel);
        
        return panel;
    }
    
    private JPanel createComplainantPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(102, 170, 51), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel complainantTitle = new JLabel("👤 COMPLAINANT");
        complainantTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
        complainantTitle.setForeground(new Color(102, 170, 51));
        complainantTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(complainantTitle);
        panel.add(Box.createVerticalStrut(8));
        
        if (complainant != null) {
            addInfoLabel(panel, "Name:", complainant.name);
            addInfoLabel(panel, "Address:", complainant.address);
            addInfoLabel(panel, "Purok:", complainant.purok);
            addInfoLabel(panel, "Sex:", complainant.sex);
        } else {
            addInfoLabel(panel, "Information:", "Not available");
        }
        
        return panel;
    }
    
    private JPanel createComplaineePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 150, 50), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel complaineeTitle = new JLabel("⚠️ COMPLAINEE");
        complaineeTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
        complaineeTitle.setForeground(new Color(200, 150, 50));
        complaineeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(complaineeTitle);
        panel.add(Box.createVerticalStrut(8));
        
        if (complainee != null) {
            addInfoLabel(panel, "Name:", complainee.name);
            addInfoLabel(panel, "Address:", complainee.address);
            addInfoLabel(panel, "Purok:", complainee.purok);
            addInfoLabel(panel, "Sex:", complainee.sex);
        } else {
            addInfoLabel(panel, "Information:", "Not available");
        }
        
        return panel;
    }
    
    private void addInfoLabel(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(5, 0));
        rowPanel.setBackground(panel.getBackground());
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblLabel.setForeground(new Color(80, 80, 80));
        lblLabel.setPreferredSize(new Dimension(65, 20));
        
        JLabel lblValue = new JLabel((value != null && !value.isEmpty()) ? value : "—");
        lblValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
        
        rowPanel.add(lblLabel, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.CENTER);
        panel.add(rowPanel);
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 248, 248));
        
        JLabel detailsTitle = new JLabel("📄 REPORT DETAILS");
        detailsTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
        detailsTitle.setForeground(new Color(70, 70, 70));
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(detailsTitle);
        panel.add(Box.createVerticalStrut(8));
        
        // Description Panel
        JPanel descPanel = new JPanel(new BorderLayout(0, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel descLabel = new JLabel("<html><b>Description:</b></html>");
        descLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        descPanel.add(descLabel, BorderLayout.NORTH);
        
        JTextArea descArea = new JTextArea(report.getDescription());
        descArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(null);
        descArea.setRows(3);
        descPanel.add(descArea, BorderLayout.CENTER);
        
        panel.add(descPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Incident Date and Status
        JPanel metaPanel = new JPanel(new BorderLayout(10, 0));
        metaPanel.setBackground(new Color(248, 248, 248));
        metaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JPanel leftMeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftMeta.setBackground(new Color(248, 248, 248));
        JLabel dateLabel = new JLabel("📅 Incident Date: " + report.getIncidentDate());
        dateLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        leftMeta.add(dateLabel);
        
        JPanel rightMeta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightMeta.setBackground(new Color(248, 248, 248));
        JLabel statusLabel = new JLabel("Current Status: " + report.getStatus());
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        statusLabel.setForeground(getStatusColor(report.getStatus()));
        rightMeta.add(statusLabel);
        
        metaPanel.add(leftMeta, BorderLayout.WEST);
        metaPanel.add(rightMeta, BorderLayout.EAST);
        
        panel.add(metaPanel);
        
        return panel;
    }
    
    private JPanel createSettlementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(102, 170, 51), 2),
                "SETTLEMENT DETAILS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 16),
                new Color(102, 170, 51)
            ),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Label at top
        JLabel settlementLabel = new JLabel("Please provide detailed settlement description:");
        settlementLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel.add(settlementLabel, BorderLayout.NORTH);
        
        // Text area in center - Fills entire width
        settlementDescriptionArea = new JTextArea(12, 40);
        settlementDescriptionArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
        settlementDescriptionArea.setLineWrap(true);
        settlementDescriptionArea.setWrapStyleWord(true);
        settlementDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(settlementDescriptionArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Hint at bottom
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hintPanel.setBackground(Color.WHITE);
        JLabel hintLabel = new JLabel("💡 Tip: Include resolution details, agreements, and actions taken");
        hintLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        hintPanel.add(hintLabel);
        panel.add(hintPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private Color getStatusColor(String status) {
        switch (status) {
            case "Settled": return new Color(102, 170, 51);
            case "Pending": return new Color(255, 165, 0);
            case "Scheduled": return new Color(70, 130, 180);
            case "Unsettled": return new Color(200, 150, 50);
            default: return Color.BLACK;
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
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
    
    private void settleReport() {
        String settlementDescription = settlementDescriptionArea.getText().trim();
        
        if (settlementDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a settlement description.\nThis is required to document the resolution.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to settle this report?\n\n" +
            "Report: " + (report.getTitle() != null ? report.getTitle() : "Untitled") + "\n" +
            "Complainant: " + (complainant != null ? complainant.name : "Unknown") + "\n" +
            "Complainee: " + (complainee != null ? complainee.name : "Unknown") + "\n\n" +
            "This action cannot be undone.",
            "Confirm Settlement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Reportservice.settleReport(report.getId(), settlementDescription);
                JOptionPane.showMessageDialog(this,
                    "✓ Report settled successfully!\n\n" +
                    "The report has been marked as Settled and the settlement\n" +
                    "details have been saved to the database.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                settled = true;
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error settling report: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    public boolean isSettled() {
        return settled;
    }
}