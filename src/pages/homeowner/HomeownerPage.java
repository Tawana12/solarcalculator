package pages.homeowner;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import pages.auth.LoginPage;
import system.*;

public class HomeownerPage extends JFrame {

    private final SolarManagementSystem system;
    private final User user;

    private JTextField lengthField;
    private JTextField widthField;
    private JTextField demandField;
    private JComboBox<Integer> shadingBox;
    private JComboBox<String> panelBox;

    private JTextArea resultArea;
    private JComboBox<HomeownerRecord> recordBox;

    private HomeownerRecord lastRecord;

    public HomeownerPage(SolarManagementSystem system, User user) {

        this.system = system;
        this.user = user;

        setTitle("Homeowner Dashboard");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Solar Assessment", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Roof Length (m):"), gbc);
        gbc.gridx = 1;
        lengthField = new JTextField(15);
        formPanel.add(lengthField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Roof Width (m):"), gbc);
        gbc.gridx = 1;
        widthField = new JTextField(15);
        formPanel.add(widthField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Daily Energy Demand (kWh):"), gbc);
        gbc.gridx = 1;
        demandField = new JTextField(15);
        formPanel.add(demandField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Shading Level (1–10):"), gbc);
        gbc.gridx = 1;
        shadingBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) shadingBox.addItem(i);
        formPanel.add(shadingBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Panel Type:"), gbc);
        gbc.gridx = 1;
        panelBox = new JComboBox<>(system.getAllPanelNames());
        formPanel.add(panelBox, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton calculateBtn = new JButton("Calculate");
        formPanel.add(calculateBtn, gbc);

        centerPanel.add(formPanel, BorderLayout.WEST);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        recordBox = new JComboBox<>();
        recordBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof HomeownerRecord) {
                    int displayIndex = index >= 0
                            ? index + 1
                            : recordBox.getSelectedIndex() + 1;
                    setText("Record " + displayIndex);
                }
                return this;
            }
        });

        recordBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        refreshRecordList();

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        JButton viewBtn = new JButton("View");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save TXT");
        JButton backBtn = new JButton("Back");

        buttonRow.add(viewBtn);
        buttonRow.add(deleteBtn);
        buttonRow.add(saveBtn);
        buttonRow.add(backBtn);

        bottomPanel.add(recordBox);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(buttonRow);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        calculateBtn.addActionListener(e -> calculateSolar());

        viewBtn.addActionListener(e -> {
            HomeownerRecord r = (HomeownerRecord) recordBox.getSelectedItem();
            if (r != null) {
                displayResult(r);
                lastRecord = r;
            }
        });

        deleteBtn.addActionListener(e -> {
            HomeownerRecord r = (HomeownerRecord) recordBox.getSelectedItem();
            if (r == null) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete selected record?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (system.deleteRecordByHomeowner(user, r)) {
                    try {
                        system.saveToFile("data/records.txt");
                    } catch (Exception ignored) {}
                    refreshRecordList();
                    resultArea.setText("");
                }
            }
        });

        saveBtn.addActionListener(e -> saveTxtReport());
        backBtn.addActionListener(e -> {
            new LoginPage(system);
            dispose();
        });

        setVisible(true);
    }

    private void calculateSolar() {

        try {
            double length = Double.parseDouble(lengthField.getText().trim());
            double width = Double.parseDouble(widthField.getText().trim());
            double demand = Double.parseDouble(demandField.getText().trim());
            int shading = (int) shadingBox.getSelectedItem();

            String panelName = panelBox.getSelectedItem().toString();
            double panelWatt = 0;

            for (PanelSpec p : system.getPanels()) {
                if (p.getName().equalsIgnoreCase(panelName)) {
                    panelWatt = p.getWattage();
                    break;
                }
            }

            lastRecord = system.calculateHomeownerSystem(user,length,width,shading,demand,user.getTown(),panelWatt);
            system.saveToFile("data/records.txt");
            refreshRecordList();
            displayResult(lastRecord);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input values");
        }
    }

    private void displayResult(HomeownerRecord r) {

        resultArea.setText("");

        resultArea.append("SOLAR RECOMMENDATION REPORT\n");
        resultArea.append("===========================\n\n");

        resultArea.append("User ID: " + r.getUserId() + "\n");
        resultArea.append("Date: " + r.getDateTime() + "\n");
        resultArea.append("Town: " + r.getTown() + "\n\n");

        resultArea.append("Roof Area: " + r.getRoofArea() + " m²\n");
        resultArea.append("Usable Area: " + r.getUsableArea() + " m²\n");
        resultArea.append("Shading Level: " + r.getShadingLevel() + "\n\n");

        resultArea.append("Daily Demand: " + r.getDailyKWh() + " kWh\n");
        resultArea.append("Sun Hours: " + r.getSunHours() + " h\n\n");

        resultArea.append("Panel Wattage: " + r.getPanelWatt() + " W\n");
        resultArea.append("Required Panels: " + r.getRequiredPanels() + "\n");
        resultArea.append("Max Panels by Roof: " + r.getMaxPanelsByRoof() + "\n");
        resultArea.append("Recommended Panels: " + r.getRecommendedPanels() + "\n");
    }

    private void refreshRecordList() {
        recordBox.removeAllItems();
        ArrayList<HomeownerRecord> list =
                system.getRecordsForUser(user.getId());
        for (HomeownerRecord r : list) recordBox.addItem(r);
    }

    private void saveTxtReport() {

        if (lastRecord == null) {
            JOptionPane.showMessageDialog(this, "Please calculate or select a record");
            return;
        }

        try {
            String filename = "reports/homeowner_report_user_" +
                    lastRecord.getUserId() + "_" +
                    lastRecord.getDateTime().toString().replace(":", "-") +
                    ".txt";

            system.printRecordToFile(lastRecord, filename);
            JOptionPane.showMessageDialog(this, "Report saved");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save report");
        }
    }

    
}
