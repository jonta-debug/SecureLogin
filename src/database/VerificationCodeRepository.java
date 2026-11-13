package database;

import auth.TwoFactorService;
import model.VerificationCode;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VerificationCodeRepository {

    public void save(VerificationCode code) throws SQLException {
        String query = """
                INSERT INTO verification_codes (user_id, code_hash, expires_at)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = PostgresConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){

            statement.setLong(1, code.getUserId());
            statement.setString(2, code.getCodeHash());
            statement.setObject(
                    3,
                    OffsetDateTime.ofInstant(code.getExpiresAt(), ZoneOffset.UTC)
            );
            statement.executeUpdate();

        }
    }

    public Optional<VerificationCode> findActiveByUserId(long userId)
        throws SQLException {

        String query = """
                SELECT * 
                FROM verification_codes
                WHERE user_id = ?
                    AND used = FALSE
                    AND expires_at > CURRENT_TIMESTAMP
                    AND attempts < 5
                ORDER BY created_at DESC, id DESC
                LIMIT 1;
                """;

        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    VerificationCode vc = new VerificationCode(
                            resultSet.getLong("id"),
                            resultSet.getLong("user_id"),
                            resultSet.getString("code_hash"),
                            resultSet.getObject("expires_at", OffsetDateTime.class).toInstant(),
                            resultSet.getInt("attempts"),
                            resultSet.getBoolean("used"),
                            (UUID) resultSet.getObject("challenge_id")
                    );
                    return Optional.of(vc);
                }
            }
        }
        return Optional.empty();
    }

    public void incrementAttempts(long id) throws SQLException {
        String query = """
                UPDATE verification_codes
                SET attempts = attempts +1
                WHERE id = ?
                """;

        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public void markAsUsed(long id) throws SQLException {
        String query = """
                UPDATE verification_codes
                SET used = TRUE
                WHERE id = ?
                """;
        try (Connection connection = PostgresConnector.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);

            statement.executeUpdate();
        }
    }

    public boolean verifyAndConsumeCode(long userId, UUID challengeId, String enteredHash)
            throws SQLException {

        String query = """
            SELECT id, code_hash
            FROM verification_codes
            WHERE user_id = ? AND challenge_id = ?
              AND used = FALSE
              AND expires_at > CURRENT_TIMESTAMP
              AND attempts < 5
            ORDER BY created_at DESC, id DESC
            LIMIT 1
            FOR UPDATE
            """;

        try (Connection connection = PostgresConnector.getConnection()) {

            connection.setAutoCommit(false);

            try {

                try (PreparedStatement statement =
                             connection.prepareStatement(query)) {

                    statement.setLong(1, userId);
                    statement.setObject(2, challengeId);

                    try (ResultSet resultSet = statement.executeQuery()) {

                        if (!resultSet.next()) {
                            connection.commit();
                            return false;
                        }

                        long codeId = resultSet.getLong("id");
                        String storedHash = resultSet.getString("code_hash");

                        boolean verified = MessageDigest.isEqual(
                                Base64.getDecoder().decode(storedHash),
                                Base64.getDecoder().decode(enteredHash)
                        );

                        String updateQuery = verified
                                ? "UPDATE verification_codes SET used = TRUE WHERE id = ?"
                                : "UPDATE verification_codes SET attempts = attempts + 1 WHERE id = ?";

                        try (PreparedStatement update =
                                     connection.prepareStatement(updateQuery)) {

                            update.setLong(1, codeId);
                            update.executeUpdate();
                        }

                        connection.commit();

                        return verified;
                    }
                }

            } catch (SQLException | RuntimeException ex) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {

                    ex.addSuppressed(rollbackError);
                }

                throw ex;
            }
        }
    }

    public void invalidatePreviousCodes(long userId) throws SQLException {
        String query = """
            UPDATE verification_codes
            SET used = TRUE
            WHERE user_id = ?
              AND used = FALSE;
        """;

        try (Connection connection = PostgresConnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    public void replaceActiveCode(VerificationCode code) throws SQLException {
        // 1. Eine Connection öffnen
        // 2. Transaktion starten
        // 3. Bisherige Codes ungültig machen
        // 4. Neuen Code speichern
        // 5. commit()

        Map<String, String>  queries = Map.of(
                "lockQuery",
                """              
                    SELECT id
                    FROM users            
                    WHERE id = ?           
                    FOR UPDATE
                """ ,

                "invalidateQuery",
                """
                    UPDATE verification_codes
                    SET used = TRUE
                    WHERE user_id = ?
                      AND used = FALSE;
                 """,

                "newCodeQuery",
                """
                INSERT INTO verification_codes (user_id, code_hash, expires_at, challenge_id)
                VALUES (?, ?, ?, ?);
                """
        );
        try (Connection connection = PostgresConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(queries.get("lockQuery"));){

            connection.setAutoCommit(false);

            statement.setLong(1, code.getUserId());
            try(ResultSet resultSet = statement.executeQuery()) {
                if(!resultSet.next()){
                    throw new SQLException("User not found");
                }

                try(PreparedStatement invalidateQuery = connection.prepareStatement(queries.get("invalidateQuery"))){
                    invalidateQuery.setLong(1, code.getUserId());
                    invalidateQuery.executeUpdate();
                }
                try(PreparedStatement newCodeQuery = connection.prepareStatement(queries.get("newCodeQuery"))){
                    newCodeQuery.setLong(1, code.getUserId());
                    newCodeQuery.setString(2, code.getCodeHash());
                    newCodeQuery.setObject(
                            3,
                            OffsetDateTime.ofInstant(
                                    code.getExpiresAt(),
                                    ZoneOffset.UTC
                            )
                    );
                    newCodeQuery.setObject(4, code.getChallengeId());
                    newCodeQuery.executeUpdate();
                }


                connection.commit();
            } catch(SQLException | RuntimeException ex){
                connection.rollback();
                throw ex;
            }
        }
    }


}
