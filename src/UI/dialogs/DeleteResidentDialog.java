package UI.dialogs;

import database.ResidentDAO;

import javax.swing.*;

import UI.components.ResidenceTable;
import UI.panels.HomePanel;

public class DeleteResidentDialog {

	private HomePanel homePanel;
	
    public DeleteResidentDialog(String name, ResidenceTable table,HomePanel homePanel) {
    	this.homePanel = homePanel;
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ResidentDAO.deleteResident(name);
            table.refresh();
            homePanel.refreshStatistics();
        }
    }
}