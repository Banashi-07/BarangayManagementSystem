package UI;
import javax.swing.*;
import java.awt.*;

public class OfficialListPanel extends JPanel {
    public OfficialListPanel() {
     
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1001));

     // ===== Gradient Header Panel =====
        GradientPanel headPanel = new GradientPanel();
        headPanel.setBounds(0, 0, 1280, 90);
        headPanel.setLayout(null);
        add(headPanel); // add to your main panel
        
        JLabel lblSubtitle = new JLabel("OFFICIAL LIST", SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblSubtitle.setBounds(10, 37, 1280, 30);
        lblSubtitle.setOpaque(false);
        headPanel.add(lblSubtitle);
        
        JPanel panel = new JPanel();
        panel.setBounds(320, 90, 960, 738);
        panel.setLayout(null);
        add(panel);
        
        OvalPanel CaptainPanel = new OvalPanel();
        CaptainPanel.setLayout(null);
        CaptainPanel.setBackground(new Color(255, 255, 255));
        CaptainPanel.setBounds(86, 29, 793, 169); // ADD THIS
        panel.add(CaptainPanel);
        
        OvalPanel GreenPanel = new OvalPanel();
        GreenPanel.setLayout(null);
        GreenPanel.setBackground(new Color(128, 255, 128));
        GreenPanel.setBounds(0, 0, 793, 46);
        CaptainPanel.add(GreenPanel);
        
        JLabel lblNewLabel = new JLabel("BARANGAY CAPTAIN");
        lblNewLabel.setForeground(new Color(0, 0, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel.setBounds(64, 10, 179, 26);
        GreenPanel.add(lblNewLabel);
        
        
        // ===== Logo =====
        JLabel cptPic = new JLabel(); // no text
        cptPic.setFont(new Font("Tahoma", Font.BOLD, 15));
        cptPic.setText("BARANGAY CAPTAIN NAME");
        
        ImageIcon cpticon = new ImageIcon(getClass().getResource("/img/pfp.png"));
        // Scale the image to fit your desired size
        Image cptimg = cpticon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(cptimg);

        cptPic.setIcon(scaledIcon);
        cptPic.setBounds(30, 70, 350, 75); // position & size
        cptPic.setOpaque(false); // important to show gradient behind
        CaptainPanel.add(cptPic);
        
        
        
        
        
        
        
        
        
        OvalPanel CaptainPanel_1 = new OvalPanel();
        CaptainPanel_1.setLayout(null);
        CaptainPanel_1.setBackground(Color.WHITE);
        CaptainPanel_1.setBounds(86, 232, 793, 169);
        panel.add(CaptainPanel_1);
        
        OvalPanel GreenPanel_1 = new OvalPanel();
        GreenPanel_1.setLayout(null);
        GreenPanel_1.setBackground(new Color(128, 255, 128));
        GreenPanel_1.setBounds(0, 0, 793, 46);
        CaptainPanel_1.add(GreenPanel_1);
        
        JLabel cptPic_1 = new JLabel();
        cptPic_1.setText("BARANGAY CAPTAIN NAME");
        cptPic_1.setOpaque(false);
        cptPic_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        cptPic_1.setBounds(30, 70, 350, 75);
        cptPic_1.setIcon(scaledIcon);
        CaptainPanel_1.add(cptPic_1);
        
        
        
        
        
        
        
        
        
        OvalPanel CaptainPanel_1_1 = new OvalPanel();
        CaptainPanel_1_1.setLayout(null);
        CaptainPanel_1_1.setBackground(Color.WHITE);
        CaptainPanel_1_1.setBounds(86, 454, 793, 169);
        panel.add(CaptainPanel_1_1);
        
        OvalPanel GreenPanel_1_1 = new OvalPanel();
        GreenPanel_1_1.setLayout(null);
        GreenPanel_1_1.setBackground(new Color(128, 255, 128));
        GreenPanel_1_1.setBounds(0, 0, 793, 46);
        CaptainPanel_1_1.add(GreenPanel_1_1);
        
        OvalPanel GreenPanel_2 = new OvalPanel();
        GreenPanel_2.setLayout(null);
        GreenPanel_2.setBackground(new Color(128, 255, 128));
        GreenPanel_2.setBounds(31, 117, 243, 52);
        add(GreenPanel_2);
        
        OvalPanel GreenSIdePanel = new OvalPanel();
        GreenSIdePanel.setLayout(null);
        GreenSIdePanel.setBackground(new Color(0, 128, 64));
        GreenSIdePanel.setBounds(31, 254, 243, 364);
        add(GreenSIdePanel);
        
        OvalPanel WhitePanel1 = new OvalPanel();
        WhitePanel1.setLayout(null);
        WhitePanel1.setBackground(new Color(255, 255, 255));
        WhitePanel1.setBounds(20, 44, 202, 52);
        GreenSIdePanel.add(WhitePanel1);
        
        OvalPanel WhitePanel2 = new OvalPanel();
        WhitePanel2.setLayout(null);
        WhitePanel2.setBackground(Color.WHITE);
        WhitePanel2.setBounds(20, 120, 202, 52);
        GreenSIdePanel.add(WhitePanel2);
        
        OvalPanel WhitePanel3 = new OvalPanel();
        WhitePanel3.setLayout(null);
        WhitePanel3.setBackground(Color.WHITE);
        WhitePanel3.setBounds(20, 196, 202, 52);
        GreenSIdePanel.add(WhitePanel3);
        
        OvalPanel WhitePanel4 = new OvalPanel();
        WhitePanel4.setLayout(null);
        WhitePanel4.setBackground(Color.WHITE);
        WhitePanel4.setBounds(20, 278, 202, 52);
        GreenSIdePanel.add(WhitePanel4);
    }
}