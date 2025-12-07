package pages.auth;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import pages.homeowner.HomeownerPage;
import pages.technician.TechnicianPage;
import system.SolarManagementSystem;
import system.User;
import system.UserType;

public class LoginPage extends JFrame {

    private SolarManagementSystem system;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage(SolarManagementSystem system) {

        this.system = system;

        setTitle("Login");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Solar System Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {

            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }

            if (!system.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format");
                return;
            }

            User user = system.findUserByEmail(email);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "User not found");
                return;
            }

            if (!system.hashMatches(password, user.getPasswordHash())) {
                JOptionPane.showMessageDialog(this, "Incorrect password");
                return;
            }

            if (user.getType() == UserType.ADMIN) {
                JOptionPane.showMessageDialog(
                        this,
                        "Admin login requires admin access"
                );
                return;
            }

            dispose();

            if (user.getType() == UserType.HOMEOWNER)
                new HomeownerPage(system, user);
            else
                new TechnicianPage(system, user);
        });

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        southPanel.add(loginBtn);

        southPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel signUpLabel =
                new JLabel("<html>Don't have an account? <a href=''>Sign up</a></html>");
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SignUpPage(system);
                dispose();
            }
        });

        JLabel adminLabel =
                new JLabel("<html><a href=''>Admin access</a></html>");
        adminLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdminAuthPage(system);
                dispose();
            }
        });

        southPanel.add(signUpLabel);
        southPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        southPanel.add(adminLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
