package UI.dialogs;

import database.ResidentDAO;
import database.ResidentDAO.ResidentRow;
import UI.components.ResidenceTable;
import UI.panels.HomePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Modern Edit Resident Dialog
 * - Professional fixed-size layout without scrollbars
 * - Properly centered and sized text fields
 * - Clean, balanced design
 * - Updates by primary key (ID) - never by name
 * - Includes PWD (Person with Disability) field
 */
public class EditResidentDialog extends JDialog {

    private JTextField nameField;
    private JComboBox<String> sexCombo;
    private JTextField addressField;
    private JTextField purokField;
    private JTextField statusField;
    private JComboBox<String> pwdCombo;  // NEW: PWD field
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JComboBox<Integer> yearCombo;

    // Modern color palette
    private static final Color PRIMARY_GREEN = new Color(76, 175, 80);
    private static final Color PRIMARY_GREEN_HOVER = new Color(67, 160, 71);
    private static final Color PRIMARY_GREEN_DARK = new Color(56, 142, 60);
    private static final Color BACKGROUND_LIGHT = new Color(250, 251, 252);
    private static final Color CARD_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(97, 97, 97);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);
    private static final Color BORDER_FOCUS = new Color(76, 175, 80);
    private static final Color SECTION_DIVIDER = new Color(76, 175, 80, 40);

    public EditResidentDialog(ResidentRow resident, ResidenceTable tableToRefresh, HomePanel homePanel) {
        setTitle("Edit Resident Information");
        setModal(true);
        setSize(550, 680);  // Slightly taller for PWD field
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main container with modern background
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_LIGHT);
        setContentPane(container);

        // MODERN HEADER
        JPanel headerPanel = createHeaderPanel();
        container.add(headerPanel, BorderLayout.NORTH);

        // CONTENT PANEL - Fixed size, no scrollbar needed
        JPanel contentPanel = createContentPanel(resident);
        container.add(contentPanel, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel buttonPanel = createButtonPanel(resident.id, tableToRefresh, homePanel);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ========== PANEL BUILDERS ==========

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_GREEN);
        headerPanel.setPreferredSize(new Dimension(550, 90));
        
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_GREEN_DARK));

        JLabel titleLabel = new JLabel("Edit Resident", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Update resident information in the barangay system", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel headerContent = new JPanel(new GridLayout(2, 1, 0, 5));
        headerContent.setOpaque(false);
        headerContent.setBorder(new EmptyBorder(20, 30, 20, 30));
        headerContent.add(titleLabel);
        headerContent.add(subtitleLabel);

        headerPanel.add(headerContent, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createContentPanel(ResidentRow resident) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_LIGHT);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // White card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(25, 30, 25, 30)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(470, Integer.MAX_VALUE));

        // === PERSONAL INFORMATION ===
        addSectionHeader(card, "Personal Information");
        card.add(Box.createVerticalStrut(15));

        // Name
        addFieldRow(card, "Full Name", true, 
            nameField = createStyledTextField(resident.name));
        card.add(Box.createVerticalStrut(12));

        // Sex
        addComboRow(card, "Sex", true,
            sexCombo = createStyledComboBox(new String[]{"Male", "Female"}));
        sexCombo.setSelectedItem(resident.sex.equals("—") ? "Male" : resident.sex);
        card.add(Box.createVerticalStrut(12));

        // Birthdate
        addDateRow(card, "Date of Birth", true, resident.birthdate);
        card.add(Box.createVerticalStrut(20));

        // === ADDRESS INFORMATION ===
        addSectionHeader(card, "Address Information");
        card.add(Box.createVerticalStrut(15));

        // Address
        addFieldRow(card, "Street Address", true,
            addressField = createStyledTextField(resident.address.equals("—") ? "" : resident.address));
        card.add(Box.createVerticalStrut(12));

        // Purok
        addFieldRow(card, "Purok/Zone", false,
            purokField = createStyledTextField(resident.purok.equals("—") ? "" : resident.purok));
        card.add(Box.createVerticalStrut(20));

        // === ADDITIONAL DETAILS ===
        addSectionHeader(card, "Additional Details");
        card.add(Box.createVerticalStrut(15));

        // Civil Status
        addFieldRow(card, "Civil Status", true,
            statusField = createStyledTextField(resident.status.equals("—") ? "" : resident.status));
        card.add(Box.createVerticalStrut(12));

        // PWD Status - NEW FIELD
        addComboRow(card, "PWD Status", false,
            pwdCombo = createStyledComboBox(new String[]{"No", "Yes"}));
        // Set current PWD status
        String currentPwd = (resident.pwd == null || resident.pwd.equals("—")) ? "No" : resident.pwd;
        pwdCombo.setSelectedItem(currentPwd.equalsIgnoreCase("Yes") ? "Yes" : "No");
        
        card.add(Box.createVerticalStrut(10));
        
        mainPanel.add(card);
        return mainPanel;
    }

    private void addSectionHeader(JPanel parent, String text) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel section = new JLabel(text);
        section.setFont(new Font("Segoe UI", Font.BOLD, 16));
        section.setForeground(PRIMARY_GREEN_DARK);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(SECTION_DIVIDER);
        
        headerPanel.add(section, BorderLayout.WEST);
        headerPanel.add(separator, BorderLayout.SOUTH);
        headerPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        parent.add(headerPanel);
    }

    private void addFieldRow(JPanel parent, String labelText, boolean required, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = createFieldLabel(labelText, required);
        label.setPreferredSize(new Dimension(110, 35));
        
        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        
        parent.add(row);
    }
    
    private void addComboRow(JPanel parent, String labelText, boolean required, JComboBox<String> combo) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = createFieldLabel(labelText, required);
        label.setPreferredSize(new Dimension(110, 35));
        
        row.add(label, BorderLayout.WEST);
        row.add(combo, BorderLayout.CENTER);
        
        parent.add(row);
    }
    
    private void addDateRow(JPanel parent, String labelText, boolean required, String birthdate) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = createFieldLabel(labelText, required);
        label.setPreferredSize(new Dimension(110, 35));
        
        JPanel datePanel = createDatePicker(birthdate);
        
        row.add(label, BorderLayout.WEST);
        row.add(datePanel, BorderLayout.CENTER);
        
        parent.add(row);
    }
    
    private JLabel createFieldLabel(String text, boolean required) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        
        if (required) {
            label.setText("<html>" + text + " <span style='color:#dc3545;'>*</span></html>");
        } else {
            label.setText(text);
        }
        
        return label;
    }

    private JTextField createStyledTextField(String value) {
        JTextField field = new JTextField(value != null ? value : "");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_GREEN);
        field.setPreferredSize(new Dimension(280, 38));
        field.setMaximumSize(new Dimension(280, 38));
        
        // Modern border with rounded corners effect
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));

        // Focus listener for interactive borders
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 2, true),
                    new EmptyBorder(0, 12, 0, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                    new EmptyBorder(0, 12, 0, 12)
                ));
            }
        });

        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(280, 38));
        combo.setMaximumSize(new Dimension(280, 38));
        
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));
        
        // Custom renderer for better item styling
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                
                if (isSelected) {
                    setBackground(new Color(76, 175, 80, 25));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_PRIMARY);
                }
                
                return this;
            }
        });
        
        return combo;
    }

    private JPanel createDatePicker(String currentBirthdate) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 38));

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        monthCombo = new JComboBox<>(months);
        dayCombo = new JComboBox<>();
        yearCombo = new JComboBox<>();

        // Populate days (1-31)
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }
        
        // Populate years (1900 - current)
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= 1900; i--) {
            yearCombo.addItem(i);
        }

        // Parse existing birthdate
        if (currentBirthdate != null && !currentBirthdate.isBlank() && 
            currentBirthdate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                String[] parts = currentBirthdate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                yearCombo.setSelectedItem(year);
                monthCombo.setSelectedIndex(month - 1);
                dayCombo.setSelectedItem(day);
            } catch (Exception e) {
                // Use defaults
            }
        }

        // Update days when month or year changes
        monthCombo.addActionListener(e -> updateDaysInMonth());
        yearCombo.addActionListener(e -> updateDaysInMonth());

        styleDateComboBox(monthCombo, 70);
        styleDateComboBox(dayCombo, 55);
        styleDateComboBox(yearCombo, 75);

        panel.add(monthCombo);
        panel.add(dayCombo);
        panel.add(yearCombo);

        return panel;
    }

    private void styleDateComboBox(JComboBox<?> combo, int width) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setPreferredSize(new Dimension(width, 35));
        combo.setMaximumSize(new Dimension(width, 35));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));
        
        // Custom renderer
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (isSelected) {
                    setBackground(new Color(76, 175, 80, 25));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_PRIMARY);
                }
                
                return this;
            }
        });
    }

    // ========== BUTTON BUILDERS ==========

    private JPanel createButtonPanel(int residentId, ResidenceTable table, HomePanel homePanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_LIGHT);
        buttonPanel.setBorder(new EmptyBorder(0, 25, 25, 25));

        JButton saveBtn = createPrimaryButton("Save Changes");
        saveBtn.addActionListener(e -> saveChanges(residentId, table, homePanel));

        JButton cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        return buttonPanel;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBackground(PRIMARY_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_GREEN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_GREEN);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_GREEN_DARK);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_GREEN_HOVER);
            }
        });

        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
                btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));
            }
        });

        return btn;
    }

    // ========== HELPER METHODS ==========

    private void updateDaysInMonth() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        
        int daysInMonth;
        if (month == 2) {
            boolean isLeapYear = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
            daysInMonth = isLeapYear ? 29 : 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        } else {
            daysInMonth = 31;
        }
        
        int currentDay = (Integer) dayCombo.getSelectedItem();
        dayCombo.removeAllItems();
        
        for (int i = 1; i <= daysInMonth; i++) {
            dayCombo.addItem(i);
        }
        
        if (currentDay <= daysInMonth) {
            dayCombo.setSelectedItem(currentDay);
        } else {
            dayCombo.setSelectedItem(daysInMonth);
        }
    }

    // ========== BUSINESS LOGIC ==========

    private void saveChanges(int id, ResidenceTable table, HomePanel homePanel) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter the resident's full name.");
            nameField.requestFocus();
            return;
        }

        if (monthCombo.getSelectedIndex() == -1) {
            showError("Please select a birth date.");
            return;
        }

        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            showError("Please enter the street address.");
            addressField.requestFocus();
            return;
        }

        String sex = sexCombo.getSelectedItem().toString();
        String purok = purokField.getText().trim();
        String status = statusField.getText().trim();
        String pwd = pwdCombo.getSelectedItem().toString();  // Get PWD status

        // Build birthdate
        int month = monthCombo.getSelectedIndex() + 1;
        int day = (Integer) dayCombo.getSelectedItem();
        int year = (Integer) yearCombo.getSelectedItem();
        String birthdate = String.format("%04d-%02d-%02d", year, month, day);

        try {
            // Use the method that includes PWD
            ResidentDAO.updateResidentWithPwd(id, name, sex, address, purok, "", birthdate, status, pwd);

            table.refresh();
            if (homePanel != null) homePanel.refreshStatistics();

            showSuccess("Resident information updated successfully!");
            dispose();
        } catch (Exception e) {
            showError("Failed to update resident: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
}