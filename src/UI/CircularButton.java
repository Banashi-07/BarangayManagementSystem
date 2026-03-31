package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CircularButton extends JButton {

    private Color borderColor = new Color(0, 255, 127);
    private int diameter = 100;

    public CircularButton(ImageIcon icon) {
        this(icon, 80);
    }

    public CircularButton(ImageIcon icon, int diameter) {
        super(icon);
        this.diameter = diameter;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diameter, diameter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(2, 2, diameter - 4, diameter - 4);

        // Draw icon scaled to fit inside the circle
        Icon icon = getIcon();
        if (icon != null) {
            int padding = 15; // smaller padding for better fit
            int size = diameter - padding * 2;
            g2.drawImage(((ImageIcon) icon).getImage(), padding , padding -6, size, size, this);
        }

        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Ellipse2D circle = new Ellipse2D.Float(0, 0, diameter, diameter);
        return circle.contains(x, y);
    }

    public void setBorderColor(Color color) {
        borderColor = color;
        repaint();
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
        revalidate();
        repaint();
    }
}