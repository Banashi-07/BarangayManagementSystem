package UI.panels;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {

    private CardLayout cardLayout;

    public ContentPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Add pages
        add(new HomePanel(), "HOME");
        add(new RecordsPanel(), "RECORDS");
        add(new OfficialListPanel(), "OFFICIAL LIST");
        add(new ReportsPanel(), "REPORTS");
    }

    public void showPage(String name) {
        cardLayout.show(this, name);
    }
}