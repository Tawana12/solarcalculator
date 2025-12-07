package system;
public class PanelSpec {

    private String name;
    private double wattage;

    public PanelSpec(String name, double wattage) {
        this.name = name;
        this.wattage = wattage;
    }

    public String getName() { return name; }
    public double getWattage() { return wattage; }

    @Override
    public String toString() {
        return name + "," + wattage;
    }
}
