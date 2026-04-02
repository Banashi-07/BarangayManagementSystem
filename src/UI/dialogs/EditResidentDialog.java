package UI.dialogs;

import database.ResidentDAO;

import javax.swing.*;

import UI.components.ResidenceTable;

import java.awt.*;

public class EditResidentDialog extends JDialog {

	public EditResidentDialog(String name, JTable tableToRefresh) {
	    setTitle("Edit Resident");
	    setSize(400, 350);
	    setLocationRelativeTo(null);
	    setModal(true);
	    setLayout(new GridLayout(7, 2, 5, 5));

	    ResidentDAO.ResidentRow resident = ResidentDAO.getResidentByName(name);

	    JTextField nameField = new JTextField(resident.name);
	    JTextField ageField = new JTextField(String.valueOf(resident.age));
	    JTextField sexField = new JTextField(resident.sex);
	    JTextField addressField = new JTextField(resident.address);
	    JTextField purokField = new JTextField(resident.purok);
	    JTextField statusField = new JTextField(resident.status);

	    add(new JLabel("Name:"));
	    add(nameField);
	    add(new JLabel("Age:"));
	    add(ageField);
	    add(new JLabel("Sex:"));
	    add(sexField);
	    add(new JLabel("Address:"));
	    add(addressField);
	    add(new JLabel("Purok:"));
	    add(purokField);
	    add(new JLabel("Status:"));
	    add(statusField);

	    JButton saveBtn = new JButton("Save");
	    add(saveBtn);

	    saveBtn.addActionListener(e -> {
	        ResidentDAO.updateResident(
	                nameField.getText(),
	                ageField.getText(),
	                sexField.getText(),
	                addressField.getText(),
	                purokField.getText(),
	                statusField.getText()
	        );

	        ((ResidenceTable) tableToRefresh).refresh();
	        dispose();
	    });

	    setVisible(true);
	}
}