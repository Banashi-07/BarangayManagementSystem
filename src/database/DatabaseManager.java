package database;

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {
    private static volatile Connection connection;
    private static final String DB_PATH = "data/barangay.db";
    private static final int BUSY_TIMEOUT = 10000;

    // ================= CONNECTION MANAGEMENT =================

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (DatabaseManager.class) {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
            }
        }
        return connection;
    }

    private static void connect() throws SQLException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found!", e);
        }

        String url = "jdbc:sqlite:" + DB_PATH;
        Properties props = new Properties();
        props.setProperty("busy_timeout", String.valueOf(BUSY_TIMEOUT));
        
        connection = DriverManager.getConnection(url, props);
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.execute("PRAGMA busy_timeout=" + BUSY_TIMEOUT);
            stmt.execute("PRAGMA foreign_keys=ON");
        }
        
        createTables();
        migrateDatabase();
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ================= TABLE CREATION =================

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
                pwd TEXT DEFAULT 'No',
                registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(name, birthdate, address)
            )
        """;

        String createReportsTable = """
            CREATE TABLE IF NOT EXISTS reports (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT NOT NULL,
                incident_date TEXT,
                status TEXT DEFAULT 'Pending',
                settlement_description TEXT,
                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                settled_date TEXT,
                complainantId INTEGER,
                complaineeId INTEGER
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createResidentsTable);
            stmt.execute(createReportsTable);
            System.out.println("Database tables created/verified");
        }
    }

    private static void migrateDatabase() throws SQLException {
        // Check if unique constraint exists by trying to create it
        if (!hasUniqueConstraint()) {
            addUniqueConstraint();
        }
        
        if (!columnExists("residents", "sex")) {
            addColumn("residents", "sex", "TEXT");
        }
        if (!columnExists("residents", "purok")) {
            addColumn("residents", "purok", "TEXT");
        }
        if (!columnExists("residents", "pwd")) {
            addColumn("residents", "pwd", "TEXT DEFAULT 'No'");
        }
        
        // Reports table migrations
        if (!columnExists("reports", "title")) {
            addColumn("reports", "title", "TEXT");
        }
        if (!columnExists("reports", "complaineeId")) {
            addColumn("reports", "complaineeId", "INTEGER");
        }
    }
    
    private static boolean hasUniqueConstraint() throws SQLException {
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name='residents'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String createSQL = rs.getString("sql");
                return createSQL != null && createSQL.contains("UNIQUE(name, birthdate, address)");
            }
        }
        return false;
    }
    
    private static void addUniqueConstraint() throws SQLException {
        System.out.println("Adding unique constraint to residents table...");
        // SQLite doesn't support adding UNIQUE constraint directly, need to recreate table
        String backupTable = """
            CREATE TABLE residents_backup AS SELECT * FROM residents
        """;
        
        String dropTable = "DROP TABLE residents";
        
        String recreateTable = """
            CREATE TABLE residents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                sex TEXT,
                address TEXT,
                purok TEXT,
                contact TEXT,
                birthdate TEXT,
                civil_status TEXT,
                pwd TEXT DEFAULT 'No',
                registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(name, birthdate, address)
            )
        """;
        
        String copyData = """
            INSERT INTO residents (id, name, sex, address, purok, contact, birthdate, civil_status, pwd, registered_date)
            SELECT id, name, sex, address, purok, contact, birthdate, civil_status, pwd, registered_date FROM residents_backup
        """;
        
        String dropBackup = "DROP TABLE residents_backup";
        
        try (Statement stmt = connection.createStatement()) {
            // Check if there are duplicates before proceeding
            stmt.execute(backupTable);
            
            // Remove duplicates if any (keep the first one, delete others)
            stmt.execute("""
                DELETE FROM residents_backup 
                WHERE id NOT IN (
                    SELECT MIN(id) 
                    FROM residents_backup 
                    GROUP BY name, birthdate, address
                )
            """);
            
            stmt.execute(dropTable);
            stmt.execute(recreateTable);
            stmt.execute(copyData);
            stmt.execute(dropBackup);
            System.out.println("Unique constraint added successfully");
        }
    }

    private static boolean columnExists(String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if (rs.getString("name").equals(columnName)) return true;
            }
        }
        return false;
    }

    private static void addColumn(String tableName, String columnName, String columnType) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        }
    }

    // ================= HELPER =================

    private static <T> T executeQuery(DatabaseOperation<T> operation) throws SQLException {
        int attempts = 0;
        while (attempts < 3) {
            try {
                return operation.execute(getConnection());
            } catch (SQLException e) {
                attempts++;
                if (attempts >= 3 || (!e.getMessage().contains("locked") && !e.getMessage().contains("BUSY"))) {
                    throw e;
                }
                try {
                    Thread.sleep(1000 * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Interrupted while waiting for database lock", ie);
                }
            }
        }
        throw new SQLException("Max retries exceeded");
    }

    // ================= RESIDENT MODEL =================

    public static class Resident {
        private int id;
        private String name;
        private String sex;
        private String address;
        private String purok;
        private String contact;
        private String birthdate;
        private String civilStatus;
        private String pwd;
        private String registeredDate;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSex() { return sex; }
        public void setSex(String sex) { this.sex = sex; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPurok() { return purok; }
        public void setPurok(String purok) { this.purok = purok; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getBirthdate() { return birthdate; }
        public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
        public String getCivilStatus() { return civilStatus; }
        public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
        public String getPwd() { return pwd; }
        public void setPwd(String pwd) { this.pwd = pwd; }
        public String getRegisteredDate() { return registeredDate; }
        public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
        public boolean isPwd() { return pwd != null && pwd.equalsIgnoreCase("Yes"); }
    }

    // ================= RESIDENT CRUD =================

    public static void addResident(String name, String sex, String address, String purok,
                                   String contact, String birthdate, String civilStatus, String pwd) throws SQLException {
        executeQuery(conn -> {
            String sql = "INSERT INTO residents (name, sex, address, purok, contact, birthdate, civil_status, pwd) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, sex);
                ps.setString(3, address);
                ps.setString(4, purok);
                ps.setString(5, contact);
                ps.setString(6, birthdate);
                ps.setString(7, civilStatus);
                ps.setString(8, pwd);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static List<Resident> getAllResidents() throws SQLException {
        return executeQuery(conn -> {
            List<Resident> list = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM residents ORDER BY id DESC")) {
                while (rs.next()) list.add(mapResident(rs));
            }
            return list;
        });
    }

    public static Resident getResidentById(int id) throws SQLException {
        return executeQuery(conn -> {
            String sql = "SELECT * FROM residents WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapResident(rs);
                }
            }
            return null;
        });
    }

    public static void updateResident(int id, String name, String sex, String address, String purok,
                                      String contact, String birthdate, String civilStatus, String pwd) throws SQLException {
        executeQuery(conn -> {
            String sql = "UPDATE residents SET name=?, sex=?, address=?, purok=?, contact=?, birthdate=?, civil_status=?, pwd=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, sex);
                ps.setString(3, address);
                ps.setString(4, purok);
                ps.setString(5, contact);
                ps.setString(6, birthdate);
                ps.setString(7, civilStatus);
                ps.setString(8, pwd);
                ps.setInt(9, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static void deleteResident(int id) throws SQLException {
        executeQuery(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM residents WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static List<Resident> searchResidents(String keyword) throws SQLException {
        return executeQuery(conn -> {
            List<Resident> list = new ArrayList<>();
            String sql = "SELECT * FROM residents WHERE name LIKE ? OR address LIKE ? OR purok LIKE ? ORDER BY id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String p = "%" + keyword + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                ps.setString(3, p);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapResident(rs));
                }
            }
            return list;
        });
    }
    
    // ================= DUPLICATE CHECK METHODS =================
    
    /**
     * Checks if a resident with the same name, birthdate, and address already exists
     */
    public static boolean residentExists(String name, String birthdate, String address) throws SQLException {
        return executeQuery(conn -> {
            String sql = "SELECT COUNT(*) FROM residents WHERE LOWER(name) = LOWER(?) AND birthdate = ? AND LOWER(address) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setString(2, birthdate);
                ps.setString(3, address.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        });
    }
    
    /**
     * Finds residents with similar names (fuzzy matching)
     */
    public static List<Resident> findSimilarResidents(String name) throws SQLException {
        return executeQuery(conn -> {
            List<Resident> list = new ArrayList<>();
            String sql = "SELECT * FROM residents WHERE LOWER(name) LIKE LOWER(?) ORDER BY name LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + name.toLowerCase() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapResident(rs));
                }
            }
            return list;
        });
    }
    
    /**
     * Finds exact duplicate resident
     */
    public static Resident findExactDuplicate(String name, String birthdate, String address) throws SQLException {
        return executeQuery(conn -> {
            String sql = "SELECT * FROM residents WHERE LOWER(name) = LOWER(?) AND birthdate = ? AND LOWER(address) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name.trim());
                ps.setString(2, birthdate);
                ps.setString(3, address.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapResident(rs);
                }
            }
            return null;
        });
    }

    private static Resident mapResident(ResultSet rs) throws SQLException {
        Resident r = new Resident();
        r.setId(rs.getInt("id"));
        r.setName(rs.getString("name"));
        r.setSex(rs.getString("sex"));
        r.setAddress(rs.getString("address"));
        r.setPurok(rs.getString("purok"));
        r.setContact(rs.getString("contact"));
        r.setBirthdate(rs.getString("birthdate"));
        r.setCivilStatus(rs.getString("civil_status"));
        r.setPwd(rs.getString("pwd"));
        r.setRegisteredDate(rs.getString("registered_date"));
        return r;
    }

    // ================= REPORT MODEL =================

    public static class Report {
        private int id;
        private String title;
        private String description;
        private String incidentDate;
        private String status;
        private String settlementDescription;
        private String createdDate;
        private String settledDate;
        private int complainantId;
        private int complaineeId;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIncidentDate() { return incidentDate; }
        public void setIncidentDate(String incidentDate) { this.incidentDate = incidentDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSettlementDescription() { return settlementDescription; }
        public void setSettlementDescription(String settlementDescription) { this.settlementDescription = settlementDescription; }
        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
        public String getSettledDate() { return settledDate; }
        public void setSettledDate(String settledDate) { this.settledDate = settledDate; }
        public int getComplainantId() { return complainantId; }
        public void setComplainantId(int complainantId) { this.complainantId = complainantId; }
        public int getComplaineeId() { return complaineeId; }
        public void setComplaineeId(int complaineeId) { this.complaineeId = complaineeId; }
    }

    // ================= REPORT CRUD =================

    public static int addReport(String title, String description, String incidentDate,
                                String status, int complainantId, int complaineeId) throws SQLException {
        return executeQuery(conn -> {
            String sql = "INSERT INTO reports (title, description, incident_date, status, complainantId, complaineeId) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, incidentDate);
                ps.setString(4, status);
                ps.setInt(5, complainantId);
                ps.setInt(6, complaineeId);
                ps.executeUpdate();
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            return -1;
        });
    }

    public static List<Report> getAllReports() throws SQLException {
        return executeQuery(conn -> {
            List<Report> list = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM reports ORDER BY created_date DESC")) {
                while (rs.next()) list.add(mapReport(rs));
            }
            return list;
        });
    }

    public static Report getReportById(int id) throws SQLException {
        return executeQuery(conn -> {
            String sql = "SELECT * FROM reports WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapReport(rs);
                }
            }
            return null;
        });
    }

    public static List<Report> getReportsByStatus(String status) throws SQLException {
        return executeQuery(conn -> {
            List<Report> list = new ArrayList<>();
            String sql = "SELECT * FROM reports WHERE status = ? ORDER BY created_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapReport(rs));
                }
            }
            return list;
        });
    }

    public static List<Report> getReportsByComplainant(int complainantId) throws SQLException {
        return executeQuery(conn -> {
            List<Report> list = new ArrayList<>();
            String sql = "SELECT * FROM reports WHERE complainantId = ? ORDER BY created_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, complainantId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapReport(rs));
                }
            }
            return list;
        });
    }

    public static List<Report> getReportsByComplainee(int complaineeId) throws SQLException {
        return executeQuery(conn -> {
            List<Report> list = new ArrayList<>();
            String sql = "SELECT * FROM reports WHERE complaineeId = ? ORDER BY created_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, complaineeId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapReport(rs));
                }
            }
            return list;
        });
    }

    public static void updateReport(int id, String title, String description, String incidentDate,
                                    String status, int complainantId, int complaineeId) throws SQLException {
        executeQuery(conn -> {
            String sql = "UPDATE reports SET title=?, description=?, incident_date=?, status=?, complainantId=?, complaineeId=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, incidentDate);
                ps.setString(4, status);
                ps.setInt(5, complainantId);
                ps.setInt(6, complaineeId);
                ps.setInt(7, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static void settleReport(int id, String settlementDescription) throws SQLException {
        executeQuery(conn -> {
            String sql = "UPDATE reports SET status='Settled', settlement_description=?, settled_date=datetime('now') WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, settlementDescription);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static void deleteReport(int id) throws SQLException {
        executeQuery(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reports WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static List<Report> searchReports(String keyword) throws SQLException {
        return executeQuery(conn -> {
            List<Report> list = new ArrayList<>();
            String sql = "SELECT * FROM reports WHERE title LIKE ? OR description LIKE ? ORDER BY created_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String p = "%" + keyword + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapReport(rs));
                }
            }
            return list;
        });
    }

    public static int getReportCountByStatus(String status) throws SQLException {
        return executeQuery(conn -> {
            String sql = "SELECT COUNT(*) FROM reports WHERE status = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            return 0;
        });
    }

    private static Report mapReport(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getInt("id"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setIncidentDate(rs.getString("incident_date"));
        r.setStatus(rs.getString("status"));
        r.setSettlementDescription(rs.getString("settlement_description"));
        r.setCreatedDate(rs.getString("created_date"));
        r.setSettledDate(rs.getString("settled_date"));
        
        try {
            r.setComplainantId(rs.getInt("complainantId"));
        } catch (SQLException e) {
            r.setComplainantId(-1);
        }
        try {
            r.setComplaineeId(rs.getInt("complaineeId"));
        } catch (SQLException e) {
            r.setComplaineeId(-1);
        }
        return r;
    }

    // ================= STATISTICS =================

    public static class Statistics {
        public int totalPopulation;
        public int maleCount;
        public int femaleCount;
        public int seniorCount;
        public int voterCount;
        public int householdCount;
        public int reportCount;
        public int pendingReportCount;
        public int pwdCount;
    }

    public static Statistics getAllStatistics() throws SQLException {
        return executeQuery(conn -> {
            Statistics stats = new Statistics();
            String sql = """
                SELECT
                    (SELECT COUNT(*) FROM residents) AS total_population,
                    (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'male') AS male_count,
                    (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'female') AS female_count,
                    (SELECT COUNT(*) FROM residents WHERE birthdate != '' AND birthdate IS NOT NULL AND date(birthdate) <= date('now','-60 years')) AS senior_count,
                    (SELECT COUNT(*) FROM residents WHERE birthdate != '' AND birthdate IS NOT NULL AND date(birthdate) <= date('now','-18 years')) AS voter_count,
                    (SELECT COUNT(DISTINCT address) FROM residents WHERE address != '' AND address IS NOT NULL) AS household_count,
                    (SELECT COUNT(*) FROM reports) AS report_count,
                    (SELECT COUNT(*) FROM reports WHERE LOWER(status) = 'pending') AS pending_count,
                    (SELECT COUNT(*) FROM residents WHERE LOWER(pwd) = 'yes') AS pwd_count
            """;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    stats.totalPopulation = rs.getInt("total_population");
                    stats.maleCount = rs.getInt("male_count");
                    stats.femaleCount = rs.getInt("female_count");
                    stats.seniorCount = rs.getInt("senior_count");
                    stats.voterCount = rs.getInt("voter_count");
                    stats.householdCount = rs.getInt("household_count");
                    stats.reportCount = rs.getInt("report_count");
                    stats.pendingReportCount = rs.getInt("pending_count");
                    stats.pwdCount = rs.getInt("pwd_count");
                    if (stats.householdCount == 0) stats.householdCount = stats.totalPopulation;
                }
            }
            return stats;
        });
    }
}

// Database operation functional interface
@FunctionalInterface
interface DatabaseOperation<T> {
    T execute(Connection conn) throws SQLException;
}