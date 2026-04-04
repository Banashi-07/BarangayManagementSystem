package UI.components;

import database.ResidentDAO;
import UI.panels.HomePanel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ResidenceTable
 * - Column 0‥5 : display data (NAME, AGE, SEX, ADDRESS, PUROK, STATUS)
 * - Column 6   : ACTION buttons (renderer + editor)
 * - Column 7   : hidden integer ID (used by ActionButtonEditor)
 * 
 * AGE is now computed from birthdate via ResidentRow.getAge()
 */
public class ResidenceTable extends JTable {

    private final DefaultTableModel model;

    /** Parallel list that maps table row index → resident DB id */
    private final List<Integer> rowIds = new ArrayList<>();

    public ResidenceTable(HomePanel homePanel) {

        // ===== MODEL (column 7 = hidden ID) =====
        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"NAME", "AGE", "SEX", "ADDRESS", "PUROK", "STATUS", "ACTION", "ID"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // only ACTION column
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class; // AGE column
                if (columnIndex == 7) return Integer.class; // ID column
                return Object.class;
            }
        };
        setModel(model);

        // ===== HIDE ID COLUMN =====
        getColumnModel().getColumn(7).setMinWidth(0);
        getColumnModel().getColumn(7).setMaxWidth(0);
        getColumnModel().getColumn(7).setWidth(0);
        getColumnModel().getColumn(7).setPreferredWidth(0);

        // ===== TABLE STYLE =====
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

        // ===== COLUMN WIDTHS =====
        getColumnModel().getColumn(0).setPreferredWidth(150); // NAME
        getColumnModel().getColumn(1).setPreferredWidth(60);  // AGE
        getColumnModel().getColumn(2).setPreferredWidth(70);  // SEX
        getColumnModel().getColumn(3).setPreferredWidth(200); // ADDRESS
        getColumnModel().getColumn(4).setPreferredWidth(90);  // PUROK
        getColumnModel().getColumn(5).setPreferredWidth(110); // STATUS
        getColumnModel().getColumn(6).setPreferredWidth(220); // ACTION

        // ===== ACTION COLUMN =====
        getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor(new JCheckBox(), this, homePanel));

        // ===== HEADER =====
        JTableHeader header = getTableHeader();
        header.setBackground(new Color(102, 170, 51));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 40));

        // ===== ROW RENDERER =====
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

        refresh();
    }

    /**
     * Reload all rows from the database.
     * Call after any add / edit / delete operation.
     */
    public void refresh() {
        loadRows(ResidentDAO.getAllResidentRows());
    }

    /**
     * Filter rows by keyword (searches name, address, purok).
     * Pass null or blank to show all.
     */
    public void search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            refresh();
        } else {
            loadRows(ResidentDAO.searchResidentRows(keyword));
        }
    }

    /** Returns the DB primary key for the given view row. */
    public int getResidentIdAt(int viewRow) {
        if (viewRow < 0 || viewRow >= rowIds.size()) return -1;
        return rowIds.get(viewRow);
    }

    // ===== private helpers =====

    private void loadRows(List<ResidentDAO.ResidentRow> rows) {
        model.setRowCount(0);
        rowIds.clear();

        for (ResidentDAO.ResidentRow r : rows) {
            // Age is computed dynamically from birthdate via getAge()
            model.addRow(new Object[]{
                r.name, 
                r.getAge(),  // ← Computed from birthdate
                r.sex, 
                r.address, 
                r.purok, 
                r.status, 
                "ACTION", 
                r.id
            });
            rowIds.add(r.id);
        }
    }
}