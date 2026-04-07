package service;

import database.DatabaseManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class AddResidenceService {
    
    public boolean addResidence(String name, String age, String sex, 
                                String address, String purok, String status, 
                                String houseNo) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            
            String fullAddress = buildFullAddress(address, purok, houseNo);
            String birthdate = calculateBirthdateFromAge(age);
            String contact = "";
            String pwd = "No";
            
            DatabaseManager.addResident(name, sex, fullAddress, purok, contact, birthdate, status, pwd);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(null,
                    "❌ DUPLICATE RESIDENT ❌\n\n" +
                    "This resident already exists in the database!\n\n" +
                    "A resident with the same:\n" +
                    "• Name\n" +
                    "• Birthdate\n" +
                    "• Address\n\n" +
                    "cannot be added again.\n\n" +
                    "Please check your records.",
                    "Duplicate Entry",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            return false;
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean addResidenceWithPwd(String name, String age, String sex, 
                                       String address, String purok, String status, 
                                       String houseNo, boolean isPwd) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            
            String fullAddress = buildFullAddress(address, purok, houseNo);
            String birthdate = calculateBirthdateFromAge(age);
            String contact = "";
            String pwd = isPwd ? "Yes" : "No";
            
            DatabaseManager.addResident(name, sex, fullAddress, purok, contact, birthdate, status, pwd);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(null,
                    "❌ DUPLICATE RESIDENT ❌\n\n" +
                    "This resident already exists in the database!\n\n" +
                    "A resident with the same:\n" +
                    "• Name\n" +
                    "• Birthdate\n" +
                    "• Address\n\n" +
                    "cannot be added again.\n\n" +
                    "Please check your records.",
                    "Duplicate Entry",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            return false;
            
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private String buildFullAddress(String address, String purok, String houseNo) {
        StringBuilder fullAddress = new StringBuilder();
        if (houseNo != null && !houseNo.trim().isEmpty()) {
            fullAddress.append("House ").append(houseNo.trim());
        }
        if (address != null && !address.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(address.trim());
        }
        if (purok != null && !purok.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append("Purok ").append(purok.trim());
        }
        if (fullAddress.length() == 0) {
            fullAddress.append("San Miguel, Agoo, La Union");
        }
        return fullAddress.toString();
    }
    
    private String calculateBirthdateFromAge(String ageStr) {
        try {
            int age = Integer.parseInt(ageStr);
            return java.time.LocalDate.now().minusYears(age).toString();
        } catch (NumberFormatException e) {
            return "2000-01-01";
        }
    }
    
    public ValidationResult validateResidence(String name, String age, String sex,
                                              String address, String purok, 
                                              String status, String houseNo) {
        ValidationResult result = new ValidationResult();
        
        if (name == null || name.trim().isEmpty()) {
            result.addError("Name is required");
        } else if (name.trim().length() < 2) {
            result.addError("Name must be at least 2 characters long");
        }
        
        if (age == null || age.trim().isEmpty()) {
            result.addError("Age is required");
        } else {
            try {
                int ageInt = Integer.parseInt(age.trim());
                if (ageInt < 0 || ageInt > 150) {
                    result.addError("Age must be between 0 and 150");
                }
            } catch (NumberFormatException e) {
                result.addError("Age must be a valid number");
            }
        }
        
        if (sex == null || sex.trim().isEmpty()) {
            result.addError("Sex is required");
        } else if (!sex.equalsIgnoreCase("Male") && !sex.equalsIgnoreCase("Female")) {
            result.addError("Sex must be either 'Male' or 'Female'");
        }
        
        if (address == null || address.trim().isEmpty()) {
            result.addError("Address is required");
        }
        
        if (status == null || status.trim().isEmpty()) {
            result.addError("Civil status is required");
        }
        
        return result;
    }
    
    public static class ValidationResult {
        private StringBuilder errors = new StringBuilder();
        private boolean isValid = true;
        
        public void addError(String error) {
            if (errors.length() > 0) errors.append("\n");
            errors.append("• ").append(error);
            isValid = false;
        }
        
        public boolean isValid() { return isValid; }
        public String getErrorMessage() { return errors.toString(); }
    }
}