package UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ResidenceTable extends JTable {

    private DefaultTableModel model;

    public ResidenceTable() {

        // ================= MODEL =================
        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"NAME", "AGE", "SEX", "ADDRESS", "PUROK", "STATUS"}
        );
        setModel(model);

        // ================= TABLE DESIGN =================
        setBackground(new Color(240, 255, 255));
        setRowHeight(40);

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 8));
        setFont(new Font("Arial", Font.PLAIN, 14));
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Disable column reordering and resizing
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);

        // Column widths
        getColumnModel().getColumn(0).setPreferredWidth(150);
        getColumnModel().getColumn(1).setPreferredWidth(80);
        getColumnModel().getColumn(2).setPreferredWidth(80);
        getColumnModel().getColumn(3).setPreferredWidth(200);
        getColumnModel().getColumn(4).setPreferredWidth(100);
        getColumnModel().getColumn(5).setPreferredWidth(120);

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

                if (row % 2 == 0) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    c.setBackground(new Color(235, 235, 235));
                }

                setHorizontalAlignment(CENTER);
                return c;
            }
        });
    }

    // Method to get table model for adding data
    public DefaultTableModel getModel() {
        return model;
    }
}