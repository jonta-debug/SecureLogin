package ui;

import auth.AuthService;
import auth.EmailService;
import auth.TwoFactorService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RegisterUI extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private final AuthService authService;
    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public RegisterUI(
            AuthService authService,
            TwoFactorService twoFactorService,
            EmailService emailService
    ) {
        this.authService = authService;
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;

        setTitle("SecureLogin - Register");
        setSize(500, 700);
        setMinimumSize(new Dimension(450, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = AppleStyle.background();
        JPanel card = AppleStyle.card();

        card.setPreferredSize(new Dimension(400, 590));

        // Header
        card.add(AppleStyle.icon("◈"));
        card.add(Box.createVerticalStrut(12));

        card.add(AppleStyle.title("Create account"));
        card.add(Box.createVerticalStrut(6));

        card.add(AppleStyle.subtitle(
                "Get started with SecureLogin"
        ));

        card.add(Box.createVerticalStrut(30));

        // Email
        card.add(AppleStyle.label("Email address"));
        card.add(Box.createVerticalStrut(8));

        emailField = new JTextField();
        AppleStyle.styleField(emailField);
        card.add(emailField);

        card.add(Box.createVerticalStrut(18));

        // Password
        card.add(AppleStyle.label("Password"));
        card.add(Box.createVerticalStrut(8));

        passwordField = new JPasswordField();
        AppleStyle.styleField(passwordField);
        card.add(passwordField);

        card.add(Box.createVerticalStrut(18));

        // Confirm password
        card.add(AppleStyle.label("Confirm password"));
        card.add(Box.createVerticalStrut(8));

        confirmPasswordField = new JPasswordField();
        AppleStyle.styleField(confirmPasswordField);
        card.add(confirmPasswordField);

        card.add(Box.createVerticalStrut(30));

        // Register button
        JButton registerButton =
                AppleStyle.primaryButton("Create Account");

        card.add(registerButton);

        card.add(Box.createVerticalStrut(16));

        // Back to login
        JButton switchButton =
                AppleStyle.textButton(
                        "Already have an account? Sign in"
                );

        card.add(switchButton);

        switchButton.addActionListener(e -> {
            LoginUI loginUI = new LoginUI(
                    authService,
                    twoFactorService,
                    emailService
            );

            loginUI.setVisible(true);
            dispose();
        });

        registerButton.addActionListener(e -> {

            String email = emailField.getText().trim();

            char[] password = passwordField.getPassword();
            char[] confirmPassword =
                    confirmPasswordField.getPassword();

            boolean passwordsMatch =
                    Arrays.equals(password, confirmPassword);

            Arrays.fill(confirmPassword, '\0');

            if (!passwordsMatch) {
                Arrays.fill(password, '\0');

                JOptionPane.showMessageDialog(
                        RegisterUI.this,
                        "Passwords do not match!",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (email.isBlank() || password.length == 0) {
                Arrays.fill(password, '\0');

                JOptionPane.showMessageDialog(
                        RegisterUI.this,
                        "Please complete all fields.",
                        "Registration Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            registerButton.setEnabled(false);
            switchButton.setEnabled(false);

            SwingWorker<Boolean, Void> worker =
                    new SwingWorker<>() {

                        @Override
                        protected Boolean doInBackground()
                                throws Exception {

                            return authService.registerUser(
                                    email,
                                    password
                            );
                        }

                        @Override
                        protected void done() {
                            try {
                                boolean success = get();

                                if (success) {
                                    JOptionPane.showMessageDialog(
                                            RegisterUI.this,
                                            "Account created successfully!"
                                    );

                                    LoginUI loginUI = new LoginUI(
                                            authService,
                                            twoFactorService,
                                            emailService
                                    );

                                    loginUI.setVisible(true);
                                    dispose();

                                } else {
                                    JOptionPane.showMessageDialog(
                                            RegisterUI.this,
                                            "Registration failed.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

                                JOptionPane.showMessageDialog(
                                        RegisterUI.this,
                                        "An error occurred during registration.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );

                            } finally {
                                Arrays.fill(password, '\0');

                                registerButton.setEnabled(true);
                                switchButton.setEnabled(true);
                            }
                        }
                    };

            worker.execute();
        });

        background.add(card);
        setContentPane(background);
    }
}