package auth;

import database.UserRepository;
import model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthServiceTest {
    public static void main(String[] args) throws SQLException {
        UserRepository userRepository = new UserRepository();
        PasswordService passwordService = new PasswordService();
        AuthService authService = new AuthService(userRepository, passwordService);

        String newEmail = "malou@example.com";
        char[] newPassword = "newPassword".toCharArray();

        boolean registerSuccess = authService.registerUser(newEmail, newPassword);
        System.out.println(registerSuccess);

        Optional<User> user = userRepository.findByEmail(newEmail);
        if(user.isPresent()) {
            User foundUser = user.get();
            String storedHash = foundUser.getPasswordHash();

            char[] correctPassword = "newPassword".toCharArray();
            char[] wrongPassword = "hello".toCharArray();


            boolean correct = passwordService.verifyPassword(correctPassword, storedHash);
            boolean wrong =  passwordService.verifyPassword(wrongPassword, storedHash);
            System.out.println("Test mit korrektem Password: " + correct);
            System.out.println("Test mit falschem Password: " + wrong);

        }















    }
}
