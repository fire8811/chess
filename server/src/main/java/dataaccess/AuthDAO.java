package dataaccess;
import java.util.Collection;
import java.util.HashMap;

import model.AuthData;

public interface AuthDAO {
    void clearAuthtokens();
    void addAuthData(AuthData authData);
    HashMap<String, String> getAuths();
}
