package UI.dialogs;

import database.DatabaseManager;
import database.DatabaseManager.Blotter;
import database.ResidentDAO.ResidentRow;
import database.Report;
import service.Reportservice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * ViewResidentDialog — Fixed blotter/report lookup
 */
public class ViewResidentDialog extends JDialog {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(245, 247, 250);
    private static final Color WHITE       = Color.WHITE;
    private static final Color CARD        = Color.WHITE;
    private static final Color BORDER      = new Color(220, 228, 235);

    private static final Color GREEN       = new Color(34,  139,  34);
    private static final Color GREEN_DARK  = new Color(20,  100,  20);
    private static final Color GREEN_LIGHT = new Color(230, 245, 230);

    private static final Color TEXT_HEAD   = new Color(15,  30,  60);
    private static final Color TEXT_BODY   = new Color(50,  65,  85);
    private static final Color TEXT_MUTED  = new Color(130, 145, 165);

    private static final Color AMBER       = new Color(217, 119,   6);
    private static final Color AMBER_BG    = new Color(255, 243, 220);
    private static final Color ROSE        = new Color(190,  35,  55);
    private static final Color ROSE_BG     = new Color(255, 228, 230);
    private static final Color TEAL        = new Color(13,  148, 136);
    private static final Color TEAL_BG     = new Color(212, 245, 242);
    private static final Color SKY         = new Color(14,  116, 189);
    private static final Color SKY_BG      = new Color(219, 239, 255);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_HERO  = new Font("Segoe UI", Font.BOLD,  23);
    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 10);
    private static final Font F_VALUE = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_CAP   = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font F_TABLE = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_BADGE = new Font("Segoe UI", Font.BOLD,  10);

    // ── State ─────────────────────────────────────────────────────────────────
    private JTable            blotterTable;
    private DefaultTableModel blotterModel;
    private JLabel            debugLabel;

    private final Map<Integer, Report> reportById = new LinkedHashMap<>();

    private final String residentName;
    private final int residentId;

    private JLabel lbTotal, lbComplainant, lbRespondent, lbPending, lbResolved;

    // ─────────────────────────────────────────────────────────────────────────
    public ViewResidentDialog(ResidentRow r) {
        this.residentName = r.name;
        this.residentId = r.id;

        setTitle("Resident Profile — " + r.name);
        setModal(true);
        setSize(720, 800);
        setMinimumSize(new Dimension(620, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        setContentPane(root);

        root.add(buildHeroHeader(r), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 22, 28, 22));
        body.add(buildInfoSection(r));
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(buildReportSection());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(BG);
        styleScrollBar(scroll.getVerticalScrollBar());
        root.add(scroll, BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);

        loadReportData();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HERO HEADER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildHeroHeader(ResidentRow r) {
        JPanel hero = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, GREEN, w, h, GREEN_DARK));
                g2.fillRect(0, 0, w, h);
                int[] xp={w-220,w,w,w-320}, yp={0,0,h,h};
                g2.setColor(new Color(255,255,255,18)); g2.fillPolygon(xp,yp,4);
                g2.setColor(new Color(255,255,255,80)); g2.setStroke(new BasicStroke(1.5f)); g2.drawLine(0,0,w,0);
                g2.setColor(new Color(0,0,0,20));       g2.setStroke(new BasicStroke(2f));   g2.drawLine(0,h-1,w,h-1);
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(255,255,255,25)); g2.drawOval(-40,h-90,140,140);
                g2.setColor(new Color(255,255,255,15)); g2.drawOval(-15,h-60,85,85);
                g2.setColor(new Color(255,255,255,22));
                for (int dx=w-140;dx<w-10;dx+=14) for (int dy=8;dy<80;dy+=14) g2.fillOval(dx,dy,3,3);
                g2.dispose();
            }
        };
        hero.setPreferredSize(new Dimension(720, 120));
        hero.setOpaque(false);

        JLabel nameLabel = new JLabel(r.name);
        nameLabel.setFont(F_HERO);
        nameLabel.setForeground(WHITE);
        nameLabel.setBounds(32, 35, 500, 32);

        JLabel idPill = createWhitePill("ID #" + r.id);
        idPill.setBounds(32, 72, 80, 20);

        hero.add(nameLabel);
        hero.add(idPill);
        return hero;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INFO SECTION
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildInfoSection(ResidentRow r) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel address = createLightCard(TEAL, "ADDRESS INFORMATION");
        AddressComponents ac = parseFullAddress(r.address, r.purok);
        addField(address, "House No.",    ac.houseNumber);
        addField(address, "Street",       ac.street);
        addField(address, "Purok / Zone", ac.purok);
        addField(address, "Full Address", ac.fullAddress);
        address.add(Box.createVerticalGlue());

        JPanel personal = createLightCard(GREEN, "PERSONAL INFORMATION");
        addField(personal, "Date of Birth", r.birthdate==null||r.birthdate.isBlank()?"—":formatDate(r.birthdate));
        addField(personal, "Age",           r.getAge()+" years old");
        addField(personal, "Sex",           blank(r.sex));
        addField(personal, "Address",  blank( ac.houseNumber+""+r.purok+"" +r.address));
        addField(personal, "Civil Status",  blank(r.status));
        addField(personal, "PWD Status",    getPwdStatus(r));
        personal.add(Box.createVerticalGlue());

     

        row.add(personal);
        row.add(address);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORT SECTION
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildReportSection() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(ROSE); g2.fillRoundRect(0,0,4,getHeight(),4,4);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 20)
        ));

        // Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("REPORT / CASE HISTORY");
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(ROSE);

        JButton refreshBtn = createOutlineButton("↻  Refresh");
        refreshBtn.addActionListener(e -> loadReportData());

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(refreshBtn, BorderLayout.EAST);
        card.add(headerRow);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        // Debug label
        debugLabel = new JLabel("Searching for report records...");
        debugLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        debugLabel.setForeground(TEXT_MUTED);
        debugLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(debugLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Stat chips
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbTotal       = createStatChip("Total 0",       SKY,   SKY_BG);
        lbComplainant = createStatChip("Complainant 0", AMBER, AMBER_BG);
        lbRespondent  = createStatChip("Respondent 0",  ROSE,  ROSE_BG);
        lbPending     = createStatChip("Pending 0",     AMBER, AMBER_BG);
        lbResolved    = createStatChip("Resolved 0",    TEAL,  TEAL_BG);
        stats.add(lbTotal); stats.add(lbComplainant); stats.add(lbRespondent);
        stats.add(lbPending); stats.add(lbResolved);
        card.add(stats);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table
        String[] cols = {"#","Date Filed","Incident Date","Title/Type","Status","Role"};
        blotterModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c==0 ? Integer.class : String.class;
            }
        };
        blotterTable = new JTable(blotterModel);
        styleBlotterTable(blotterTable);

        int[] widths = {42,95,95,150,85,85};
        for (int i=0;i<widths.length;i++)
            blotterTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        blotterTable.getColumnModel().getColumn(0).setMaxWidth(52);

        JScrollPane tblScroll = new JScrollPane(blotterTable);
        tblScroll.setBorder(BorderFactory.createLineBorder(BORDER,1));
        tblScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tblScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tblScroll.getViewport().setBackground(WHITE);
        tblScroll.setPreferredSize(new Dimension(Short.MAX_VALUE,165));
        tblScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE,165));
        tblScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleScrollBar(tblScroll.getVerticalScrollBar());
        card.add(tblScroll);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton viewBtn = createGreenButton("🔍  View Case Details");
        viewBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewBtn.addActionListener(e -> openReportDetails());
        card.add(viewBtn);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FOOTER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setBackground(WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        JButton closeBtn = createGreenButton("Close");
        closeBtn.setPreferredSize(new Dimension(130,40));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        return footer;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOAD REPORT DATA - USING REPORTSERVICE
    // ══════════════════════════════════════════════════════════════════════════
    private void loadReportData() {
        reportById.clear();
        blotterModel.setRowCount(0);

        try {
            System.out.println("\n========== SEARCHING REPORTS FOR RESIDENT ID: " + residentId + " ==========");
            System.out.println("Resident Name: '" + residentName + "'");
            
            // Get reports where resident is complainant
            List<Report> asComplainant = Reportservice.getReportsByComplainantId(residentId);
            // Get reports where resident is complainee
            List<Report> asComplainee = Reportservice.getReportsByComplaineeId(residentId);
            
            System.out.println("Reports as Complainant: " + (asComplainant != null ? asComplainant.size() : 0));
            System.out.println("Reports as Complainee: " + (asComplainee != null ? asComplainee.size() : 0));
            
            // Track matches
            Set<Integer> complainantIds = new HashSet<>();
            Set<Integer> complaineeIds = new HashSet<>();
            
            // Add reports where resident is complainant
            if (asComplainant != null) {
                for (Report r : asComplainant) {
                    reportById.put(r.getId(), r);
                    complainantIds.add(r.getId());
                    System.out.println("✓ Resident is COMPLAINANT in Report ID: " + r.getId() + " - " + r.getTitle());
                }
            }
            
            // Add reports where resident is complainee
            if (asComplainee != null) {
                for (Report r : asComplainee) {
                    reportById.put(r.getId(), r);
                    complaineeIds.add(r.getId());
                    System.out.println("✓ Resident is COMPLAINEE in Report ID: " + r.getId() + " - " + r.getTitle());
                }
            }
            
            System.out.println("Total unique reports found: " + reportById.size());
            
            // ── Populate table ────────────────────────────────────────────────
            int cCount = 0, rCount = 0, pCount = 0, resCount = 0;
            
            for (Map.Entry<Integer, Report> entry : reportById.entrySet()) {
                int id = entry.getKey();
                Report report = entry.getValue();
                
                boolean isC = complainantIds.contains(id);
                boolean isR = complaineeIds.contains(id);
                
                String role;
                if (isC && isR) {
                    role = "Both";
                    cCount++;
                    rCount++;
                } else if (isC) {
                    role = "Complainant";
                    cCount++;
                } else if (isR) {
                    role = "Respondent";
                    rCount++;
                } else {
                    role = "—";
                }
                
                String status = report.getStatus() != null ? report.getStatus() : "Pending";
                if ("Pending".equalsIgnoreCase(status)) {
                    pCount++;
                } else if ("Settled".equalsIgnoreCase(status) || "Resolved".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
                    resCount++;
                }
                
                String title = report.getTitle() != null && !report.getTitle().isEmpty() ? report.getTitle() : report.getDescription();
                if (title.length() > 50) title = title.substring(0, 47) + "...";
                
                blotterModel.addRow(new Object[]{
                    id,
                    formatDate(report.getCreatedDate()),
                    formatDate(report.getIncidentDate()),
                    title,
                    status,
                    role
                });
            }
            
            // Update stat chips
            int total = reportById.size();
            lbTotal.setText("Total " + total);
            lbComplainant.setText("Complainant " + cCount);
            lbRespondent.setText("Respondent " + rCount);
            lbPending.setText("Pending " + pCount);
            lbResolved.setText("Resolved " + resCount);
            
            // Debug label
            debugLabel.setText("<html>Searched: \"" + residentName + "\" (ID: " + residentId + ")  →  " + total + " record(s) found<br>" +
                              "(Complainant: " + cCount + ", Respondent: " + rCount + 
                              ", Pending: " + pCount + ", Resolved: " + resCount + ")</html>");
            
            if (total == 0) {
                blotterModel.setRowCount(0);
                blotterModel.addRow(new Object[]{
                    null, "No report records found for this resident.", "", "", "", ""
                });
                blotterTable.getColumnModel().getColumn(0).setMaxWidth(0);
                blotterTable.getColumnModel().getColumn(0).setMinWidth(0);
                blotterTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            } else {
                blotterTable.getColumnModel().getColumn(0).setMaxWidth(52);
                blotterTable.getColumnModel().getColumn(0).setMinWidth(42);
                blotterTable.getColumnModel().getColumn(0).setPreferredWidth(42);
            }
            
            System.out.println("========== SEARCH COMPLETE ==========\n");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            debugLabel.setText("⚠ Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error loading report data:\n" + ex.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  OPEN DETAIL DIALOG
    // ══════════════════════════════════════════════════════════════════════════
    private void openReportDetails() {
        int selectedRow = blotterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a case from the table first.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object idObj = blotterModel.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Integer)) return;

        int reportId = (Integer) idObj;
        Report report = reportById.get(reportId);
        if (report == null) {
            try { 
                report = Reportservice.getReportById(reportId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (report != null) showReportDetails(report);
        else JOptionPane.showMessageDialog(this,
            "Could not find case #" + reportId, "Not Found", JOptionPane.WARNING_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORT DETAIL DIALOG
    // ══════════════════════════════════════════════════════════════════════════
    private void showReportDetails(Report report) {
        JDialog dlg = new JDialog(this, "Case #" + report.getId(), true);
        dlg.setSize(580, 580);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Mini header
        JPanel mini = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setPaint(new GradientPaint(0,0,GREEN,getWidth(),0,GREEN_DARK));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(0,0,0,20)); g2.setStroke(new BasicStroke(2));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.dispose();
            }
        };
        mini.setOpaque(false);
        mini.setPreferredSize(new Dimension(580,68));
        mini.setBorder(new EmptyBorder(16,22,12,22));

        String titleText = report.getTitle() != null && !report.getTitle().isEmpty() 
            ? report.getTitle() : "Case #" + report.getId();
        JLabel ttl = new JLabel("CASE #"+report.getId()+"  ·  "+titleText);
        ttl.setFont(new Font("Segoe UI",Font.BOLD,15));
        ttl.setForeground(WHITE);
        JLabel statusPill = createWhitePill(report.getStatus() != null ? report.getStatus() : "Pending");
        mini.add(ttl, BorderLayout.WEST);
        mini.add(statusPill, BorderLayout.EAST);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20,24,20,24));

        // Role badge
        boolean isComplainant = (report.getComplainantId() == residentId);
        boolean isComplainee = (report.getComplaineeId() == residentId);
        String role = isComplainant && isComplainee ? "COMPLAINANT & RESPONDENT" 
                    : isComplainant ? "COMPLAINANT" 
                    : isComplainee ? "RESPONDENT" : "";

        if (!role.isEmpty()) {
            JPanel rp = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
            rp.setOpaque(false); rp.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel badge = new JLabel("⚑  "+role+"  in this case");
            badge.setFont(new Font("Segoe UI",Font.BOLD,11));
            badge.setForeground(isComplainant ? AMBER : ROSE);
            badge.setBackground(isComplainant ? AMBER_BG : ROSE_BG);
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(6,14,6,14));
            rp.add(badge);
            body.add(rp);
            body.add(Box.createRigidArea(new Dimension(0,16)));
        }

        // Get complainant and complainee names
        String complainantName = "ID #" + report.getComplainantId();
        String complaineeName = "ID #" + report.getComplaineeId();
        
        try {
            if (report.getComplainantId() > 0) {
                DatabaseManager.Resident complainant = DatabaseManager.getResidentById(report.getComplainantId());
                if (complainant != null) complainantName = complainant.getName();
            }
            if (report.getComplaineeId() > 0) {
                DatabaseManager.Resident complainee = DatabaseManager.getResidentById(report.getComplaineeId());
                if (complainee != null) complaineeName = complainee.getName();
            }
        } catch (Exception e) {
            // Use IDs if names can't be fetched
        }

        // Info grid
        JPanel grid = new JPanel(new GridLayout(0,2,16,10));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        addDialogField(grid, "Complainant",   complainantName);
        addDialogField(grid, "Respondent",    complaineeName);
        addDialogField(grid, "Title",         report.getTitle() != null ? report.getTitle() : "—");
        addDialogField(grid, "Status",        report.getStatus() != null ? report.getStatus() : "Pending");
        addDialogField(grid, "Date Filed",    formatDate(report.getCreatedDate()));
        addDialogField(grid, "Incident Date", formatDate(report.getIncidentDate()));
        body.add(grid);
        body.add(Box.createRigidArea(new Dimension(0,18)));

        // Description
        JLabel descCap = new JLabel("INCIDENT DESCRIPTION / REPORT");
        descCap.setFont(F_CAP); descCap.setForeground(TEXT_MUTED);
        descCap.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(descCap);
        body.add(Box.createRigidArea(new Dimension(0,8)));

        String descText = (report.getDescription() != null && !report.getDescription().isBlank())
            ? report.getDescription() : "(No description recorded for this case.)";
        JTextArea desc = new JTextArea(descText);
        desc.setFont(new Font("Segoe UI",Font.PLAIN,12));
        desc.setEditable(false); desc.setLineWrap(true); desc.setWrapStyleWord(true);
        desc.setBackground(new Color(248,250,252)); desc.setForeground(TEXT_BODY);
        desc.setBorder(new EmptyBorder(12,16,12,16));

        JScrollPane ds = new JScrollPane(desc);
        ds.setBorder(BorderFactory.createLineBorder(BORDER));
        ds.setPreferredSize(new Dimension(Integer.MAX_VALUE,130));
        ds.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));
        ds.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleScrollBar(ds.getVerticalScrollBar());
        body.add(ds);
        body.add(Box.createRigidArea(new Dimension(0,16)));

        // Settlement banner
        if (report.getStatus() != null && 
            (report.getStatus().equalsIgnoreCase("Settled") || 
             report.getStatus().equalsIgnoreCase("Resolved") || 
             report.getStatus().equalsIgnoreCase("Closed"))) {
            
            JLabel sc = new JLabel("SETTLEMENT / RESOLUTION");
            sc.setFont(F_CAP); sc.setForeground(TEXT_MUTED);
            sc.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(sc); body.add(Box.createRigidArea(new Dimension(0,8)));

            String settlementText = report.getSettlementDescription() != null && !report.getSettlementDescription().isEmpty()
                ? report.getSettlementDescription() : "This case has been marked as " + report.getStatus() + ".";
            JTextArea settlement = new JTextArea(settlementText);
            settlement.setFont(new Font("Segoe UI",Font.PLAIN,12));
            settlement.setEditable(false); settlement.setLineWrap(true); settlement.setWrapStyleWord(true);
            settlement.setBackground(new Color(212,245,242)); settlement.setForeground(TEAL);
            settlement.setBorder(new EmptyBorder(12,16,12,16));

            JScrollPane ss = new JScrollPane(settlement);
            ss.setBorder(BorderFactory.createLineBorder(new Color(13,148,136,60)));
            ss.setMaximumSize(new Dimension(Integer.MAX_VALUE,72));
            ss.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(ss);
        }

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(null);
        bodyScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        bodyScroll.getViewport().setBackground(BG);
        styleScrollBar(bodyScroll.getVerticalScrollBar());

        JButton closeBtn = createGreenButton("Close");
        closeBtn.addActionListener(e -> dlg.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,12));
        btnPanel.setBackground(WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        btnPanel.add(closeBtn);

        root.add(mini, BorderLayout.NORTH);
        root.add(bodyScroll, BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private static class AddressComponents {
        String houseNumber,street,purok,fullAddress;
        AddressComponents(String h,String s,String p,String f){houseNumber=h;street=s;purok=p;fullAddress=f;}
    }

    private AddressComponents parseFullAddress(String address, String purok) {
        String h="—",s="—",f;
        if (address==null||address.equals("—")||address.isBlank()) { f="Not specified"; }
        else {
            f=address;
            String[] parts=address.split("\\s+",2);
            if (parts.length==2&&(parts[0].matches("^[0-9]+.*$")||
                parts[0].matches("(?i)^(blk|block|lot|phase|unit|#).*$"))) {
                h=parts[0]; s=parts[1];
            } else { s=address; }
        }
        String pv=(purok==null||purok.equals("—")||purok.isBlank())?"Not specified":purok;
        return new AddressComponents(h,s,pv,f);
    }

    private String getPwdStatus(ResidentRow r) {
        return (r.pwd!=null&&r.pwd.equalsIgnoreCase("Yes"))?"Yes":"No";
    }

    private String blank(String s) {
        return (s==null||s.isBlank()||s.equals("—"))?"—":s;
    }

    private JPanel createLightCard(Color accent, String caption) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,8)); g2.fillRoundRect(2,3,getWidth()-2,getHeight()-2,12,12);
                g2.setColor(CARD); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(accent); g2.fillRoundRect(0,0,4,getHeight()-1,4,4);
                g2.setColor(BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16,20,16,16));

        JLabel cap=new JLabel(caption);
        cap.setFont(F_CAP); cap.setForeground(accent);
        cap.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cap);
        card.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel line=new JPanel();
        line.setBackground(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),30));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(line);
        card.add(Box.createRigidArea(new Dimension(0,10)));
        return card;
    }

    private void addField(JPanel card, String label, String value) {
        JLabel lbl=new JLabel(label.toUpperCase());
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel val=new JLabel(value==null||value.isBlank()?"—":value);
        val.setFont(F_VALUE); val.setForeground(TEXT_HEAD);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl); card.add(Box.createRigidArea(new Dimension(0,2)));
        card.add(val); card.add(Box.createRigidArea(new Dimension(0,10)));
    }

    private void addDialogField(JPanel grid, String label, String value) {
        JPanel cell=new JPanel();
        cell.setLayout(new BoxLayout(cell,BoxLayout.Y_AXIS));
        cell.setOpaque(false); cell.setBorder(new EmptyBorder(4,0,8,8));
        JLabel lbl=new JLabel(label.toUpperCase());
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_MUTED);
        JLabel val=new JLabel(value==null||value.isBlank()?"—":value);
        val.setFont(F_VALUE); val.setForeground(TEXT_HEAD);
        cell.add(lbl); cell.add(Box.createRigidArea(new Dimension(0,2))); cell.add(val);
        grid.add(cell);
    }

    private JLabel createStatChip(String text, Color fg, Color bg) {
        JLabel chip=new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        chip.setFont(F_BADGE); chip.setForeground(fg);
        chip.setBorder(new EmptyBorder(4,10,4,10)); chip.setOpaque(false);
        return chip;
    }

    private JLabel createWhitePill(String text) {
        JLabel pill=new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        pill.setFont(F_BADGE); pill.setForeground(WHITE);
        pill.setBorder(new EmptyBorder(4,10,4,10)); pill.setOpaque(false);
        return pill;
    }

    private JButton createGreenButton(String text) {
        JButton btn=new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?GREEN_DARK:GREEN);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(WHITE);
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9,22,9,22));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn=new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(34,139,34,10)); g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                }
                g2.setColor(BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BADGE); btn.setForeground(TEXT_MUTED);
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5,12,5,12));
        return btn;
    }

    private void styleBlotterTable(JTable t) {
        t.setFont(F_TABLE); t.setRowHeight(34);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
        t.setBackground(WHITE); t.setForeground(TEXT_BODY);
        t.setSelectionBackground(GREEN_LIGHT); t.setSelectionForeground(TEXT_HEAD);
        t.setFillsViewportHeight(true);

        JTableHeader header=t.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,11));
        header.setBackground(new Color(248,250,252)); header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(header.getWidth(),36));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl,Object val,boolean sel,boolean focus,int row,int col) {
                super.getTableCellRendererComponent(tbl,val,sel,focus,row,col);
                setBorder(new EmptyBorder(0,10,0,10)); setFont(F_TABLE);
                if (!sel) {
                    setBackground(row%2==0?WHITE:new Color(248,250,252));
                    setForeground(TEXT_BODY);
                }
                if (col==4&&val!=null) {
                    String s=val.toString();
                    if ("Pending".equalsIgnoreCase(s)) {setForeground(AMBER);setFont(F_BADGE);}
                    else if ("Settled".equalsIgnoreCase(s)||"Resolved".equalsIgnoreCase(s)||"Closed".equalsIgnoreCase(s)) {
                        setForeground(TEAL); setFont(F_BADGE);
                    }
                }
                if (col==5&&val!=null) {
                    String s=val.toString();
                    if ("Complainant".equalsIgnoreCase(s)) {setForeground(AMBER);setFont(F_BADGE);}
                    else if ("Respondent".equalsIgnoreCase(s)) {setForeground(ROSE); setFont(F_BADGE);}
                    else if ("Both".equalsIgnoreCase(s)) {setForeground(SKY); setFont(F_BADGE);}
                }
                return this;
            }
        });
    }

    private void styleScrollBar(JScrollBar sb) {
        sb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor=new Color(34,139,34,90); trackColor=BG;
            }
            @Override protected JButton createDecreaseButton(int o){return zeroBtn();}
            @Override protected JButton createIncreaseButton(int o){return zeroBtn();}
            private JButton zeroBtn(){
                JButton b=new JButton();
                b.setPreferredSize(new Dimension(0,0));
                b.setMinimumSize(new Dimension(0,0));
                b.setMaximumSize(new Dimension(0,0));
                return b;
            }
        });
    }

    private String nvl(String s){return (s==null||s.isBlank())?"N/A":s;}

    private String formatDate(String date) {
        if (date==null||date.isEmpty()||date.equals("N/A")) return "—";
        try {
            String[] p=date.split("-");
            if (p.length==3) return p[1]+"/"+p[2]+"/"+p[0];
        } catch (Exception ignored){}
        return date;
    }
}