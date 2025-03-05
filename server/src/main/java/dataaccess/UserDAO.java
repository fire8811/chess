package dataaccess;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    void clearUsers();
    boolean isUsernameFree(String username) throws DataAccessException;
    boolean findUser(String username) throws DataAccessException;
    void addUser(UserData userData) throws DataAccessException;
}
