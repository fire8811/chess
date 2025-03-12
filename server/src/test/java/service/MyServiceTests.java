package service;

import chess.ChessGame;
import dataaccess.*;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MyServiceTests {
    static final UserDAO USER_DAO = new MemoryUserDAO();
    static final GameDAO GAME_DAO = new MemoryGameDAO();
    static final AuthDAO AUTH_DAO = new MemoryAuthDAO();

    final MemoryUserDAO usersMemory = new MemoryUserDAO();
    final MemoryGameDAO gamesMemory = new MemoryGameDAO();
    final MemoryAuthDAO authMemory = new MemoryAuthDAO();

    final UserDAO userDatabase = new SqlUserDAO();
    final AuthDAO authDatabase = new SqlAuthDAO();
    final GameDAO gameDatabase = new SqlGameDAO();
    final AuthData authData = new AuthData(UUID.randomUUID().toString(), "jonbob");

    final ClearService clearServiceMemory = new ClearService(authMemory, usersMemory, gamesMemory);
    final UserService userServiceMemory = new UserService(authMemory, usersMemory, gamesMemory);
    final GameService gameServiceMemory = new GameService(authMemory, gamesMemory);

    public MyServiceTests() throws SQLException, DataAccessException {
    }

    void registerValidUser() throws BadRequestException, UsernameTakenException, DataAccessException, SQLException {
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userServiceMemory.register(registerRequest);
    }

    @BeforeEach
    void configureTest() throws SQLException, DataAccessException {
        clearServiceMemory.clear();
    }

    @Test
    void testRegister() throws DataAccessException, SQLException {
        registerValidUser();

        assertEquals(1, usersMemory.getUsers().size());
        assertTrue(usersMemory.getUsers().containsKey("jonbob"));
    }

    @Test
    void testRegisterNegative() {
        assertThrows(BadRequestException.class, () ->
        userServiceMemory.register(new RegisterRequest("jonbob", "oogabooga", null)));
    }

    @Test
    void testClear() throws DataAccessException, SQLException {
        registerValidUser();

        clearServiceMemory.clear();
        assertEquals(0, usersMemory.getUsers().size());
        assertEquals(0, authMemory.getAuths().size());
        assertEquals(0, gamesMemory.listGames().size());
    }

    @Test
    void testLoginValid() throws DataAccessException, SQLException {
        usersMemory.addUser(new UserData("jonbob", "oui", "email@email.com"));
        LoginResult result = userServiceMemory.login(new LoginRequest("jonbob", "oui"));
        assertEquals("jonbob", result.username());
    }

    @Test
    void testLoginInvalid() throws DataAccessException, SQLException {
        registerValidUser();
        assertThrows(UnauthorizedException.class, () ->
                userServiceMemory.login(new LoginRequest("jonbob", "wii")));
    }

    @Test
    void testLogoutValid() throws DataAccessException, SQLException {
        registerValidUser();
        String authToken = authMemory.getAuths().get(0).authToken();

        userServiceMemory.logout(new LogoutRequest(authToken));
        assertEquals(0, authMemory.getAuths().size());
    }

    @Test
    void testLogoutInvalid() throws UnauthorizedException{
        assertThrows(UnauthorizedException.class, () ->
                userServiceMemory.logout(new LogoutRequest("")));
    }

    @Test
    void testCreateGame() throws DataAccessException, SQLException {
        registerValidUser();
        String authToken = authMemory.getAuths().get(0).authToken();

        gameServiceMemory.createGame(new CreateRequest(authToken, "banana"));

        assertEquals(1, gamesMemory.listGames().size());
    }

    @Test
    void testCreateGameException() throws UnauthorizedException { //create game without an authToken
        assertThrows(UnauthorizedException.class, () ->
                gameServiceMemory.createGame(new CreateRequest("", "banana")));
    }

    @Test
    void testListGames() throws DataAccessException, SQLException {
        registerValidUser();
        String authToken = authMemory.getAuths().get(0).authToken();
        gameServiceMemory.createGame(new CreateRequest(authToken, "banana"));

        ListResult listResult = new ListResult(gamesMemory.listGames());
        GameData actualGame = listResult.games().iterator().next();

        assertEquals(1, actualGame.gameID());
        assertEquals(null, actualGame.whiteUsername());
        assertEquals(null, actualGame.blackUsername());
        assertEquals("banana", actualGame.gameName());
    }

    @Test
    void testListGamesException() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        assertThrows(UnauthorizedException.class, () ->
                gameServiceMemory.listGames(new ListRequest("")));
    }

    @Test
    void testJoinGame() throws DataAccessException, SQLException {
        registerValidUser();
        String authToken = authMemory.getAuths().get(0).authToken();
        gameServiceMemory.createGame(new CreateRequest(authToken, "banana"));
        gamesMemory.updateGame(1, ChessGame.TeamColor.WHITE, "jonbob");

        assertEquals("jonbob", gamesMemory.getGame(1).whiteUsername());
        assertEquals(1, gamesMemory.getGame(1).gameID());
    }

    @Test
    void testJoinGameFail(){
        assertThrows(UnauthorizedException.class, () ->
                gameServiceMemory.joinGame(new JoinRequest("", ChessGame.TeamColor.BLACK, 1)));
    }


//SQL DAO tests below

    @Test
    void testRegisterUserSQL() throws DataAccessException, SQLException {
        //registerValidUser();
        userDatabase.addUser(new UserData("jonbob", "banana", "email@email.com"));
    }

    @Test void testAddAuthdata() throws DataAccessException, SQLException {
        authDatabase.addAuthData(new AuthData(UUID.randomUUID().toString(), "jonbob"));
    }

    @Test void testDeleteAuthData() throws DataAccessException, SQLException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), "bruno");
        authDatabase.addAuthData(authData);
        authDatabase.deleteAuthData(authData.authToken());
    }

    @Test void testFindUser() throws UnauthorizedException, DataAccessException, SQLException {
        assertTrue(userDatabase.findUser("jonbob", "banana"));
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
