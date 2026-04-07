package UI.dialogs;

import database.DatabaseManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;

public class AddReportDialog extends JDialog {

    private JTextField complainantField;
    private JTextField respondentField;
    private JComboBox<String> incidentTypeBox;
    private JTextArea descriptionArea;
    private JTextField dateField;
    private JComboBox<String> statusBox;
    private Runnable onRefresh;

    public AddReportDialog(JFrame parent, String residentName, Runnable onRefresh) {
        super(parent, "Add Report Case", true);
        this.onRefresh = onRefresh;
        init(residentName);
    }

    public AddReportDialog(JDialog parent, String residentName, Runnable onRefresh) {
        super(parent, "Add Report Case", true);
        this.onRefresh = onRefresh;
        init(residentName);
    }

    private void init(String residentName) {
        setSize(500, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("FILE NEW REPORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 25, 10, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Complainant (read-only)
        addLabel(formPanel, "Complainant:", gbc, y);
        complainantField = createTextField(residentName);
        complainantField.setEditable(false);
        complainantField.setBackground(new Color(245, 245, 245));
        addField(formPanel, complainantField, gbc, y++);

        // Respondent
        addLabel(formPanel, "Respondent:", gbc, y);
        respondentField = createTextField("");
        addField(formPanel, respondentField, gbc, y++);

        // Incident Type (used as title)
        addLabel(formPanel, "Incident Type:", gbc, y);
        String[] types = {"Select Type", "Theft", "Physical Injury", "Verbal Abuse",
                          "Property Damage", "Harassment", "Threat", "Fraud", "Disturbance", "Other"};
        incidentTypeBox = new JComboBox<>(types);
        incidentTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        incidentTypeBox.setBackground(Color.WHITE);
        addField(formPanel, incidentTypeBox, gbc, y++);

        // Date of Incident
        addLabel(formPanel, "Date of Incident:", gbc, y);
        dateField = createTextField(LocalDate.now().toString());
        addField(formPanel, dateField, gbc, y++);

        // Description
        addLabel(formPanel, "Description:", gbc, y);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(null);
        descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        addField(formPanel, descScroll, gbc, y++);

        // Status
        addLabel(formPanel, "Status:", gbc, y);
        String[] statuses = {"Pending", "Under Investigation", "Resolved", "Dismissed"};
        statusBox = new JComboBox<>(statuses);
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusBox.setBackground(Color.WHITE);
        addField(formPanel, statusBox, gbc, y++);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(formScroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton cancelBtn = createButton("CANCEL", new Color(149, 165, 166), new Color(127, 140, 141));
        JButton saveBtn = createButton("FILE REPORT", new Color(46, 204, 113), new Color(39, 174, 96));

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> { if (validateFields()) saveReport(); });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private boolean validateFields() {
        if (respondentField.getText().trim().isEmpty()) {
            showError("Respondent name is required!");
            return false;
        }
        if (incidentTypeBox.getSelectedIndex() == 0) {
            showError("Please select an incident type!");
            return false;
        }
        if (dateField.getText().trim().isEmpty()) {
            showError("Date of incident is required!");
            return false;
        }
        if (!dateField.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            showError("Date must be in YYYY-MM-DD format!");
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            showError("Please provide a description of the incident!");
            return false;
        }
        return true;
    }

    private void saveReport() {
        try {
            // Get complainant ID (you may need to fetch this from the database)
            int complainantId = getResidentIdByName(complainantField.getText().trim());
            int respondentId = getResidentIdByName(respondentField.getText().trim());
            
            String title = incidentTypeBox.getSelectedItem().toString();
            String description = descriptionArea.getText().trim();
            String incidentDate = dateField.getText().trim();
            String status = statusBox.getSelectedItem().toString();
            
            DatabaseManager.addReport(title, description, incidentDate, status, complainantId, respondentId);
            
            JOptionPane.showMessageDialog(this, "Report filed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onRefresh != null) onRefresh.run();
            dispose();
        } catch (Exception ex) {
            showError("Error filing report: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private int getResidentIdByName(String name) {
        try {
            for (DatabaseManager.Resident r : DatabaseManager.getAllResidents()) {
                if (r.getName().equalsIgnoreCase(name)) {
                    return r.getId();
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding resident: " + e.getMessage());
        }
        return -1;
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int y) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0.3;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        panel.add(label, c);
    }

    private void addField(JPanel panel, JComponent comp, GridBagConstraints gbc, int y) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridx = 1;
        c.gridy = y;
        c.weightx = 0.7;
        panel.add(comp, c);
    }

    private JTextField createTextField(String value) {
        JTextField field = new JTextField(value != null ? value : "");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}