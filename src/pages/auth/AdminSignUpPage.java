package pages.auth;

import java.awt.*;
import javax.swing.*;
import system.SolarManagementSystem;
import system.UserType;

public class AdminSignUpPage extends JFrame {

    private SolarManagementSystem system;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> townBox;

    public AdminSignUpPage(SolarManagementSystem system) {

        this.system = system;

        setTitle("Admin Sign Up");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Create Admin Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Town:"), gbc);
        gbc.gridx = 1;
        townBox = new JComboBox<>(system.getAllTownNames());
        formPanel.add(townBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        JButton createBtn = new JButton("Create Admin");
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        createBtn.addActionListener(e -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }

            if (!system.isValidName(name)) {
                JOptionPane.showMessageDialog(this, "Enter full name (first and surname)");
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

            system.addUser(name,email,phone,townBox.getSelectedItem().toString(),UserType.ADMIN,password);

            try {
                system.saveToFile("data/users.txt");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving admin");
                return;
            }

            JOptionPane.showMessageDialog(this, "Admin created successfully");
            new LoginPage(system);
            dispose();
        });

        southPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        southPanel.add(createBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
