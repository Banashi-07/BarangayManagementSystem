package database;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * ResidentDAO — Data Access Object for the residents table.
 * Handles all database queries and data transformation.
 * No UI code here.
 */
public class ResidentDAO {

    // ================= DATA MODEL =================

    /** Plain data container representing one row in ResidenceTable. */
    public static class ResidentRow {
        public final String name;
        public final int    age;
        public final String sex;
        public final String address;
        public final String purok;
        public final String status;

        public ResidentRow(String name, int age, String sex,
                           String address, String purok, String status) {
            this.name    = name;
            this.age     = age;
            this.sex     = sex;
            this.address = address;
            this.purok   = purok;
            this.status  = status;
        }
    }

    // ================= PUBLIC METHOD =================

    /**
     * Fetches all residents from SQLite and returns them as a
     * ready-to-display list of ResidentRow objects.
     */
    public static List<ResidentRow> getAllResidentRows() {
        List<ResidentRow> rows = new ArrayList<>();

        try {
            ResultSet rs = DatabaseManager.getAllResidents();
            while (rs.next()) {
                String name        = rs.getString("name");
                String birthdate   = rs.getString("birthdate");
                String address     = rs.getString("address");
                String civilStatus = rs.getString("civil_status");

                rows.add(new ResidentRow(
                        name,
                        calculateAge(birthdate),
                        parseSex(civilStatus),
                        address,
                        parsePurok(address),
                        parseStatus(civilStatus)
                ));
            }
            rs.getStatement().close();

        } catch (SQLException e) {
            System.err.println("ResidentDAO: failed to load residents — " + e.getMessage());
            e.printStackTrace();
        }

        return rows;
    }

    // ================= PRIVATE HELPERS =================

    /** Computes age from a "yyyy-MM-dd" birthdate string. */
    private static int calculateAge(String birthdate) {
        if (birthdate == null || birthdate.isBlank()) return 0;
        try {
            return Period.between(LocalDate.parse(birthdate), LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Extracts sex from civil_status.
     * Seeder format: "Married Male", "Single Female", etc.
     */
    private static String parseSex(String civilStatus) {
        if (civilStatus == null)                return "—";
        if (civilStatus.contains("Female"))     return "Female";
        if (civilStatus.contains("Male"))       return "Male";
        return "—";
    }

    /**
     * Strips the sex word to get the marital status.
     * e.g. "Married Male" → "Married"
     */
    private static String parseStatus(String civilStatus) {
        if (civilStatus == null) return "—";
        return civilStatus.replace("Male", "").replace("Female", "").trim();
    }

    /**
     * Extracts purok/barangay from the address string.
     * e.g. "123 Rizal St., Brgy. Poblacion" → "Brgy. Poblacion"
     */
    private static String parsePurok(String address) {
        if (address == null) return "—";
        int idx = address.indexOf("Brgy.");
        return idx != -1 ? address.substring(idx).trim() : "—";
    }
    
    
    public static ResidentRow getResidentByName(String nameSearch) {
        try {
            Connection conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM residents WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameSearch);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name        = rs.getString("name");
                String birthdate   = rs.getString("birthdate");
                String address     = rs.getString("address");
                String civilStatus = rs.getString("civil_status");

                return new ResidentRow(
                        name,
                        calculateAge(birthdate),
                        parseSex(civilStatus),
                        address,
                        parsePurok(address),
                        parseStatus(civilStatus)
                );
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
	    public static void updateResident(String name,String age, String sex, String address, String purok, String status) {
			try {
			Connection conn = DatabaseManager.getConnection();
			
			String civilStatus = status + " " + sex;
			
			String sql = "UPDATE residents SET address=?, civil_status=? WHERE name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, address);
			ps.setString(2, civilStatus);
			ps.setString(3, name);
			
			ps.executeUpdate();
			ps.close();
			
			} catch (SQLException e) {
			e.printStackTrace();
			}
		}
	    
	    public static void deleteResident(String name) {
	        try {
	            Connection conn = DatabaseManager.getConnection();

	            String sql = "DELETE FROM residents WHERE name=?";
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setString(1, name);

	            ps.executeUpdate();
	            ps.close();

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
    
}