package UI.panels;

import javax.swing.*;
import java.awt.*;

public class GradientTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gradient Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);
            frame.setLayout(new BorderLayout());

            // Create GradientPanel
            GradientPanel gradientPanel = new GradientPanel();
            gradientPanel.setPreferredSize(new Dimension(800, 200));
            gradientPanel.setLayout(null); // use absolute positioning for testing

            // Add a test label to check visibility
            JLabel lblTest = new JLabel("Gradient Panel Works!");
            lblTest.setForeground(Color.WHITE);
            lblTest.setFont(new Font("Arial", Font.BOLD, 24));
            lblTest.setBounds(50, 50, 400, 50);
            gradientPanel.add(lblTest);

            frame.add(gradientPanel, BorderLayout.NORTH);

            frame.setVisible(true);
        });
    }
}