package UI.panels;
import UI.components.ResidenceTable;
import UI.components.OvalButton;
import UI.components.OvalPanel;
import UI.dialogs.AddResidence;

import javax.swing.*;
import java.awt.*;

public class RecordsPanel extends JPanel {
    private JTextField textField_4;

	public RecordsPanel() {
     
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1001));

     // ===== Gradient Header Panel =====
        GradientPanel headPanel = new GradientPanel();
        headPanel.setBounds(0, 0, 1280, 90);
        add(headPanel); // add to your main panel
        headPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblSubtitle = new JLabel("RECORDS LIST", SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblSubtitle.setOpaque(false);
        headPanel.add(lblSubtitle,BorderLayout.CENTER);
        
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
        
        
        JLabel lblNewLabel_1 = new JLabel("SEARCH RESIDEDENCE:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel_1.setForeground(new Color(0, 128, 0));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setBounds(264, 142, 181, 20);
        add(lblNewLabel_1);

        textField_4 = new JTextField();
        textField_4.setBounds(455, 137, 286, 26);
        add(textField_4);

        OvalButton btnAddResidence = new OvalButton("ADD RESIDENCE");
        btnAddResidence.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnAddResidence.setBounds(400, 190, 181, 41);
        add(btnAddResidence);

        
        
        btnAddResidence.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddResidence dialog = new AddResidence(parentFrame);
            dialog.setVisible(true);
        });
        
        
        
        OvalButton btnPrintResidence = new OvalButton("PRINT RESIDENCY");
        btnPrintResidence.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnPrintResidence.setBounds(600, 190, 173, 41);
       add(btnPrintResidence);

        OvalButton btnPrintClearance = new OvalButton("PRINT CLEARANCE");
        btnPrintClearance.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnPrintClearance.setBounds(800, 190, 181, 41);
        add(btnPrintClearance);

        OvalButton btnPrintIndigency = new OvalButton("PRINT INDIGENCY");
        btnPrintIndigency.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnPrintIndigency.setBounds(1000, 190, 175, 41);
        add(btnPrintIndigency);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBounds(341, 242, 902, 532);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 170, 51), 2));

        add(tablePanel);
                                                        
     ResidenceTable table = new ResidenceTable();
     tablePanel.add(table, BorderLayout.CENTER);
     table.setShowHorizontalLines(true);
     tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
    }

  
    
}