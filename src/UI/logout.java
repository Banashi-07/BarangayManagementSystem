package UI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class logout extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					logout frame = new logout();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public logout() {
		setTitle("BARANGAYMANAGEMENTSYSTEM");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 525, 249);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		GradientPanel panel = new GradientPanel ();
		panel.setBounds(0, 0, 511, 218);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("ARE YOU SURE YOU WANT TO LOGOUT?");
		lblNewLabel.setForeground(new Color(60, 179, 113));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(99, 56, 321, 20);
		panel.add(lblNewLabel);
		
		JButton YESBTN = new JButton("YES");
		YESBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(ABORT);
			}
		});
		YESBTN.setFont(new Font("Tahoma", Font.BOLD, 15));
		YESBTN.setBounds(99, 116, 125, 40);

		// Remove default styles
		YESBTN.setBorderPainted(false);
		YESBTN.setFocusPainted(false);
		YESBTN.setFocusable(false);
		YESBTN.setContentAreaFilled(true); // IMPORTANT for background color
		YESBTN.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Set default color
		YESBTN.setBackground(new Color(0, 128, 0)); // green
		YESBTN.setForeground(Color.WHITE);

		// Hover effect
		YESBTN.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		        YESBTN.setBackground(new Color(0, 200, 0)); // lighter green
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        YESBTN.setBackground(new Color(0, 153, 0)); // back to normal
		    }
		});

		panel.add(YESBTN);
		
		JButton NOBTN = new JButton("NO");
		NOBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
		NOBTN.setFont(new Font("Tahoma", Font.BOLD, 15));
		NOBTN.setBounds(295, 116, 125, 40);

		// Remove default styles
		NOBTN.setBorderPainted(false);
		NOBTN.setFocusPainted(false);
		NOBTN.setFocusable(false);
		NOBTN.setContentAreaFilled(true);
		NOBTN.setOpaque(true);

		// Default color (red)
		NOBTN.setBackground(new Color(204, 0, 0));
		NOBTN.setForeground(Color.WHITE);

		// Cursor (optional but nice)
		NOBTN.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Hover effect
		NOBTN.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		        NOBTN.setBackground(new Color(255, 51, 51)); 
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        NOBTN.setBackground(new Color(204, 0, 0)); 
		    }
		});

		panel.add(NOBTN);

	}
}