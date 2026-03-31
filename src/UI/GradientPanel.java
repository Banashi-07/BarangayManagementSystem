package UI;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    public GradientPanel() {
        setOpaque(true); // MUST be true for the gradient to show
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create(); // create a copy to avoid side effects

        // Gradient from black (top) to green (bottom)
        GradientPaint gp = new GradientPaint(
                0, 0, Color.black,
                0, getHeight(), new Color(0, 200, 0)
        );

        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose(); // clean up
    }
    
    
}