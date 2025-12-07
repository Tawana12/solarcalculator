package pages.technician;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import pages.auth.LoginPage;
import system.*;

public class TechnicianPage extends JFrame {

    private final SolarManagementSystem system;
    private final User user;

    private JTextField lengthField;
    private JTextField widthField;
    private JTextField demandField;
    private JTextField sunHoursField;
    private JTextField shadingLossField;

    private JComboBox<String> townBox;
    private JComboBox<HomeownerRecord> recordBox;

    private JTextArea resultArea;
    private HomeownerRecord lastRecord;

    public TechnicianPage(SolarManagementSystem system, User user) {

        this.system = system;
        this.user = user;

        setTitle("Technician Dashboard");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(mainPanel);

        JLabel title = new JLabel("Technician Solar Assessment", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= FORM =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Roof Length (m):"), gbc);
        gbc.gridx = 1;
        lengthField = new JTextField(12);
        formPanel.add(lengthField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Roof Width (m):"), gbc);
        gbc.gridx = 1;
        widthField = new JTextField(12);
        formPanel.add(widthField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Daily Energy Demand (kWh):"), gbc);
        gbc.gridx = 1;
        demandField = new JTextField(12);
        formPanel.add(demandField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Measured Sun Hours:"), gbc);
        gbc.gridx = 1;
        sunHoursField = new JTextField(12);
        formPanel.add(sunHoursField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Shading Loss (%):"), gbc);
        gbc.gridx = 1;
        shadingLossField = new JTextField(12);
        formPanel.add(shadingLossField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Town (Reference):"), gbc);
        gbc.gridx = 1;
        townBox = new JComboBox<>(system.getAllTownNames());
        formPanel.add(townBox, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton calculateBtn = new JButton("Calculate");
        formPanel.add(calculateBtn, gbc);

        centerPanel.add(formPanel, BorderLayout.WEST);

        // ================= RESULT =================
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // ================= BOTTOM =================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        recordBox = new JComboBox<>();

        // ✅ RECORD NUMBER RENDERER (FIXED)
        recordBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof HomeownerRecord) {
                    int displayIndex =
                            (index >= 0)
                                    ? index + 1
                                    : recordBox.getSelectedIndex() + 1;

                    setText("Record " + displayIndex);
                }

                return this;
            }
        });

        refreshRecordList();

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton viewBtn = new JButton("View");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save TXT");
        JButton pdfBtn = new JButton("Export PDF");
        JButton backBtn = new JButton("Back");

        buttonRow.add(viewBtn);
        buttonRow.add(deleteBtn);
        buttonRow.add(saveBtn);
        buttonRow.add(pdfBtn);
        buttonRow.add(backBtn);

        bottomPanel.add(recordBox);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomPanel.add(buttonRow);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // ================= ACTIONS =================
        calculateBtn.addActionListener(e -> calculateManual());

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
                if (system.deleteRecordByTechnician(r)) {
                    try {
                        system.saveToFile("data/records.txt");
                    } catch (Exception ignored) {}
                    refreshRecordList();
                    resultArea.setText("");
                }
            }
        });

        saveBtn.addActionListener(e -> saveTxtReport());
        pdfBtn.addActionListener(e -> exportPdfReport());

        backBtn.addActionListener(e -> {
            new LoginPage(system);
            dispose();
        });

        setVisible(true);
    }

    // ================= LOGIC =================

    private void calculateManual() {

        try {
            double length = Double.parseDouble(lengthField.getText().trim());
            double width = Double.parseDouble(widthField.getText().trim());
            double demand = Double.parseDouble(demandField.getText().trim());
            double sunHours = Double.parseDouble(sunHoursField.getText().trim());
            double shadingLoss = Double.parseDouble(shadingLossField.getText().trim()) / 100.0;

            PanelSpec panel = system.getPanels().get(0);
            int shadingLevel = (int) Math.min(10, Math.max(1, shadingLoss * 10));

            lastRecord = system.calculateHomeownerSystem(
                    user,
                    length,
                    width,
                    shadingLevel,
                    demand,
                    townBox.getSelectedItem().toString(),
                    panel.getWattage()
            );

            system.saveToFile("data/records.txt");
            refreshRecordList();
            displayResult(lastRecord);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid technician input values");
        }
    }

    private void displayResult(HomeownerRecord r) {

        resultArea.setText("");

        resultArea.append("TECHNICIAN SOLAR REPORT\n");
        resultArea.append("=======================\n\n");

        resultArea.append("User ID: " + r.getUserId() + "\n");
        resultArea.append("Date: " + r.getDateTime() + "\n");
        resultArea.append("Town: " + r.getTown() + "\n\n");

        resultArea.append("Roof Area: " + r.getRoofArea() + " m²\n");
        resultArea.append("Usable Area: " + r.getUsableArea() + " m²\n\n");

        resultArea.append("Daily Demand: " + r.getDailyKWh() + " kWh\n");
        resultArea.append("Sun Hours Used: " + r.getSunHours() + " h\n\n");

        resultArea.append("Panel Wattage: " + r.getPanelWatt() + " W\n");
        resultArea.append("Required Panels: " + r.getRequiredPanels() + "\n");
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
            JOptionPane.showMessageDialog(this, "Select or calculate a record");
            return;
        }

        try {
            String filename = "reports/technician_report_user_" +
                    lastRecord.getUserId() + "_" +
                    lastRecord.getDateTime().toString().replace(":", "-") +
                    ".txt";

            system.printRecordToFile(lastRecord, filename);
            JOptionPane.showMessageDialog(this, "Report saved");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save report");
        }
    }

    private void exportPdfReport() {

        if (lastRecord == null) {
            JOptionPane.showMessageDialog(this, "Select or calculate a record");
            return;
        }

        try {
            String filename = "reports/technician_report_user_" +
                    lastRecord.getUserId() + "_" +
                    lastRecord.getDateTime().toString().replace(":", "-") +
                    ".pdf";

            system.exportRecordToPDF(lastRecord, filename);
            JOptionPane.showMessageDialog(this, "PDF exported");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "PDF export failed");
        }
    }
}
