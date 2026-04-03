package UI.components;

import UI.dialogs.DeleteResidentDialog;
import UI.dialogs.EditResidentDialog;
import UI.dialogs.ViewResidentDialog;
import UI.panels.HomePanel;
import database.ResidentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Editor for the ACTION column.
 * Intercepts clicks on View / Edit / Delete buttons and opens the appropriate dialog.
 * Uses the row's ID (stored in the model) for all operations — never the name string.
 */
public class ActionButtonEditor extends DefaultCellEditor {

    private final JPanel  panel     = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
    private final JButton btnView   = makeButton("View",   new Color(70, 130, 180));
    private final JButton btnEdit   = makeButton("Edit",   new Color(102, 170, 51));
    private final JButton btnDelete = makeButton("Delete", new Color(200, 60, 60));

    private final ResidenceTable table;
    private final HomePanel      homePanel;
    private int  currentRowId   = -1;
    private String currentName  = "";

    public ActionButtonEditor(JCheckBox checkBox, ResidenceTable table, HomePanel homePanel) {
        super(checkBox);
        this.table     = table;
        this.homePanel = homePanel;

        panel.setOpaque(true);
        panel.add(btnView);
        panel.add(btnEdit);
        panel.add(btnDelete);

        btnView.addActionListener(this::onView);
        btnEdit.addActionListener(this::onEdit);
        btnDelete.addActionListener(this::onDelete);
    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object value,
                                                  boolean isSelected, int row, int column) {
        // Resolve the resident ID from the hidden ID column (column index 7)
        currentRowId  = table.getResidentIdAt(row);
        currentName   = table.getModel().getValueAt(row, 0).toString();

        panel.setBackground(new Color(102, 170, 51));
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "ACTION";
    }

    // -------- button handlers --------

    private void onView(ActionEvent e) {
        fireEditingStopped();
        ResidentDAO.ResidentRow r = ResidentDAO.getResidentById(currentRowId);
        if (r != null) new ViewResidentDialog(r);
    }

    private void onEdit(ActionEvent e) {
        fireEditingStopped();
        ResidentDAO.ResidentRow r = ResidentDAO.getResidentById(currentRowId);
        if (r != null) {
            new EditResidentDialog(r, table, homePanel);
        }
    }

    private void onDelete(ActionEvent e) {
        fireEditingStopped();
        new DeleteResidentDialog(currentRowId, currentName, table, homePanel);
    }

    // -------- helper --------

    private static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(65, 28));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}