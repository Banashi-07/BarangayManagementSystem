package UI.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;

public class ReportsPanel extends JPanel {

    public ReportsPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ================= HEADER =================
        JPanel headerPanel = new GradientPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(100, 110));

        JLabel subtitle = new JLabel("REPORTS", SwingConstants.CENTER);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Tahoma", Font.BOLD, 28));

        headerPanel.add(subtitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(230, 230, 230));
        add(mainPanel, BorderLayout.CENTER);

        // ================= LEFT PANEL =================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(500, 200));

        cardsPanel.add(createCard("Settled Cases", "/img/CheckIcon.png", new Color(102, 170, 51)));
        cardsPanel.add(createCard("Unsettled Cases", "/img/clockicon.png", new Color(200, 150, 50)));
        cardsPanel.add(createCard("Scheduled Cases", "/img/calendaricon.png", new Color(100, 150, 200)));
        cardsPanel.add(createCard("Pending Cases", "/img/hourglassicon.png", new Color(180, 140, 70)));

        leftPanel.add(cardsPanel);
        leftPanel.add(Box.createVerticalStrut(20));

        JPanel monthlyPanel = new JPanel();
        monthlyPanel.setBackground(Color.WHITE);
        monthlyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Monthly Reports",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Tahoma", Font.BOLD, 14)
        ));
        monthlyPanel.setPreferredSize(new Dimension(500, 300));
        monthlyPanel.setMaximumSize(new Dimension(500, 300));

        leftPanel.add(monthlyPanel);
        mainPanel.add(leftPanel);

        // ================= RIGHT PANEL =================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header with "Recent Records" and "View All >" button
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(102, 170, 51));
        header.setBorder(new EmptyBorder(15, 15, 10, 15));

        JLabel recentLabel = new JLabel("Recent Records");
        recentLabel.setForeground(Color.WHITE);
        recentLabel.setFont(new Font("Tahoma", Font.BOLD, 23));

        JButton viewAll = new JButton("View All >");
        viewAll.setForeground(Color.WHITE);
        viewAll.setFont(new Font("Tahoma", Font.BOLD, 17));
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.setFocusPainted(false);
        viewAll.setBorderPainted(false);
        viewAll.setContentAreaFilled(false);
        viewAll.setOpaque(false);
        viewAll.addActionListener(e -> System.out.println("View All clicked!"));

        header.add(recentLabel, BorderLayout.CENTER);
        header.add(viewAll, BorderLayout.EAST);
        rightPanel.add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Description", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(rightPanel);
    }

    // ================= CREATE CARD =================
    private JButton createCard(String title, String resourcePath, Color iconColor) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setFocusPainted(false);
        card.setBorderPainted(false);
        card.setContentAreaFilled(true);
        card.setOpaque(true);
        card.setPreferredSize(new Dimension(180, 90));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ICON
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
            Image scaled = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.out.println("Could not load icon: " + resourcePath);
            iconLabel.setIcon(createColoredCircleIcon(iconColor, 40));
        }
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.WEST);

        // TEXT
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        valueLabel.setForeground(new Color(50, 50, 50));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);

        // HOVER EFFECT
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    // Helper method to create colored circle icon
    private ImageIcon createColoredCircleIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(2, 2, size - 4, size - 4);
        g2d.dispose();
        return new ImageIcon(image);
    }

    // ================= GRADIENT PANEL =================
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(102, 170, 51);
            Color color2 = new Color(70, 130, 35);
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}