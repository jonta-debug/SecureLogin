
import auth.*;
import database.PostgresConnector;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;


import database.UserRepository;
import database.VerificationCodeRepository;
import ui.LoginUI;
import ui.RegisterUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        UserRepository userRepository = new UserRepository();
        PasswordService passwordService = new PasswordService();
        AuthService authService = new AuthService(userRepository, passwordService);
        CodeHashService codeHashService = new CodeHashService();
        VerificationCodeRepository verificationCodeRepository = new VerificationCodeRepository();
        TwoFactorService twoFactorService = new TwoFactorService(codeHashService, verificationCodeRepository);

        EmailService emailService = new EmailService();

        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI(authService, twoFactorService, emailService);
            loginUI.setVisible(true);
        });


        try (Connection connection = PostgresConnector.getConnection()) {
            System.out.println("Connected to the Postgres Database");

            System.out.println("Connected to Database: " + connection.getCatalog());

        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}