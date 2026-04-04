package UI.panels;

import database.DatabaseManager;
import service.StatisticsService;
import service.Reportservice;
import UI.components.OvalPanel;
import UI.components.CircularButton;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    private JLabel lblHouseholdCount, lblPopulationCount, lblVotersCount;
    private JLabel lblFemaleCount, lblMaleCount, lblSeniorCount;
    private JLabel lblPWDCount, lblReportCasesCount;
    private Timer refreshTimer;

    public HomePanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1100));

        // ===== Gradient Header Panel =====
        GradientPanel headPanel = new GradientPanel();
        headPanel.setBounds(0, 0, 1280, 209);
        headPanel.setLayout(null);
        add(headPanel);

        // ===== Logo =====
        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/logoo.png"));
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);
            lblLogo.setIcon(scaledIcon);
        } catch (Exception e) {
            System.err.println("Logo not found: " + e.getMessage());
        }
        lblLogo.setBounds(70, 5, 200, 200);
        lblLogo.setOpaque(false);
        headPanel.add(lblLogo);

        // ===== Header Text =====
        JLabel lblTitle = new JLabel("Barangay Management System", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setBounds(0, 50, 1280, 50);
        lblTitle.setOpaque(false);
        headPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("SAN MIGUEL, AGOO, LA UNION", SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblSubtitle.setBounds(0, 110, 1280, 30);
        lblSubtitle.setOpaque(false);
        headPanel.add(lblSubtitle);

        // Paragraph Panel
        JPanel paragraphPanel = new JPanel();
        paragraphPanel.setBounds(94, 241, 1080, 250);
        paragraphPanel.setLayout(null);
        paragraphPanel.setBackground(Color.white);

        JLabel lblParagraph = new JLabel("About Our Barangay", SwingConstants.CENTER);
        lblParagraph.setFont(new Font("Arial", Font.BOLD, 22));
        lblParagraph.setBounds(0, 0, 1080, 50);
        paragraphPanel.add(lblParagraph);
        add(paragraphPanel);

        JTextPane txtpnloremIpsumDolor = new JTextPane();
        txtpnloremIpsumDolor.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtpnloremIpsumDolor.setText("\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"");
        txtpnloremIpsumDolor.setBounds(197, 60, 690, 152);
        paragraphPanel.add(txtpnloremIpsumDolor);

        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(94, 486, 1080, 150);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setLayout(null);

        // ===== COUNT LABELS =====
        lblHouseholdCount = new JLabel("0", SwingConstants.CENTER);
        lblHouseholdCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblHouseholdCount.setBounds(105, 100, 60, 20);
        statsPanel.add(lblHouseholdCount);

        lblPopulationCount = new JLabel("0", SwingConstants.CENTER);
        lblPopulationCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblPopulationCount.setBounds(224, 100, 60, 20);
        statsPanel.add(lblPopulationCount);

        lblVotersCount = new JLabel("0", SwingConstants.CENTER);
        lblVotersCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblVotersCount.setBounds(342, 100, 60, 20);
        statsPanel.add(lblVotersCount);

        lblFemaleCount = new JLabel("0", SwingConstants.CENTER);
        lblFemaleCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblFemaleCount.setBounds(459, 100, 60, 20);
        statsPanel.add(lblFemaleCount);

        lblMaleCount = new JLabel("0", SwingConstants.CENTER);
        lblMaleCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblMaleCount.setBounds(576, 100, 60, 20);
        statsPanel.add(lblMaleCount);

        lblSeniorCount = new JLabel("0", SwingConstants.CENTER);
        lblSeniorCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblSeniorCount.setBounds(684, 100, 60, 20);
        statsPanel.add(lblSeniorCount);

        lblPWDCount = new JLabel("0", SwingConstants.CENTER);
        lblPWDCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblPWDCount.setBounds(794, 100, 60, 20);
        statsPanel.add(lblPWDCount);

        lblReportCasesCount = new JLabel("0", SwingConstants.CENTER);
        lblReportCasesCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblReportCasesCount.setBounds(906, 100, 60, 20);
        statsPanel.add(lblReportCasesCount);

        add(statsPanel);

        // ===== STATS PANEL ICONS =====
        try {
            ImageIcon hh = new ImageIcon(CircularButton.class.getResource("/img/1.png"));
            Image hhimg = hh.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon hhicon = new ImageIcon(hhimg);

            ImageIcon pp = new ImageIcon(CircularButton.class.getResource("/img/2.png"));
            Image ppimg = pp.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon ppicon = new ImageIcon(ppimg);

            ImageIcon rv = new ImageIcon(CircularButton.class.getResource("/img/3.png"));
            Image rvimg = rv.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon rvicon = new ImageIcon(rvimg);

            ImageIcon fem = new ImageIcon(CircularButton.class.getResource("/img/4.png"));
            Image femimg = fem.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon femicon = new ImageIcon(femimg);

            ImageIcon male = new ImageIcon(CircularButton.class.getResource("/img/5.png"));
            Image maleimg = male.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon maleicon = new ImageIcon(maleimg);

            ImageIcon sr = new ImageIcon(CircularButton.class.getResource("/img/6.png"));
            Image srimg = sr.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon sricon = new ImageIcon(srimg);

            ImageIcon pwd = new ImageIcon(CircularButton.class.getResource("/img/7.png"));
            Image pwdimg = pwd.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon pwdicon = new ImageIcon(pwdimg);

            ImageIcon rc = new ImageIcon(CircularButton.class.getResource("/img/8.png"));
            Image rcimg = rc.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon rcicon = new ImageIcon(rcimg);

            CircularButton btnNewButton = new CircularButton((ImageIcon) null, 80);
            btnNewButton.setBounds(93, 45, 90, 90);
            btnNewButton.setIcon(hhicon);
            statsPanel.add(btnNewButton);

            CircularButton btnNewButton_1 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_1.setBounds(212, 45, 80, 80);
            btnNewButton_1.setIcon(ppicon);
            statsPanel.add(btnNewButton_1);

            CircularButton btnNewButton_2 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_2.setBounds(330, 45, 80, 80);
            btnNewButton_2.setIcon(rvicon);
            statsPanel.add(btnNewButton_2);

            CircularButton btnNewButton_3 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_3.setBounds(448, 45, 80, 80);
            btnNewButton_3.setIcon(femicon);
            statsPanel.add(btnNewButton_3);

            CircularButton btnNewButton_4 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_4.setBounds(564, 45, 80, 80);
            btnNewButton_4.setIcon(maleicon);
            statsPanel.add(btnNewButton_4);

            CircularButton btnNewButton_5 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_5.setBounds(673, 45, 80, 80);
            btnNewButton_5.setIcon(sricon);
            statsPanel.add(btnNewButton_5);

            CircularButton btnNewButton_6 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_6.setBounds(784, 45, 80, 80);
            btnNewButton_6.setIcon(pwdicon);
            statsPanel.add(btnNewButton_6);

            CircularButton btnNewButton_7 = new CircularButton((ImageIcon) null, 80);
            btnNewButton_7.setBounds(894, 45, 80, 80);
            btnNewButton_7.setIcon(rcicon);
            statsPanel.add(btnNewButton_7);
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
        }

        JLabel lblNewLabel = new JLabel("HOUSEHOLD");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblNewLabel.setBounds(80, 135, 111, 14);
        statsPanel.add(lblNewLabel);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation = new JLabel("POPULATION");
        lblTotalPopulation.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation.setBounds(200, 135, 106, 14);
        statsPanel.add(lblTotalPopulation);
        lblTotalPopulation.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1 = new JLabel("REGISTERED VOTERS");
        lblTotalPopulation_1.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1.setBounds(310, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1);
        lblTotalPopulation_1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1_1 = new JLabel("MALE");
        lblTotalPopulation_1_1.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1_1.setBounds(545, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1_1);
        lblTotalPopulation_1_1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1_1_1 = new JLabel("FEMALE");
        lblTotalPopulation_1_1_1.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1_1_1.setBounds(430, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1_1_1);
        lblTotalPopulation_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1_1_1_1 = new JLabel("SENIOR");
        lblTotalPopulation_1_1_1_1.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1_1_1_1.setBounds(655, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1_1_1_1);
        lblTotalPopulation_1_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1_1_1_2 = new JLabel("PWD");
        lblTotalPopulation_1_1_1_2.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1_1_1_2.setBounds(765, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1_1_1_2);
        lblTotalPopulation_1_1_1_2.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalPopulation_1_1_1_3 = new JLabel("REPORT CASES");
        lblTotalPopulation_1_1_1_3.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTotalPopulation_1_1_1_3.setBounds(875, 135, 116, 14);
        statsPanel.add(lblTotalPopulation_1_1_1_3);
        lblTotalPopulation_1_1_1_3.setHorizontalAlignment(SwingConstants.CENTER);

        // Announcement Panel
        OvalPanel announcementPanel = new OvalPanel();
        announcementPanel.setLayout(null);
        announcementPanel.setBackground(new Color(0, 180, 0));
        announcementPanel.setBounds(94, 673, 1080, 350);

        JLabel lblAnnouncement = new JLabel("Official Announcement");
        lblAnnouncement.setForeground(Color.WHITE);
        lblAnnouncement.setFont(new Font("Arial", Font.BOLD, 20));
        lblAnnouncement.setBounds(20, 20, 300, 30);
        announcementPanel.add(lblAnnouncement);

        add(announcementPanel);

        // Load statistics after UI is built
        loadStatistics();

        // Auto-refresh every 30 seconds
        refreshTimer = new Timer(30000, e -> loadStatistics());
        refreshTimer.start();
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
            private int blotterCount = 0;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Initialize reports table
                    Reportservice.initializeReportsTable();
                    
                    // Get statistics from StatisticsService
                    StatisticsService.Stats stats = StatisticsService.getAllStats();
                    totalPopulation = stats.totalPopulation;
                    maleCount = stats.maleCount;
                    femaleCount = stats.femaleCount;
                    seniorCount = stats.seniorCount;
                    voterCount = stats.voterCount;
                    householdCount = stats.householdCount;
                    blotterCount = stats.blotterCount;
                    pwdCount = stats.pwdCount;
                    
                    // Get ONLY PENDING reports count
                    pendingReportCount = Reportservice.getReportCountByStatus("Pending");
                    
                    System.out.println("=== Statistics Loaded ===");
                    System.out.println("Total Population: " + totalPopulation);
                    System.out.println("Male: " + maleCount);
                    System.out.println("Female: " + femaleCount);
                    System.out.println("Senior: " + seniorCount);
                    System.out.println("Voters: " + voterCount);
                    System.out.println("Households: " + householdCount);
                    System.out.println("Blotters: " + blotterCount);
                    System.out.println("PENDING Reports: " + pendingReportCount);
                    System.out.println("PWD Count: " + pwdCount);
                } catch (Exception e) {
                    System.err.println("Error loading statistics: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                lblHouseholdCount.setText(String.valueOf(householdCount));
                lblPopulationCount.setText(String.valueOf(totalPopulation));
                lblVotersCount.setText(String.valueOf(voterCount));
                lblFemaleCount.setText(String.valueOf(femaleCount));
                lblMaleCount.setText(String.valueOf(maleCount));
                lblSeniorCount.setText(String.valueOf(seniorCount));
                lblPWDCount.setText(String.valueOf(pwdCount));
                lblReportCasesCount.setText(String.valueOf(pendingReportCount));
            }
        };
        worker.execute();
    }
    
    public void refreshStatistics() {
        SwingUtilities.invokeLater(() -> loadStatistics());
    }
}

// GradientPanel class
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(34, 139, 34), 0, h, new Color(20, 100, 20));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}