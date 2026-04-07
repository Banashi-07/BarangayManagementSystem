package service;

import database.DatabaseManager;
import java.sql.SQLException;

public class StatisticsService {

    public static class Stats {
        public int totalPopulation;
        public int maleCount;
        public int femaleCount;
        public int seniorCount;
        public int pwdCount;
        public int reportCount;
        public int pendingReportCount;
        public int householdCount;
        public int voterCount;

        @Override
        public String toString() {
            return String.format(
                "Population: %d, Male: %d, Female: %d, Senior: %d, PWD: %d, Reports: %d, Pending: %d, Households: %d, Voters: %d",
                totalPopulation, maleCount, femaleCount, seniorCount, pwdCount, reportCount, pendingReportCount, householdCount, voterCount
            );
        }
    }

    public static Stats getAllStats() {
        Stats stats = new Stats();
        try {
            DatabaseManager.Statistics dbStats = DatabaseManager.getAllStatistics();
            stats.totalPopulation = dbStats.totalPopulation;
            stats.maleCount = dbStats.maleCount;
            stats.femaleCount = dbStats.femaleCount;
            stats.seniorCount = dbStats.seniorCount;
            stats.pwdCount = dbStats.pwdCount;
            stats.reportCount = dbStats.reportCount;
            stats.pendingReportCount = dbStats.pendingReportCount;
            stats.householdCount = dbStats.householdCount;
            stats.voterCount = dbStats.voterCount;
        } catch (SQLException e) {
            System.err.println("StatisticsService error: " + e.getMessage());
        }
        return stats;
    }
}