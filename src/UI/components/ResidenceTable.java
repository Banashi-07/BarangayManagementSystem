package UI.components;

import database.ResidentDAO;

import javax.swing.*;
import javax.swing.table.*;

import UI.panels.HomePanel;

import java.awt.*;
import java.util.List;

public class ResidenceTable extends JTable {

    private DefaultTableModel model;
    
    
    public ResidenceTable(HomePanel homePanel) {

        // ================= MODEL =================
        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"NAME", "AGE", "SEX", "ADDRESS", "PUROK", "STATUS","ACTION"}
        ) {
        	@Override
        	public boolean isCellEditable(int row, int column) {
        	    return column == 6; // Only ACTION column editable
        	}
        };
        setModel(model);

        // ================= TABLE DESIGN =================
        setBackground(new Color(240, 255, 255));
        setRowHeight(40);
        setShowGrid(false);
        
        setFocusable(true);
        setRowSelectionAllowed(true);
        
        setIntercellSpacing(new Dimension(0, 8));
        setFont(new Font("Tahoma", Font.PLAIN, 12));
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);

        getColumnModel().getColumn(0).setPreferredWidth(150);
        getColumnModel().getColumn(1).setPreferredWidth(80);
        getColumnModel().getColumn(2).setPreferredWidth(80);
        getColumnModel().getColumn(3).setPreferredWidth(200);
        getColumnModel().getColumn(4).setPreferredWidth(100);
        getColumnModel().getColumn(5).setPreferredWidth(120);
        getColumnModel().getColumn(6).setPreferredWidth(250);
        
        getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor(new JCheckBox(), this, homePanel));

        // ================= HEADER =================
        JTableHeader header = getTableHeader();
        header.setBackground(new Color(102, 170, 51));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 40));

        // ================= ROW STYLE =================
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(102, 170, 51));
                    c.setForeground(Color.WHITE);
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(220, 220, 220));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(235, 235, 235));
                    c.setForeground(Color.BLACK);
                }

                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        // Load data on creation
        refresh();
    }

    /**
     * Clears and reloads the table from the database.
     * Call this after any add, edit, or delete operation.
     */
    public void refresh() {
        model.setRowCount(0);

        List<ResidentDAO.ResidentRow> rows = ResidentDAO.getAllResidentRows();
        for (ResidentDAO.ResidentRow r : rows) {
            model.addRow(new Object[]{
                    r.name, r.age, r.sex, r.address, r.purok, r.status, "ACTION"
            });
        }
    }
}