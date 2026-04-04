package service;

import database.DatabaseManager;
import database.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Reportservice {
    
    private static Connection getConnection() {
        return DatabaseManager.getConnection();
    }

    /**
     * Creates the reports table if it doesn't exist
     * Updated to include title, complainantId, and complaineeId columns
     */
    public static void initializeReportsTable() throws SQLException {
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
        
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createReportsTable);
            System.out.println("Reports table created/verified");
            
            // Add missing columns for existing databases
            try {
                stmt.execute("ALTER TABLE reports ADD COLUMN title TEXT");
                System.out.println("Added title column");
            } catch (SQLException e) {
                // Column already exists
            }
            
            try {
                stmt.execute("ALTER TABLE reports ADD COLUMN complaineeId INTEGER");
                System.out.println("Added complaineeId column");
            } catch (SQLException e) {
                // Column already exists
            }
            
            // Rename citizenId to complainantId if needed
            try {
                stmt.execute("ALTER TABLE reports RENAME COLUMN citizenId TO complainantId");
                System.out.println("Renamed citizenId to complainantId");
            } catch (SQLException e) {
                // Column doesn't exist or already renamed
            }
        }
    }

    /**
     * Adds a new report with title, description, complainant, and complainee
     */
    public static int addReport(String title, String description, String incidentDate, 
                               String status, int complainantId, int complaineeId) throws SQLException {
        String sql = "INSERT INTO reports (title, description, incident_date, status, complainantId, complaineeId) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, incidentDate);
            ps.setString(4, status);
            ps.setInt(5, complainantId);
            ps.setInt(6, complaineeId);
            ps.executeUpdate();
            
            // SQLite-specific way to get the last inserted ID
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    DatabaseManager.refreshCache();
                    return generatedId;
                }
            }
        }
        return -1;
    }

    /**
     * Adds a new report to the database (without citizenId - legacy)
     * Fixed for SQLite compatibility
     */
    public static int addReport(String description, String incidentDate, String status) throws SQLException {
        String sql = "INSERT INTO reports (description, incident_date, status) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.executeUpdate();
            
            // SQLite-specific way to get the last inserted ID
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    DatabaseManager.refreshCache();
                    return generatedId;
                }
            }
        }
        return -1;
    }

    /**
     * Adds a new report to the database with citizen ID (legacy)
     */
    public static int addReport(String description, String incidentDate, String status, int citizenId) throws SQLException {
        String sql = "INSERT INTO reports (description, incident_date, status, complainantId) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.setInt(4, citizenId);
            ps.executeUpdate();
            
            // SQLite-specific way to get the last inserted ID
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    DatabaseManager.refreshCache();
                    return generatedId;
                }
            }
        }
        return -1;
    }

    /**
     * Gets all reports from the database
     */
    public static List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY created_date DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reports.add(mapReport(rs));
            }
        }
        return reports;
    }

    /**
     * Gets reports filtered by status
     */
    public static List<Report> getReportsByStatus(String status) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE status = ? ORDER BY created_date DESC";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Gets reports by complainant ID
     */
    public static List<Report> getReportsByComplainantId(int complainantId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE complainantId = ? ORDER BY created_date DESC";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, complainantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Gets reports by complainee ID
     */
    public static List<Report> getReportsByComplaineeId(int complaineeId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE complaineeId = ? ORDER BY created_date DESC";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, complaineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Gets reports by citizen ID (legacy - returns reports where person is complainant)
     */
    public static List<Report> getReportsByCitizenId(int citizenId) throws SQLException {
        return getReportsByComplainantId(citizenId);
    }

    /**
     * Gets count of reports by complainant ID
     */
    public static int getReportCountByComplainantId(int complainantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports WHERE complainantId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, complainantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Gets count of reports by complainee ID
     */
    public static int getReportCountByComplaineeId(int complaineeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports WHERE complaineeId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, complaineeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Gets count of reports by citizen ID (legacy)
     */
    public static int getReportCountByCitizenId(int citizenId) throws SQLException {
        return getReportCountByComplainantId(citizenId);
    }

    /**
     * Gets recent reports (last 10)
     */
    public static List<Report> getRecentReports(int limit) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY created_date DESC LIMIT ?";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Updates an existing report with full details
     */
    public static void updateReport(int id, String title, String description, String incidentDate, 
                                   String status, int complainantId, int complaineeId) throws SQLException {
        String sql = "UPDATE reports SET title = ?, description = ?, incident_date = ?, status = ?, complainantId = ?, complaineeId = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, incidentDate);
            ps.setString(4, status);
            ps.setInt(5, complainantId);
            ps.setInt(6, complaineeId);
            ps.setInt(7, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    /**
     * Updates an existing report (without citizenId - legacy)
     */
    public static void updateReport(int id, String description, String incidentDate, String status) throws SQLException {
        String sql = "UPDATE reports SET description = ?, incident_date = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.setInt(4, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    /**
     * Updates an existing report with citizen ID (legacy)
     */
    public static void updateReport(int id, String description, String incidentDate, String status, int citizenId) throws SQLException {
        String sql = "UPDATE reports SET description = ?, incident_date = ?, status = ?, complainantId = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.setInt(4, citizenId);
            ps.setInt(5, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    /**
     * Settles a report with a settlement description
     */
    public static void settleReport(int id, String settlementDescription) throws SQLException {
        String sql = "UPDATE reports SET status = 'Settled', settlement_description = ?, settled_date = datetime('now') WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, settlementDescription);
            ps.setInt(2, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    /**
     * Deletes a report
     */
    public static void deleteReport(int id) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    /**
     * Gets count of reports by status
     */
    public static int getReportCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ?"; 
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Gets a report by ID
     */
    public static Report getReportById(int id) throws SQLException {
        String sql = "SELECT * FROM reports WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReport(rs);
                }
            }
        }
        return null;
    }

    /**
     * Search reports by title or description
     */
    public static List<Report> searchReports(String keyword) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE title LIKE ? OR description LIKE ? ORDER BY created_date DESC";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Gets reports by both complainant and complainee
     */
    public static List<Report> getReportsByParties(int complainantId, int complaineeId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE complainantId = ? AND complaineeId = ? ORDER BY created_date DESC";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, complainantId);
            ps.setInt(2, complaineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    /**
     * Maps ResultSet to Report object
     * Updated to include title, complainantId, and complaineeId
     */
    private static Report mapReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        
        // Handle title - may be null for old records
        try {
            report.setTitle(rs.getString("title"));
        } catch (SQLException e) {
            report.setTitle("");
        }
        
        report.setDescription(rs.getString("description"));
        report.setIncidentDate(rs.getString("incident_date"));
        report.setStatus(rs.getString("status"));
        report.setSettlementDescription(rs.getString("settlement_description"));
        report.setCreatedDate(rs.getString("created_date"));
        report.setSettledDate(rs.getString("settled_date"));
        
        // Handle complainantId - may be null for old records
        try {
            report.setComplainantId(rs.getInt("complainantId"));
        } catch (SQLException e) {
            report.setComplainantId(-1);
        }
        
        // Handle complaineeId - may be null for old records
        try {
            report.setComplaineeId(rs.getInt("complaineeId"));
        } catch (SQLException e) {
            report.setComplaineeId(-1);
        }
        
        return report;
    }
}