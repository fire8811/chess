package dataaccess;

import exceptions.DataAccessException;
import exceptions.ResponseException;
import exceptions.UnauthorizedException;
import exceptions.UsernameTakenException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO implements UserDAO, DatabaseCreator{
    public SqlUserDAO() throws SQLException, DataAccessException {
        configureDatabase(createUserSchema);
    }

    public boolean findUser(String username, String passwordClearText) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT username, password FROM users WHERE username=?")) {
                preparedStatement.setString(1, username);

                try(var result = preparedStatement.executeQuery()){
                    if (pleasePass(passwordClearText, result)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e){
            throw new ResponseException(String.format("Couldn't retrieve username or password from database: %s", e.getMessage()));
        }

        throw new UnauthorizedException("unauthorized");
    }

    private static boolean pleasePass(String passwordClearText, ResultSet result) throws SQLException {
        while(result.next()){
            String storedPassword = result.getString("password");

            if(BCrypt.checkpw(passwordClearText, storedPassword)){
                return true;
            }
        }
        return false;
    }

    public boolean isUsernameFree(String username) throws DataAccessException{
        try(var goodConnection = DatabaseManager.getConnection()){
            var command = "SELECT 1 FROM users WHERE username = ?";
            try (var ps = goodConnection.prepareStatement(command)){
                ps.setString(1, username);
                try (var result = ps.executeQuery()){
                    if(result.next()){ //username already exists in table
                        throw new UsernameTakenException("username already taken");
                    }
                }
            }
        } catch (SQLException e){
            throw new ResponseException(String.format("something bad happened when retrieving usernames: %s", e.getMessage()));
        }

        return true; //username not found in table
    }

    public void addUser(UserData userData) throws SQLException, DataAccessException {
        String username = userData.username();
        String password = hashPassword(userData.password());
        String email = userData.email();

        var command = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        updateTable(command, username, password, email);
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    public void clearUsers() throws SQLException, DataAccessException {
        var command = "TRUNCATE users";
        updateTable(command);
    }

    private void updateTable(String statement, Object... params) throws DataAccessException, SQLException {
        try (var goodConnect = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnect.prepareStatement(statement)){
                for (var i = 0; i < params.length; i++){
                    String param = (String) params[i];
                    preparedStatement.setString(i+1, param);
                }
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new ResponseException(String.format("can't update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createUserSchema = {
        """
        CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            PRIMARY KEY (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
