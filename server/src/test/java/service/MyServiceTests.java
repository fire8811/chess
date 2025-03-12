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
    final UserDAO userDatabase = new SqlUserDAO();
    final AuthDAO authDatabase = new SqlAuthDAO();
    final GameDAO gameDatabase = new SqlGameDAO();
    static final AuthData authData = new AuthData(UUID.randomUUID().toString(), "jonbob");

    final ClearService clearService = new ClearService(AUTH_DAO, USER_DAO, GAME_DAO);
    final UserService userService = new UserService(AUTH_DAO, USER_DAO, GAME_DAO);
    final GameService gameService = new GameService(AUTH_DAO, GAME_DAO);

    public MyServiceTests() throws SQLException, DataAccessException {
    }

    void registerValidUser() throws BadRequestException, UsernameTakenException, DataAccessException, SQLException {
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userService.register(registerRequest);
    }

//    @BeforeEach
//    void configureTest(){
//        clearService.clear();
//    }

//    @Test
//    void testRegister() throws BadRequestException, UsernameTakenException {
//        registerValidUser();
//
//        assertEquals(1, USER_DAO.getUsers().size());
//        assertTrue(USER_DAO.getUsers().containsKey("jonbob"));
//    }
//
//    @Test
//    void testRegisterNegative() {
//        assertThrows(BadRequestException.class, () ->
//        userService.register(new RegisterRequest("jonbob", "oogabooga", null)));
//    }
//
//    @Test
//    void testClear() throws BadRequestException, UsernameTakenException{
//        registerValidUser();
//
//        clearService.clear();
//        assertEquals(0, USER_DAO.getUsers().size());
//        assertEquals(0, AUTH_DAO.getAuths().size());
//        assertEquals(0, GAME_DAO.getGamesFromMemory().size());
//    }
//
//    @Test
//    void testLoginValid() throws UnauthorizedException{
//        USER_DAO.addUser(new UserData("jonbob", "oui", "email@email.com"));
//        LoginResult result = userService.login(new LoginRequest("jonbob", "oui"));
//        assertEquals("jonbob", result.username());
//    }
//
//    @Test
//    void testLoginInvalid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
//        registerValidUser();
//        assertThrows(UnauthorizedException.class, () ->
//                userService.login(new LoginRequest("jonbob", "wii")));
//    }
//
//    @Test
//    void testLogoutValid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
//        registerValidUser();
//        String authToken = AUTH_DAO.getAuths().get(0).authToken();
//
//        userService.logout(new LogoutRequest(authToken));
//        assertEquals(0, AUTH_DAO.getAuths().size());
//    }
//
//    @Test
//    void testLogoutInvalid() throws UnauthorizedException{
//        assertThrows(UnauthorizedException.class, () ->
//                userService.logout(new LogoutRequest("")));
//    }
//
//    @Test
//    void testCreateGame() throws UnauthorizedException, BadRequestException, UsernameTakenException{
//        registerValidUser();
//        String authToken = AUTH_DAO.getAuths().get(0).authToken();
//
//        gameService.createGame(new CreateRequest(authToken, "banana"));
//
//        assertEquals(1, GAME_DAO.getGamesFromMemory().size());
//    }
//
//    @Test
//    void testCreateGameException() throws UnauthorizedException { //create game without an authToken
//        assertThrows(UnauthorizedException.class, () ->
//                gameService.createGame(new CreateRequest("", "banana")));
//    }
//
//    @Test
//    void testListGames() throws UnauthorizedException, BadRequestException, UsernameTakenException {
//        registerValidUser();
//        String authToken = AUTH_DAO.getAuths().get(0).authToken();
//        gameService.createGame(new CreateRequest(authToken, "banana"));
//
//        ListResult listResult = new ListResult(GAME_DAO.getGamesFromMemory());
//        GameData actualGame = listResult.games().iterator().next();
//
//        assertEquals(2, actualGame.gameID());
//        assertEquals(null, actualGame.whiteUsername());
//        assertEquals(null, actualGame.blackUsername());
//        assertEquals("banana", actualGame.gameName());
//    }
//
//    @Test
//    void testListGamesException() throws UnauthorizedException, BadRequestException, UsernameTakenException {
//        assertThrows(UnauthorizedException.class, () ->
//                gameService.listGames(new ListRequest("")));
//    }
//
//    @Test
//    void testJoinGame() throws UnauthorizedException, BadRequestException, UsernameTakenException, AlreadyTakenException {
//        registerValidUser();
//        String authToken = AUTH_DAO.getAuths().get(0).authToken();
//        gameService.createGame(new CreateRequest(authToken, "banana"));
//        GAME_DAO.updateGame(1, ChessGame.TeamColor.WHITE, "jonbob");
//
//        assertEquals("jonbob", GAME_DAO.getGame(1).whiteUsername());
//        assertEquals(1, GAME_DAO.getGame(1).gameID());
//    }
//
//    @Test
//    void testJoinGameFail(){
//        assertThrows(UnauthorizedException.class, () ->
//                gameService.joinGame(new JoinRequest("", ChessGame.TeamColor.BLACK, 1)));
//    }

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
        userDatabase.clearUsers();;
    }

    @Test void testTokenFinding() throws DataAccessException {
        assertTrue(authDatabase.authTokenExists("91a3d9f6-b426-4a0e-aaef-6af4590548c3"));
    }

    @Test void testGetUsernameFromAuth() throws SQLException, DataAccessException {
        assertEquals("jonbob", authDatabase.getUsername("91a3d9f6-b426-4a0e-aaef-6af4590548c3"));
    }

    @Test void testCreateGame() throws SQLException, DataAccessException {
        gameDatabase.createGame("juego");
    }

    @Test void testListGames() throws DataAccessException {
        ArrayList<GameData> gamesList = (ArrayList<GameData>) gameDatabase.listGames();
        assertEquals(1, gamesList.size());

    }
}
