package UI.dialogs;

import database.ResidentDAO;
import database.ResidentDAO.ResidentRow;

import javax.swing.*;
import java.awt.*;

public class ViewResidentDialog extends JDialog {

    public ViewResidentDialog(String name) {
        setTitle("View Resident");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridLayout(6, 2, 5, 5));

        ResidentRow r = ResidentDAO.getResidentByName(name);

        add(new JLabel("Name:"));
        add(new JLabel(r.name));

        add(new JLabel("Age:"));
        add(new JLabel(String.valueOf(r.age)));

        add(new JLabel("Sex:"));
        add(new JLabel(r.sex));

        add(new JLabel("Address:"));
        add(new JLabel(r.address));

        add(new JLabel("Purok:"));
        add(new JLabel(r.purok));

        add(new JLabel("Status:"));
        add(new JLabel(r.status));

        setVisible(true);
    }
}