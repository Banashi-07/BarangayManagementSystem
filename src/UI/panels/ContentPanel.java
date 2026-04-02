package UI.panels;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {

    private CardLayout cardLayout;
    
    public ContentPanel(HomePanel homePanel) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // ✅ USE THE SAME INSTANCE
        add(homePanel, "HOME");

        add(new RecordsPanel(homePanel), "RECORDS");
        add(new OfficialListPanel(), "OFFICIAL LIST");
        add(new ReportsPanel(), "REPORTS");
    }

    public void showPage(String name) {
        cardLayout.show(this, name);
    }
}