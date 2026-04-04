package UI.components;

import service.CertificateService;

import javax.swing.*;

/**
 * PrintIndigency — Handles printing of Certificate of Indigency
 */
public class PrintIndigency {

    /**
     * Print a Certificate of Indigency for the given resident ID.
     * Prompts user for the purpose before generating.
     */
    public static void print(int residentId) {
        // Prompt for purpose
        String purpose = JOptionPane.showInputDialog(
            null,
            "Enter the purpose for this Certificate of Indigency:",
            "Certificate Purpose",
            JOptionPane.QUESTION_MESSAGE
        );

        // User cancelled
        if (purpose == null) {
            return;
        }

        // Default purpose if left blank
        if (purpose.trim().isEmpty()) {
            purpose = "financial assistance";
        }

        try {
            // Generate and open certificate
            CertificateService.generateIndigency(residentId, purpose.trim());
            
            JOptionPane.showMessageDialog(
                null,
                "Certificate of Indigency generated successfully!\nOpening in your browser...",
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