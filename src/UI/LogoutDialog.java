package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogoutDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public LogoutDialog(Window parent) { // Window instead of JFrame
        super(parent, "Confirm Logout", ModalityType.APPLICATION_MODAL); // modal dialog

        setBounds(100, 100, 525, 249);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 511, 218);
        panel.setBackground(Color.white);
        panel.setLayout(null);
        getContentPane().add(panel);

        // Label
        JLabel lblMessage = new JLabel("ARE YOU SURE YOU WANT TO LOGOUT?");
        lblMessage.setForeground(new Color(60, 179, 113));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblMessage.setBounds(99, 56, 321, 20);
        panel.add(lblMessage);

        // YES button
        JButton btnYes = new JButton("YES");
        btnYes.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnYes.setBounds(99, 116, 125, 40);
        btnYes.setBorderPainted(false);
        btnYes.setFocusPainted(false);
        btnYes.setFocusable(false);
        btnYes.setContentAreaFilled(true);
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYes.setBackground(new Color(0, 128, 0));
        btnYes.setForeground(Color.WHITE);

        btnYes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnYes.setBackground(new Color(0, 200, 0)); }
            public void mouseExited(MouseEvent evt) { btnYes.setBackground(new Color(0, 128, 0)); }
        });

        // Close parent window on YES
        btnYes.addActionListener(e -> {
            parent.dispose(); // close main app frame or parent window
            dispose();        // close dialog
        });

        panel.add(btnYes);

        // NO button
        JButton btnNo = new JButton("NO");
        btnNo.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnNo.setBounds(295, 116, 125, 40);
        btnNo.setBorderPainted(false);
        btnNo.setFocusPainted(false);
        btnNo.setFocusable(false);
        btnNo.setContentAreaFilled(true);
        btnNo.setOpaque(true);
        btnNo.setBackground(new Color(204, 0, 0));
        btnNo.setForeground(Color.WHITE);
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnNo.setBackground(new Color(255, 51, 51)); }
            public void mouseExited(MouseEvent evt) { btnNo.setBackground(new Color(204, 0, 0)); }
        });

        btnNo.addActionListener(e -> dispose()); // just close dialog

        panel.add(btnNo);
    }
}