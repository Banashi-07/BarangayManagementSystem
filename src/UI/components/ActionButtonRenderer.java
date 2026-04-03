package UI.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renders the ACTION column as a panel with View / Edit / Delete buttons.
 * No real logic here — just paints the buttons.
 */
public class ActionButtonRenderer implements TableCellRenderer {

    private final JPanel  panel  = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
    private final JButton btnView   = makeButton("View",   new Color(70, 130, 180));
    private final JButton btnEdit   = makeButton("Edit",   new Color(102, 170, 51));
    private final JButton btnDelete = makeButton("Delete", new Color(200, 60, 60));

    public ActionButtonRenderer() {
        panel.setOpaque(true);
        panel.add(btnView);
        panel.add(btnEdit);
        panel.add(btnDelete);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (isSelected) {
            panel.setBackground(new Color(102, 170, 51));
        } else {
            panel.setBackground(row % 2 == 0
                    ? new Color(220, 220, 220)
                    : new Color(235, 235, 235));
        }
        return panel;
    }

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