package UI.panels;

import UI.dialogs.LogoutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainPanel extends JPanel {

    public JButton btnHome;
    public JButton btnRecords;
    public JButton btnOfficialList;
    public JButton btnReports;
    public JButton btnLogout;
    
    private JPanel headPanel;
    private JLabel lblTitle;
    private ContentPanel contentPanel;

    public MainPanel(ContentPanel contentPanel) {
        this.contentPanel = contentPanel;

        setLayout(null);
        
        // Initialize components
        headPanel = new JPanel();
        headPanel.setLayout(null);
        headPanel.setBackground(Color.BLACK);
        add(headPanel);

        lblTitle = new JLabel("BARANGAY MANAGEMENT SYSTEM", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        headPanel.add(lblTitle);

        // Create buttons
        btnHome = new JButton("HOME");
        btnRecords = new JButton("RECORDS");
        btnOfficialList = new JButton("OFFICIAL LIST");
        btnReports = new JButton("REPORTS");
        btnLogout = new JButton("LOGOUT");

        styleNavButton(btnHome);
        styleNavButton(btnRecords);
        styleNavButton(btnOfficialList);
        styleNavButton(btnReports);
        styleNavButton(btnLogout);

        headPanel.add(btnHome);
        headPanel.add(btnRecords);
        headPanel.add(btnOfficialList);
        headPanel.add(btnReports);
        headPanel.add(btnLogout);

        // Add action listeners
        btnHome.addActionListener(e -> contentPanel.showPage("HOME"));
        btnRecords.addActionListener(e -> contentPanel.showPage("RECORDS"));
        btnOfficialList.addActionListener(e -> contentPanel.showPage("OFFICIAL LIST"));
        btnReports.addActionListener(e -> contentPanel.showPage("REPORTS"));
        btnLogout.addActionListener(e -> {
        	
           LogoutDialog logoutDialog = new LogoutDialog(SwingUtilities.getWindowAncestor(this));
            logoutDialog.setLocationRelativeTo(this); // center on main panel
            logoutDialog.setVisible(true);
        });
        
        // Add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayout();
            }
        });
    }
    
    private void updateLayout() {
        int width = getWidth();
        int height = Math.max(65, getHeight()); // Minimum height of 65
        
        if (width == 0) return;
        
        // Scale font based on width
        double widthRatio = width / 1280.0;
        int fontSize = (int)(15 * widthRatio);
        fontSize = Math.max(fontSize, 10); // Minimum font size
        lblTitle.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        int buttonFontSize = (int)(12 * widthRatio);
        buttonFontSize = Math.max(buttonFontSize, 9);
        Font buttonFont = new Font("Dialog", Font.BOLD, buttonFontSize);
        
        btnHome.setFont(buttonFont);
        btnRecords.setFont(buttonFont);
        btnOfficialList.setFont(buttonFont);
        btnReports.setFont(buttonFont);
        btnLogout.setFont(buttonFont);
        
        // Set header panel to full width
        headPanel.setBounds(0, 0, width, height);
        
        // Title on the left
        int titleWidth = (int)(511 * widthRatio);
        lblTitle.setBounds(0, 3, titleWidth, height - 6);
        
        // Calculate button dimensions
        int buttonWidth = (int)(120 * widthRatio);
        int buttonHeight = (int)(25 * (height / 65.0));
        int buttonY = (height - buttonHeight) / 2;
        
        // Right-align buttons with proportional spacing
        int totalButtonWidth = buttonWidth * 5;
        int spacing = (int)(10 * widthRatio);
        int rightMargin = (int)(25 * widthRatio);
        int startX = width - totalButtonWidth - (spacing * 4) - rightMargin;
        
        btnHome.setBounds(startX, buttonY, buttonWidth, buttonHeight);
        btnRecords.setBounds(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
        btnOfficialList.setBounds(startX + (buttonWidth + spacing) * 2, buttonY, buttonWidth, buttonHeight);
        btnReports.setBounds(startX + (buttonWidth + spacing) * 3, buttonY, buttonWidth, buttonHeight);
        btnLogout.setBounds(startX + (buttonWidth + spacing) * 4, buttonY, buttonWidth, buttonHeight);
        
        // Update preferred size
        setPreferredSize(new Dimension(width, height));
    }

    private void styleNavButton(JButton button) {
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }
    
    @Override
    public void doLayout() {
        super.doLayout();
        updateLayout();
    }
}
