package database;
import model.User;

import java.sql.*;
import java.util.Optional;

public class UserRepository {

    //1. save User
    public void save(User user)throws SQLException{

        String query = """
                INSERT INTO users (email, password_hash)
                VALUES (?, ?)
                """;

        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPasswordHash());
            statement.executeUpdate();
        }
    }

    //2. find User
    public Optional<User> findByEmail(String email) throws SQLException{

        String query = """
                SELECT id, email, password_hash, email_verified
                FROM users 
                WHERE email = ?;
                """;

        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, email);

            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()){

                    User user = new User(
                            resultSet.getLong("id"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getBoolean("email_verified")
                    );

                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }
    //3. check if email exists
    public boolean existsByEmail(String email) throws SQLException{
        String query = """
                SELECT 1 FROM users WHERE email = ?;
                """;
        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, email);

            try(ResultSet resultSet = statement.executeQuery()){
                return (resultSet.next());
            }
        }
    }

    public Optional<User> findByID(long id) throws SQLException{

        String query = """
                SELECT id, email, password_hash, email_verified
                FROM users
                WHERE id = ?;
                """;
        try (Connection connection = PostgresConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    User user = new User(
                            resultSet.getLong("id"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getBoolean("email_verified")
                    );
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }



}
