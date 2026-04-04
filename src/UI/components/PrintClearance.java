package UI.components;

import service.CertificateService;

import javax.swing.*;

/**
 * PrintClearance — Handles printing of Barangay Clearance
 */
public class PrintClearance {

    /**
     * Print a Barangay Clearance for the given resident ID.
     * Prompts user for the purpose before generating.
     */
    public static void print(int residentId) {
        // Prompt for purpose
        String purpose = JOptionPane.showInputDialog(
            null,
            "Enter the purpose for this Barangay Clearance:",
            "Clearance Purpose",
            JOptionPane.QUESTION_MESSAGE
        );

        // User cancelled
        if (purpose == null) {
            return;
        }

        // Default purpose if left blank
        if (purpose.trim().isEmpty()) {
            purpose = "whatever legal purpose it may serve";
        }

        try {
            // Generate and open certificate
            CertificateService.generateClearance(residentId, purpose.trim());
            
            JOptionPane.showMessageDialog(
                null,
                "Barangay Clearance generated successfully!\nOpening in your browser...",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to generate clearance:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}