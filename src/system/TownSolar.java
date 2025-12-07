package system;

public class TownSolar {

    private String town;
    private double sunHours;

    public TownSolar(String town, double sunHours) {
        this.town = town;
        this.sunHours = sunHours;
    }

    public String getTown() {
        return town;
    }

    public double getSunHours() {
        return sunHours;
    }

    @Override
    public String toString() {
        return town + "," + sunHours;
    }
}
