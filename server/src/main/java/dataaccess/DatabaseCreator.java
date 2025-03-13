package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;

import java.sql.SQLException;

public interface DatabaseCreator {
    public default void configureDatabase(String[] schema) throws ResponseException, DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : schema) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("Can't configure database: %s", e.getMessage()));
        }
    }
}
