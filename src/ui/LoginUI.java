package ui;

import auth.AuthService;
import auth.EmailService;
import auth.PendingLogin;
import auth.TwoFactorService;
import auth.VerificationResult;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Optional;

public class LoginUI extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private final AuthService authService;
    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public LoginUI(
            AuthService authService,
            TwoFactorService twoFactorService,
            EmailService emailService
    ) {
        this.authService = authService;
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;

        setTitle("SecureLogin");
        setSize(500, 620);
        setMinimumSize(new Dimension(450, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = AppleStyle.background();
        JPanel card = AppleStyle.card();

        // Header
        JLabel icon = AppleStyle.icon("◈");
        JLabel title = AppleStyle.title("Welcome back");
        JLabel subtitle = AppleStyle.subtitle(
                "Sign in to your SecureLogin account"
        );

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(36));

        // Email
        card.add(AppleStyle.label("Email address"));
        card.add(Box.createVerticalStrut(8));

        emailField = new JTextField();
        AppleStyle.styleField(emailField);
        card.add(emailField);

        card.add(Box.createVerticalStrut(22));

        // Password
        card.add(AppleStyle.label("Password"));
        card.add(Box.createVerticalStrut(8));

        passwordField = new JPasswordField();
        AppleStyle.styleField(passwordField);
        card.add(passwordField);

        card.add(Box.createVerticalStrut(32));

        // Login button
        JButton loginButton = AppleStyle.primaryButton("Sign In");
        card.add(loginButton);

        card.add(Box.createVerticalStrut(18));

        // Register button
        JButton switchButton = AppleStyle.textButton(
                "Don't have an account? Create one"
        );
        card.add(switchButton);

        switchButton.addActionListener(e -> {
            RegisterUI registerUI = new RegisterUI(
                    authService,
                    twoFactorService,
                    emailService
            );

            registerUI.setVisible(true);
            dispose();
        });

        loginButton.addActionListener(e -> {

            String email = emailField.getText().trim();
            char[] password = passwordField.getPassword();

            if (email.isBlank() || password.length == 0) {
                Arrays.fill(password, '\0');

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter your email and password."
                );
                return;
            }

            loginButton.setEnabled(false);
            switchButton.setEnabled(false);

            SwingWorker<Optional<PendingLogin>, Void> worker =
                    new SwingWorker<>() {

                        @Override
                        protected Optional<PendingLogin> doInBackground()
                                throws Exception {

                            Optional<User> authenticatedUser =
                                    authService.loginUser(email, password);

                            if (authenticatedUser.isEmpty()) {
                                return Optional.empty();
                            }

                            long userId = authenticatedUser.get().getId();

                            VerificationResult verification =
                                    twoFactorService.createVerification(userId);

                            emailService.sendVerificationCode(
                                    email,
                                    verification.code()
                            );

                            return Optional.of(
                                    new PendingLogin(
                                            userId,
                                            verification.challengeId()
                                    )
                            );
                        }

                        @Override
                        protected void done() {
                            try {
                                Optional<PendingLogin> pendingLogin = get();

                                if (pendingLogin.isPresent()) {

                                    VerificationUI verificationUI =
                                            new VerificationUI(
                                                    pendingLogin.get(),
                                                    twoFactorService
                                            );

                                    verificationUI.setVisible(true);
                                    dispose();

                                } else {
                                    JOptionPane.showMessageDialog(
                                            LoginUI.this,
                                            "Invalid email or password.",
                                            "Sign In Failed",
                                            JOptionPane.WARNING_MESSAGE
                                    );
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

                                JOptionPane.showMessageDialog(
                                        LoginUI.this,
                                        "Unable to sign in. Please try again.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );

                            } finally {
                                Arrays.fill(password, '\0');
                                loginButton.setEnabled(true);
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


/*
 * Gemeinsames Apple-inspiriertes Design.
 *
 * Diese Klasse liegt absichtlich in derselben Datei
 * und kann von RegisterUI und VerificationUI
 * verwendet werden.
 */
class AppleStyle {

    static final Color BACKGROUND = new Color(245, 245, 247);
    static final Color WHITE = Color.WHITE;
    static final Color TEXT = new Color(29, 29, 31);
    static final Color SECONDARY = new Color(110, 110, 115);
    static final Color BLUE = new Color(0, 122, 255);
    static final Color BORDER = new Color(220, 220, 225);

    static final String FONT = "Helvetica Neue";

    static JPanel background() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        return panel;
    }

    static JPanel card() {
        JPanel panel = new RoundedPanel(28);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);

        panel.setBorder(
                new EmptyBorder(38, 40, 38, 40)
        );

        panel.setPreferredSize(new Dimension(400, 510));

        return panel;
    }

    static JLabel icon(String text) {
        JLabel label = new JLabel(text);

        label.setFont(new Font(FONT, Font.PLAIN, 42));
        label.setForeground(BLUE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }

    static JLabel title(String text) {
        JLabel label = new JLabel(text);

        label.setFont(new Font(FONT, Font.BOLD, 27));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }

    static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);

        label.setFont(new Font(FONT, Font.PLAIN, 13));
        label.setForeground(SECONDARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }

    static JLabel label(String text) {
        JLabel label = new JLabel(text);

        label.setFont(new Font(FONT, Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    static void styleField(JTextField field) {
        field.setFont(new Font(FONT, Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setBackground(new Color(250, 250, 252));
        field.setCaretColor(BLUE);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        new EmptyBorder(10, 14, 10, 14)
                )
        );

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46)
        );

        field.setPreferredSize(new Dimension(320, 46));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    static JButton primaryButton(String text) {
        JButton button = new RoundedButton(text, BLUE, WHITE);

        button.setFont(new Font(FONT, Font.BOLD, 15));
        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );
        button.setPreferredSize(new Dimension(320, 48));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    static JButton textButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font(FONT, Font.PLAIN, 13));
        button.setForeground(BLUE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    static class RoundedPanel extends JPanel {

        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedButton extends JButton {

        private final Color backgroundColor;
        private final Color textColor;

        RoundedButton(
                String text,
                Color backgroundColor,
                Color textColor
        ) {
            super(text);

            this.backgroundColor = backgroundColor;
            this.textColor = textColor;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(textColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color color = isEnabled()
                    ? backgroundColor
                    : new Color(170, 190, 215);

            if (getModel().isPressed() && isEnabled()) {
                color = color.darker();
            }

            g2.setColor(color);

            g2.fill(
                    new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            16,
                            16
                    )
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}