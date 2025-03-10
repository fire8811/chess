package dataaccess;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO, DatabaseCreator{
    public SqlUserDAO() throws SQLException, DataAccessException {
        configureDatabase(createUserSchema);
    }

    public void clearUsers(){
        var command = "TRUNCATE users";

    }

    public void updateTable(String statement, Object... params) throws DataAccessException, SQLException {
        try (var goodConnect = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnect.prepareStatement(statement)){
                for (var i = 0; i < params.length; i++){
                    String param = (String) params[i];
                    preparedStatement.setString(i+1, param);
                }
            }
        }
        catch (SQLException e) {
            throw new ResponseException(String.format("can't update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createUserSchema = {
        """
        CREATE TABLE IF NOT EXISTS users (
            'username' varchar(256) NOT NULL,
            'password' varchar(256) NOT NULL,
            'email' varchar(256) NOT NULL,
            PRIMARY KEY ('username')
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
