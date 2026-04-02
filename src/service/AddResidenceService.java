package service;

import database.DatabaseManager;
import java.sql.SQLException;

public class AddResidenceService {
    
    /**
     * Service class to handle adding residence operations
     */
    public AddResidenceService() {
        // Ensure database connection is established
        DatabaseManager.connect();
    }
    
    /**
     * Add a new residence to the database
     * @param name Resident's full name
     * @param age Resident's age (will be converted to birthdate)
     * @param sex Resident's sex
     * @param address Complete address
     * @param purok Purok/Zone
     * @param status Civil status
     * @param houseNo House number
     * @return true if successful, false otherwise
     */
    public boolean addResidence(String name, String age, String sex, 
		            String address, String purok, String status, 
		            String houseNo) {
		try {
		// Validate inputs
		if (name == null || name.trim().isEmpty()) {
		throw new IllegalArgumentException("Name is required");
		}
		
		// Combine address components
		String fullAddress = buildFullAddress(address, purok, houseNo);
		
		// Convert age to birthdate (approximate - you might want to add a date picker instead)
		String birthdate = calculateBirthdateFromAge(age);
		
		// Contact field - you might want to add this to your dialog
		String contact = ""; // Default empty, you can add a contact field to dialog
		
		// Add to database using the UPDATED DatabaseManager with sex and purok
		DatabaseManager.addResident(name, sex, fullAddress, purok, contact, birthdate, status);
		
		return true;
		
		} catch (SQLException e) {
		System.err.println("Database error while adding residence: " + e.getMessage());
		e.printStackTrace();
		return false;
		} catch (IllegalArgumentException e) {
		System.err.println("Validation error: " + e.getMessage());
		return false;
		}
	}
    
    /**
     * Build full address from components
     */
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
        
        return fullAddress.toString();
    }
    
    /**
     * Calculate approximate birthdate from age
     * Note: This is just an approximation. Better to use a date picker in the dialog
     */
    private String calculateBirthdateFromAge(String ageStr) {
        try {
            int age = Integer.parseInt(ageStr);
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate birthdate = now.minusYears(age);
            return birthdate.toString(); // Returns YYYY-MM-DD format for SQLite
        } catch (NumberFormatException e) {
            return ""; // Return empty string if age is invalid
        }
    }
    
    /**
     * Validate all fields before submission
     */
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
    
    /**
     * Inner class for validation results
     */
    public static class ValidationResult {
        private StringBuilder errors = new StringBuilder();
        private boolean isValid = true;
        
        public void addError(String error) {
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append("• ").append(error);
            isValid = false;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errors.toString();
        }
    }
}