package UI.panels;
import UI.components.OvalPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class OfficialListPanel extends JPanel {
    
    // Components that need to be repositioned
    private GradientPanel headPanel;
    private JLabel lblSubtitle;
    private JPanel mainPanel;
    private OvalPanel[] officialPanels;
    private OvalPanel[] headerPanels;
    private JLabel[] titleLabels;
    private JLabel[] namePics;
    private OvalPanel greenSidePanel;
    private OvalPanel[] whitePanels;
    private ImageIcon scaledIcon;
    
    public OfficialListPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1001));

        // ===== Gradient Header Panel =====
        headPanel = new GradientPanel();
        headPanel.setLayout(null);
        add(headPanel);
        
        lblSubtitle = new JLabel("OFFICIAL LIST", SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblSubtitle.setOpaque(false);
        headPanel.add(lblSubtitle);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        add(mainPanel);
        
        // Create official panels array
        officialPanels = new OvalPanel[3];
        headerPanels = new OvalPanel[3];
        titleLabels = new JLabel[3];
        namePics = new JLabel[3];
        
        String[] titles = {"BARANGAY CAPTAIN", "BARANGAY KAGAWAD", "BARANGAY TREASURER"};
        
        // Load and scale profile picture icon
        try {
            ImageIcon cpticon = new ImageIcon(getClass().getResource("/img/pfp.png"));
            Image cptimg = cpticon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            scaledIcon = new ImageIcon(cptimg);
        } catch (Exception e) {
            System.err.println("Profile picture not found: " + e.getMessage());
            scaledIcon = null;
        }
        
        // Create 3 official panels
        for (int i = 0; i < 3; i++) {
            // Main container panel
            officialPanels[i] = new OvalPanel();
            officialPanels[i].setLayout(null);
            officialPanels[i].setBackground(Color.WHITE);
            mainPanel.add(officialPanels[i]);
            
            // Green header panel
            headerPanels[i] = new OvalPanel();
            headerPanels[i].setLayout(null);
            headerPanels[i].setBackground(new Color(128, 255, 128));
            officialPanels[i].add(headerPanels[i]);
            
            // Title label
            titleLabels[i] = new JLabel(titles[i]);
            titleLabels[i].setForeground(Color.BLACK);
            titleLabels[i].setFont(new Font("Tahoma", Font.BOLD, 15));
            headerPanels[i].add(titleLabels[i]);
            
            // Name and picture label
            namePics[i] = new JLabel();
            namePics[i].setText("BARANGAY OFFICIAL NAME");
            namePics[i].setFont(new Font("Tahoma", Font.BOLD, 15));
            namePics[i].setOpaque(false);
            if (scaledIcon != null) {
                namePics[i].setIcon(scaledIcon);
            }
            officialPanels[i].add(namePics[i]);
        }
        
        // ===== Green Side Panel =====
        greenSidePanel = new OvalPanel();
        greenSidePanel.setLayout(null);
        greenSidePanel.setBackground(new Color(0, 128, 64));
        add(greenSidePanel);
        
        // Create 4 white panels inside green side panel
        whitePanels = new OvalPanel[4];
        for (int i = 0; i < 4; i++) {
            whitePanels[i] = new OvalPanel();
            whitePanels[i].setLayout(null);
            whitePanels[i].setBackground(Color.WHITE);
            greenSidePanel.add(whitePanels[i]);
        }
        
        // Add component listener for responsive layout
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
        
        // Initial layout
        resizeComponents();
    }
    
    /**
     * Resize and reposition all components based on current panel size
     */
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) {
            width = 1280;
            height = 1001;
        }

        // Calculate proportions
        double widthScale = width / 1280.0;
        double heightScale = height / 1001.0;
        double minScale = Math.min(widthScale, heightScale);

        // ===== Header Panel =====
        int headerHeight = (int)(90 * heightScale);
        headPanel.setBounds(0, 0, width, headerHeight);
        
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, (int)(30 * minScale)));
        lblSubtitle.setBounds(10, (int)(37 * heightScale), width - 20, (int)(30 * heightScale));
        
        // ===== Main Panel =====
        int mainPanelX = (int)(320 * widthScale);
        int mainPanelY = headerHeight;
        int mainPanelWidth = (int)(960 * widthScale);
        int mainPanelHeight = (int)(738 * heightScale);
        mainPanel.setBounds(mainPanelX, mainPanelY, mainPanelWidth, mainPanelHeight);
        
        // ===== Official Panels (3 panels vertically stacked) =====
        int officialPanelWidth = (int)(793 * widthScale);
        int officialPanelHeight = (int)(169 * heightScale);
        int officialPanelX = (mainPanelWidth - officialPanelWidth) / 2;
        int[] officialYPositions = {
            (int)(29 * heightScale),
            (int)(232 * heightScale),
            (int)(454 * heightScale)
        };
        
        for (int i = 0; i < 3; i++) {
            officialPanels[i].setBounds(officialPanelX, officialYPositions[i], 
                                        officialPanelWidth, officialPanelHeight);
            
            // Green header inside each panel
            headerPanels[i].setBounds(0, 0, officialPanelWidth, (int)(46 * heightScale));
            
            // Title label
            titleLabels[i].setFont(new Font("Tahoma", Font.BOLD, (int)(15 * minScale)));
            int titleWidth = (int)(206 * widthScale);
            titleLabels[i].setBounds((int)(64 * widthScale), (int)(10 * heightScale), 
                                     titleWidth, (int)(26 * heightScale));
            
            // Profile picture and name
            int picSize = (int)(75 * minScale);
            namePics[i].setFont(new Font("Tahoma", Font.BOLD, (int)(15 * minScale)));
            namePics[i].setBounds((int)(30 * widthScale), (int)(70 * heightScale), 
                                  (int)(350 * widthScale), picSize);
            
            // Update icon size if available
            if (scaledIcon != null) {
                try {
                    ImageIcon cpticon = new ImageIcon(getClass().getResource("/img/pfp.png"));
                    Image cptimg = cpticon.getImage().getScaledInstance(picSize, picSize, Image.SCALE_SMOOTH);
                    namePics[i].setIcon(new ImageIcon(cptimg));
                } catch (Exception e) {
                    // Keep existing icon
                }
            }
        }
        
        // ===== Green Side Panel =====
        int sidePanelX = (int)(31 * widthScale);
        int sidePanelY = (int)(254 * heightScale);
        int sidePanelWidth = (int)(243 * widthScale);
        int sidePanelHeight = (int)(364 * heightScale);
        greenSidePanel.setBounds(sidePanelX, sidePanelY, sidePanelWidth, sidePanelHeight);
        
        // White panels inside green side panel
        int whitePanelWidth = (int)(202 * widthScale);
        int whitePanelHeight = (int)(52 * heightScale);
        int whitePanelX = (int)(20 * widthScale);
        int[] whitePanelYPositions = {
            (int)(44 * heightScale),
            (int)(120 * heightScale),
            (int)(196 * heightScale),
            (int)(278 * heightScale)
        };
        
        for (int i = 0; i < 4; i++) {
            whitePanels[i].setBounds(whitePanelX, whitePanelYPositions[i], 
                                     whitePanelWidth, whitePanelHeight);
        }
    }
}