package service;

import chess.ChessGame;
import dataaccess.*;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyServiceTests {
    static final UserDAO UserDOA = new MemoryUserDAO();
    static final GameDAO GameDAO = new MemoryGameDAO();
    static final AuthDAO AuthDAO = new MemoryAuthDAO();

    static final ClearService ClearService = new ClearService(AuthDAO, UserDOA, GameDAO);
    UserService UserService = new UserService(AuthDAO, UserDOA, GameDAO);
    static final GameService GameService = new GameService(AuthDAO, GameDAO);

    void registerValidUser() throws BadRequestException, UsernameTakenException{
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        UserService.register(registerRequest);
    }

    @BeforeEach
    void configureTest(){
        ClearService.clear();
    }

    @Test
    void testRegister() throws BadRequestException, UsernameTakenException {
        registerValidUser();

        assertEquals(1, UserDOA.getUsers().size());
        assertTrue(UserDOA.getUsers().containsKey("jonbob"));
    }

    @Test
    void testRegisterNegative() {
        assertThrows(BadRequestException.class, () ->
        UserService.register(new RegisterRequest("jonbob", "oogabooga", null)));
    }

    @Test
    void testClear() throws BadRequestException, UsernameTakenException{
        registerValidUser();

        ClearService.clear();
        assertEquals(0, UserDOA.getUsers().size());
        assertEquals(0, AuthDAO.getAuths().size());
        assertEquals(0, GameDAO.getGamesFromMemory().size());
    }

    @Test
    void testLoginValid() throws UnauthorizedException{
        UserDOA.addUser(new UserData("jonbob", "oui", "email@email.com"));
        LoginResult result = UserService.login(new LoginRequest("jonbob", "oui"));
        assertEquals("jonbob", result.username());
    }

    @Test
    void testLoginInvalid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        assertThrows(UnauthorizedException.class, () ->
                UserService.login(new LoginRequest("jonbob", "wii")));
    }

    @Test
    void testLogoutValid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        String authToken = AuthDAO.getAuths().get(0).authToken();

        UserService.logout(new LogoutRequest(authToken));
        assertEquals(0, AuthDAO.getAuths().size());
    }

    @Test
    void testLogoutInvalid() throws UnauthorizedException{
        assertThrows(UnauthorizedException.class, () ->
                UserService.logout(new LogoutRequest("")));
    }

    @Test
    void testCreateGame() throws UnauthorizedException, BadRequestException, UsernameTakenException{
        registerValidUser();
        String authToken = AuthDAO.getAuths().get(0).authToken();

        GameService.createGame(new CreateRequest(authToken, "banana"));

        assertEquals(1, GameDAO.getGamesFromMemory().size());
    }

    @Test
    void testCreateGameException() throws UnauthorizedException { //create game without an authToken
        assertThrows(UnauthorizedException.class, () ->
                GameService.createGame(new CreateRequest("", "banana")));
    }

    @Test
    void testListGames() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        String authToken = AuthDAO.getAuths().get(0).authToken();
        GameService.createGame(new CreateRequest(authToken, "banana"));

        ListResult listResult = new ListResult(GameDAO.getGamesFromMemory());
        GameData actualGame = listResult.games().iterator().next();

        assertEquals(2, actualGame.gameID());
        assertEquals(null, actualGame.whiteUsername());
        assertEquals(null, actualGame.blackUsername());
        assertEquals("banana", actualGame.gameName());
    }

    @Test
    void testListGamesException() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        assertThrows(UnauthorizedException.class, () ->
                GameService.listGames(new ListRequest("")));
    }

    @Test
    void testJoinGame() throws UnauthorizedException, BadRequestException, UsernameTakenException, AlreadyTakenException {
        registerValidUser();
        String authToken = AuthDAO.getAuths().get(0).authToken();
        GameService.createGame(new CreateRequest(authToken, "banana"));
        GameDAO.updateGame(1, ChessGame.TeamColor.WHITE, "jonbob");

        assertEquals("jonbob", GameDAO.getGame(1).whiteUsername());
        assertEquals(1, GameDAO.getGame(1).gameID());
    }

    @Test
    void testJoinGameFail(){
        assertThrows(UnauthorizedException.class, () ->
                GameService.joinGame(new JoinRequest("", ChessGame.TeamColor.BLACK, 1)));
    }


}
