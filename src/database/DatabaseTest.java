package database;

import database.UserRepository;
import model.User;

import java.sql.SQLException;
import java.util.Optional;

public class DatabaseTest {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();

        String testEmail = "jan@example.com";

        User janUser = new User(45, testEmail,
                "Jans_hashPassword", true);

        try {
            boolean exists =
                    repository.existsByEmail(testEmail);
            //1. Test
            if(!exists){
                repository.save(janUser);
            }


            //2. Test
            Optional<User> user =
                    repository.findByEmail(testEmail);

            if (user.isPresent()) {

                User foundUser = user.get();

                System.out.println("Benutzer gefunden!");
                System.out.println("ID: " + foundUser.getId());
                System.out.println("E-Mail: " + foundUser.getEmail());
                System.out.println("Verifiziert: " + foundUser.isVerified());

            } else {
                System.out.println("Benutzer nicht gefunden!");
            }

            //3. Test

            boolean existsAfterSave = repository.existsByEmail(testEmail);

            if(exists){
                System.out.println("Email exists");
            } else {
                System.out.println("Email doesn't exist");
            }


            //4. Test
            if (user.isPresent()) {
                long id = user.get().getId();

                Optional<User> userById = repository.findByID(id);
                if(userById.isPresent()){
                    System.out.println("ID: " + user.get().getId());

                }
            }
            else {
                System.out.println("ID doesn't exist");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}