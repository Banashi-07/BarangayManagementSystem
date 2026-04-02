package UI.components;

import UI.dialogs.DeleteResidentDialog;
import UI.dialogs.EditResidentDialog;
import UI.dialogs.ViewResidentDialog;
import UI.panels.HomePanel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private JPanel panel;
    private JButton viewBtn, editBtn, deleteBtn;
    private JTable table;
    private ResidenceTable residenceTable;
    private HomePanel homePanel;
    private int row;

    public ActionButtonEditor(JCheckBox checkBox, ResidenceTable residenceTable, HomePanel homePanel) {
        this.residenceTable = residenceTable;
        this.homePanel = homePanel;

        panel = new JPanel(new GridLayout(1, 3, 5, 0));
        panel.setOpaque(true);

        viewBtn = createButton("View");
        editBtn = createButton("Edit");
        deleteBtn = createButton("Delete");

        panel.add(viewBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        // Button actions
        viewBtn.addActionListener(e -> {
            fireEditingStopped();
            String name = table.getValueAt(row, 0).toString();
            new ViewResidentDialog(name);
        });

        editBtn.addActionListener(e -> {
            fireEditingStopped();
            String name = table.getValueAt(row, 0).toString();
            new EditResidentDialog(name, residenceTable);
        });

        deleteBtn.addActionListener(e -> {
            fireEditingStopped();
            String name = table.getValueAt(row, 0).toString();
            new DeleteResidentDialog(name, residenceTable, homePanel);
        });
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setForeground(new Color(50, 50, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(200, 200, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // background will be reset in getTableCellEditorComponent
            }
        });

        return btn;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;

        // Match panel and buttons to row or selection
        if (table.getSelectionModel().isSelectedIndex(row)) {
            panel.setBackground(new Color(102, 170, 51));
        } else if (row % 2 == 0) {
            panel.setBackground(new Color(220, 220, 220));
        } else {
            panel.setBackground(new Color(235, 235, 235));
        }

        viewBtn.setBackground(panel.getBackground());
        editBtn.setBackground(panel.getBackground());
        deleteBtn.setBackground(panel.getBackground());

        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}