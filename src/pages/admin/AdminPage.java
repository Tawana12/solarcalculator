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

        // ========== COMPANY CODE ==========
        JPanel codePanel = createSectionPanel("Company Verification");

        JPasswordField adminCodeField = new JPasswordField(15);
        JButton updateCodeBtn = new JButton("Update Verification Code");

        addRow(codePanel, 0, "New Company Code:", adminCodeField);
        addButtonRow(codePanel, 1, updateCodeBtn);

        centerPanel.add(codePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ========== ADD TOWN ==========
        JPanel townPanel = createSectionPanel("Town Solar Data");

        JTextField townField = new JTextField(15);
        JTextField sunField = new JTextField(15);
        JButton addTownBtn = new JButton("Add Town");

        addRow(townPanel, 0, "Town Name:", townField);
        addRow(townPanel, 1, "Avg Sun Hours:", sunField);
        addButtonRow(townPanel, 2, addTownBtn);

        centerPanel.add(townPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ========== RESET USER ==========
        JPanel resetPanel = createSectionPanel("Reset User Account");

        JTextField userIdField = new JTextField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        JButton resetUserBtn = new JButton("Reset Password");

        addRow(resetPanel, 0, "User ID:", userIdField);
        addRow(resetPanel, 1, "New Password:", newPasswordField);
        addButtonRow(resetPanel, 2, resetUserBtn);

        centerPanel.add(resetPanel);

        JButton backBtn = new JButton("Back");

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        southPanel.add(backBtn);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // ========== ACTIONS ==========
        updateCodeBtn.addActionListener(e -> {
            String newCode = new String(adminCodeField.getPassword());

            if (newCode.length() < 6) {
                JOptionPane.showMessageDialog(this, "Code must be at least 6 characters");
                return;
            }

            system.changeAdminCode(newCode);
            JOptionPane.showMessageDialog(this, "Verification code updated");
            adminCodeField.setText("");
        });

        addTownBtn.addActionListener(e -> {
            try {
                system.addTown(
                        townField.getText().trim(),
                        Double.parseDouble(sunField.getText().trim())
                );
                system.saveToFile("data/towns.txt");
                JOptionPane.showMessageDialog(this, "Town added successfully");
                townField.setText("");
                sunField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid town data");
            }
        });

        resetUserBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(userIdField.getText().trim());
                String newPass = new String(newPasswordField.getPassword());

                if (newPass.length() < 6) {
                    JOptionPane.showMessageDialog(this, "Password too short");
                    return;
                }

                if (system.resetUserPassword(id, newPass)) {
                    system.saveToFile("data/users.txt");
                    JOptionPane.showMessageDialog(this, "User password reset");
                    userIdField.setText("");
                    newPasswordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "User not found");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        backBtn.addActionListener(e -> {
            new LoginPage(system);
            dispose();
        });

        setVisible(true);
    }

    // ================= HELPERS =================
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addButtonRow(JPanel panel, int row, JButton button) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 8, 8);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(button, gbc);
    }
}
