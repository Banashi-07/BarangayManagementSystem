package UI.dialogs;

import database.ResidentDAO.ResidentRow;

import javax.swing.*;
import java.awt.*;

/**
 * Read-only view of a resident record.
 * Accepts a ResidentRow so the caller never has to pass a name string.
 */
public class ViewResidentDialog extends JDialog {

    public ViewResidentDialog(ResidentRow r) {
        setTitle("View Resident — " + r.name);
        setModal(true);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        addRow(content, gbc, y++, "Name:",      r.name);
        addRow(content, gbc, y++, "Age:",        String.valueOf(r.age));
        addRow(content, gbc, y++, "Sex:",        r.sex);
        addRow(content, gbc, y++, "Address:",    r.address);
        addRow(content, gbc, y++, "Purok:",      r.purok);
        addRow(content, gbc, y++, "Civil Status:", r.status);
        addRow(content, gbc, y++, "Birthdate:", r.birthdate.isBlank() ? "—" : r.birthdate);

        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(120, 35));
        closeBtn.addActionListener(e -> dispose());

        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        content.add(closeBtn, gbc);

        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));

        GridBagConstraints lg = (GridBagConstraints) gbc.clone();
        lg.gridx = 0; lg.gridy = y; lg.weightx = 0.35;
        lg.anchor = GridBagConstraints.LINE_END;
        panel.add(label, lg);

        JLabel value = new JLabel(valueText != null ? valueText : "—");
        value.setFont(new Font("Tahoma", Font.PLAIN, 14));

        GridBagConstraints vg = (GridBagConstraints) gbc.clone();
        vg.gridx = 1; vg.gridy = y; vg.weightx = 0.65;
        vg.anchor = GridBagConstraints.LINE_START;
        panel.add(value, vg);
    }
}