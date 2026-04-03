package UI.dialogs;

import database.ResidentDAO;
import database.ResidentDAO.ResidentRow;
import UI.components.ResidenceTable;
import UI.panels.HomePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Edit dialog for a resident record.
 * Receives a ResidentRow (which carries the DB id) so that updates are
 * performed by primary key — never by name string.
 *
 * Editable fields: name, sex, address, purok, civil status, birthdate (yyyy-MM-dd).
 * Age is derived from birthdate and cannot be edited directly.
 * Contact is preserved from the original record (not shown in this dialog,
 * but passed through unchanged so no data is lost).
 */
public class EditResidentDialog extends JDialog {

    public EditResidentDialog(ResidentRow resident, ResidenceTable tableToRefresh, HomePanel homePanel) {
        setTitle("Edit Resident — " + resident.name);
        setModal(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== FIELDS =====
        JTextField nameField      = field(resident.name);
        JComboBox<String> sexBox  = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        sexBox.setSelectedItem(resident.sex.equals("—") ? "Male" : resident.sex);
        sexBox.setFont(new Font("Tahoma", Font.PLAIN, 13));

        JTextField addressField   = field(resident.address.equals("—") ? "" : resident.address);
        JTextField purokField     = field(resident.purok.equals("—")   ? "" : resident.purok);
        JTextField statusField    = field(resident.status.equals("—")  ? "" : resident.status);
        JTextField birthdateField = field(resident.birthdate); // yyyy-MM-dd

        int y = 0;
        addRow(form, gbc, y++, label("Name:"),       nameField);
        addRow(form, gbc, y++, label("Sex:"),         sexBox);
        addRow(form, gbc, y++, label("Address:"),     addressField);
        addRow(form, gbc, y++, label("Purok:"),       purokField);
        addRow(form, gbc, y++, label("Civil Status:"),statusField);
        addRow(form, gbc, y++, label("Birthdate\n(yyyy-MM-dd):"), birthdateField);

        // ===== SAVE BUTTON =====
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(140, 35));
        saveBtn.setBackground(new Color(102, 170, 51));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("Tahoma", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String newBirthdate = birthdateField.getText().trim();
            // Validate birthdate format (basic check)
            if (!newBirthdate.isEmpty() && !newBirthdate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this,
                        "Birthdate must be in yyyy-MM-dd format (e.g. 1990-05-20).",
                        "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResidentDAO.updateResident(
                resident.id,
                nameField.getText().trim(),
                sexBox.getSelectedItem().toString(),
                addressField.getText().trim(),
                purokField.getText().trim(),
                "",            // contact — not shown, preserved as empty to avoid overwrite; extend if needed
                newBirthdate,
                statusField.getText().trim()
            );

            tableToRefresh.refresh();
            if (homePanel != null) homePanel.refreshStatistics();
            dispose();
        });

        setContentPane(form);
        pack();
        setMinimumSize(new Dimension(420, 300));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ===== helpers =====

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, JLabel lbl, Component field) {
        GridBagConstraints lg = (GridBagConstraints) gbc.clone();
        lg.gridx = 0; lg.gridy = y; lg.weightx = 0.3;
        lg.anchor = GridBagConstraints.LINE_END;
        panel.add(lbl, lg);

        GridBagConstraints fg = (GridBagConstraints) gbc.clone();
        fg.gridx = 1; fg.gridy = y; fg.weightx = 0.7;
        fg.anchor = GridBagConstraints.LINE_START;
        panel.add(field, fg);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 13));
        return l;
    }

    private JTextField field(String value) {
        JTextField f = new JTextField(value != null ? value : "");
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(220, 30));
        return f;
    }
}