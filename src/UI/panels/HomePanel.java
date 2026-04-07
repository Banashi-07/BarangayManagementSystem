package UI.panels;

import database.DatabaseManager;
import service.StatisticsService;
import UI.components.OvalPanel;
import UI.components.CircularButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

public class HomePanel extends JPanel {

    private JLabel lblHouseholdCount, lblPopulationCount, lblVotersCount;
    private JLabel lblFemaleCount, lblMaleCount, lblSeniorCount;
    private JLabel lblPWDCount, lblReportCasesCount;
    private Timer refreshTimer;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    
    // Components that need to be repositioned
    private GradientPanel headPanel;
    private JLabel lblLogo, lblTitle, lblSubtitle;
    private JPanel paragraphPanel, statsPanel;
    private JLabel lblParagraph;
    private JTextPane txtpnloremIpsumDolor;
    private CircularButton[] statButtons;
    private OvalPanel announcementPanel;
    private JLabel lblAnnouncement;

    public HomePanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1100));
        setMinimumSize(new Dimension(800, 600));

        // ===== Gradient Header Panel =====
        headPanel = new GradientPanel();
        headPanel.setLayout(null);
        add(headPanel);

        // ===== Logo =====
        lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/logoo.png"));
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);
            lblLogo.setIcon(scaledIcon);
        } catch (Exception e) {
            System.err.println("Logo not found: " + e.getMessage());
        }
        lblLogo.setOpaque(false);
        headPanel.add(lblLogo);

        // ===== Header Text =====
        lblTitle = new JLabel("Barangay Management System", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setOpaque(false);
        headPanel.add(lblTitle);

        lblSubtitle = new JLabel("SAN MIGUEL, AGOO, LA UNION", SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblSubtitle.setOpaque(false);
        headPanel.add(lblSubtitle);

        // Paragraph Panel
        paragraphPanel = new JPanel();
        paragraphPanel.setLayout(null);
        paragraphPanel.setBackground(Color.white);

        lblParagraph = new JLabel("About Our Barangay", SwingConstants.CENTER);
        lblParagraph.setFont(new Font("Arial", Font.BOLD, 22));
        paragraphPanel.add(lblParagraph);
        add(paragraphPanel);

        txtpnloremIpsumDolor = new JTextPane();
        txtpnloremIpsumDolor.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtpnloremIpsumDolor.setText("\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"");
        paragraphPanel.add(txtpnloremIpsumDolor);

        statsPanel = new JPanel();
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setLayout(null);

        // ===== COUNT LABELS =====
        lblHouseholdCount = new JLabel("0", SwingConstants.CENTER);
        lblHouseholdCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblHouseholdCount);

        lblPopulationCount = new JLabel("0", SwingConstants.CENTER);
        lblPopulationCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblPopulationCount);

        lblVotersCount = new JLabel("0", SwingConstants.CENTER);
        lblVotersCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblVotersCount);

        lblFemaleCount = new JLabel("0", SwingConstants.CENTER);
        lblFemaleCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblFemaleCount);

        lblMaleCount = new JLabel("0", SwingConstants.CENTER);
        lblMaleCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblMaleCount);

        lblSeniorCount = new JLabel("0", SwingConstants.CENTER);
        lblSeniorCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblSeniorCount);

        lblPWDCount = new JLabel("0", SwingConstants.CENTER);
        lblPWDCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblPWDCount);

        lblReportCasesCount = new JLabel("0", SwingConstants.CENTER);
        lblReportCasesCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        statsPanel.add(lblReportCasesCount);

        add(statsPanel);

        // ===== STATS PANEL ICONS =====
        statButtons = new CircularButton[8];
        try {
            ImageIcon[] icons = new ImageIcon[8];
            String[] iconPaths = {"/img/1.png", "/img/2.png", "/img/3.png", "/img/4.png", 
                                  "/img/5.png", "/img/6.png", "/img/7.png", "/img/8.png"};
            
            for (int i = 0; i < 8; i++) {
                ImageIcon icon = new ImageIcon(CircularButton.class.getResource(iconPaths[i]));
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icons[i] = new ImageIcon(img);
                
                statButtons[i] = new CircularButton(null, 80);
                statButtons[i].setIcon(icons[i]);
                statsPanel.add(statButtons[i]);
            }
        } catch (Exception e) {
            System.err.println("Error loading stat icons: " + e.getMessage());
        }

        // Add labels for stat descriptions
        String[] statLabels = {"Total Household", "Total Population", "Registered Voters", 
                               "Female Count", "Male Count", "Senior Citizens", "PWD", "Report Cases"};
        for (int i = 0; i < statLabels.length; i++) {
            JLabel lbl = new JLabel(statLabels[i]);
            lbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            statsPanel.add(lbl);
        }

        // Announcement Panel
        announcementPanel = new OvalPanel();
        announcementPanel.setLayout(null);
        announcementPanel.setBackground(new Color(0, 180, 0));

        lblAnnouncement = new JLabel("Official Announcement");
        lblAnnouncement.setForeground(Color.WHITE);
        lblAnnouncement.setFont(new Font("Arial", Font.BOLD, 20));
        announcementPanel.add(lblAnnouncement);

        add(announcementPanel);

        // Add component listener for responsive layout
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        // Initial layout
        resizeComponents();

        // Load statistics after UI is built
        loadStatistics();

        // Auto-refresh every 30 seconds
        refreshTimer = new Timer(30000, e -> loadStatistics());
        refreshTimer.start();
    }

    /**
     * Resize and reposition all components based on current panel size
     */
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) {
            width = 1280;
            height = 1100;
        }

        // Calculate proportions
        double widthScale = width / 1280.0;
        double heightScale = height / 1100.0;
        
        // Use minimum scale to maintain aspect ratio for certain elements
        double minScale = Math.min(widthScale, heightScale);

        // ===== Header Panel (proportional height) =====
        int headerHeight = (int)(209 * heightScale);
        headPanel.setBounds(0, 0, width, headerHeight);

        // Logo (fixed aspect ratio)
        int logoSize = (int)(200 * minScale);
        lblLogo.setBounds((int)(70 * widthScale), (int)(5 * heightScale), logoSize, logoSize);
        
        // Update logo image
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/logoo.png"));
            Image img = icon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Ignore if already loaded
        }

        // Title
        lblTitle.setFont(new Font("Arial", Font.BOLD, (int)(26 * minScale)));
        lblTitle.setBounds(0, (int)(50 * heightScale), width, (int)(50 * heightScale));

        // Subtitle
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, (int)(18 * minScale)));
        lblSubtitle.setBounds(0, (int)(110 * heightScale), width, (int)(30 * heightScale));

        // ===== Paragraph Panel =====
        int paragraphY = (int)(241 * heightScale);
        int paragraphWidth = (int)(1080 * widthScale);
        int paragraphHeight = (int)(250 * heightScale);
        int paragraphX = (width - paragraphWidth) / 2;
        
        paragraphPanel.setBounds(paragraphX, paragraphY, paragraphWidth, paragraphHeight);
        
        lblParagraph.setFont(new Font("Arial", Font.BOLD, (int)(22 * minScale)));
        lblParagraph.setBounds(0, 0, paragraphWidth, (int)(50 * heightScale));
        
        int textWidth = (int)(690 * widthScale);
        int textHeight = (int)(152 * heightScale);
        int textX = (paragraphWidth - textWidth) / 2;
        txtpnloremIpsumDolor.setFont(new Font("Tahoma", Font.PLAIN, (int)(12 * minScale)));
        txtpnloremIpsumDolor.setBounds(textX, (int)(60 * heightScale), textWidth, textHeight);

        // ===== Stats Panel =====
        int statsY = (int)(486 * heightScale);
        int statsHeight = (int)(150 * heightScale);
        statsPanel.setBounds(paragraphX, statsY, paragraphWidth, statsHeight);

        // Position stat buttons and labels
        int buttonSize = (int)(80 * minScale);
        int spacing = paragraphWidth / 8;
        int buttonY = (int)(45 * heightScale);
        int labelY = (int)(100 * heightScale);
        int descY = (int)(135 * heightScale);
        
        Component[] components = statsPanel.getComponents();
        int buttonIndex = 0;
        int labelIndex = 0;
        
        for (Component comp : components) {
            if (comp instanceof CircularButton) {
                int x = spacing / 2 - buttonSize / 2 + (buttonIndex * spacing);
                comp.setBounds(x, buttonY, buttonSize, buttonSize);
                buttonIndex++;
            } else if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                int idx = labelIndex;
                
                // First 8 are count labels, next 8 are description labels
                if (labelIndex < 8) {
                    int x = spacing / 2 - 30 + (idx * spacing);
                    lbl.setBounds(x, labelY, 60, 20);
                    lbl.setFont(new Font("Tahoma", Font.BOLD, (int)(15 * minScale)));
                } else {
                    int descIdx = idx - 8;
                    int x = spacing / 2 - 58 + (descIdx * spacing);
                    lbl.setBounds(x, descY, 116, (int)(14 * minScale));
                    lbl.setFont(new Font("Tahoma", Font.PLAIN, (int)(11 * minScale)));
                }
                labelIndex++;
            }
        }

        // ===== Announcement Panel =====
        int announcementY = (int)(673 * heightScale);
        int announcementHeight = (int)(350 * heightScale);
        announcementPanel.setBounds(paragraphX, announcementY, paragraphWidth, announcementHeight);
        
        lblAnnouncement.setFont(new Font("Arial", Font.BOLD, (int)(20 * minScale)));
        lblAnnouncement.setBounds((int)(20 * widthScale), (int)(20 * heightScale), 
                                  (int)(300 * widthScale), (int)(30 * heightScale));
    }

    private void loadStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int totalPopulation = 0;
            private int maleCount = 0;
            private int femaleCount = 0;
            private int seniorCount = 0;
            private int voterCount = 0;
            private int householdCount = 0;
            private int pendingReportCount = 0;
            private int pwdCount = 0;
            private int reportCount = 0;

            @Override
            protected Void doInBackground() throws Exception {
                for (int retry = 0; retry < MAX_RETRIES; retry++) {
                    try {
                        // Ensure database connection
                        DatabaseManager.getConnection();
                        
                        // Get all statistics from DatabaseManager
                        DatabaseManager.Statistics stats = DatabaseManager.getAllStatistics();
                        totalPopulation = stats.totalPopulation;
                        maleCount = stats.maleCount;
                        femaleCount = stats.femaleCount;
                        seniorCount = stats.seniorCount;
                        voterCount = stats.voterCount;
                        householdCount = stats.householdCount;
                        reportCount = stats.reportCount;
                        pwdCount = stats.pwdCount;
                        
                        // Get pending report count from DatabaseManager
                        pendingReportCount = DatabaseManager.getReportCountByStatus("Pending");
                        
                        System.out.println("=== Statistics Loaded Successfully ===");
                        System.out.println("Total Population: " + totalPopulation);
                        System.out.println("Male: " + maleCount);
                        System.out.println("Female: " + femaleCount);
                        System.out.println("Senior: " + seniorCount);
                        System.out.println("Voters: " + voterCount);
                        System.out.println("Households: " + householdCount);
                        System.out.println("Reports: " + reportCount);
                        System.out.println("PENDING Reports: " + pendingReportCount);
                        System.out.println("PWD Count: " + pwdCount);
                        
                        break;
                        
                    } catch (SQLException e) {
                        System.err.println("Error loading statistics (attempt " + (retry + 1) + "): " + e.getMessage());
                        e.printStackTrace();
                        
                        if (retry < MAX_RETRIES - 1) {
                            try {
                                Thread.sleep(RETRY_DELAY_MS);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            try {
                                DatabaseManager.close();
                                Thread.sleep(500);
                                DatabaseManager.getConnection();
                            } catch (Exception reconnectEx) {
                                System.err.println("Reconnect failed: " + reconnectEx.getMessage());
                            }
                        } else {
                            throw e;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    
                    lblHouseholdCount.setText(String.valueOf(householdCount));
                    lblPopulationCount.setText(String.valueOf(totalPopulation));
                    lblVotersCount.setText(String.valueOf(voterCount));
                    lblFemaleCount.setText(String.valueOf(femaleCount));
                    lblMaleCount.setText(String.valueOf(maleCount));
                    lblSeniorCount.setText(String.valueOf(seniorCount));
                    lblPWDCount.setText(String.valueOf(pwdCount));
                    lblReportCasesCount.setText(String.valueOf(pendingReportCount));
                    
                } catch (Exception e) {
                    System.err.println("Failed to load statistics after " + MAX_RETRIES + " attempts");
                    e.printStackTrace();
                    
                    lblHouseholdCount.setText("Error");
                    lblPopulationCount.setText("Error");
                    lblVotersCount.setText("Error");
                    lblFemaleCount.setText("Error");
                    lblMaleCount.setText("Error");
                    lblSeniorCount.setText("Error");
                    lblPWDCount.setText("Error");
                    lblReportCasesCount.setText("Error");
                    
                    Timer retryTimer = new Timer(10000, evt -> loadStatistics());
                    retryTimer.setRepeats(false);
                    retryTimer.start();
                }
            }
        };
        worker.execute();
    }
    
    public void refreshStatistics() {
        SwingUtilities.invokeLater(() -> loadStatistics());
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}