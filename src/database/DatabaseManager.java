package database;

import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static Connection connection;
    private static final String DB_PATH = "data/barangay.db";

    // Initialize database connection
    public static void connect() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    System.out.println("Data directory created: " + dataDir.getAbsolutePath());
                }
            }

            // Explicitly register the SQLite JDBC driver
            try {
                Class.forName("org.sqlite.JDBC");
                System.out.println("SQLite JDBC Driver registered successfully");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC Driver not found in classpath!");
                e.printStackTrace();
                return;
            }

            // Connect to database
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            System.out.println("Database connected successfully!");
            System.out.println("Database location: " + new File(DB_PATH).getAbsolutePath());

            // Create tables
            createTables();

            // Migrate existing database if needed (add new columns)
            migrateDatabase();

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create necessary tables
    private static void createTables() throws SQLException {
        String createResidentsTable = """
            CREATE TABLE IF NOT EXISTS residents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                sex TEXT,
                address TEXT,
                purok TEXT,
                contact TEXT,
                birthdate TEXT,
                civil_status TEXT,
                registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createOfficialsTable = """
            CREATE TABLE IF NOT EXISTS officials (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                position TEXT,
                term_start TEXT,
                term_end TEXT,
                contact TEXT,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createBlottersTable = """
            CREATE TABLE IF NOT EXISTS blotters (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                complainant TEXT NOT NULL,
                respondent TEXT NOT NULL,
                incident_type TEXT,
                description TEXT,
                date_incident TEXT,
                status TEXT DEFAULT 'Pending',
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createResidentsTable);
            stmt.execute(createOfficialsTable);
            stmt.execute(createBlottersTable);
            System.out.println("Database tables created/verified");
        }
    }

    // Migrate existing database to add new columns if they don't exist
    private static void migrateDatabase() {
        try {
            // Check if sex column exists, if not add it
            if (!columnExists("residents", "sex")) {
                addColumn("residents", "sex", "TEXT");
                System.out.println("Added 'sex' column to residents table");
            }
            
            // Check if purok column exists, if not add it
            if (!columnExists("residents", "purok")) {
                addColumn("residents", "purok", "TEXT");
                System.out.println("Added 'purok' column to residents table");
            }
        } catch (SQLException e) {
            System.err.println("Error migrating database: " + e.getMessage());
        }
    }

    // Helper method to check if a column exists
    private static boolean columnExists(String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if (rs.getString("name").equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper method to add a column to a table
    private static void addColumn(String tableName, String columnName, String columnType) throws SQLException {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Get connection
    public static Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    // Check if connection is valid
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Close connection
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    // ========== STATISTICS METHODS ==========

    // Get total population
    public static int getTotalPopulation() {
        String sql = "SELECT COUNT(*) FROM residents";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting total population: " + e.getMessage());
            return 0;
        }
    }

    // Get male count
    public static int getMaleCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE sex = 'Male' OR sex = 'MALE' OR sex = 'male'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting male count: " + e.getMessage());
            return 0;
        }
    }

    // Get female count
    public static int getFemaleCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE sex = 'Female' OR sex = 'FEMALE' OR sex = 'female'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting female count: " + e.getMessage());
            return 0;
        }
    }

    // Get senior count (age 60 and above)
    public static int getSeniorCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE birthdate <= date('now', '-60 years') AND birthdate != '' AND birthdate IS NOT NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting senior count: " + e.getMessage());
            return 0;
        }
    }

    // Get voter count (age 18 and above)
    public static int getVoterCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE birthdate <= date('now', '-18 years') AND birthdate != '' AND birthdate IS NOT NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting voter count: " + e.getMessage());
            return 0;
        }
    }

    // Get household count (group by address - unique addresses)
    public static int getHouseholdCount() {
        String sql = "SELECT COUNT(DISTINCT address) FROM residents WHERE address != '' AND address IS NOT NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int count = rs.getInt(1);
            return count > 0 ? count : getTotalPopulation();
        } catch (SQLException e) {
            System.err.println("Error getting household count: " + e.getMessage());
            return getTotalPopulation();
        }
    }

    // Get blotter cases count
    public static int getBlotterCount() {
        String sql = "SELECT COUNT(*) FROM blotters";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting blotter count: " + e.getMessage());
            return 0;
        }
    }

    // Get all statistics in one call (more efficient)
    public static class Statistics {
        public int totalPopulation;
        public int maleCount;
        public int femaleCount;
        public int seniorCount;
        public int voterCount;
        public int householdCount;
        public int blotterCount;
        public int pwdCount;

        @Override
        public String toString() {
            return String.format("Population: %d, Male: %d, Female: %d, Senior: %d, Voters: %d, Households: %d, Blotters: %d",
                    totalPopulation, maleCount, femaleCount, seniorCount, voterCount, householdCount, blotterCount);
        }
    }

    // Get all statistics in one database query
    public static Statistics getAllStatistics() {
        Statistics stats = new Statistics();

        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM residents) as total_population,
                (SELECT COUNT(*) FROM residents WHERE sex = 'Male' OR sex = 'MALE' OR sex = 'male') as male_count,
                (SELECT COUNT(*) FROM residents WHERE sex = 'Female' OR sex = 'FEMALE' OR sex = 'female') as female_count,
                (SELECT COUNT(*) FROM residents WHERE birthdate <= date('now', '-60 years') AND birthdate != '' AND birthdate IS NOT NULL) as senior_count,
                (SELECT COUNT(*) FROM residents WHERE birthdate <= date('now', '-18 years') AND birthdate != '' AND birthdate IS NOT NULL) as voter_count,
                (SELECT COUNT(DISTINCT address) FROM residents WHERE address != '' AND address IS NOT NULL) as household_count,
                (SELECT COUNT(*) FROM blotters) as blotter_count
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.totalPopulation = rs.getInt("total_population");
                stats.maleCount = rs.getInt("male_count");
                stats.femaleCount = rs.getInt("female_count");
                stats.seniorCount = rs.getInt("senior_count");
                stats.voterCount = rs.getInt("voter_count");
                stats.householdCount = rs.getInt("household_count");
                stats.blotterCount = rs.getInt("blotter_count");

                if (stats.householdCount == 0) {
                    stats.householdCount = stats.totalPopulation;
                }

                stats.pwdCount = 0;
            }

        } catch (SQLException e) {
            System.err.println("Error getting all statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // ========== RESIDENTS CRUD OPERATIONS ==========

    // Updated addResident method with sex and purok
    public static void addResident(String name, String sex, String address, 
                                   String purok, String contact, String birthdate, 
                                   String civilStatus) throws SQLException {
        String sql = "INSERT INTO residents (name, sex, address, purok, contact, birthdate, civil_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, sex);
            pstmt.setString(3, address);
            pstmt.setString(4, purok);
            pstmt.setString(5, contact);
            pstmt.setString(6, birthdate);
            pstmt.setString(7, civilStatus);
            pstmt.executeUpdate();
        }
    }

    // Overloaded method for backward compatibility (if needed)
    public static void addResident(String name, String address, String contact,
                                   String birthdate, String civilStatus) throws SQLException {
        addResident(name, "", address, "", contact, birthdate, civilStatus);
    }

    public static ResultSet getAllResidents() throws SQLException {
        String sql = "SELECT * FROM residents ORDER BY id DESC";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public static ResultSet getResidentById(int id) throws SQLException {
        String sql = "SELECT * FROM residents WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        return pstmt.executeQuery(sql);
    }

    // Updated updateResident method with sex and purok
    public static void updateResident(int id, String name, String sex, String address,
                                      String purok, String contact, String birthdate, 
                                      String civilStatus) throws SQLException {
        String sql = "UPDATE residents SET name=?, sex=?, address=?, purok=?, contact=?, birthdate=?, civil_status=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, sex);
            pstmt.setString(3, address);
            pstmt.setString(4, purok);
            pstmt.setString(5, contact);
            pstmt.setString(6, birthdate);
            pstmt.setString(7, civilStatus);
            pstmt.setInt(8, id);
            pstmt.executeUpdate();
        }
    }

    // Overloaded method for backward compatibility
    public static void updateResident(int id, String name, String address,
                                      String contact, String birthdate, String civilStatus) throws SQLException {
        updateResident(id, name, "", address, "", contact, birthdate, civilStatus);
    }

    public static void deleteResident(int id) throws SQLException {
        String sql = "DELETE FROM residents WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet searchResidents(String keyword) throws SQLException {
        String sql = "SELECT * FROM residents WHERE name LIKE ? OR address LIKE ? OR purok LIKE ? ORDER BY id DESC";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");
        return pstmt.executeQuery(sql);
    }

    public static int getResidentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM residents";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        }
    }

    // ========== OFFICIALS CRUD OPERATIONS ==========

    public static void addOfficial(String name, String position, String termStart,
                                   String termEnd, String contact) throws SQLException {
        String sql = "INSERT INTO officials (name, position, term_start, term_end, contact) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setString(3, termStart);
            pstmt.setString(4, termEnd);
            pstmt.setString(5, contact);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet getAllOfficials() throws SQLException {
        String sql = "SELECT * FROM officials ORDER BY position";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public static void updateOfficial(int id, String name, String position,
                                      String termStart, String termEnd, String contact) throws SQLException {
        String sql = "UPDATE officials SET name=?, position=?, term_start=?, term_end=?, contact=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setString(3, termStart);
            pstmt.setString(4, termEnd);
            pstmt.setString(5, contact);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        }
    }

    public static void deleteOfficial(int id) throws SQLException {
        String sql = "DELETE FROM officials WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ========== BLOTTERS CRUD OPERATIONS ==========

    public static void addBlotter(String complainant, String respondent,
                                  String incidentType, String description,
                                  String dateIncident, String status) throws SQLException {
        String sql = "INSERT INTO blotters (complainant, respondent, incident_type, description, date_incident, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, complainant);
            pstmt.setString(2, respondent);
            pstmt.setString(3, incidentType);
            pstmt.setString(4, description);
            pstmt.setString(5, dateIncident);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet getAllBlotters() throws SQLException {
        String sql = "SELECT * FROM blotters ORDER BY id DESC";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public static void updateBlotterStatus(int id, String status) throws SQLException {
        String sql = "UPDATE blotters SET status=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    public static void deleteBlotter(int id) throws SQLException {
        String sql = "DELETE FROM blotters WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}