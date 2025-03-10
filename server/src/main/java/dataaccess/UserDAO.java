package dataaccess;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.HashMap;

public interface UserDAO {
    void clearUsers() throws DataAccessException, SQLException;
    boolean isUsernameFree(String username) throws UsernameTakenException, SQLException, DataAccessException;
//    boolean findUser(String username, String password) throws UnauthorizedException;
    void addUser(UserData userData) throws SQLException, DataAccessException;
//    HashMap<String, UserData> getUsers();
}
