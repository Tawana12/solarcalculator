package pages.admin;

import java.awt.*;
import javax.swing.*;
import pages.auth.LoginPage;
import system.*;

public class AdminPage extends JFrame {

    private final SolarManagementSystem system;

    public AdminPage(SolarManagementSystem system, User user) {

        this.system = system;

        setTitle("Admin Panel");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= COMPANY CODE =================
        JPanel codePanel = createSectionPanel("Company Verification");

        JPasswordField adminCodeField = new JPasswordField(15);
        JButton updateCodeBtn = new JButton("Update Verification Code");

        addRow(codePanel, 0, "New Company Code:", adminCodeField);
        addButtonRow(codePanel, 1, updateCodeBtn);

        centerPanel.add(codePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ================= TOWN SOLAR DATA =================
        JPanel townPanel = createSectionPanel("Town Solar Data");

        JRadioButton addTownRadio = new JRadioButton("Add new town", true);
        JRadioButton updateTownRadio = new JRadioButton("Update existing town");

        ButtonGroup townGroup = new ButtonGroup();
        townGroup.add(addTownRadio);
        townGroup.add(updateTownRadio);

        JTextField townField = new JTextField(15);
        JComboBox<String> townBox = new JComboBox<>(system.getAllTownNames());
        townBox.setEnabled(false);

        JTextField sunField = new JTextField(15);
        JButton saveTownBtn = new JButton("Save");

        addRow(townPanel, 0, "", addTownRadio);
        addRow(townPanel, 1, "Town Name:", townField);
        addRow(townPanel, 2, "", updateTownRadio);
        addRow(townPanel, 3, "Select Town:", townBox);
        addRow(townPanel, 4, "Avg Sun Hours:", sunField);
        addButtonRow(townPanel, 5, saveTownBtn);

        centerPanel.add(townPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addTownRadio.addActionListener(e -> {
            townField.setEnabled(true);
            townBox.setEnabled(false);
        });

        updateTownRadio.addActionListener(e -> {
            townField.setEnabled(false);
            townBox.setEnabled(true);
        });

        // ================= PANEL SPECIFICATIONS =================
        JPanel panelSpecPanel = createSectionPanel("Solar Panel Specifications");

        JRadioButton addPanelRadio = new JRadioButton("Add new panel", true);
        JRadioButton updatePanelRadio = new JRadioButton("Update existing panel");

        ButtonGroup panelGroup = new ButtonGroup();
        panelGroup.add(addPanelRadio);
        panelGroup.add(updatePanelRadio);

        JTextField panelNameField = new JTextField(15);
        JComboBox<String> panelBox = new JComboBox<>();
        for (PanelSpec p : system.getPanels()) {
            panelBox.addItem(p.getName());
        }
        panelBox.setEnabled(false);

        JTextField panelWattField = new JTextField(15);
        JButton savePanelBtn = new JButton("Save");

        addRow(panelSpecPanel, 0, "", addPanelRadio);
        addRow(panelSpecPanel, 1, "Panel Name:", panelNameField);
        addRow(panelSpecPanel, 2, "", updatePanelRadio);
        addRow(panelSpecPanel, 3, "Select Panel:", panelBox);
        addRow(panelSpecPanel, 4, "Wattage (W):", panelWattField);
        addButtonRow(panelSpecPanel, 5, savePanelBtn);

        centerPanel.add(panelSpecPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addPanelRadio.addActionListener(e -> {
            panelNameField.setEnabled(true);
            panelBox.setEnabled(false);
        });

        updatePanelRadio.addActionListener(e -> {
            panelNameField.setEnabled(false);
            panelBox.setEnabled(true);
        });

        // ================= RESET USER =================
        JPanel resetPanel = createSectionPanel("Reset User Account");

        JTextField userIdField = new JTextField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        JButton resetUserBtn = new JButton("Reset Password");

        addRow(resetPanel, 0, "User ID:", userIdField);
        addRow(resetPanel, 1, "New Password:", newPasswordField);
        addButtonRow(resetPanel, 2, resetUserBtn);

        centerPanel.add(resetPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ================= TOWN STATISTICS =================
        JPanel statsPanel = createSectionPanel("Town Statistics");

        JComboBox<String> statsTownBox = new JComboBox<>(system.getAllTownNames());
        JButton viewStatsBtn = new JButton("View Stats");

        JLabel sunLabel = new JLabel("-");
        JLabel installLabel = new JLabel("-");
        JLabel capacityLabel = new JLabel("-");
        JLabel outputLabel = new JLabel("-");

        addRow(statsPanel, 0, "Select Town:", statsTownBox);
        addButtonRow(statsPanel, 1, viewStatsBtn);
        addRow(statsPanel, 2, "Avg Sun Hours:", sunLabel);
        addRow(statsPanel, 3, "Installations:", installLabel);
        addRow(statsPanel, 4, "Total Capacity (W):", capacityLabel);
        addRow(statsPanel, 5, "Est. Daily Output (kWh):", outputLabel);

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(380, 0));
        eastPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(eastPanel, BorderLayout.EAST);

        JButton backBtn = new JButton("Back");

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        southPanel.add(backBtn);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // ================= ACTIONS =================
        updateCodeBtn.addActionListener(e -> {
            String newCode = new String(adminCodeField.getPassword());
            if (newCode.length() < 6) return;
            system.changeAdminCode(newCode);
            adminCodeField.setText("");
        });

        saveTownBtn.addActionListener(e -> {
            try {
                String townName = addTownRadio.isSelected()
                        ? townField.getText().trim()
                        : townBox.getSelectedItem().toString();

                double sunHours = Double.parseDouble(sunField.getText().trim());

                system.addTown(townName, sunHours);
                system.saveToFile("data/towns.txt");

                townField.setText("");
                sunField.setText("");

            } catch (Exception ignored) {}
        });

        savePanelBtn.addActionListener(e -> {
            try {
                String panelName = addPanelRadio.isSelected()
                        ? panelNameField.getText().trim()
                        : panelBox.getSelectedItem().toString();

                double wattage = Double.parseDouble(panelWattField.getText().trim());

                system.addPanel(panelName, wattage);
                system.saveToFile("data/panels.txt");

                panelNameField.setText("");
                panelWattField.setText("");

            } catch (Exception ignored) {}
        });

        resetUserBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(userIdField.getText().trim());
                String newPass = new String(newPasswordField.getPassword());

                if (system.resetUserPassword(id, newPass)) {
                    system.saveToFile("data/users.txt");
                    userIdField.setText("");
                    newPasswordField.setText("");
                }
            } catch (Exception ignored) {}
        });

        viewStatsBtn.addActionListener(e -> {
            String townName = statsTownBox.getSelectedItem().toString();
            TownSolar town = system.getTown(townName);

            if (town != null) {
                sunLabel.setText(String.valueOf(town.getAvgSunHours()));
                installLabel.setText(String.valueOf(town.getInstallationCount()));
                capacityLabel.setText(String.valueOf(town.getTotalCapacity()));
                outputLabel.setText(String.format("%.2f", town.getEstimatedDailyOutput()));
            }
        });

        backBtn.addActionListener(e -> {
            new LoginPage(system);
            dispose();
        });

        setVisible(true);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addButtonRow(JPanel panel, int row, JButton button) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 8, 8);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(button, gbc);
    }
}
