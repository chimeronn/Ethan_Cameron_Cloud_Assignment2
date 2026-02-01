package edu.utexas.cs.cs378;

public class DriverStat {

    private final String driverId;
    private final double totalEarnings;
    private final int distinctMedallions;

    public DriverStat(String driverId, double totalEarnings, int distinctMedallions) {
        this.driverId = driverId;
        this.totalEarnings = totalEarnings;
        this.distinctMedallions = distinctMedallions;
    }

    public String getDriverId() {
        return driverId;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public int getDistinctMedallions() {
        return distinctMedallions;
    }

    public DriverStat merged(DriverStat other) {
        if (!driverId.equals(other.driverId)) {
            throw new IllegalArgumentException("Cannot merge stats for different drivers");
        }
        // This assumes medallion sets are disjoint per sender; if they overlap, counts will be over.
        return new DriverStat(driverId, totalEarnings + other.totalEarnings,
                distinctMedallions + other.distinctMedallions);
    }

    @Override
    public String toString() {
        return "(" + driverId + ", " + distinctMedallions + ", " + totalEarnings + ")";
    }
}
