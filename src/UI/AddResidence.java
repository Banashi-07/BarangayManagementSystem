package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AddResidence extends JDialog {

    private JTextField textField;     // Name
    private JTextField textField_1;   // Age
    private JTextField textField_2;   // Sex
    private JTextField textField_3;   // Address
    private JTextField textField_4;   // Purok
    private JTextField textField_5;   // Status
    private JTextField textField_6;   // House No.

    public AddResidence(JFrame parent) {
        super(parent, "Add Residence", true); // modal dialog

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
            // SIMPLE VALIDATION
            if (textField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!");
                return;
            }

            // 👉 You can connect this to database later

            JOptionPane.showMessageDialog(this, "Residence Added Successfully!");

            dispose(); // CLOSE POPUP
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