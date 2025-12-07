package system;

import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SolarManagementSystem implements FileOperations, ReportGenerator {

    private HashMap<Integer, User> users;
    private HashMap<String, TownSolar> towns;
    private ArrayList<PanelSpec> panels;
    private ArrayList<HomeownerRecord> records;

    private static int nextUserId = 1;
    private static final String ADMIN_CODE_FILE = "data/verification.txt";
    private static final String DEFAULT_ADMIN_CODE = "12345678";
    private String adminCodeHash;




    Scanner scanner = new Scanner(System.in);

    public SolarManagementSystem() {
        users = new HashMap<>();
        towns = new HashMap<>();
        panels = new ArrayList<>();
        records = new ArrayList<>();
        loadAdminCode();
    }

    public void saveToFile(String filename) throws Exception {
        try (FileWriter writer = new FileWriter(filename)) {

            if (filename.endsWith("users.txt")) {
                for (User u : users.values())
                    writer.write(u.toString() + "\n");
            }

            else if (filename.endsWith("towns.txt")) {
                for (TownSolar t : towns.values())
                    writer.write(t.toString() + "\n");
            }

            else if (filename.endsWith("panels.txt")) {
                for (PanelSpec p : panels)
                    writer.write(p.toString() + "\n");
            }

            else if (filename.endsWith("records.txt")) {
                for (HomeownerRecord r : records)
                    writer.write(r.toString() + "\n");
            }
        }
    }

    public void loadFromFile(String filename) throws Exception {
        try (Scanner fileScanner = new Scanner(new File(filename))) {

            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");

                if (filename.endsWith("users.txt")) {
                    int id = Integer.parseInt(parts[0]);
                    users.put(
                            id,
                            new User(
                                    id,
                                    parts[1],
                                    parts[2],
                                    parts[3],
                                    parts[4],
                                    UserType.valueOf(parts[5]),
                                    parts[6]
                            )
                    );
                    nextUserId = Math.max(nextUserId, id + 1);
                }

                else if (filename.endsWith("towns.txt")) {
                    towns.put(
                            parts[0].toLowerCase(),
                            new TownSolar(parts[0], Double.parseDouble(parts[1]))
                    );
                }

                else if (filename.endsWith("panels.txt")) {
                    panels.add(new PanelSpec(parts[0], Double.parseDouble(parts[1])));
                }

                else if (filename.endsWith("records.txt")) {
                    int index = 0;
                    int userId = Integer.parseInt(parts[index++]);
                    LocalDateTime dt = LocalDateTime.parse(parts[index++]);
                    String town = parts[index++];
                    double roofLength = Double.parseDouble(parts[index++]);
                    double roofWidth = Double.parseDouble(parts[index++]);
                    double roofArea = Double.parseDouble(parts[index++]);
                    int shadingLevel = Integer.parseInt(parts[index++]);
                    double usableArea = Double.parseDouble(parts[index++]);
                    double dailyKWh = Double.parseDouble(parts[index++]);
                    double sunHours = Double.parseDouble(parts[index++]);
                    double panelWatt = Double.parseDouble(parts[index++]);
                    int requiredPanels = Integer.parseInt(parts[index++]);
                    int maxPanelsByRoof = Integer.parseInt(parts[index++]);
                    int recommendedPanels = Integer.parseInt(parts[index++]);
                    
                    HomeownerRecord r = new HomeownerRecord(
                            userId,
                            dt,
                            town,
                            roofLength,
                            roofWidth,
                            roofArea,
                            shadingLevel,
                            usableArea,
                            dailyKWh,
                            sunHours,
                            panelWatt,
                            requiredPanels,
                            maxPanelsByRoof,
                            recommendedPanels
                    );
                    records.add(r);
                    TownSolar townObj = getTownSolar(town);
                    if (townObj != null) {
                        townObj.addRecord(r);
                    }

                }
            }
        }
    }

    public void addUser(String name, String email, String phone,
                        String town, UserType type, String password) {

        String hashedPassword = hashPassword(password);

        users.put(
                nextUserId,
                new User(nextUserId, name, email, phone, town, type, hashedPassword)
        );

        nextUserId++;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());

            String hexString = "";
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
                if (hex.length() == 1) hexString += '0';
                hexString += hex;
            }
            return hexString;

        } catch (Exception e) {
            return null;
        }
    }

    public boolean hashMatches(String plainPassword, String storedHash) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput != null && hashedInput.equals(storedHash);
    }

    public boolean isValidAdminCode(String code) {
        String hashedInput = hashPassword(code);
        return hashedInput != null && hashedInput.equals(adminCodeHash);
    }

    public void changeAdminCode(String newCode) {

        adminCodeHash = hashPassword(newCode);

        try (FileWriter writer = new FileWriter(ADMIN_CODE_FILE)) {
            writer.write(adminCodeHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public User findUser(int id) {
        return users.get(id);
    }

    public User findUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public void addTown(String town, double sunHours) {
        towns.put(town.toLowerCase(), new TownSolar(town, sunHours));
    }

    public void addPanel(String name, double wattage) {
        panels.removeIf(p -> p.getName().equalsIgnoreCase(name));
        panels.add(new PanelSpec(name, wattage));
    }
    public int calculatePanels(double dailyKWh, double sunHours, double wattage) {
        double dailyPanelEnergy = (wattage / 1000) * sunHours * 0.75;
        return (int) Math.ceil(dailyKWh / dailyPanelEnergy);
    }

    public String generateUserReport(int userId) {
        User u = findUser(userId);
        if (u == null) return "User not found";
        return u.toString();
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean isValidName(String name) {
        return name != null && name.trim().split("\\s+").length >= 2;
    }

    public boolean isValidPhone(String phone) {
        if (phone.startsWith("+")) phone = phone.substring(1);
        for (char c : phone.toCharArray())
            if (!Character.isDigit(c)) return false;
        return phone.length() >= 7;
    }

    public String[] getAllTownNames() {
        String[] townNames = new String[towns.size()];
        int index = 0;
        for (TownSolar town : towns.values()) {
            townNames[index++] = town.getTownName();
        }
        return townNames;
    }

    public TownSolar getTownSolar(String townName) {
        if (townName == null) return null;
        return towns.get(townName.toLowerCase());
    }

    public HomeownerRecord calculateHomeownerSystem(User user,
                                                    double roofLength,
                                                    double roofWidth,
                                                    int shadingLevel,
                                                    double dailyKWh,
                                                    String townName,
                                                    double panelWatt) {

        double roofArea = roofLength * roofWidth;

        if (shadingLevel < 1) shadingLevel = 1;
        if (shadingLevel > 10) shadingLevel = 10;

        double shadingLoss = shadingLevel * 0.05;
        if (shadingLoss > 0.8) shadingLoss = 0.8;

        double usableArea = roofArea * (1 - shadingLoss);

        double panelArea = 1.7;
        int maxPanelsByRoof = (int) Math.floor(usableArea / panelArea);

        TownSolar t = getTownSolar(townName);
        double sunHours = 0.0;
        if (t != null) {
            sunHours = t.getAvgSunHours();
        }

        int requiredPanels = calculatePanels(dailyKWh, sunHours, panelWatt);

        int recommendedPanels = requiredPanels;
        if (maxPanelsByRoof > 0 && requiredPanels > maxPanelsByRoof) {
            recommendedPanels = maxPanelsByRoof;
        }

        HomeownerRecord record = new HomeownerRecord(
                user.getId(),
                LocalDateTime.now(),
                townName,
                roofLength,
                roofWidth,
                roofArea,
                shadingLevel,
                usableArea,
                dailyKWh,
                sunHours,
                panelWatt,
                requiredPanels,
                maxPanelsByRoof,
                recommendedPanels
        );

        records.add(record);
        TownSolar town = getTownSolar(townName);
        if (town != null) {
            town.addRecord(record);
        }


        return record;
    }

    public ArrayList<HomeownerRecord> getRecordsForUser(int userId) {
        ArrayList<HomeownerRecord> list = new ArrayList<>();
        for (HomeownerRecord r : records) {
            if (r.getUserId() == userId) {
                list.add(r);
            }
        }
        return list;
    }

    public void printRecordToFile(HomeownerRecord record, String filename) throws Exception {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Solar Recommendation Report\n");
            writer.write("---------------------------\n");
            writer.write("User ID: " + record.getUserId() + "\n");
            writer.write("Date: " + record.getDateTime().toString() + "\n");
            writer.write("Town: " + record.getTown() + "\n\n");

            writer.write("Roof length: " + record.getRoofLength() + " m\n");
            writer.write("Roof width: " + record.getRoofWidth() + " m\n");
            writer.write("Roof area: " + record.getRoofArea() + " m^2\n");
            writer.write("Shading level (1-10): " + record.getShadingLevel() + "\n");
            writer.write("Usable roof area: " + record.getUsableArea() + " m^2\n\n");

            writer.write("Daily energy demand: " + record.getDailyKWh() + " kWh\n");
            writer.write("Average sun hours: " + record.getSunHours() + " h\n");
            writer.write("Panel wattage: " + record.getPanelWatt() + " W\n\n");

            writer.write("Panels required (theoretical): " + record.getRequiredPanels() + "\n");
            writer.write("Maximum panels by roof area: " + record.getMaxPanelsByRoof() + "\n");
            writer.write("Recommended panels: " + record.getRecommendedPanels() + "\n");
        }
    }

    public ArrayList<PanelSpec> getPanels() {
        return panels;
    }

    public void saveRecords() {
        try {
            saveToFile("data/records.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRecord(HomeownerRecord record) {
        records.remove(record);
    }

    public boolean deleteRecordByHomeowner(User user, HomeownerRecord record) {

        if (record.getUserId() != user.getId()) {
            return false;
        }

        removeRecord(record);
        return true;
    }

    public boolean deleteRecordByTechnician(HomeownerRecord record) {
        removeRecord(record);
        return true;
    }
    public void exportRecordToPDF(HomeownerRecord record, String filename) throws Exception {

        String content =
                "Solar Recommendation Report\n\n" +
                "User ID: " + record.getUserId() + "\n" +
                "Date: " + record.getDateTime() + "\n" +
                "Town: " + record.getTown() + "\n\n" +

                "Roof Area: " + record.getRoofArea() + " m^2\n" +
                "Usable Area: " + record.getUsableArea() + " m^2\n" +
                "Shading Level: " + record.getShadingLevel() + "\n\n" +

                "Daily Energy Demand: " + record.getDailyKWh() + " kWh\n" +
                "Sun Hours: " + record.getSunHours() + "\n\n" +

                "Panel Wattage: " + record.getPanelWatt() + " W\n" +
                "Required Panels: " + record.getRequiredPanels() + "\n" +
                "Recommended Panels: " + record.getRecommendedPanels();

        String pdf =
                "%PDF-1.4\n" +
                "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n" +
                "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n" +
                "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >>\nendobj\n" +
                "4 0 obj\n<< /Length 5 0 R >>\nstream\n" +
                "BT\n/F1 12 Tf\n72 720 Td\n(" +
                content.replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("\n", ") Tj\n0 -16 Td\n(")
                + ") Tj\nET\nendstream\nendobj\n" +
                "5 0 obj\n" + content.length() + "\nendobj\n" +
                "xref\n0 6\n0000000000 65535 f \n" +
                "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n0\n%%EOF";

        FileWriter writer = new FileWriter(filename);
        writer.write(pdf);
        writer.close();
    }
    
    public void resetNonAdminUsers() {
        users.entrySet().removeIf(entry ->
            entry.getValue().getType() != UserType.ADMIN
        );
    }

    public void clearAllRecords() {
        records.clear();
    }
    public boolean resetUserPassword(int userId, String newPassword) {
        User user = users.get(userId);
        if (user == null) return false;

        users.put(
                userId,
                new User(
                        user.getId(),
                        user.toString().split(",")[1],
                        user.getEmail(),
                        user.toString().split(",")[3],
                        user.toString().split(",")[4],
                        user.getType(),
                        hashPassword(newPassword)
                )
        );
        return true;
    }
    private void loadAdminCode() {

        File file = new File(ADMIN_CODE_FILE);

        try {
            // First run: file does not exist
            if (!file.exists()) {
                adminCodeHash = hashPassword(DEFAULT_ADMIN_CODE);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(adminCodeHash);
                }
                return;
            }

            // Normal runs: load existing hash
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    adminCodeHash = scanner.nextLine().trim();
                }
            }

        } catch (Exception e) {
            // Emergency fallback
            adminCodeHash = hashPassword(DEFAULT_ADMIN_CODE);
        }
    }
    public String[] getAllPanelNames() {
            String[] names = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                names[i] = panels.get(i).getName();
            }
            return names;
        }
        public TownSolar getTown(String name) {
            return towns.get(name.toLowerCase());
        }

    public void assignRecordToTown(String townName, HomeownerRecord record) {
        TownSolar town = towns.get(townName.toLowerCase());
        if (town != null) {
            town.addRecord(record);
        }
    }


}
