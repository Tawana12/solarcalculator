package pages.auth;

import java.awt.*;
import javax.swing.*;
import system.SolarManagementSystem;
import system.UserType;

public class SignUpPage extends JFrame {

    private final SolarManagementSystem system;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> townBox;
    private JComboBox<UserType> roleBox;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public SignUpPage(SolarManagementSystem system) {

        this.system = system;

        setTitle("Create Account");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(16);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(16);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(16);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Town:"), gbc);
        gbc.gridx = 1;
        townBox = new JComboBox<>(system.getAllTownNames());
        formPanel.add(townBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleBox = new JComboBox<>(new UserType[]{
                UserType.HOMEOWNER,
                UserType.TECHNICIAN
        });
        formPanel.add(roleBox, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(16);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(16);
        formPanel.add(confirmPasswordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");

        bottomPanel.add(createBtn);
        bottomPanel.add(backBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> createAccount());

        backBtn.addActionListener(e -> {
            new LoginPage(system);
            dispose();
        });

        setVisible(true);
    }

    private void createAccount() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String town = townBox.getSelectedItem().toString();
        UserType role = (UserType) roleBox.getSelectedItem();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (!system.isValidName(name)) {
            JOptionPane.showMessageDialog(this, "Enter full name (first & last)");
            return;
        }

        if (!system.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email address");
            return;
        }

        if (!system.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid phone number");
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password too short");
            return;
        }

        system.addUser(
                name,
                email,
                phone,
                town,
                role,
                password
        );

        try {
            system.saveToFile("data/users.txt");
        } catch (Exception ignored) {}

        JOptionPane.showMessageDialog(this, "Account created successfully");

        new LoginPage(system);
        dispose();
    }
}
