package UI.components;

import java.awt.*;
import javax.swing.*;

public class OvalPanel extends JPanel {

    public OvalPanel() {
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Use the panel background color
        g2.setColor(getBackground());

        // Draw rounded rectangle
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);

        g2.dispose();

        super.paintComponent(g);
    }
}