package pages.auth;

import java.awt.*;
import javax.swing.*;
import system.SolarManagementSystem;

public class AdminAuthPage extends JFrame {

    public AdminAuthPage(SolarManagementSystem system) {

        setTitle("Admin Authentication");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel title = new JLabel("Admin Authentication", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Admin Code:"), gbc);

        gbc.gridx = 1;
        JPasswordField codeField = new JPasswordField(15);
        formPanel.add(codeField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton verifyBtn = new JButton("Verify");

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        verifyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        southPanel.add(verifyBtn);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        verifyBtn.addActionListener(e -> {
            String code = new String(codeField.getPassword());

            if (system.isValidAdminCode(code)) {

                Object[] options = {"Admin Login", "Admin Sign Up"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Admin access verified",
                        "Admin Options",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == 0)
                    new AdminLoginPage(system);
                else if (choice == 1)
                    new AdminSignUpPage(system);

                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin code");
            }
        });

        setVisible(true);
    }
}
