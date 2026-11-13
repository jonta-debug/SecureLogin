package auth;

import database.UserRepository;
import model.User;

import java.sql.SQLException;
import java.util.Arrays;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public AuthService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }


    public boolean registerUser(String email, char[] password) throws SQLException {
        if(email == null || password == null) {
            return false;
        }
        if(userRepository.existsByEmail(email)) {
            return false;
        }

        String passwordHash = passwordService.hashPassword(password);
        User newUser = new User(
                0,
                email,
                passwordHash,
                false);

        userRepository.save(newUser);
        return true;
    }

//    public boolean loginUser(String email, char[] password) throws SQLException {
//        try {
//
//            if (email == null || email.isBlank()
//                    || password == null || password.length == 0) {
//                return false;
//            }
//
//            Optional<User> user = userRepository.findByEmail(email);
//            if (user.isEmpty()) return false;
//
//            String storedHash = user.get().getPasswordHash();
//            return passwordService.verifyPassword(password, storedHash);
//        }
//        finally {
//            if(password != null) {
//                Arrays.fill(password, '\0');
//            }
//        }
//    }
    public Optional<User> loginUser(String email, char[] password) throws SQLException {
        try {

            if (email == null || email.isBlank()
                    || password == null || password.length == 0) {
                return Optional.empty();
            }

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) return Optional.empty();

            String storedHash = user.get().getPasswordHash();
            if(passwordService.verifyPassword(password, storedHash)){
                return Optional.of(user.get());
            }
            return Optional .empty();
        }
        finally {
            if(password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }
}
