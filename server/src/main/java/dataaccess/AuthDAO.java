package dataaccess;
import java.sql.SQLException;

import dataaccess.Exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void clearAuths() throws SQLException, DataAccessException;
    void addAuthData(AuthData authData) throws SQLException, DataAccessException;
    void deleteAuthData(String authToken) throws DataAccessException;
    boolean authTokenExists(String authToken) throws DataAccessException;
    String getUsername(String authToken) throws DataAccessException, SQLException;
}
