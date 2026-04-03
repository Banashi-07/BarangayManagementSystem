package UI.dialogs;

import UI.panels.GradientPanel;
import UI.panels.HomePanel;
import UI.components.OvalButton;
import service.AddResidenceService;

import javax.swing.*;
import java.awt.*;

public class AddResidence extends JDialog {

    private JTextField textField;     // Name
    private JTextField textField_1;   // Age
    private JTextField textField_2;   // Sex
    private JTextField textField_3;   // Address
    private JTextField textField_4;   // Purok
    private JTextField textField_5;   // Status
    private JTextField textField_6;   // House No.
    
    private AddResidenceService residenceService;

    public AddResidence(JFrame parent, HomePanel homePanel) {
        super(parent, "Add Residence", true); // modal dialog
        
        // Initialize the service
        residenceService = new AddResidenceService();

        setSize(500, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // ================= BACKGROUND PANEL =================
        GradientPanel panel = new GradientPanel();
        panel.setBounds(0, 0, 500, 473);
        contentPane.add(panel);
        panel.setLayout(null);
        
        try {
            ImageIcon icon = new ImageIcon(
                getClass().getResource("residencelogopng.png")
            );
            Image scaled = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.out.println("Logo image not found.");
        }

        // ================= FIELDS =================
        textField = createField(panel, 25);
        addLabel(panel, "NAME:", 35);

        textField_1 = createField(panel, 75);
        addLabel(panel, "AGE:",  85);

        textField_2 = createField(panel, 125);
        addLabel(panel, "SEX:",  135);

        textField_3 = createField(panel, 175);
        addLabel(panel, "ADDRESS:", 185);

        textField_4 = createField(panel, 225);
        addLabel(panel, "PUROK:", 235);

        textField_5 = createField(panel, 275);
        addLabel(panel, "STATUS:", 285);

        textField_6 = createField(panel, 325);
        addLabel(panel, "HOUSE #:", 335);

        // ================= BUTTON =================
        OvalButton btnAdd = new OvalButton("ADD RESIDENCE");
        btnAdd.setBounds(150, 375, 170, 50);
        btnAdd.setBackground(new Color(0, 200, 0));
        btnAdd.setForeground(Color.WHITE);
        panel.add(btnAdd);

        btnAdd.addActionListener(e -> {
            // Get values from fields
            String name = textField.getText().trim();
            String age = textField_1.getText().trim();
            String sex = textField_2.getText().trim();
            String address = textField_3.getText().trim();
            String purok = textField_4.getText().trim();
            String status = textField_5.getText().trim();
            String houseNo = textField_6.getText().trim();
            
            // Validate using service
            AddResidenceService.ValidationResult validation = 
                residenceService.validateResidence(name, age, sex, address, purok, status, houseNo);
            
            if (!validation.isValid()) {
                JOptionPane.showMessageDialog(this, 
                    "Validation Errors:\n" + validation.getErrorMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Try to add residence
            boolean success = residenceService.addResidence(name, age, sex, address, purok, status, houseNo);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Residence Added Successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose(); // CLOSE POPUP
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add residence. Please check the database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // ================= HELPER METHODS =================
    private JTextField createField(JPanel panel, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Tahoma", Font.BOLD, 15));
        field.setBounds(120, y , 308, 35);
        panel.add(field);
        return field;
    }

    private void addLabel(JPanel panel, String text,  int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(10, y, 100, 20);
        panel.add(label);
    }
}