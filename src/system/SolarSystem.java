package system;
public class SolarSystem {

    private int panelCount;
    private double panelWatt;

    public SolarSystem(int panelCount, double panelWatt) {
        this.panelCount = panelCount;
        this.panelWatt = panelWatt;
    }

    public double getSystemSizeKW() {
        return (panelCount * panelWatt) / 1000.0;
    }


}
