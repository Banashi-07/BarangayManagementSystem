package service;

import database.DatabaseManager;
import database.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Reportservice {
    
    private static Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Creates the reports table if it doesn't exist
     */
    public static void initializeReportsTable() throws SQLException {
        // Ensure connection is established
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.connect();
        }
        
        Connection conn = getConnection();
        
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
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createReportsTable);
            System.out.println("Reports table created/verified");
            
            // Add missing columns for existing databases
            addColumnIfNotExists(stmt, "reports", "title", "TEXT");
            addColumnIfNotExists(stmt, "reports", "complaineeId", "INTEGER");
            
            // Rename citizenId to complainantId if needed
            try {
                stmt.execute("ALTER TABLE reports RENAME COLUMN citizenId TO complainantId");
                System.out.println("Renamed citizenId to complainantId");
            } catch (SQLException e) {
                // Column doesn't exist or already renamed - this is fine
                if (!e.getMessage().contains("no such column")) {
                    // Log other errors but don't fail
                    System.out.println("Note: " + e.getMessage());
                }
            }
        }
    }
    
    private static void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnType) {
        try {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
            System.out.println("Added " + columnName + " column to " + tableName);
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                System.out.println("Note: Could not add " + columnName + " column: " + e.getMessage());
            }
        }
    }

    public static int addReport(String title, String description, String incidentDate, 
                               String status, int complainantId, int complaineeId) throws SQLException {
        // Ensure connection
        ensureConnection();
        
        String sql = "INSERT INTO reports (title, description, incident_date, status, complainantId, complaineeId) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, incidentDate);
            ps.setString(4, status);
            ps.setInt(5, complainantId);
            ps.setInt(6, complaineeId);
            ps.executeUpdate();
            
            // Get the generated ID
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

    public static int addReport(String description, String incidentDate, String status) throws SQLException {
        ensureConnection();
        
        String sql = "INSERT INTO reports (description, incident_date, status) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.executeUpdate();
            
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

    public static int addReport(String description, String incidentDate, String status, int citizenId) throws SQLException {
        ensureConnection();
        
        String sql = "INSERT INTO reports (description, incident_date, status, complainantId) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, incidentDate);
            ps.setString(3, status);
            ps.setInt(4, citizenId);
            ps.executeUpdate();
            
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

    public static List<Report> getAllReports() throws SQLException {
        ensureConnection();
        
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

    public static List<Report> getReportsByStatus(String status) throws SQLException {
        ensureConnection();
        
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

    public static List<Report> getReportsByComplainantId(int complainantId) throws SQLException {
        ensureConnection();
        
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

    public static List<Report> getReportsByComplaineeId(int complaineeId) throws SQLException {
        ensureConnection();
        
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

    public static List<Report> getReportsByCitizenId(int citizenId) throws SQLException {
        return getReportsByComplainantId(citizenId);
    }

    public static int getReportCountByComplainantId(int complainantId) throws SQLException {
        ensureConnection();
        
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

    public static int getReportCountByComplaineeId(int complaineeId) throws SQLException {
        ensureConnection();
        
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

    public static int getReportCountByCitizenId(int citizenId) throws SQLException {
        return getReportCountByComplainantId(citizenId);
    }

    public static List<Report> getRecentReports(int limit) throws SQLException {
        ensureConnection();
        
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

    public static void updateReport(int id, String title, String description, String incidentDate, 
                                   String status, int complainantId, int complaineeId) throws SQLException {
        ensureConnection();
        
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

    public static void updateReport(int id, String description, String incidentDate, String status) throws SQLException {
        ensureConnection();
        
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

    public static void updateReport(int id, String description, String incidentDate, String status, int citizenId) throws SQLException {
        ensureConnection();
        
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

    public static void settleReport(int id, String settlementDescription) throws SQLException {
        ensureConnection();
        
        String sql = "UPDATE reports SET status = 'Settled', settlement_description = ?, settled_date = datetime('now') WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, settlementDescription);
            ps.setInt(2, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    public static void deleteReport(int id) throws SQLException {
        ensureConnection();
        
        String sql = "DELETE FROM reports WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            DatabaseManager.refreshCache();
        }
    }

    public static int getReportCountByStatus(String status) throws SQLException {
        ensureConnection();
        
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

    public static Report getReportById(int id) throws SQLException {
        ensureConnection();
        
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

    public static List<Report> searchReports(String keyword) throws SQLException {
        ensureConnection();
        
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

    public static List<Report> getReportsByParties(int complainantId, int complaineeId) throws SQLException {
        ensureConnection();
        
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
     * Ensures database connection is established
     */
    private static void ensureConnection() throws SQLException {
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.connect();
        }
    }

    private static Report mapReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        
        // Handle title (might not exist in old schema)
        try {
            String title = rs.getString("title");
            report.setTitle(title != null ? title : "");
        } catch (SQLException e) {
            report.setTitle("");
        }
        
        report.setDescription(rs.getString("description"));
        report.setIncidentDate(rs.getString("incident_date"));
        report.setStatus(rs.getString("status"));
        
        // Handle settlement_description (might be null)
        String settlementDesc = rs.getString("settlement_description");
        report.setSettlementDescription(settlementDesc != null ? settlementDesc : "");
        
        report.setCreatedDate(rs.getString("created_date"));
        
        // Handle settled_date (might be null)
        String settledDate = rs.getString("settled_date");
        report.setSettledDate(settledDate != null ? settledDate : "");
        
        // Handle complainantId (might not exist or be null)
        try {
            int complainantId = rs.getInt("complainantId");
            report.setComplainantId(rs.wasNull() ? -1 : complainantId);
        } catch (SQLException e) {
            report.setComplainantId(-1);
        }
        
        // Handle complaineeId (might not exist or be null)
        try {
            int complaineeId = rs.getInt("complaineeId");
            report.setComplaineeId(rs.wasNull() ? -1 : complaineeId);
        } catch (SQLException e) {
            report.setComplaineeId(-1);
        }
        
        return report;
    }
}