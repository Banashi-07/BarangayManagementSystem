package service;

import java.sql.*;
import database.DatabaseManager;

public class StatisticsService {

    public static class Stats {
        public int totalPopulation;
        public int maleCount;
        public int femaleCount;
        public int seniorCount;
        public int pwdCount;
        public int blotterCount;
        public int householdCount;
        public int voterCount;

        @Override
        public String toString() {
            return String.format(
                    "Population: %d, Male: %d, Female: %d, Senior: %d, PWD: %d, Blotters: %d",
                    totalPopulation, maleCount, femaleCount, seniorCount, pwdCount, blotterCount
            );
        }
    }

    public static Stats getAllStats() {
        Stats stats = new Stats();

        // NOTE: is_pwd column does not exist yet — using 0 as placeholder.
        // When you add "is_pwd INTEGER DEFAULT 0" to the residents table,
        // replace the 0 literal with: (SELECT COUNT(*) FROM residents WHERE is_pwd = 1)
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM residents) AS total_population,
                (SELECT COUNT(*) FROM residents
                    WHERE civil_status LIKE '%Male%'
                       OR civil_status = 'Male') AS male_count,
                (SELECT COUNT(*) FROM residents
                    WHERE civil_status LIKE '%Female%'
                       OR civil_status = 'Female') AS female_count,
                (SELECT COUNT(*) FROM residents
                    WHERE birthdate != '' AND birthdate IS NOT NULL
                      AND birthdate <= date('now', '-60 years')) AS senior_count,
                0 AS pwd_count,
                (SELECT COUNT(*) FROM blotters) AS blotter_count,
                (SELECT COUNT(DISTINCT address) FROM residents
                    WHERE address != '' AND address IS NOT NULL) AS household_count,
                (SELECT COUNT(*) FROM residents
                    WHERE birthdate != '' AND birthdate IS NOT NULL
                      AND birthdate <= date('now', '-18 years')) AS voter_count
        """;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.totalPopulation = rs.getInt("total_population");
                stats.maleCount       = rs.getInt("male_count");
                stats.femaleCount     = rs.getInt("female_count");
                stats.seniorCount     = rs.getInt("senior_count");
                stats.pwdCount        = rs.getInt("pwd_count");
                stats.blotterCount    = rs.getInt("blotter_count");
                stats.householdCount  = rs.getInt("household_count");
                stats.voterCount      = rs.getInt("voter_count");

                // Fallback: if no addresses stored, use total population
                if (stats.householdCount == 0) {
                    stats.householdCount = stats.totalPopulation;
                }
            }

        } catch (SQLException e) {
            System.err.println("StatisticsService error: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    public static int getTotalPopulation() {
        String sql = "SELECT COUNT(*) FROM residents";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getMaleCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE civil_status LIKE '%Male%' OR civil_status = 'Male'";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getFemaleCount() {
        String sql = "SELECT COUNT(*) FROM residents WHERE civil_status LIKE '%Female%' OR civil_status = 'Female'";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getBlotterCount() {
        String sql = "SELECT COUNT(*) FROM blotters";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}