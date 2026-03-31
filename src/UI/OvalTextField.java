package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// ================= OVAL TEXTFIELD CLASS =================
public class OvalTextField extends JTextField {

    private int arcWidth = 30;  // Horizontal roundness
    private int arcHeight = 30; // Vertical roundness

    public OvalTextField() {
        super();
        setOpaque(false); // Let paintComponent handle background
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding inside textfield
        setFont(new Font("Tahoma", Font.PLAIN, 14));
    }

    public OvalTextField(int columns) {
        this();
        setColumns(columns);
    }

    public OvalTextField(String text) {
        this();
        setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        // Draw textfield border
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arcWidth, arcHeight);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        repaint();
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.height = 35; // default height
        return dim;
    }
}