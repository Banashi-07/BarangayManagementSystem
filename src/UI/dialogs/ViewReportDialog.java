package UI.dialogs;

import database.Report;
import database.ResidentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ViewReportDialog extends JDialog {
    private JPanel contentPane;
    private JLabel lblReportTitle;
    private JTextArea txtDescription;
    private JLabel lblIncidentDate;
    private JLabel lblStatus;
    private JTextArea txtSettlement;
    private JLabel lblSettledDate;
    private JPanel complainantPanel;
    private JPanel complaineePanel;
    private JPanel settlementPanel;
    
    private Report report;
    private ResidentDAO.ResidentRow complainant;
    private ResidentDAO.ResidentRow complainee;
    
    public ViewReportDialog(Frame parent, Report report) {
        super(parent, "Report Details", true);
        this.report = report;
        loadParties();
        initialize();
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
    
    private void initialize() {
        setSize(700, 650);
        setMinimumSize(new Dimension(650, 750));
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10));
        
        // Header Panel (NORTH)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(102, 170, 51));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        JLabel lblHeaderTitle = new JLabel("REPORT DETAILS");
        lblHeaderTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblHeaderTitle, BorderLayout.CENTER);
        
        // Main Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        
        // Top section of center (Title + Parties)
        JPanel topCenterPanel = new JPanel();
        topCenterPanel.setLayout(new BoxLayout(topCenterPanel, BoxLayout.Y_AXIS));
        topCenterPanel.setBackground(Color.WHITE);
        
        // Title Section
        JPanel titleSection = createTitleSection();
        titleSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleSection.getPreferredSize().height));
        topCenterPanel.add(titleSection);
        topCenterPanel.add(Box.createVerticalStrut(10));
        
        // Parties Section
        JPanel partiesSection = createPartiesSection();
        partiesSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, partiesSection.getPreferredSize().height));
        topCenterPanel.add(partiesSection);
        
        centerPanel.add(topCenterPanel, BorderLayout.NORTH);
        
        // Details Section (Center)
        JPanel detailsSection = createDetailsSection();
        centerPanel.add(detailsSection, BorderLayout.CENTER);
        
        // Settlement Section (South of center, only if settled)
        if ("Settled".equals(report.getStatus()) && report.getSettlementDescription() != null && !report.getSettlementDescription().isEmpty()) {
            JPanel settlementSection = createSettlementSection();
            settlementSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            centerPanel.add(settlementSection, BorderLayout.SOUTH);
        }
        
        // Button Panel (SOUTH)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(102, 170, 51));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
    }
    
    private JPanel createTitleSection() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        panel.setLayout(new BorderLayout(10, 0));
        
        JLabel lblTitleLabel = new JLabel("Report Title:");
        lblTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblTitleLabel.setForeground(new Color(80, 80, 80));
        panel.add(lblTitleLabel, BorderLayout.WEST);
        
        String reportTitle = (report.getTitle() != null && !report.getTitle().isEmpty()) ? report.getTitle() : "Untitled Report";
        lblReportTitle = new JLabel(reportTitle);
        lblReportTitle.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblReportTitle.setForeground(new Color(60, 60, 60));
        panel.add(lblReportTitle, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPartiesSection() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridLayout(1, 2, 15, 0));
        
        // Complainant Panel
        complainantPanel = new JPanel();
        complainantPanel.setBackground(Color.WHITE);
        complainantPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(102, 170, 51), 1),
                "COMPLAINANT",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 12),
                new Color(102, 170, 51)
            ),
            new EmptyBorder(8, 10, 8, 10)
        ));
        complainantPanel.setLayout(new BoxLayout(complainantPanel, BoxLayout.Y_AXIS));
        
        if (complainant != null) {
            addInfoRow(complainantPanel, "Name:", complainant.name);
            addInfoRow(complainantPanel, "Address:", complainant.address);
            addInfoRow(complainantPanel, "Purok:", complainant.purok);
            addInfoRow(complainantPanel, "Sex:", complainant.sex);
        } else {
            addInfoRow(complainantPanel, "Information:", "Not available");
        }
        
        // Complainee Panel
        complaineePanel = new JPanel();
        complaineePanel.setBackground(Color.WHITE);
        complaineePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 150, 50), 1),
                "COMPLAINEE",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 12),
                new Color(200, 150, 50)
            ),
            new EmptyBorder(8, 10, 8, 10)
        ));
        complaineePanel.setLayout(new BoxLayout(complaineePanel, BoxLayout.Y_AXIS));
        
        if (complainee != null) {
            addInfoRow(complaineePanel, "Name:", complainee.name);
            addInfoRow(complaineePanel, "Address:", complainee.address);
            addInfoRow(complaineePanel, "Purok:", complainee.purok);
            addInfoRow(complaineePanel, "Sex:", complainee.sex);
        } else {
            addInfoRow(complaineePanel, "Information:", "Not available");
        }
        
        panel.add(complainantPanel);
        panel.add(complaineePanel);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value) {
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
    
    private JPanel createDetailsSection() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "REPORT DETAILS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 13),
                new Color(70, 70, 70)
            ),
            new EmptyBorder(10, 15, 15, 15)
        ));
        panel.setLayout(new BorderLayout(0, 10));
        
        // Description (NORTH)
        JPanel descPanel = new JPanel(new BorderLayout(0, 5));
        descPanel.setBackground(Color.WHITE);
        
        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setFont(new Font("Tahoma", Font.BOLD, 12));
        descPanel.add(lblDescription, BorderLayout.NORTH);
        
        txtDescription = new JTextArea(report.getDescription());
        txtDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setBackground(new Color(248, 248, 248));
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(8, 8, 8, 8)
        ));
        txtDescription.setRows(4);
        
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setPreferredSize(new Dimension(descScroll.getPreferredSize().width, 100));
        descPanel.add(descScroll, BorderLayout.CENTER);
        
        panel.add(descPanel, BorderLayout.NORTH);
        
        // Incident Date and Status (SOUTH)
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BorderLayout(10, 0));
        
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftInfo.setBackground(Color.WHITE);
        lblIncidentDate = new JLabel("📅 Incident Date: " + report.getIncidentDate());
        lblIncidentDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
        leftInfo.add(lblIncidentDate);
        
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightInfo.setBackground(Color.WHITE);
        lblStatus = new JLabel("Status: " + report.getStatus());
        lblStatus.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStatus.setForeground(getStatusColor(report.getStatus()));
        rightInfo.add(lblStatus);
        
        infoPanel.add(leftInfo, BorderLayout.WEST);
        infoPanel.add(rightInfo, BorderLayout.EAST);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSettlementSection() {
        settlementPanel = new JPanel();
        settlementPanel.setBackground(Color.WHITE);
        settlementPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(102, 170, 51), 2),
                "SETTLEMENT INFORMATION",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 13),
                new Color(102, 170, 51)
            ),
            new EmptyBorder(10, 15, 15, 15)
        ));
        settlementPanel.setLayout(new BorderLayout(0, 8));
        
        // Settlement Description
        JLabel lblSettlementDesc = new JLabel("Settlement Description:");
        lblSettlementDesc.setFont(new Font("Tahoma", Font.BOLD, 12));
        settlementPanel.add(lblSettlementDesc, BorderLayout.NORTH);
        
        txtSettlement = new JTextArea(report.getSettlementDescription());
        txtSettlement.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtSettlement.setLineWrap(true);
        txtSettlement.setWrapStyleWord(true);
        txtSettlement.setEditable(false);
        txtSettlement.setBackground(new Color(248, 248, 248));
        txtSettlement.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(8, 8, 8, 8)
        ));
        txtSettlement.setRows(3);
        
        JScrollPane settlementScroll = new JScrollPane(txtSettlement);
        settlementScroll.setPreferredSize(new Dimension(settlementScroll.getPreferredSize().width, 80));
        settlementPanel.add(settlementScroll, BorderLayout.CENTER);
        
        // Settled Date
        if (report.getSettledDate() != null && !report.getSettledDate().isEmpty()) {
            JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            datePanel.setBackground(Color.WHITE);
            lblSettledDate = new JLabel("Settled on: " + report.getSettledDate());
            lblSettledDate.setFont(new Font("Tahoma", Font.ITALIC, 11));
            lblSettledDate.setForeground(Color.GRAY);
            datePanel.add(lblSettledDate);
            settlementPanel.add(datePanel, BorderLayout.SOUTH);
        }
        
        return settlementPanel;
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
}