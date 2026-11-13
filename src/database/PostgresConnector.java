package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnector {


    private static final String DATABASE = "secure_login";
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE;
    private static final String USER = "olelaufer";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                DB_PASSWORD
        );
    }
}
