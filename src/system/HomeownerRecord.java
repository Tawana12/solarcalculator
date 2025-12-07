package system;

import java.time.LocalDateTime;

public class HomeownerRecord {

    private int userId;
    private LocalDateTime dateTime;
    private String town;
    private double roofLength;
    private double roofWidth;
    private double roofArea;
    private int shadingLevel;
    private double usableArea;
    private double dailyKWh;
    private double sunHours;
    private double panelWatt;
    private int requiredPanels;
    private int maxPanelsByRoof;
    private int recommendedPanels;

    public HomeownerRecord(int userId,
                           LocalDateTime dateTime,
                           String town,
                           double roofLength,
                           double roofWidth,
                           double roofArea,
                           int shadingLevel,
                           double usableArea,
                           double dailyKWh,
                           double sunHours,
                           double panelWatt,
                           int requiredPanels,
                           int maxPanelsByRoof,
                           int recommendedPanels) {

        this.userId = userId;
        this.dateTime = dateTime;
        this.town = town;
        this.roofLength = roofLength;
        this.roofWidth = roofWidth;
        this.roofArea = roofArea;
        this.shadingLevel = shadingLevel;
        this.usableArea = usableArea;
        this.dailyKWh = dailyKWh;
        this.sunHours = sunHours;
        this.panelWatt = panelWatt;
        this.requiredPanels = requiredPanels;
        this.maxPanelsByRoof = maxPanelsByRoof;
        this.recommendedPanels = recommendedPanels;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getTown() {
        return town;
    }

    public double getRoofLength() {
        return roofLength;
    }

    public double getRoofWidth() {
        return roofWidth;
    }

    public double getRoofArea() {
        return roofArea;
    }

    public int getShadingLevel() {
        return shadingLevel;
    }

    public double getUsableArea() {
        return usableArea;
    }

    public double getDailyKWh() {
        return dailyKWh;
    }

    public double getSunHours() {
        return sunHours;
    }

    public double getPanelWatt() {
        return panelWatt;
    }

    public int getRequiredPanels() {
        return requiredPanels;
    }

    public int getMaxPanelsByRoof() {
        return maxPanelsByRoof;
    }

    public int getRecommendedPanels() {
        return recommendedPanels;
    }

    // CSV for saving to file
    @Override
    public String toString() {
        return userId + "," +
            dateTime + "," +
            town + "," +
            roofLength + "," +
            roofWidth + "," +
            roofArea + "," +
            shadingLevel + "," +
            usableArea + "," +
            dailyKWh + "," +
            sunHours + "," +
            panelWatt + "," +
            requiredPanels + "," +
            maxPanelsByRoof + "," +
            recommendedPanels;
    }


    // Label for dropdown
    public String toLabel() {
        return dateTime.toLocalDate() + " | " +
            town + " | " +
            recommendedPanels + " panels (" +
            (int) panelWatt + "W)";
    }

    

}
