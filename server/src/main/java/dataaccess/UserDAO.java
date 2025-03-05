package dataaccess;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public interface UserDAO {
    void clearUsers();
    boolean isUsernameFree(String username) throws UsernameTakenException;
    boolean findUser(String username) throws DataAccessException;
    void addUser(UserData userData);
    HashMap<String, UserData> getUsers();
}
