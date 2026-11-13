package ui;

import auth.PendingLogin;
import auth.TwoFactorService;

import javax.swing.*;
import java.awt.*;

public class VerificationUI extends JFrame {

    private JTextField codeField;

    private final PendingLogin pendingLogin;
    private final TwoFactorService twoFactorService;

    public VerificationUI(
            PendingLogin pendingLogin,
            TwoFactorService twoFactorService
    ) {
        this.pendingLogin = pendingLogin;
        this.twoFactorService = twoFactorService;

        setTitle("SecureLogin - Verification");
        setSize(500, 550);
        setMinimumSize(new Dimension(450, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = AppleStyle.background();
        JPanel card = AppleStyle.card();

        card.setPreferredSize(new Dimension(400, 430));

        // Header
        card.add(AppleStyle.icon("✉"));
        card.add(Box.createVerticalStrut(18));

        card.add(AppleStyle.title("Verify your identity"));
        card.add(Box.createVerticalStrut(10));

        card.add(AppleStyle.subtitle(
                "We've sent a code to your email"
        ));

        card.add(Box.createVerticalStrut(38));

        // Code input
        card.add(AppleStyle.label("Verification code"));
        card.add(Box.createVerticalStrut(8));

        codeField = new JTextField();
        AppleStyle.styleField(codeField);

        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setFont(
                new Font(AppleStyle.FONT, Font.BOLD, 22)
        );

        card.add(codeField);

        card.add(Box.createVerticalStrut(12));

        JLabel hint = AppleStyle.subtitle(
                "Enter the 6-digit code from your email"
        );

        card.add(hint);

        card.add(Box.createVerticalStrut(30));

        // Verify button
        JButton submitButton =
                AppleStyle.primaryButton("Verify Code");

        card.add(submitButton);

        submitButton.addActionListener(e -> {

            String code = codeField.getText().trim();

            if (!code.matches("[0-9]{6}")) {
                JOptionPane.showMessageDialog(
                        VerificationUI.this,
                        "Please enter a valid 6-digit code.",
                        "Invalid Code",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            submitButton.setEnabled(false);
            codeField.setEnabled(false);

            SwingWorker<Boolean, Void> worker =
                    new SwingWorker<>() {

                        @Override
                        protected Boolean doInBackground()
                                throws Exception {

                            return twoFactorService.verifyCode(
                                    pendingLogin.userId(),
                                    pendingLogin.challengeId(),
                                    code
                            );
                        }

                        @Override
                        protected void done() {
                            try {
                                boolean verified = get();

                                if (verified) {
                                    JOptionPane.showMessageDialog(
                                            VerificationUI.this,
                                            "Identity verified successfully!",
                                            "Success",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );

                                    // Hier kommt später das Dashboard.
                                    // Noch keine Session vorhanden.

                                    codeField.setText("");

                                } else {
                                    JOptionPane.showMessageDialog(
                                            VerificationUI.this,
                                            "Invalid or expired verification code.",
                                            "Verification Failed",
                                            JOptionPane.WARNING_MESSAGE
                                    );

                                    codeField.setText("");
                                    codeField.requestFocusInWindow();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

                                JOptionPane.showMessageDialog(
                                        VerificationUI.this,
                                        "An error occurred during verification.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );

                            } finally {
                                submitButton.setEnabled(true);
                                codeField.setEnabled(true);
                            }
                        }
                    };

            worker.execute();
        });

        background.add(card);
        setContentPane(background);
    }
}