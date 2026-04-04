package UI.dialogs;

import UI.panels.HomePanel;
import database.DatabaseManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Professional Add Residence Dialog
 * - Modern centered layout with proper spacing
 * - Clean, professional design with card-like appearance
 */
public class AddResidence extends JDialog {

    private JTextField nameField;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JComboBox<Integer> yearCombo;
    private ButtonGroup sexGroup;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private JTextField addressField;
    private JComboBox<String> purokComboBox;
    private ButtonGroup statusGroup;
    private JRadioButton singleRadio;
    private JRadioButton marriedRadio;
    private JRadioButton widowedRadio;
    private ButtonGroup pwdGroup;
    private JRadioButton pwdYesRadio;
    private JRadioButton pwdNoRadio;
    private JTextField houseNoField;

    private HomePanel homePanel;

    public AddResidence(JFrame parent, HomePanel homePanel) {
        super(parent, "New Resident Registration", true);

        this.homePanel = homePanel;

        setSize(600, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 247, 250));
        setContentPane(container);

        // HEADER
        JPanel headerPanel = createHeader();
        container.add(headerPanel, BorderLayout.NORTH);

        // FORM PANEL (using GridBagLayout for better control)
        JPanel formPanel = createFormPanel();
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        container.add(scrollPane, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel buttonPanel = createButtonPanel();
        container.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(102, 170, 51));
        headerPanel.setPreferredSize(new Dimension(600, 100));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(82, 150, 31)));

        JLabel titleLabel = new JLabel("RESIDENT REGISTRATION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Add a new resident to the barangay records", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel headerContent = new JPanel(new GridLayout(2, 1, 0, 5));
        headerContent.setOpaque(false);
        headerContent.setBorder(new EmptyBorder(15, 0, 15, 0));
        headerContent.add(titleLabel);
        headerContent.add(subtitleLabel);
        
        headerPanel.add(headerContent, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        int y = 0;
        
        // ===== SECTION 1: PERSONAL INFORMATION =====
        addSectionHeader(panel, gbc, y++, "PERSONAL INFORMATION");
        y++;
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = y++; gbc.weightx = 1;
        addFieldLabel(panel, gbc, "Full Name *");
        
        nameField = createStyledTextField();
        gbc.gridy = y++;
        panel.add(nameField, gbc);
        y++;
        
        // Birthdate
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Date of Birth *");
        
        JPanel datePanel = createDatePicker();
        gbc.gridy = y++;
        panel.add(datePanel, gbc);
        y++;
        
        // Sex
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Sex *");
        
        JPanel sexPanel = createSexSelector();
        gbc.gridy = y++;
        panel.add(sexPanel, gbc);
        y++;
        
        // ===== SECTION 2: ADDRESS INFORMATION =====
        addSectionHeader(panel, gbc, y++, "ADDRESS INFORMATION");
        y++;
        
        // House No.
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "House No. *");
        
        houseNoField = createStyledTextField();
        gbc.gridy = y++;
        panel.add(houseNoField, gbc);
        y++;
        
        // Street Address
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Street Address *");
        
        addressField = createStyledTextField();
        gbc.gridy = y++;
        panel.add(addressField, gbc);
        y++;
        
        // Purok
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Purok/Zone");
        
        purokComboBox = createStyledComboBox(new String[]{
            "Select Purok", "1", "2", " 3", 
            "4", "5", "6", "7"
        });
        gbc.gridy = y++;
        panel.add(purokComboBox, gbc);
        y++;
        
        // ===== SECTION 3: ADDITIONAL DETAILS =====
        addSectionHeader(panel, gbc, y++, "ADDITIONAL DETAILS");
        y++;
        
        // Civil Status
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Civil Status *");
        
        JPanel statusPanel = createCivilStatusSelector();
        gbc.gridy = y++;
        panel.add(statusPanel, gbc);
        y++;
        
        // PWD Status
        gbc.gridy = y++;
        addFieldLabel(panel, gbc, "Person with Disability (PWD)");
        
        JPanel pwdPanel = createPwdSelector();
        gbc.gridy = y++;
        panel.add(pwdPanel, gbc);
        
        return panel;
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int y, String text) {
        JLabel section = new JLabel(text);
        section.setFont(new Font("Segoe UI", Font.BOLD, 16));
        section.setForeground(new Color(102, 170, 51));
        
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(section, gbc);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(102, 170, 51, 100));
        gbc.gridy = y + 1;
        panel.add(separator, gbc);
    }

    private void addFieldLabel(JPanel panel, GridBagConstraints gbc, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(60, 70, 80));
        panel.add(label, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(450, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(450, 38));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return combo;
    }

    private JPanel createDatePicker() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(450, 40));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] months = {"Month", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        monthCombo = new JComboBox<>(months);
        dayCombo = new JComboBox<>();
        yearCombo = new JComboBox<>();

        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 100; i++) {
            yearCombo.addItem(currentYear - i);
        }

        populateDays(31);
        monthCombo.addActionListener(e -> updateDaysInMonth());

        styleComboBox(monthCombo, 130);
        styleComboBox(dayCombo, 75);
        styleComboBox(yearCombo, 95);

        panel.add(monthCombo);
        panel.add(dayCombo);
        panel.add(yearCombo);

        return panel;
    }

    private void styleComboBox(JComboBox<?> combo, int width) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(width, 36));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
    }

    private JPanel createSexSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(450, 35));

        sexGroup = new ButtonGroup();
        maleRadio = createStyledRadioButton("Male");
        femaleRadio = createStyledRadioButton("Female");

        sexGroup.add(maleRadio);
        sexGroup.add(femaleRadio);
        maleRadio.setSelected(true);

        panel.add(maleRadio);
        panel.add(femaleRadio);

        return panel;
    }

    private JPanel createCivilStatusSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(450, 35));

        statusGroup = new ButtonGroup();
        singleRadio = createStyledRadioButton("Single");
        marriedRadio = createStyledRadioButton("Married");
        widowedRadio = createStyledRadioButton("Widowed");

        statusGroup.add(singleRadio);
        statusGroup.add(marriedRadio);
        statusGroup.add(widowedRadio);
        singleRadio.setSelected(true);

        panel.add(singleRadio);
        panel.add(marriedRadio);
        panel.add(widowedRadio);

        return panel;
    }

    private JPanel createPwdSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(450, 35));

        pwdGroup = new ButtonGroup();
        pwdYesRadio = createStyledRadioButton("Yes");
        pwdNoRadio = createStyledRadioButton("No");

        pwdGroup.add(pwdYesRadio);
        pwdGroup.add(pwdNoRadio);
        pwdNoRadio.setSelected(true);

        panel.add(pwdYesRadio);
        panel.add(pwdNoRadio);

        return panel;
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        radio.setBackground(Color.WHITE);
        radio.setForeground(new Color(60, 70, 80));
        radio.setFocusPainted(false);
        return radio;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnAdd = createPrimaryButton("ADD RESIDENT");
        btnAdd.addActionListener(e -> addResident());

        JButton btnCancel = createSecondaryButton("CANCEL");
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);
        
        return buttonPanel;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(new Color(102, 170, 51));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(82, 150, 31));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(102, 170, 51));
            }
        });
        
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(new Color(80, 80, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 240, 240));
            }
        });
        
        return btn;
    }

    // ========== BUSINESS LOGIC ==========

    private void addResident() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter the resident's full name.");
            nameField.requestFocus();
            return;
        }

        if (monthCombo.getSelectedIndex() == 0 || dayCombo.getSelectedItem() == null || 
            yearCombo.getSelectedItem() == null) {
            showError("Please select a complete birth date.");
            return;
        }

        String sex = maleRadio.isSelected() ? "Male" : "Female";

        String houseNo = houseNoField.getText().trim();
        if (houseNo.isEmpty()) {
            showError("Please enter the house number.");
            houseNoField.requestFocus();
            return;
        }

        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            showError("Please enter the street address.");
            addressField.requestFocus();
            return;
        }

        String purok = purokComboBox.getSelectedIndex() > 0 
            ? purokComboBox.getSelectedItem().toString() : "";

        int month = monthCombo.getSelectedIndex();
        int day = (Integer) dayCombo.getSelectedItem();
        int year = (Integer) yearCombo.getSelectedItem();
        
        // Validate date
        try {
            LocalDate.of(year, month, day);
        } catch (Exception e) {
            showError("Invalid date selected. Please check the date.");
            return;
        }
        
        String birthdate = String.format("%04d-%02d-%02d", year, month, day);

        String civilStatus = singleRadio.isSelected() ? "Single"
                : marriedRadio.isSelected() ? "Married" : "Widowed";

        String pwd = pwdYesRadio.isSelected() ? "Yes" : "No";

        // Build full address with house number
        String fullAddress = houseNo + ", " + address;

        try {
            DatabaseManager.addResident(name, sex, fullAddress, purok, "", birthdate, civilStatus, pwd);
            
            JOptionPane.showMessageDialog(this,
                "Resident has been successfully registered!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            if (homePanel != null) {
                homePanel.refreshStatistics();
            }

            dispose();
        } catch (Exception ex) {
            showError("Error adding resident: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
    }

    private void populateDays(int max) {
        dayCombo.removeAllItems();
        for (int i = 1; i <= max; i++) dayCombo.addItem(i);
    }

    private void updateDaysInMonth() {
        int monthIndex = monthCombo.getSelectedIndex();
        if (monthIndex == 0) {
            populateDays(31);
            return;
        }

        int daysInMonth;
        if (monthIndex == 2) { // February
            Integer year = (Integer) yearCombo.getSelectedItem();
            if (year != null && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
                daysInMonth = 29;
            } else {
                daysInMonth = 28;
            }
        } else if (monthIndex == 4 || monthIndex == 6 || monthIndex == 9 || monthIndex == 11) {
            daysInMonth = 30;
        } else {
            daysInMonth = 31;
        }

        populateDays(daysInMonth);
    }
}