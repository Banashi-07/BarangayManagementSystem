package UI.dialogs;

import database.Report;
import database.ResidentDAO;
import service.Reportservice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Reportdialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField incidentDateField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> residentComplainantComboBox;
    private JComboBox<String> residentComplaineeComboBox;
    private JTextField complainantSearchField;
    private JTextField complaineeSearchField;
    
    private boolean saved = false;
    private Report report;
    private List<ResidentDAO.ResidentRow> residents;
    private List<ResidentDAO.ResidentRow> filteredComplainants;
    private List<ResidentDAO.ResidentRow> filteredComplainees;
    
    public Reportdialog(Frame parent, Report report) {
        super(parent, report == null ? "Add New Report" : "Edit Report", true);
        this.report = report;
        loadResidents();
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void loadResidents() {
        residents = ResidentDAO.getAllResidentRows();
        filteredComplainants = new ArrayList<>(residents);
        filteredComplainees = new ArrayList<>(residents);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(102, 170, 51));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(report == null ? "Add New Report" : "Edit Report");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Left Panel - Resident Information
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(102, 170, 51), 2),
            "Party Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 16),
            new Color(102, 170, 51)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Complainant Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel complainantTitle = new JLabel("COMPLAINANT");
        complainantTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        complainantTitle.setForeground(new Color(102, 170, 51));
        leftPanel.add(complainantTitle, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel searchComplainantLabel = new JLabel("Search:");
        searchComplainantLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        leftPanel.add(searchComplainantLabel, gbc);
        
        gbc.gridx = 1;
        complainantSearchField = new JTextField();
        complainantSearchField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        complainantSearchField.setPreferredSize(new Dimension(200, 30));
        complainantSearchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterComplainants(complainantSearchField.getText());
            }
        });
        leftPanel.add(complainantSearchField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        residentComplainantComboBox = new JComboBox<>();
        residentComplainantComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        residentComplainantComboBox.setPreferredSize(new Dimension(280, 35));
        updateComplainantComboBox();
        leftPanel.add(residentComplainantComboBox, gbc);
        
        // Complainee Section
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel complaineeTitle = new JLabel("COMPLAINEE");
        complaineeTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        complaineeTitle.setForeground(new Color(200, 150, 50));
        leftPanel.add(complaineeTitle, gbc);
        
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel searchComplaineeLabel = new JLabel("Search:");
        searchComplaineeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        leftPanel.add(searchComplaineeLabel, gbc);
        
        gbc.gridx = 1;
        complaineeSearchField = new JTextField();
        complaineeSearchField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        complaineeSearchField.setPreferredSize(new Dimension(200, 30));
        complaineeSearchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterComplainees(complaineeSearchField.getText());
            }
        });
        leftPanel.add(complaineeSearchField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        residentComplaineeComboBox = new JComboBox<>();
        residentComplaineeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        residentComplaineeComboBox.setPreferredSize(new Dimension(280, 35));
        updateComplaineeComboBox();
        leftPanel.add(residentComplaineeComboBox, gbc);
        
        // Right Panel - Report Details
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(102, 170, 51), 2),
            "Report Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 16),
            new Color(102, 170, 51)
        ));
        
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.insets = new Insets(10, 10, 10, 10);
        
        // Title Field
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.gridwidth = 1;
        JLabel titleFieldLabel = new JLabel("Report Title:");
        titleFieldLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        rightPanel.add(titleFieldLabel, gbcRight);
        
        gbcRight.gridx = 1;
        titleField = new JTextField();
        titleField.setFont(new Font("Tahoma", Font.PLAIN, 13));
        titleField.setPreferredSize(new Dimension(300, 35));
        rightPanel.add(titleField, gbcRight);
        
        // Incident Date
        gbcRight.gridx = 0;
        gbcRight.gridy = 1;
        JLabel dateLabel = new JLabel("Incident Date:");
        dateLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        rightPanel.add(dateLabel, gbcRight);
        
        gbcRight.gridx = 1;
        incidentDateField = new JTextField();
        incidentDateField.setFont(new Font("Tahoma", Font.PLAIN, 13));
        incidentDateField.setPreferredSize(new Dimension(300, 35));
        if (report == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            incidentDateField.setText(sdf.format(new Date()));
        }
        rightPanel.add(incidentDateField, gbcRight);
        
        // Status
        gbcRight.gridx = 0;
        gbcRight.gridy = 2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        rightPanel.add(statusLabel, gbcRight);
        
        gbcRight.gridx = 1;
        String[] statuses = {"Pending", "Scheduled", "Unsettled", "Settled"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
        statusComboBox.setPreferredSize(new Dimension(300, 35));
        rightPanel.add(statusComboBox, gbcRight);
        
        // Description
        gbcRight.gridx = 0;
        gbcRight.gridy = 3;
        gbcRight.gridwidth = 2;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        rightPanel.add(descLabel, gbcRight);
        
        gbcRight.gridy = 4;
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 150));
        rightPanel.add(descScrollPane, gbcRight);
        
        // Load existing data if editing
        if (report != null) {
            titleField.setText(report.getTitle());
            descriptionArea.setText(report.getDescription());
            incidentDateField.setText(report.getIncidentDate());
            statusComboBox.setSelectedItem(report.getStatus());
            
            // Select the complainant
            if (report.getComplainantId() > 0 && residents != null) {
                for (int i = 0; i < residents.size(); i++) {
                    if (residents.get(i).id == report.getComplainantId()) {
                        residentComplainantComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Select the complainee
            if (report.getComplaineeId() > 0 && residents != null) {
                for (int i = 0; i < residents.size(); i++) {
                    if (residents.get(i).id == report.getComplaineeId()) {
                        residentComplaineeComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        
        // Add panels to main content
        mainContentPanel.add(leftPanel, BorderLayout.WEST);
        mainContentPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JButton saveButton = createStyledButton("Save Report", new Color(102, 170, 51));
        saveButton.addActionListener(e -> saveReport());
        
        JButton cancelButton = createStyledButton("Cancel", new Color(200, 200, 200));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void filterComplainants(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredComplainants = new ArrayList<>(residents);
        } else {
            String search = searchText.toLowerCase();
            filteredComplainants = residents.stream()
                .filter(r -> r.name.toLowerCase().contains(search) || 
                           (r.purok != null && r.purok.toLowerCase().contains(search)))
                .collect(Collectors.toList());
        }
        updateComplainantComboBox();
    }
    
    private void filterComplainees(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredComplainees = new ArrayList<>(residents);
        } else {
            String search = searchText.toLowerCase();
            filteredComplainees = residents.stream()
                .filter(r -> r.name.toLowerCase().contains(search) || 
                           (r.purok != null && r.purok.toLowerCase().contains(search)))
                .collect(Collectors.toList());
        }
        updateComplaineeComboBox();
    }
    
    private void updateComplainantComboBox() {
        residentComplainantComboBox.removeAllItems();
        if (filteredComplainants != null && !filteredComplainants.isEmpty()) {
            for (ResidentDAO.ResidentRow resident : filteredComplainants) {
                residentComplainantComboBox.addItem(resident.name + " (" + resident.purok + ")");
            }
        } else {
            residentComplainantComboBox.addItem("No residents found");
            residentComplainantComboBox.setEnabled(false);
        }
        residentComplainantComboBox.setEnabled(true);
    }
    
    private void updateComplaineeComboBox() {
        residentComplaineeComboBox.removeAllItems();
        if (filteredComplainees != null && !filteredComplainees.isEmpty()) {
            for (ResidentDAO.ResidentRow resident : filteredComplainees) {
                residentComplaineeComboBox.addItem(resident.name + " (" + resident.purok + ")");
            }
        } else {
            residentComplaineeComboBox.addItem("No residents found");
            residentComplaineeComboBox.setEnabled(false);
        }
        residentComplaineeComboBox.setEnabled(true);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 40));
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
    
    private ResidentDAO.ResidentRow getSelectedComplainant() {
        int index = residentComplainantComboBox.getSelectedIndex();
        if (index >= 0 && index < filteredComplainants.size()) {
            return filteredComplainants.get(index);
        }
        return null;
    }
    
    private ResidentDAO.ResidentRow getSelectedComplainee() {
        int index = residentComplaineeComboBox.getSelectedIndex();
        if (index >= 0 && index < filteredComplainees.size()) {
            return filteredComplainees.get(index);
        }
        return null;
    }
    
    private void saveReport() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String incidentDate = incidentDateField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        
        ResidentDAO.ResidentRow complainant = getSelectedComplainant();
        ResidentDAO.ResidentRow complainee = getSelectedComplainee();
        
        // Validation
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a report title.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (complainant == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a complainant.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (complainee == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a complainee.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a description.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (incidentDate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter an incident date.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!incidentDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Please enter date in YYYY-MM-DD format.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (report == null) {
                int reportId = Reportservice.addReport(title, description, incidentDate, status, 
                                                       complainant.id, complainee.id);
                if (reportId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Report added successfully!\n" +
                        "Complainant: " + complainant.name + "\n" +
                        "Complainee: " + complainee.name,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add report.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Reportservice.updateReport(report.getId(), title, description, incidentDate, 
                                          status, complainant.id, complainee.id);
                JOptionPane.showMessageDialog(this,
                    "Report updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving report: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}