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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * RecordsPanel — shows the resident records list with search and action buttons.
 * Now fully scalable and responsive to screen size changes.
 */
public class RecordsPanel extends JPanel {

    private final JTextField searchField;
    private final ResidenceTable table;
    private final HomePanel homePanel;
    
    // Components that need to be repositioned
    private GradientPanel headPanel;
    private JLabel lblTitle;
    private OvalPanel greenSidePanel;
    private OvalPanel[] whitePanels;
    private JLabel lblSearch;
    private OvalButton btnAddResidence;
    private OvalButton btnPrintResidence;
    private OvalButton btnPrintClearance;
    private OvalButton btnPrintIndigency;
    private JPanel tablePanel;

    public RecordsPanel(HomePanel homePanel) {
        this.homePanel = homePanel;
        
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1280, 1001));

        // ===== HEADER =====
        headPanel = new GradientPanel();
        add(headPanel);
        headPanel.setLayout(new BorderLayout());

        lblTitle = new JLabel("RECORDS LIST", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        headPanel.add(lblTitle, BorderLayout.CENTER);

        // ===== LEFT SIDE PANEL =====
        greenSidePanel = new OvalPanel();
        greenSidePanel.setLayout(null);
        greenSidePanel.setBackground(new Color(0, 128, 64));
        add(greenSidePanel);

        whitePanels = new OvalPanel[4];
        for (int i = 0; i < 4; i++) {
            whitePanels[i] = new OvalPanel();
            whitePanels[i].setBackground(Color.WHITE);
            greenSidePanel.add(whitePanels[i]);
        }

        // ===== SEARCH =====
        lblSearch = new JLabel("SEARCH RESIDENCE:");
        lblSearch.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblSearch.setForeground(new Color(0, 128, 0));
        lblSearch.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblSearch);

        searchField = new JTextField();
        add(searchField);

        // ===== BUTTONS =====
        btnAddResidence = new OvalButton("ADD RESIDENCE");
        btnAddResidence.setFont(new Font("Tahoma", Font.BOLD, 13));
        add(btnAddResidence);

        btnPrintResidence = new OvalButton("PRINT RESIDENCY");
        btnPrintResidence.setFont(new Font("Tahoma", Font.BOLD, 13));
        add(btnPrintResidence);

        btnPrintClearance = new OvalButton("PRINT CLEARANCE");
        btnPrintClearance.setFont(new Font("Tahoma", Font.BOLD, 13));
        add(btnPrintClearance);

        btnPrintIndigency = new OvalButton("PRINT INDIGENCY");
        btnPrintIndigency.setFont(new Font("Tahoma", Font.BOLD, 13));
        add(btnPrintIndigency);

        // ===== TABLE =====
        tablePanel = new JPanel(new BorderLayout());
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
        
        lblTitle.setFont(new Font("Arial", Font.BOLD, (int)(30 * minScale)));

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
        
        for (int i = 0; i < 4; i++) {
            int whitePanelY = (int)((44 + i * 76) * heightScale);
            whitePanels[i].setBounds(whitePanelX, whitePanelY, whitePanelWidth, whitePanelHeight);
        }

        // ===== Search Label and Field =====
        int searchLabelX = (int)(264 * widthScale);
        int searchLabelY = (int)(142 * heightScale);
        int searchLabelWidth = (int)(181 * widthScale);
        int searchLabelHeight = (int)(20 * heightScale);
        lblSearch.setFont(new Font("Tahoma", Font.BOLD, (int)(14 * minScale)));
        lblSearch.setBounds(searchLabelX, searchLabelY, searchLabelWidth, searchLabelHeight);

        int searchFieldX = (int)(455 * widthScale);
        int searchFieldY = (int)(137 * heightScale);
        int searchFieldWidth = (int)(286 * widthScale);
        int searchFieldHeight = (int)(26 * heightScale);
        searchField.setBounds(searchFieldX, searchFieldY, searchFieldWidth, searchFieldHeight);

        // ===== Buttons =====
        int buttonY = (int)(190 * heightScale);
        int buttonHeight = (int)(41 * heightScale);
        int buttonFontSize = (int)(13 * minScale);
        
        int btnAddX = (int)(400 * widthScale);
        int btnAddWidth = (int)(181 * widthScale);
        btnAddResidence.setBounds(btnAddX, buttonY, btnAddWidth, buttonHeight);
        btnAddResidence.setFont(new Font("Tahoma", Font.BOLD, buttonFontSize));

        int btnPrintResX = (int)(600 * widthScale);
        int btnPrintResWidth = (int)(173 * widthScale);
        btnPrintResidence.setBounds(btnPrintResX, buttonY, btnPrintResWidth, buttonHeight);
        btnPrintResidence.setFont(new Font("Tahoma", Font.BOLD, buttonFontSize));

        int btnPrintClearX = (int)(800 * widthScale);
        int btnPrintClearWidth = (int)(181 * widthScale);
        btnPrintClearance.setBounds(btnPrintClearX, buttonY, btnPrintClearWidth, buttonHeight);
        btnPrintClearance.setFont(new Font("Tahoma", Font.BOLD, buttonFontSize));

        int btnPrintIndigX = (int)(1000 * widthScale);
        int btnPrintIndigWidth = (int)(175 * widthScale);
        btnPrintIndigency.setBounds(btnPrintIndigX, buttonY, btnPrintIndigWidth, buttonHeight);
        btnPrintIndigency.setFont(new Font("Tahoma", Font.BOLD, buttonFontSize));

        // ===== Table Panel =====
        int tablePanelX = (int)(341 * widthScale);
        int tablePanelY = (int)(242 * heightScale);
        int tablePanelWidth = (int)(902 * widthScale);
        int tablePanelHeight = (int)(532 * heightScale);
        tablePanel.setBounds(tablePanelX, tablePanelY, tablePanelWidth, tablePanelHeight);
        
        // Scale border thickness
        int borderThickness = Math.max(1, (int)(2 * minScale));
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 170, 51), borderThickness));
    }

    /** Returns the DB id of the currently selected row, or -1 if nothing is selected. */
    private int getSelectedResidentId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        return table.getResidentIdAt(row);
    }
}