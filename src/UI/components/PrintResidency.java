package UI.components;

import service.CertificateService;

import javax.swing.*;

/**
 * PrintResidency — Handles printing of Certificate of Residency
 */
public class PrintResidency {

    /**
     * Print a Certificate of Residency for the given resident ID.
     * Prompts user for the purpose before generating.
     */
    public static void print(int residentId) {
        // Prompt for purpose
        String purpose = JOptionPane.showInputDialog(
            null,
            "Enter the purpose for this Certificate of Residency:",
            "Certificate Purpose",
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
            CertificateService.generateResidency(residentId, purpose.trim());
            
            JOptionPane.showMessageDialog(
                null,
                "Certificate of Residency generated successfully!\nOpening in your browser...",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to generate certificate:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}