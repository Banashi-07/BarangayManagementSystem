package UI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Barangay Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start fullscreen
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Create main panel
        ContentPanel content = new ContentPanel();
        MainPanel header = new MainPanel(content);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        
        
        // Wrap in scroll pane
      JScrollPane scrollPane = new JScrollPane(content);

     // Keep scrollbars visible
     scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

     // Customize vertical scrollbar color
     scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
         @Override
         protected void configureScrollBarColors() {
             this.thumbColor = new Color(150, 150, 150); // green thumb
             this.trackColor = new Color(220, 220, 220); // light gray track
         }
     });

     // Increase scroll speed
     scrollPane.getVerticalScrollBar().setUnitIncrement(30);   // mouse wheel
     scrollPane.getVerticalScrollBar().setBlockIncrement(100); // page up/down

        
        
        
	    add(header,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);
        
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}