import pages.auth.LoginPage;
import system.SolarManagementSystem;
public class Main {

    public static void main(String[] args) {


        SolarManagementSystem system = new SolarManagementSystem();

        try {
            system.loadFromFile("data/users.txt");
            system.loadFromFile("data/towns.txt");
            system.loadFromFile("data/panels.txt");
            system.loadFromFile("data/records.txt");
            System.out.println("(: Program has started :)");
            

        } catch (Exception e) {
            System.out.println("Starting with empty data");
        }
        new LoginPage(system);
    }
}
