package UI.dialogs;

import database.ResidentDAO;
import UI.components.ResidenceTable;
import UI.panels.HomePanel;

import javax.swing.*;

/**
 * Confirmation dialog for deleting a resident.
 * Deletes by primary key (id) — not by name — to avoid matching the wrong record.
 */
public class DeleteResidentDialog {

    public DeleteResidentDialog(int residentId, String residentName,
                                ResidenceTable table, HomePanel homePanel) {

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete resident:\n\"" + residentName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ResidentDAO.deleteResident(residentId);
            table.refresh();
            if (homePanel != null) homePanel.refreshStatistics();
        }
    }
}