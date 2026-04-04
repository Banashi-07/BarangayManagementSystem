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
                    "Population: %d, Male: %d, Female: %d, Senior: %d, PWD: %d, Blotters: %d, Households: %d, Voters: %d",
                    totalPopulation, maleCount, femaleCount, seniorCount, pwdCount, blotterCount, householdCount, voterCount
            );
        }
    }

    public static Stats getAllStats() {
        Stats stats = new Stats();

        String sql = """
            SELECT
                (SELECT COUNT(*) FROM residents) AS total_population,
                (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'male') AS male_count,
                (SELECT COUNT(*) FROM residents WHERE LOWER(sex) = 'female') AS female_count,
                (SELECT COUNT(*) FROM residents 
                    WHERE birthdate != '' AND birthdate IS NOT NULL
                      AND date(birthdate) <= date('now', '-60 years')) AS senior_count,
                (SELECT COUNT(*) FROM residents WHERE LOWER(pwd) = 'yes') AS pwd_count,
                (SELECT COUNT(*) FROM blotters) AS blotter_count,
                (SELECT COUNT(DISTINCT address) FROM residents 
                    WHERE address != '' AND address IS NOT NULL) AS household_count,
                (SELECT COUNT(*) FROM residents 
                    WHERE birthdate != '' AND birthdate IS NOT NULL
                      AND date(birthdate) <= date('now', '-18 years')) AS voter_count
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
}