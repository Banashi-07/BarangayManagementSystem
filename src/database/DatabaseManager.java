package database;

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static Connection connection;
    private static final String DB_PATH = "data/barangay.db";

    public static void connect() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) System.out.println("Data directory created: " + dataDir.getAbsolutePath());
            }

            try {
                Class.forName("org.sqlite.JDBC");
                System.out.println("SQLite JDBC Driver registered successfully");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC Driver not found!");
                e.printStackTrace();
                return;
            }

            if (connection != null && !connection.isClosed()) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            System.out.println("Database connected: " + new File(DB_PATH).getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
                stmt.execute("PRAGMA cache_size=-20000");
                stmt.execute("PRAGMA synchronous=NORMAL");
                stmt.execute("PRAGMA foreign_keys=ON");
            }

            createTables();
            migrateDatabase();

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    private static void migrateDatabase() {
        try {
            if (!columnExists("residents", "sex")) {
                addColumn("residents", "sex", "TEXT");
                System.out.println("Added 'sex' column");
            }
            if (!columnExists("residents", "purok")) {
                addColumn("residents", "purok", "TEXT");
                System.out.println("Added 'purok' column");
            }
            if (!columnExists("residents", "pwd")) {
                addColumn("residents", "pwd", "TEXT DEFAULT 'No'");
                System.out.println("Added 'pwd' column");
            }
        } catch (SQLException e) {
            System.err.println("Error migrating database: " + e.getMessage());
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
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void refreshCache() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA wal_checkpoint(TRUNCATE)");
            }
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }

    // ========== RESIDENT MODEL ==========
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
        
        public boolean isPwd() {
            return pwd != null && pwd.equalsIgnoreCase("Yes");
        }
    }

    // ========== OFFICIAL MODEL ==========
    public static class Official {
        private int id;
        private String name;
        private String position;
        private String termStart;
        private String termEnd;
        private String contact;
        private String createdDate;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getTermStart() { return termStart; }
        public void setTermStart(String termStart) { this.termStart = termStart; }
        public String getTermEnd() { return termEnd; }
        public void setTermEnd(String termEnd) { this.termEnd = termEnd; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    }

    // ========== BLOTTER MODEL ==========
    public static class Blotter {
        private int id;
        private String complainant;
        private String respondent;
        private String incidentType;
        private String description;
        private String dateIncident;
        private String status;
        private String createdDate;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getComplainant() { return complainant; }
        public void setComplainant(String complainant) { this.complainant = complainant; }
        public String getRespondent() { return respondent; }
        public void setRespondent(String respondent) { this.respondent = respondent; }
        public String getIncidentType() { return incidentType; }
        public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDateIncident() { return dateIncident; }
        public void setDateIncident(String dateIncident) { this.dateIncident = dateIncident; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    }

    // ========== STATISTICS ==========
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
            return String.format(
                "Population: %d, Male: %d, Female: %d, Senior: %d, Voters: %d, Households: %d, Blotters: %d, PWD: %d",
                totalPopulation, maleCount, femaleCount, seniorCount, voterCount, householdCount, blotterCount, pwdCount);
        }
    }

    public static Statistics getAllStatistics() {
        Statistics stats = new Statistics();
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM residents) AS total_population,
                (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'male') AS male_count,
                (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'female') AS female_count,
                (SELECT COUNT(*) FROM residents WHERE birthdate != '' AND birthdate IS NOT NULL AND date(birthdate) <= date('now','-60 years')) AS senior_count,
                (SELECT COUNT(*) FROM residents WHERE birthdate != '' AND birthdate IS NOT NULL AND date(birthdate) <= date('now','-18 years')) AS voter_count,
                (SELECT COUNT(DISTINCT address) FROM residents WHERE address != '' AND address IS NOT NULL) AS household_count,
                (SELECT COUNT(*) FROM blotters) AS blotter_count,
                (SELECT COUNT(*) FROM residents WHERE LOWER(pwd) = 'yes') AS pwd_count
        """;

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                stats.totalPopulation = rs.getInt("total_population");
                stats.maleCount       = rs.getInt("male_count");
                stats.femaleCount     = rs.getInt("female_count");
                stats.seniorCount     = rs.getInt("senior_count");
                stats.voterCount      = rs.getInt("voter_count");
                stats.householdCount  = rs.getInt("household_count");
                stats.blotterCount    = rs.getInt("blotter_count");
                stats.pwdCount        = rs.getInt("pwd_count");
                if (stats.householdCount == 0) stats.householdCount = stats.totalPopulation;
            }
        } catch (SQLException e) {
            System.err.println("Error getting statistics: " + e.getMessage());
        }
        return stats;
    }

    public static Statistics getFreshStatistics() {
        try { refreshCache(); } catch (SQLException e) { System.err.println("Cache refresh error: " + e.getMessage()); }
        return getAllStatistics();
    }

    public static int getTotalPopulation()  { return getAllStatistics().totalPopulation; }
    public static int getMaleCount()        { return getAllStatistics().maleCount; }
    public static int getFemaleCount()      { return getAllStatistics().femaleCount; }
    public static int getSeniorCount()      { return getAllStatistics().seniorCount; }
    public static int getVoterCount()       { return getAllStatistics().voterCount; }
    public static int getHouseholdCount()   { return getAllStatistics().householdCount; }
    public static int getBlotterCount()     { return getAllStatistics().blotterCount; }
    public static int getPwdCount()         { return getAllStatistics().pwdCount; }

    // ========== RESIDENTS CRUD ==========

    public static void addResident(String name, String sex, String address,
                                   String purok, String contact, String birthdate,
                                   String civilStatus, String pwd) throws SQLException {
        String sql = "INSERT INTO residents (name, sex, address, purok, contact, birthdate, civil_status, pwd) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, sex);
            ps.setString(3, address);
            ps.setString(4, purok);
            ps.setString(5, contact);
            ps.setString(6, birthdate);
            ps.setString(7, civilStatus);
            ps.setString(8, pwd);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static void addResident(String name, String sex, String address,
                                   String purok, String contact, String birthdate,
                                   String civilStatus) throws SQLException {
        addResident(name, sex, address, purok, contact, birthdate, civilStatus, "No");
    }

    public static void addResident(String name, String address, String contact,
                                   String birthdate, String civilStatus) throws SQLException {
        addResident(name, "", address, "", contact, birthdate, civilStatus, "No");
    }

    public static List<Resident> getAllResidents() throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents ORDER BY id DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResident(rs));
        }
        return list;
    }

    public static Resident getResidentById(int id) throws SQLException {
        String sql = "SELECT * FROM residents WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResident(rs);
            }
        }
        return null;
    }

    public static void updateResident(int id, String name, String sex, String address,
                                      String purok, String contact, String birthdate,
                                      String civilStatus, String pwd) throws SQLException {
        String sql = "UPDATE residents SET name=?, sex=?, address=?, purok=?, contact=?, birthdate=?, civil_status=?, pwd=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
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
            refreshCache();
        }
    }

    public static void updateResident(int id, String name, String sex, String address,
                                      String purok, String contact, String birthdate,
                                      String civilStatus) throws SQLException {
        updateResident(id, name, sex, address, purok, contact, birthdate, civilStatus, "No");
    }

    public static void updateResident(int id, String name, String address,
                                      String contact, String birthdate, String civilStatus) throws SQLException {
        updateResident(id, name, "", address, "", contact, birthdate, civilStatus, "No");
    }

    public static void deleteResident(int id) throws SQLException {
        String sql = "DELETE FROM residents WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static List<Resident> searchResidents(String keyword) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE name LIKE ? OR address LIKE ? OR purok LIKE ? ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResident(rs));
            }
        }
        return list;
    }

    public static List<Resident> getResidentsByPwd(String pwd) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE LOWER(pwd) = LOWER(?) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, pwd);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResident(rs));
            }
        }
        return list;
    }

    public static List<Resident> getResidentsByCivilStatus(String civilStatus) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE LOWER(civil_status) = LOWER(?) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, civilStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResident(rs));
            }
        }
        return list;
    }

    public static List<Resident> getResidentsByPurok(String purok) throws SQLException {
        List<Resident> list = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE LOWER(purok) = LOWER(?) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, purok);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResident(rs));
            }
        }
        return list;
    }

    public static int getResidentsCount() throws SQLException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM residents")) {
            return rs.getInt(1);
        }
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

    // ========== OFFICIALS CRUD ==========

    public static void addOfficial(String name, String position, String termStart,
                                   String termEnd, String contact) throws SQLException {
        String sql = "INSERT INTO officials (name, position, term_start, term_end, contact) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, position);
            ps.setString(3, termStart); ps.setString(4, termEnd);
            ps.setString(5, contact);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static List<Official> getAllOfficials() throws SQLException {
        List<Official> list = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM officials ORDER BY position")) {
            while (rs.next()) {
                Official o = new Official();
                o.setId(rs.getInt("id")); o.setName(rs.getString("name"));
                o.setPosition(rs.getString("position")); o.setTermStart(rs.getString("term_start"));
                o.setTermEnd(rs.getString("term_end")); o.setContact(rs.getString("contact"));
                o.setCreatedDate(rs.getString("created_date"));
                list.add(o);
            }
        }
        return list;
    }

    public static Official getOfficialById(int id) throws SQLException {
        String sql = "SELECT * FROM officials WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Official o = new Official();
                    o.setId(rs.getInt("id"));
                    o.setName(rs.getString("name"));
                    o.setPosition(rs.getString("position"));
                    o.setTermStart(rs.getString("term_start"));
                    o.setTermEnd(rs.getString("term_end"));
                    o.setContact(rs.getString("contact"));
                    o.setCreatedDate(rs.getString("created_date"));
                    return o;
                }
            }
        }
        return null;
    }

    public static void updateOfficial(int id, String name, String position,
                                      String termStart, String termEnd, String contact) throws SQLException {
        String sql = "UPDATE officials SET name=?, position=?, term_start=?, term_end=?, contact=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, position);
            ps.setString(3, termStart); ps.setString(4, termEnd);
            ps.setString(5, contact); ps.setInt(6, id);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static void deleteOfficial(int id) throws SQLException {
        String sql = "DELETE FROM officials WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate(); refreshCache();
        }
    }

    public static List<Official> searchOfficials(String keyword) throws SQLException {
        List<Official> list = new ArrayList<>();
        String sql = "SELECT * FROM officials WHERE name LIKE ? OR position LIKE ? ORDER BY position";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Official o = new Official();
                    o.setId(rs.getInt("id")); o.setName(rs.getString("name"));
                    o.setPosition(rs.getString("position")); o.setTermStart(rs.getString("term_start"));
                    o.setTermEnd(rs.getString("term_end")); o.setContact(rs.getString("contact"));
                    o.setCreatedDate(rs.getString("created_date"));
                    list.add(o);
                }
            }
        }
        return list;
    }

    // ========== BLOTTERS CRUD ==========

    public static void addBlotter(String complainant, String respondent,
                                  String incidentType, String description,
                                  String dateIncident, String status) throws SQLException {
        String sql = "INSERT INTO blotters (complainant, respondent, incident_type, description, date_incident, status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, complainant); ps.setString(2, respondent);
            ps.setString(3, incidentType); ps.setString(4, description);
            ps.setString(5, dateIncident); ps.setString(6, status);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static List<Blotter> getAllBlotters() throws SQLException {
        List<Blotter> list = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM blotters ORDER BY id DESC")) {
            while (rs.next()) {
                Blotter b = new Blotter();
                b.setId(rs.getInt("id")); b.setComplainant(rs.getString("complainant"));
                b.setRespondent(rs.getString("respondent")); b.setIncidentType(rs.getString("incident_type"));
                b.setDescription(rs.getString("description")); b.setDateIncident(rs.getString("date_incident"));
                b.setStatus(rs.getString("status")); b.setCreatedDate(rs.getString("created_date"));
                list.add(b);
            }
        }
        return list;
    }

    public static Blotter getBlotterById(int id) throws SQLException {
        String sql = "SELECT * FROM blotters WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Blotter b = new Blotter();
                    b.setId(rs.getInt("id"));
                    b.setComplainant(rs.getString("complainant"));
                    b.setRespondent(rs.getString("respondent"));
                    b.setIncidentType(rs.getString("incident_type"));
                    b.setDescription(rs.getString("description"));
                    b.setDateIncident(rs.getString("date_incident"));
                    b.setStatus(rs.getString("status"));
                    b.setCreatedDate(rs.getString("created_date"));
                    return b;
                }
            }
        }
        return null;
    }

    public static List<Blotter> getBlottersByComplainant(String complainant) throws SQLException {
        List<Blotter> list = new ArrayList<>();
        if (complainant == null || complainant.isBlank()) return list;
        
        String sql = "SELECT * FROM blotters WHERE LOWER(TRIM(complainant)) = LOWER(TRIM(?)) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, complainant);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blotter b = new Blotter();
                    b.setId(rs.getInt("id")); b.setComplainant(rs.getString("complainant"));
                    b.setRespondent(rs.getString("respondent")); b.setIncidentType(rs.getString("incident_type"));
                    b.setDescription(rs.getString("description")); b.setDateIncident(rs.getString("date_incident"));
                    b.setStatus(rs.getString("status")); b.setCreatedDate(rs.getString("created_date"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public static List<Blotter> getBlottersByRespondent(String respondent) throws SQLException {
        List<Blotter> list = new ArrayList<>();
        if (respondent == null || respondent.isBlank()) return list;
        
        String sql = "SELECT * FROM blotters WHERE LOWER(TRIM(respondent)) = LOWER(TRIM(?)) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, respondent);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blotter b = new Blotter();
                    b.setId(rs.getInt("id")); b.setComplainant(rs.getString("complainant"));
                    b.setRespondent(rs.getString("respondent")); b.setIncidentType(rs.getString("incident_type"));
                    b.setDescription(rs.getString("description")); b.setDateIncident(rs.getString("date_incident"));
                    b.setStatus(rs.getString("status")); b.setCreatedDate(rs.getString("created_date"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public static List<Blotter> getBlottersByStatus(String status) throws SQLException {
        List<Blotter> list = new ArrayList<>();
        String sql = "SELECT * FROM blotters WHERE LOWER(status) = LOWER(?) ORDER BY id DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blotter b = new Blotter();
                    b.setId(rs.getInt("id")); b.setComplainant(rs.getString("complainant"));
                    b.setRespondent(rs.getString("respondent")); b.setIncidentType(rs.getString("incident_type"));
                    b.setDescription(rs.getString("description")); b.setDateIncident(rs.getString("date_incident"));
                    b.setStatus(rs.getString("status")); b.setCreatedDate(rs.getString("created_date"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public static void updateBlotterStatus(int id, String status) throws SQLException {
        String sql = "UPDATE blotters SET status=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, id);
            ps.executeUpdate(); refreshCache();
        }
    }

    public static void updateBlotter(int id, String complainant, String respondent,
                                     String incidentType, String description,
                                     String dateIncident, String status) throws SQLException {
        String sql = "UPDATE blotters SET complainant=?, respondent=?, incident_type=?, description=?, date_incident=?, status=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, complainant); ps.setString(2, respondent);
            ps.setString(3, incidentType); ps.setString(4, description);
            ps.setString(5, dateIncident); ps.setString(6, status);
            ps.setInt(7, id);
            ps.executeUpdate();
            refreshCache();
        }
    }

    public static void deleteBlotter(int id) throws SQLException {
        String sql = "DELETE FROM blotters WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate(); refreshCache();
        }
    }

    public static int getBlottersCount() throws SQLException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM blotters")) {
            return rs.getInt(1);
        }
    }

    public static int getPendingBlottersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM blotters WHERE LOWER(status) = 'pending'";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        }
    }
}