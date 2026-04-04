package database;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class ResidentDAO {

    // ================= DATA MODEL =================
    public static class ResidentRow {
        public final int id;
        public final String name;
        public final String sex;
        public final String address;
        public final String purok;
        public final String status;
        public final String birthdate;
        public final String pwd; // ✅ ADD THIS

        public ResidentRow(int id, String name, String sex,
                           String address, String purok, String status,
                           String birthdate, String pwd) {
            this.id = id;
            this.name = name;
            this.sex = sex;
            this.address = address;
            this.purok = purok;
            this.status = status;
            this.birthdate = birthdate;
            this.pwd = pwd; // ✅ STORE IT
        }

        public int getAge() {
            return calculateAge(birthdate);
        }

        private static int calculateAge(String birthdate) {
            if (birthdate == null || birthdate.isBlank()) return 0;
            try {
                LocalDate birth = LocalDate.parse(birthdate);
                return Math.max(Period.between(birth, LocalDate.now()).getYears(), 0);
            } catch (Exception e) {
                return 0;
            }
        }

        public boolean isPwd() {
            return pwd != null && pwd.equalsIgnoreCase("Yes");
        }

        @Override
        public String toString() {
            return name + " (Age: " + getAge() + ")";
        }
    }

    // ================= READ =================

    public static List<ResidentRow> getAllResidentRows() {
        List<ResidentRow> rows = new ArrayList<>();
        try {
            for (DatabaseManager.Resident r : DatabaseManager.getAllResidents()) {
                rows.add(toRow(r));
            }
        } catch (Exception e) {
            System.err.println("ResidentDAO.getAllResidentRows: " + e.getMessage());
        }
        return rows;
    }

    public static List<ResidentRow> searchResidentRows(String keyword) {
        List<ResidentRow> rows = new ArrayList<>();
        try {
            for (DatabaseManager.Resident r : DatabaseManager.searchResidents(keyword)) {
                rows.add(toRow(r));
            }
        } catch (Exception e) {
            System.err.println("ResidentDAO.searchResidentRows: " + e.getMessage());
        }
        return rows;
    }

    public static ResidentRow getResidentById(int id) {
        try {
            DatabaseManager.Resident r = DatabaseManager.getResidentById(id);
            if (r != null) return toRow(r);
        } catch (Exception e) {
            System.err.println("ResidentDAO.getResidentById: " + e.getMessage());
        }
        return null;
    }

    // ================= WRITE =================

    public static void updateResident(int id, String name, String sex,
                                      String address, String purok,
                                      String contact, String birthdate,
                                      String civilStatus) {
        try {
            DatabaseManager.updateResident(id, name, sex, address, purok,
                                           contact, birthdate, civilStatus);
        } catch (Exception e) {
            System.err.println("ResidentDAO.updateResident: " + e.getMessage());
        }
    }

    public static void updateResidentWithPwd(int id, String name, String sex,
                                             String address, String purok,
                                             String contact, String birthdate,
                                             String civilStatus, String pwd) {
        try {
            DatabaseManager.updateResident(id, name, sex, address, purok,
                                           contact, birthdate, civilStatus, pwd);
        } catch (Exception e) {
            System.err.println("ResidentDAO.updateResidentWithPwd: " + e.getMessage());
        }
    }

    public static void deleteResident(int id) {
        try {
            DatabaseManager.deleteResident(id);
        } catch (Exception e) {
            System.err.println("ResidentDAO.deleteResident: " + e.getMessage());
        }
    }

    // ================= FIXED MAPPING =================

    private static ResidentRow toRow(DatabaseManager.Resident r) {
        return new ResidentRow(
            r.getId(),
            safe(r.getName(), "—"),
            safe(r.getSex(), "—"),
            safe(r.getAddress(), "—"),
            safe(r.getPurok(), "—"),
            safe(r.getCivilStatus(), "—"),
            r.getBirthdate() != null ? r.getBirthdate() : "",
            safe(r.getPwd(), "No") // ✅ THIS IS THE FIX
        );
    }

    private static String safe(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}