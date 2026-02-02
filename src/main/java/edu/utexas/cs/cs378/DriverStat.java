package edu.utexas.cs.cs378;

import java.util.Set;
import java.util.HashSet;

public class DriverStat {

    private final String driverId;
    private double totalEarnings;
    private Set<String> medallions;

    public DriverStat(String driverId){
        this.driverId = driverId;
        this.totalEarnings = 0.0;
        this.medallions = new HashSet<>();
    }

    public DriverStat(String driverId, double totalEarnings, String medallion) {
        this.driverId = driverId;
        this.totalEarnings = totalEarnings;
        this.medallions = new HashSet<>();
        this.medallions.add(medallion);
    }

    public String getDriverId() {
        return driverId;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public int getDistinctMedallions() {
        return medallions.size();
    }

    public void increaseEarnings(double amount) {
        totalEarnings += amount;
    }

    public void addMedallion(String medallion) {
        medallions.add(medallion);
    }

    // public DriverStat merged(DriverStat other) {
    //     if (!driverId.equals(other.driverId)) {
    //         throw new IllegalArgumentException("Cannot merge stats for different drivers");
    //     }
    //     // This assumes medallion sets are disjoint per sender; if they overlap, counts will be over.
    //     return new DriverStat(driverId, totalEarnings + other.totalEarnings,
    //             new HashSet<>(medallions).stream().filter(other.medallions::contains).collect(Collectors.toSet()));
    // }

    @Override
    public String toString() {
        return "(DriverId: " + driverId + ", Unique Medallions: " + medallions.size() + ", Total Earnings: " + totalEarnings + ")";
    }
}
