package UI.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ActionButtonRenderer extends JPanel implements TableCellRenderer {

    private JButton viewBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    public ActionButtonRenderer() {
        setLayout(new GridLayout(1, 3, 5, 0));
        setOpaque(true);

        viewBtn = createButton("View");
        editBtn = createButton("Edit");
        deleteBtn = createButton("Delete");

        add(viewBtn);
        add(editBtn);
        add(deleteBtn);
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
        return btn;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Set panel background to match row
        if (isSelected) {
            setBackground(new Color(102, 170, 51)); // same as your row selection green
        } else if (row % 2 == 0) {
            setBackground(new Color(220, 220, 220));
        } else {
            setBackground(new Color(235, 235, 235));
        }

        // Set all buttons to match panel background
        viewBtn.setBackground(getBackground());
        editBtn.setBackground(getBackground());
        deleteBtn.setBackground(getBackground());

        return this;
    }
}