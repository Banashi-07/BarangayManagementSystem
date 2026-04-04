package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseSeeder — populates the barangay database with realistic sample data.
 *
 * Usage (call once at startup, before showing the main window):
 *
 *   DatabaseManager.connect();
 *   DatabaseSeeder.seed();        // safe to call every run; skips if data exists
 *
 * To force a fresh seed (wipes all rows first):
 *
 *   DatabaseSeeder.seedForce();
 */
public class DatabaseSeeder {

    // ---------------------------------------------------------------
    // Public entry points
    // ---------------------------------------------------------------

    /**
     * Seeds sample data only when each table is empty.
     * Safe to call on every application start.
     */
    public static void seed() {
        System.out.println("=== DatabaseSeeder: checking tables... ===");
        try {
            Connection conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Seeder: no database connection.");
                return;
            }

            if (isTableEmpty("residents"))   seedResidents();
            if (isTableEmpty("officials"))   seedOfficials();
            if (isTableEmpty("blotters"))    seedBlotters();

            printSummary();
            System.out.println("=== DatabaseSeeder: done ===");

        } catch (SQLException e) {
            System.err.println("Seeder error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clears all rows then re-inserts sample data.
     * Useful during development / UI testing.
     */
    public static void seedForce() {
        System.out.println("=== DatabaseSeeder: force-reseed ===");
        try {
            Connection conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("Seeder: no database connection.");
                return;
            }

            clearAllTables();
            seedResidents();
            seedOfficials();
            seedBlotters();

            printSummary();
            System.out.println("=== DatabaseSeeder: force-reseed done ===");

        } catch (SQLException e) {
            System.err.println("Seeder error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private static boolean isTableEmpty(String table) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs  = stmt.executeQuery(sql)) {
            return rs.getInt(1) == 0;
        }
    }

    private static void clearAllTables() throws SQLException {
        try (Statement stmt = DatabaseManager.getConnection().createStatement()) {
            stmt.execute("DELETE FROM blotters");
            stmt.execute("DELETE FROM officials");
            stmt.execute("DELETE FROM residents");
            System.out.println("  All tables cleared.");
        }
    }

    // ---------------------------------------------------------------
    // Residents  (20 sample records — mix of age/sex/address)
    // ---------------------------------------------------------------
    private static void seedResidents() throws SQLException {
        System.out.println("  Seeding residents...");

        // { name, address, contact, birthdate (yyyy-MM-dd), civil_status }
        String[][] residents = {
                {"Juan dela Cruz",       "123 Rizal St., Brgy. Poblacion",   "09171234567", "1985-03-14", "Married Male"},
                {"Maria Santos",         "45 Mabini Ave., Brgy. Poblacion",  "09271234568", "1990-07-22", "Single Female"},
                {"Jose Reyes",           "78 Luna Blvd., Brgy. San Isidro",  "09371234569", "1958-11-05", "Married Male"},
                {"Ana Garcia",           "12 Aguinaldo St., Brgy. San Isidro","09481234570","2000-01-30", "Single Female"},
                {"Pedro Gonzales",       "99 Bonifacio Rd., Brgy. Poblacion", "09191234571", "1949-06-18", "Widowed Male"},
                {"Rosario Flores",       "34 Laurel St., Brgy. Sta. Cruz",   "09291234572", "1975-09-09", "Married Female"},
                {"Eduardo Mendoza",      "67 Quezon Ave., Brgy. Sta. Cruz",  "09391234573", "2005-12-01", "Single Male"},
                {"Luisa Villanueva",     "88 MacArthur Blvd., Brgy. Pag-asa","09481234574","1962-04-25", "Married Female"},
                {"Ricardo Torres",       "22 Osmena St., Brgy. Pag-asa",     "09191234575", "1995-08-17", "Single Male"},
                {"Elena Castillo",       "55 Roxas Blvd., Brgy. Pagkakaisa", "09291234576", "1938-02-28", "Widowed Female"},
                {"Fernando Ramos",       "11 Magsaysay St., Brgy. Pagkakaisa","09391234577","1980-05-13","Married Male"},
                {"Gloria Cruz",          "44 Marcos Ave., Brgy. Masagana",   "09491234578", "1972-10-07", "Separated Female"},
                {"Alfredo Navarro",      "66 Arroyo Rd., Brgy. Masagana",    "09181234579", "1953-03-30", "Married Male"},
                {"Cecilia Bautista",     "77 Estrada St., Brgy. Bagong Pag-asa","09281234580","2003-11-19","Single Female"},
                {"Manuel Santiago",      "33 Cory Blvd., Brgy. Bagong Pag-asa","09381234581","1967-06-06","Married Male"},
                {"Teresita Aquino",      "88 Enrile Ave., Brgy. San Isidro", "09481234582", "1945-08-21", "Widowed Female"},
                {"Roberto Dela Torre",   "19 Binay St., Brgy. Sta. Cruz",    "09191234583", "1988-12-25", "Single Male"},
                {"Natividad Soriano",    "27 Duterte Rd., Brgy. Pag-asa",    "09291234584", "1931-01-11", "Widowed Female"},
                {"Danilo Fernandez",     "50 Lacson St., Brgy. Masagana",    "09391234585", "1999-07-04", "Single Male"},
                {"Carmela Aguilar",      "61 Sotto Ave., Brgy. Pagkakaisa",  "09491234586", "1977-09-16", "Married Female"},
        };

        String sql = "INSERT INTO residents (name, address, contact, birthdate, civil_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            for (String[] r : residents) {
                ps.setString(1, r[0]);
                ps.setString(2, r[1]);
                ps.setString(3, r[2]);
                ps.setString(4, r[3]);
                ps.setString(5, r[4]);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            System.out.println("  Inserted " + results.length + " residents.");
        }
    }

    // ---------------------------------------------------------------
    // Officials  (9 barangay council positions)
    // ---------------------------------------------------------------
    private static void seedOfficials() throws SQLException {
        System.out.println("  Seeding officials...");

        // { name, position, term_start, term_end, contact }
        String[][] officials = {
                {"Benigno Macaraeg",  "Barangay Captain",        "2023-01-01", "2025-12-31", "09171112233"},
                {"Crisanto Dela Paz", "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09172223344"},
                {"Liwayway Buenaventura","Barangay Kagawad",     "2023-01-01", "2025-12-31", "09173334455"},
                {"Herminio Corpuz",   "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09174445566"},
                {"Milagros Cuevas",   "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09175556677"},
                {"Amando Ocampo",     "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09176667788"},
                {"Divina Tolentino",  "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09177778899"},
                {"Rogelio Macapagal", "Barangay Kagawad",        "2023-01-01", "2025-12-31", "09178889900"},
                {"Salvacion Rebuelta","SK Chairperson",          "2023-01-01", "2025-12-31", "09179990011"},
        };

        String sql = "INSERT INTO officials (name, position, term_start, term_end, contact) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            for (String[] o : officials) {
                ps.setString(1, o[0]);
                ps.setString(2, o[1]);
                ps.setString(3, o[2]);
                ps.setString(4, o[3]);
                ps.setString(5, o[4]);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            System.out.println("  Inserted " + results.length + " officials.");
        }
    }

