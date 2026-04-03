package database;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * ResidentDAO — Data Access Object for the residents table.
 * All operations use the primary key (id) to avoid name-collision bugs.
 */
public class ResidentDAO {

    // ================= DATA MODEL =================

    /** Plain data container representing one row in ResidenceTable. */
    public static class ResidentRow {
        public final int    id;        // ← primary key carried through to UI
        public final String name;
        public final int    age;
        public final String sex;
        public final String address;
        public final String purok;
        public final String status;
        public final String birthdate; // kept for round-trip editing

        public ResidentRow(int id, String name, int age, String sex,
                           String address, String purok, String status,
                           String birthdate) {
            this.id        = id;
            this.name      = name;
            this.age       = age;
            this.sex       = sex;
            this.address   = address;
            this.purok     = purok;
            this.status    = status;
            this.birthdate = birthdate;
        }
    }

    // ================= READ =================

    /** Returns all residents as display rows. */
    public static List<ResidentRow> getAllResidentRows() {
        List<ResidentRow> rows = new ArrayList<>();
        try {
            for (DatabaseManager.Resident r : DatabaseManager.getAllResidents()) {
                rows.add(toRow(r));
            }
        } catch (SQLException e) {
            System.err.println("ResidentDAO.getAllResidentRows: " + e.getMessage());
        }
        return rows;
    }

    /** Search residents by keyword (name / address / purok). */
    public static List<ResidentRow> searchResidentRows(String keyword) {
        List<ResidentRow> rows = new ArrayList<>();
        try {
            for (DatabaseManager.Resident r : DatabaseManager.searchResidents(keyword)) {
                rows.add(toRow(r));
            }
        } catch (SQLException e) {
            System.err.println("ResidentDAO.searchResidentRows: " + e.getMessage());
        }
        return rows;
    }

    /** Fetch a single resident by ID. Returns null if not found. */
    public static ResidentRow getResidentById(int id) {
        try {
            DatabaseManager.Resident r = DatabaseManager.getResidentById(id);
            if (r != null) return toRow(r);
        } catch (SQLException e) {
            System.err.println("ResidentDAO.getResidentById: " + e.getMessage());
        }
        return null;
    }

    // ================= WRITE =================

    /**
     * Full update — updates ALL fields by ID.
     * birthdate must be in "yyyy-MM-dd" format or blank.
     */
    public static void updateResident(int id, String name, String sex,
                                      String address, String purok,
                                      String contact, String birthdate,
                                      String civilStatus) {
        try {
            DatabaseManager.updateResident(id, name, sex, address, purok,
                                           contact, birthdate, civilStatus);
        } catch (SQLException e) {
            System.err.println("ResidentDAO.updateResident: " + e.getMessage());
        }
    }

    /** Delete resident by primary key. */
    public static void deleteResident(int id) {
        try {
            DatabaseManager.deleteResident(id);
        } catch (SQLException e) {
            System.err.println("ResidentDAO.deleteResident: " + e.getMessage());
        }
    }

    // ================= PRIVATE HELPERS =================

    private static ResidentRow toRow(DatabaseManager.Resident r) {
        return new ResidentRow(
            r.getId(),
            r.getName(),
            calculateAge(r.getBirthdate()),
            safe(r.getSex(),        "—"),
            safe(r.getAddress(),    "—"),
            safe(r.getPurok(),      "—"),
            safe(r.getCivilStatus(),"—"),
            r.getBirthdate() != null ? r.getBirthdate() : ""
        );
    }

    private static String safe(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    private static int calculateAge(String birthdate) {
        if (birthdate == null || birthdate.isBlank()) return 0;
        try {
            return Period.between(LocalDate.parse(birthdate), LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}