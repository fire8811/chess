package dataaccess;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO {
    public SqlAuthDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    private void clearDatabase() throws SQLException, DataAccessException {
        var command = "TRUNCATE auth";
        updateTable(command);
    }

    private void updateTable(String statement, Object... params) throws DataAccessException, SQLException {
        try (var goodConnect = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnect.prepareStatement(statement)){
                for (int i = 0; i < params.length; i++){
                    String param = (String) params[i];
                    preparedStatement.setString(i+1, param);
                }
            }
        }
    }

    private final String[] createAuthSchema = {
        """
        CREATE TABLE IF NOT EXISTS auth (
        'token' varchar(256) NOT NULL
        'username' varchar(256) NOT NULL
        PRIMARY KEY ('token')
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private void configureDatabase() throws ResponseException, DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createAuthSchema) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("Can't configure database: %s", e.getMessage()));
        }
    }
}
