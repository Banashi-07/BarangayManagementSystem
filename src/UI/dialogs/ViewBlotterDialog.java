package UI.dialogs;

import database.DatabaseManager.Blotter;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ViewBlotterDialog extends JDialog {

    public ViewBlotterDialog(Blotter blotter) {
        setTitle("Blotter Case #" + blotter.getId());
        setModal(true);
        setSize(500, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getStatusColor(blotter.getStatus()));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("BLOTTER CASE DETAILS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel idLabel = new JLabel("Case ID: " + blotter.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        idLabel.setForeground(new Color(236, 240, 241));

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(idLabel, BorderLayout.SOUTH);

        JLabel statusLabel = new JLabel(blotter.getStatus());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ── Content ───────────────────────────────────────────────────────────
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        addInfoRow(contentPanel, gbc, y++, "Complainant:",    blotter.getComplainant());
        addInfoRow(contentPanel, gbc, y++, "Respondent:",     blotter.getRespondent());
        addInfoRow(contentPanel, gbc, y++, "Incident Type:",  blotter.getIncidentType());
        addInfoRow(contentPanel, gbc, y++, "Date of Incident:", blotter.getDateIncident());

        // Description (multi-line)
        GridBagConstraints lg = (GridBagConstraints) gbc.clone();
        lg.gridx = 0; lg.gridy = y; lg.weightx = 0.3; lg.anchor = GridBagConstraints.FIRST_LINE_END;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(new Color(52, 73, 94));
        contentPanel.add(descLabel, lg);

        JTextArea descArea = new JTextArea(blotter.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null);
        descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descScroll.setPreferredSize(new Dimension(280, 90));

        GridBagConstraints vg = (GridBagConstraints) gbc.clone();
        vg.gridx = 1; vg.gridy = y++; vg.weightx = 0.7;
        contentPanel.add(descScroll, vg);

        addInfoRow(contentPanel, gbc, y++, "Filed Date:", blotter.getCreatedDate());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ── Close Button ──────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(52, 152, 219));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.addActionListener(e -> dispose());

        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { closeBtn.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { closeBtn.setBackground(new Color(52, 152, 219)); }
        });

        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, String valueText) {
        GridBagConstraints lg = (GridBagConstraints) gbc.clone();
        lg.gridx = 0; lg.gridy = y; lg.weightx = 0.3; lg.anchor = GridBagConstraints.LINE_END;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        panel.add(label, lg);

        GridBagConstraints vg = (GridBagConstraints) gbc.clone();
        vg.gridx = 1; vg.gridy = y; vg.weightx = 0.7; vg.anchor = GridBagConstraints.LINE_START;
        JLabel value = new JLabel(valueText != null ? valueText : "—");
        value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        value.setForeground(new Color(44, 62, 80));
        panel.add(value, vg);
    }

    private Color getStatusColor(String status) {
        if (status == null) return new Color(41, 128, 185);
        switch (status.toLowerCase()) {
            case "pending":              return new Color(241, 196, 15);
            case "under investigation":  return new Color(52, 152, 219);
            case "resolved":             return new Color(46, 204, 113);
            case "dismissed":            return new Color(231, 76, 60);
            default:                     return new Color(41, 128, 185);
        }
    }
}