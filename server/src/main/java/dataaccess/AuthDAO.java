package dataaccess;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {
    void clearAuths() throws SQLException, DataAccessException;
    void addAuthData(AuthData authData) throws SQLException, DataAccessException;
//    ArrayList<AuthData> getAuths();

//    void deleteAuthToken(String authToken) throws UnauthorizedException;
//    int findAuthToken(String authToken) throws UnauthorizedException;
//    String getUsername(String authToken) throws UnauthorizedException;
}
