package dataaccess;

import chess.ChessGame;
import exceptions.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;
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

    //game SQL tests below
    @Test void testClearGames() throws SQLException, DataAccessException {
        gameDatabase.createGame("gameName");
        Collection<GameData> gameDataList = gameDatabase.listGames();
        assertEquals(1, gameDataList.size());

        gameDatabase.clearGames();
        gameDataList = gameDatabase.listGames();
        assertEquals(0, gameDataList.size());
    }

    @Test void testCreateGame() throws SQLException, DataAccessException {
        assertEquals(1, gameDatabase.createGame("GameName"));
    }

    @Test void testCreateGameFail() {
        assertThrows(ResponseException.class, ()-> gameDatabase.createGame(null));
    }

    @Test void testFindGame() throws SQLException, DataAccessException {
        gameDatabase.createGame("game");
        assertTrue(gameDatabase.findGame(1));
    }

    @Test void testFindGameFail() throws DataAccessException {
        assertFalse(gameDatabase.findGame(1));
    }

    @Test void testListGames() throws SQLException, DataAccessException {
        gameDatabase.createGame("game1");
        gameDatabase.createGame("game2");

        Collection<GameData> gamesList = gameDatabase.listGames();

        assertEquals(2, gamesList.size());
    }

    @Test void testListGames2() throws DataAccessException {
        Collection<GameData> gamesList = gameDatabase.listGames();
        assertEquals(0, gamesList.size());
    }

    @Test void testUpdateGame() throws SQLException, DataAccessException {
        gameDatabase.createGame("game");
        assertDoesNotThrow(()->gameDatabase.updateGame(1, ChessGame.TeamColor.BLACK, "username"));
    }

    @Test void testUpdateGameFail() throws SQLException, DataAccessException {
        gameDatabase.createGame("a_game");
        gameDatabase.updateGame(1, ChessGame.TeamColor.BLACK, "player1");

        assertThrows(AlreadyTakenException.class, () -> gameDatabase.updateGame(1, ChessGame.TeamColor.BLACK, "player2"));
    }
}