package dataaccess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import model.AuthData;

public interface AuthDAO {
    void clearAuthtokens();
    void addAuthData(AuthData authData);
    ArrayList<AuthData> getAuths();
    void deleteAuthToken(String authToken) throws UnauthorizedException;
    int findAuthToken(String authToken) throws UnauthorizedException;
    String getUsername(String authToken) throws UnauthorizedException;
}
