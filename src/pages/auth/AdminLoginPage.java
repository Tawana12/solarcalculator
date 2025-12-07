package pages.auth;

import java.awt.*;
import javax.swing.*;
import pages.admin.AdminPage;
import system.SolarManagementSystem;
import system.User;
import system.UserType;

public class AdminLoginPage extends JFrame {

    private SolarManagementSystem system;
    private JTextField emailField;
    private JPasswordField passwordField;

    public AdminLoginPage(SolarManagementSystem system) {

        this.system = system;

        setTitle("Admin Login");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Admin Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        formPanel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {

            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }

            User user = system.findUserByEmail(email);

            if (user == null || user.getType() != UserType.ADMIN) {
                JOptionPane.showMessageDialog(this, "Invalid admin account");
                return;
            }

            if (!system.hashMatches(password, user.getPasswordHash())) {
                JOptionPane.showMessageDialog(this, "Incorrect password");
                return;
            }

            new AdminPage(system, user);
            dispose();
        });

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        southPanel.add(loginBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