    // ---------------------------------------------------------------
    // Blotters  (8 sample cases with varied statuses)
    // ---------------------------------------------------------------
    private static void seedBlotters() throws SQLException {
        System.out.println("  Seeding blotters...");

        // { complainant, respondent, incident_type, description, date_incident, status }
        String[][] blotters = {
                {
                        "Juan dela Cruz", "Pedro Gonzales",
                        "Physical Altercation",
                        "Complainant alleges respondent punched him during a dispute over a parking space in front of 123 Rizal St.",
                        "2025-03-10", "Pending"
                },
                {
                        "Maria Santos", "Eduardo Mendoza",
                        "Noise Complaint",
                        "Respondent allegedly plays loud music after 10 PM on weekdays, disturbing neighboring households.",
                        "2025-03-15", "Under Investigation"
                },
                {
                        "Rosario Flores", "Unknown",
                        "Theft",
                        "Complainant reports that her motorcycle helmet was stolen from her front yard while she was inside the house.",
                        "2025-03-18", "Pending"
                },
                {
                        "Ricardo Torres", "Fernando Ramos",
                        "Property Damage",
                        "Respondent allegedly cut down a mango tree on the boundary of complainant's property without permission.",
                        "2025-03-20", "Settled"
                },
                {
                        "Gloria Cruz", "Danilo Fernandez",
                        "Verbal Abuse",
                        "Complainant claims respondent hurled insulting words at her in public near the barangay hall.",
                        "2025-03-22", "Pending"
                },
                {
                        "Alfredo Navarro", "Roberto Dela Torre",
                        "Trespassing",
                        "Respondent allegedly entered complainant's farm lot and picked fruits without consent.",
                        "2025-03-25", "Under Investigation"
                },
                {
                        "Cecilia Bautista", "Manuel Santiago",
                        "Harassment",
                        "Complainant reports repeated unsolicited messages and personal visits from respondent despite being told to stop.",
                        "2025-03-28", "Pending"
                },
                {
                        "Teresita Aquino", "Carmela Aguilar",
                        "Land Dispute",
                        "Both parties claim ownership of a 200 sq m lot along Enrile Ave. Complainant presents older title documents.",
                        "2025-04-01", "Referred to Higher Authority"
                },
        };

        String sql = "INSERT INTO blotters (complainant, respondent, incident_type, description, date_incident, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            for (String[] b : blotters) {
                ps.setString(1, b[0]);
                ps.setString(2, b[1]);
                ps.setString(3, b[2]);
                ps.setString(4, b[3]);
                ps.setString(5, b[4]);
                ps.setString(6, b[5]);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            System.out.println("  Inserted " + results.length + " blotter records.");
        }
    }

    // ---------------------------------------------------------------
    // Summary
    // ---------------------------------------------------------------
    private static void printSummary() throws SQLException {
        DatabaseManager.Statistics s = DatabaseManager.getAllStatistics();
        System.out.println("--------------------------------------------");
        System.out.println("  DB Summary after seed:");
        System.out.println("  Total Residents : " + s.totalPopulation);
        System.out.println("  Male            : " + s.maleCount);
        System.out.println("  Female          : " + s.femaleCount);
        System.out.println("  Seniors (60+)   : " + s.seniorCount);
        System.out.println("  Voters  (18+)   : " + s.voterCount);
        System.out.println("  Households      : " + s.householdCount);
        System.out.println("  Blotter Cases   : " + s.blotterCount);
        System.out.println("--------------------------------------------");
    }
}