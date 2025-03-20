package dataaccess;

import exceptions.DataAccessException;
import exceptions.ResponseException;
import exceptions.UnauthorizedException;
import model.AuthData;

import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO, DatabaseCreator {
    public SqlAuthDAO() throws SQLException, DataAccessException {
        configureDatabase(createAuthSchema);
    }

    public String getUsername(String authToken) throws DataAccessException, SQLException {
        try (var goodConnection = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnection.prepareStatement("SELECT username FROM auth WHERE token=?")){
                preparedStatement.setString(1, authToken);

                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){
                        String username = result.getString("username");
                        return username;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("something went wrong when trying to access the auth database: %s", e.getMessage()));
        }
        throw new UnauthorizedException("unauthorized"); //token ont found in database, meaning player isn't authorized
    }

    public boolean authTokenExists(String authToken) throws DataAccessException {
        try (var goodConnection = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnection.prepareStatement("SELECT token FROM auth WHERE token=?")) {
                preparedStatement.setString(1, authToken);

                try (var result = preparedStatement.executeQuery()){
                    if (result.next()){ //token found in database
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("something went wrong when trying to access the auth database: %s", e.getMessage()));
        }
        throw new UnauthorizedException("unauthorized"); //token not found in database meaning player isn't authorized
    }

    public void addAuthData(AuthData authData) throws SQLException, DataAccessException {
        String username = authData.username();
        String authToken = authData.authToken();

        var statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        updateTable(statement, authToken, username);
    }


    public void deleteAuthData(String authToken) throws DataAccessException {
        try (var goodConnect = DatabaseManager.getConnection()){
            try(var preparedStatement = goodConnect.prepareStatement("DELETE FROM auth WHERE token=?")){
                preparedStatement.setString(1, authToken);
                int deletedRow = preparedStatement.executeUpdate();

                if (deletedRow == 0){ //authToken not found in table
                    throw new UnauthorizedException("unauthorized");
                }
            }


        } catch (SQLException e) {
            throw new ResponseException(String.format("something went wrong when trying to delete authData: %s", e.getMessage()));
        }
    }

    public void clearAuths() throws DataAccessException, SQLException {
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
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            throw new ResponseException(String.format("can't update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createAuthSchema = {
        """
        CREATE TABLE IF NOT EXISTS auth (
        `token` varchar(256) NOT NULL,
        `username` varchar(256) NOT NULL,
        PRIMARY KEY (`token`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
