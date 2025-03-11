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
//    ArrayList<AuthData> getAuths(); //needed for my memory data tests

    void deleteAuthData(String authToken) throws DataAccessException;
    boolean authTokenExists(String authToken) throws DataAccessException;
    String getUsername(String authToken) throws DataAccessException, SQLException;
}
