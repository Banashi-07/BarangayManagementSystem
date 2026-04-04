package UI.panels;

import UI.components.ResidenceTable;
import UI.components.OvalButton;
import UI.components.OvalPanel;
import UI.components.PrintClearance;
import UI.components.PrintIndigency;
import UI.components.PrintResidency;
import UI.dialogs.AddResidence;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * RecordsPanel — shows the resident records list with search and action buttons.
 *
 * All print buttons now functional:
 *  - Print Residency uses PrintResidency.print()
 *  - Print Clearance uses PrintClearance.print()
 *  - Print Indigency uses PrintIndigency.print()
 */
public class RecordsPanel extends JPanel {

    private final JTextField searchField;
    private final ResidenceTable table;

    public RecordsPanel(HomePanel homePanel) {

        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1001));

        // ===== HEADER =====
        GradientPanel headPanel = new GradientPanel();
        headPanel.setBounds(0, 0, 1280, 90);
        add(headPanel);
        headPanel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("RECORDS LIST", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        headPanel.add(lblTitle, BorderLayout.CENTER);

        // ===== LEFT SIDE PANEL =====
        OvalPanel greenSidePanel = new OvalPanel();
        greenSidePanel.setLayout(null);
        greenSidePanel.setBackground(new Color(0, 128, 64));
        greenSidePanel.setBounds(31, 254, 243, 364);
        add(greenSidePanel);

        for (int i = 0; i < 4; i++) {
            OvalPanel wp = new OvalPanel();
            wp.setBackground(Color.WHITE);
            wp.setBounds(20, 44 + i * 76, 202, 52);
            greenSidePanel.add(wp);
        }

        // ===== SEARCH =====
        JLabel lblSearch = new JLabel("SEARCH RESIDENCE:");
        lblSearch.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblSearch.setForeground(new Color(0, 128, 0));
        lblSearch.setHorizontalAlignment(SwingConstants.CENTER);
        lblSearch.setBounds(264, 142, 181, 20);
        add(lblSearch);

        searchField = new JTextField();
        searchField.setBounds(455, 137, 286, 26);
        add(searchField);

        // ===== BUTTONS =====
        OvalButton btnAddResidence = new OvalButton("ADD RESIDENCE");
        btnAddResidence.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnAddResidence.setBounds(400, 190, 181, 41);
        add(btnAddResidence);

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

        // ===== TABLE =====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(341, 242, 902, 532);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 170, 51), 2));
        add(tablePanel);

        table = new ResidenceTable(homePanel);
        table.setShowHorizontalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JScrollBar vsb = scrollPane.getVerticalScrollBar();
        vsb.setUnitIncrement(16);
        vsb.setBackground(new Color(240, 240, 240));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // ===== WIRE UP EVENTS =====

        // Search — live filter as user types
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { doSearch(); }
            @Override public void removeUpdate(DocumentEvent e)  { doSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { doSearch(); }
            private void doSearch() {
                table.search(searchField.getText().trim());
            }
        });

        // Add Residence
        btnAddResidence.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddResidence dialog = new AddResidence(parent, homePanel);
            dialog.setVisible(true);
            // Refresh after dialog closes
            table.refresh();
            homePanel.refreshStatistics();
        });

        // Print Residency
        btnPrintResidence.addActionListener(e -> {
            int id = getSelectedResidentId();
            if (id == -1) {
                JOptionPane.showMessageDialog(this, "Please select a resident first.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            PrintResidency.print(id);
        });

        // Print Clearance
        btnPrintClearance.addActionListener(e -> {
            int id = getSelectedResidentId();
            if (id == -1) {
                JOptionPane.showMessageDialog(this, "Please select a resident first.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            PrintClearance.print(id);
        });

        // Print Indigency
        btnPrintIndigency.addActionListener(e -> {
            int id = getSelectedResidentId();
            if (id == -1) {
                JOptionPane.showMessageDialog(this, "Please select a resident first.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            PrintIndigency.print(id);
        });
    }

    /** Returns the DB id of the currently selected row, or -1 if nothing is selected. */
    private int getSelectedResidentId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        return table.getResidentIdAt(row);
    }
}