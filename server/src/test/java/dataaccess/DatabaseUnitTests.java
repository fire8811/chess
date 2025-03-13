package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseUnitTests {
    final UserDAO userDatabase = new SqlUserDAO();
    final AuthDAO authDatabase = new SqlAuthDAO();
    final GameDAO gameDatabase = new SqlGameDAO();

    public DatabaseUnitTests() throws SQLException, DataAccessException {
    }

    @BeforeEach
    void clearUsers() throws SQLException, DataAccessException {
        userDatabase.clearUsers();
        authDatabase.clearAuths();
        gameDatabase.clearGames();
    }

    //userSQL tests
    @Test void testClearUsers() throws SQLException, DataAccessException {
        userDatabase.addUser(new UserData("yeee", "haww", "email@email.com"));
        assertDoesNotThrow(userDatabase::clearUsers);
    }

    @Test void testAddUser() {
        assertDoesNotThrow(() -> userDatabase.addUser(new UserData("jonbob", "banana", "email@email.com")));
    }

    @Test void testAddUserFail() {
        assertThrows(ResponseException.class, () -> userDatabase.addUser(new UserData(null, "oooo", "email@email.com")));
    }

    @Test void testIsUsernameFree() {
        assertDoesNotThrow(() -> userDatabase.isUsernameFree("username"));
    }

    @Test void testIsUsernameFreeFail() throws SQLException, DataAccessException {
        userDatabase.addUser(new UserData("username", "password", "email"));
        assertThrows(UsernameTakenException.class, () -> userDatabase.isUsernameFree("username"));
    }

    @Test void testFindUser() throws DataAccessException, SQLException {
        userDatabase.addUser(new UserData("jonbob", "banana", "email"));
        assertTrue(userDatabase.findUser("jonbob", "banana"));
    }

    @Test void testFindUserFail() throws SQLException, DataAccessException {
        userDatabase.addUser(new UserData("jonbob1", "banana", "email"));
        assertThrows(UnauthorizedException.class, () -> userDatabase.findUser("jonbob1", "apple"));
    }

    //authSQL tests
    @Test void testClearAuths() throws SQLException, DataAccessException {
        authDatabase.addAuthData(new AuthData("token", "username"));
        assertDoesNotThrow(authDatabase::clearAuths);
    }

    @Test void testAddAuthData() {
        assertDoesNotThrow(() -> authDatabase.addAuthData(new AuthData("token", "jonbob")));
    }

    @Test void testAddAuthDataFail() {
        assertThrows(ResponseException.class, () -> authDatabase.addAuthData(new AuthData(null, "username")));
    }

    @Test void testDeleteAuthData() throws DataAccessException, SQLException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), "bruno");
        authDatabase.addAuthData(authData);
        assertDoesNotThrow(() -> authDatabase.deleteAuthData(authData.authToken()));
    }

    @Test void testDeleteAuthDataFail() throws DataAccessException, SQLException {
        assertThrows(UnauthorizedException.class, ()-> authDatabase.deleteAuthData("non-existent-token"));
    }

    @Test void testAuthTokenExists() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("authToken", "bruno");
        authDatabase.addAuthData(authData);

        assertDoesNotThrow(() -> authDatabase.authTokenExists("authToken"));
    }

    @Test void testAuthTokenExistsFail() {
        assertThrows(UnauthorizedException.class, () -> authDatabase.authTokenExists("non-existent-token"));
    }

    @Test void testGetUsername() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("authToken", "jonbob");
        authDatabase.addAuthData(authData);
        assertDoesNotThrow(()-> authDatabase.getUsername("authToken"));
    }

    @Test void testGetUsernameFail() {
        assertThrows(UnauthorizedException.class, ()-> authDatabase.getUsername("non-existent-token"));
    }





    @Test void testAddAuthdata() throws DataAccessException, SQLException {
        authDatabase.addAuthData(new AuthData(UUID.randomUUID().toString(), "jonbob"));
    }



    @Test void testClearSQL() throws SQLException, DataAccessException {
        authDatabase.clearAuths();
        userDatabase.clearUsers();
        gameDatabase.clearGames();
    }

    @Test void testTokenFinding() throws DataAccessException {
        assertTrue(authDatabase.authTokenExists("91a3d9f6-b426-4a0e-aaef-6af4590548c3"));
    }

    @Test void testGetUsernameFromAuth() throws SQLException, DataAccessException {
        assertEquals("jonbob", authDatabase.getUsername("91a3d9f6-b426-4a0e-aaef-6af4590548c3"));
    }

    @Test void testCreateGameSQL() throws SQLException, DataAccessException {
        gameDatabase.createGame("juego");
    }

    @Test void testListGamesSQL() throws DataAccessException {
        ArrayList<GameData> gamesList = (ArrayList<GameData>) gameDatabase.listGames();
        assertEquals(1, gamesList.size());
    }

    @Test void testJoinGameSQL() throws DataAccessException {
        gameDatabase.updateGame(1, ChessGame.TeamColor.WHITE, "jonbob");
    }
}
