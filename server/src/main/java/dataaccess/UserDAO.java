package dataaccess;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import exceptions.UsernameTakenException;
import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void clearUsers() throws DataAccessException, SQLException;
    boolean isUsernameFree(String username) throws UsernameTakenException, SQLException, DataAccessException;
    boolean findUser(String username, String password) throws UnauthorizedException, DataAccessException, SQLException;
    void addUser(UserData userData) throws SQLException, DataAccessException;
    //HashMap<String, UserData> getUsers();
}
