package service;

import database.DatabaseManager;
import database.DatabaseManager.Report;
import java.sql.SQLException;
import java.util.List;

public class ReportService {
    
    // ================= CREATE =================
    
    public static int addReport(String title, String description, String incidentDate,
                                String status, int complainantId, int complaineeId) throws SQLException {
        return DatabaseManager.addReport(title, description, incidentDate, status, complainantId, complaineeId);
    }
    
    public static int addReport(String description, String incidentDate, String status) throws SQLException {
        return DatabaseManager.addReport("", description, incidentDate, status, -1, -1);
    }
    
    public static int addReport(String description, String incidentDate, String status, int complainantId) throws SQLException {
        return DatabaseManager.addReport("", description, incidentDate, status, complainantId, -1);
    }
    
    // ================= READ =================
    
    public static List<Report> getAllReports() throws SQLException {
        return DatabaseManager.getAllReports();
    }
    
    public static Report getReportById(int id) throws SQLException {
        return DatabaseManager.getReportById(id);
    }
    
    public static List<Report> getReportsByStatus(String status) throws SQLException {
        return DatabaseManager.getReportsByStatus(status);
    }
    
    public static List<Report> getReportsByComplainant(int complainantId) throws SQLException {
        return DatabaseManager.getReportsByComplainant(complainantId);
    }
    
    public static List<Report> getReportsByComplainee(int complaineeId) throws SQLException {
        return DatabaseManager.getReportsByComplainee(complaineeId);
    }
    
    public static List<Report> searchReports(String keyword) throws SQLException {
        return DatabaseManager.searchReports(keyword);
    }
    
    public static int getReportCountByStatus(String status) throws SQLException {
        return DatabaseManager.getReportCountByStatus(status);
    }
    
    // ================= UPDATE =================
    
    public static void updateReport(int id, String title, String description, String incidentDate,
                                    String status, int complainantId, int complaineeId) throws SQLException {
        DatabaseManager.updateReport(id, title, description, incidentDate, status, complainantId, complaineeId);
    }
    
    public static void settleReport(int id, String settlementDescription) throws SQLException {
        DatabaseManager.settleReport(id, settlementDescription);
    }
    
    // ================= DELETE =================
    
    public static void deleteReport(int id) throws SQLException {
        DatabaseManager.deleteReport(id);
    }
}