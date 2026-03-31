package UI;

import javax.swing.*;
import java.awt.*;

public class OvalButton extends JButton {

    private Color normalColor = new Color(0, 150, 0);
    private Color hoverColor = new Color(0, 120, 0);
    private Color pressColor = new Color(0, 90, 0);

    private boolean isHovered = false;
    private boolean isPressed = false;

    public OvalButton(String text) {
        super(text);

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Tahoma", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // 🔥 hand cursor

        // ✅ Mouse effects
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 🎨 Choose color based on state
        if (isPressed) {
            g2.setColor(pressColor);
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        // Draw oval button
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        g2.dispose();

        super.paintComponent(g); // draw text
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(isHovered ? 3 : 2)); // 🔥 thicker on hover
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                getHeight(), getHeight());

        g2.dispose();
    }
}