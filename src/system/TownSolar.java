package system;

import java.util.ArrayList;

public class TownSolar {

    private String townName;
    private double avgSunHours;
    private ArrayList<HomeownerRecord> records;

    public TownSolar(String townName, double avgSunHours) {
        this.townName = townName;
        this.avgSunHours = avgSunHours;
        this.records = new ArrayList<>();
    }

    public String getTownName() {
        return townName;
    }

    public double getAvgSunHours() {
        return avgSunHours;
    }

    public void setAvgSunHours(double avgSunHours) {
        this.avgSunHours = avgSunHours;
    }

    public void addRecord(HomeownerRecord record) {
        records.add(record);
    }

    public int getInstallationCount() {
        return records.size();
    }

    public double getTotalCapacity() {
        double total = 0;
        for (HomeownerRecord r : records) {
            total += r.getRecommendedPanels() * r.getPanelWatt();
        }
        return total;
    }

    public double getEstimatedDailyOutput() {
        return (getTotalCapacity() * avgSunHours) / 1000;
    }
}
