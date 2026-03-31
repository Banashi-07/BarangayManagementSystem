package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {

    public ReportsPanel() {

        setLayout(new BorderLayout()); // IMPORTANT
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
        leftPanel.setLayout(new GridLayout(2, 1, 10, 10));
        leftPanel.setOpaque(false);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createCard("Settled Cases"));
        cardsPanel.add(createCard("Unsettled Cases"));
        cardsPanel.add(createCard("Scheduled Cases"));
        cardsPanel.add(createCard("Pending Cases"));

        leftPanel.add(cardsPanel);

        JPanel monthlyPanel = new JPanel();
        monthlyPanel.setBackground(Color.WHITE);
        monthlyPanel.setBorder(BorderFactory.createTitledBorder("Monthly Reports"));

        leftPanel.add(monthlyPanel);

        mainPanel.add(leftPanel);

        // ================= RIGHT PANEL =================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new LineBorder(new Color(102, 170, 51), 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(102, 170, 51));

        JLabel recentLabel = new JLabel("Recent Records");
        recentLabel.setForeground(Color.WHITE);
        recentLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel viewAll = new JLabel("View All >");
        viewAll.setForeground(Color.WHITE);
        viewAll.setBorder(new EmptyBorder(5, 10, 5, 10));

        header.add(recentLabel, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        rightPanel.add(header, BorderLayout.NORTH);

        JTable table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Description", "Date", "Status"}
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(rightPanel);
    }

    // ================= CARD =================
    private JPanel createCard(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(200, 200, 200)));
        panel.setPreferredSize(new Dimension(150, 80));

        JLabel label = new JLabel(title);
        label.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel value = new JLabel("0");
        value.setFont(new Font("Tahoma", Font.BOLD, 20));
        value.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

   
}