package service;

import chess.ChessGame;
import dataaccess.*;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MyServiceTests {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final GameDAO gameDAO = new MemoryGameDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();

    static final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO, gameDAO);
    static final GameService gameService = new GameService(authDAO, gameDAO);

    void registerValidUser() throws BadRequestException, UsernameTakenException{
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userService.register(registerRequest);
    }

    @BeforeEach
    void configureTest(){
        clearService.clear();
    }

    @Test
    void testRegister() throws BadRequestException, UsernameTakenException {
        registerValidUser();

        assertEquals(1, userDAO.getUsers().size());
        assertTrue(userDAO.getUsers().containsKey("jonbob"));
    }

    @Test
    void testRegisterNegative() {
        assertThrows(BadRequestException.class, () ->
        userService.register(new RegisterRequest("jonbob", "oogabooga", null)));
    }

    @Test
    void test_clear() throws BadRequestException, UsernameTakenException{
        registerValidUser();

        clearService.clear();
        assertEquals(0, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuths().size());
        assertEquals(0, gameDAO.getGamesFromMemory().size());
    }

    @Test
    void testLoginValid() throws UnauthorizedException{
        userDAO.addUser(new UserData("jonbob", "oui", "email@email.com"));
        LoginResult result = userService.login(new LoginRequest("jonbob", "oui"));
        assertEquals("jonbob", result.username());
    }

    @Test
    void testLoginInvalid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("jonbob", "wii")));
    }

    @Test
    void testLogoutValid() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        String authToken = authDAO.getAuths().get(0).authToken();

        userService.logout(new LogoutRequest(authToken));
        assertEquals(0, authDAO.getAuths().size());
    }

    @Test
    void testLogoutInvalid() throws UnauthorizedException{
        assertThrows(UnauthorizedException.class, () ->
                userService.logout(new LogoutRequest("")));
    }

    @Test
    void testCreateGame() throws UnauthorizedException, BadRequestException, UsernameTakenException{
        registerValidUser();
        String authToken = authDAO.getAuths().get(0).authToken();

        gameService.createGame(new CreateRequest(authToken, "banana"));

        assertEquals(1, gameDAO.getGamesFromMemory().size());
    }

    @Test
    void testCreateGameException() throws UnauthorizedException { //create game without an authToken
        assertThrows(UnauthorizedException.class, () ->
                gameService.createGame(new CreateRequest("", "banana")));
    }

    @Test
    void testListGames() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        registerValidUser();
        String authToken = authDAO.getAuths().get(0).authToken();
        gameService.createGame(new CreateRequest(authToken, "banana"));

        ListResult listResult = new ListResult(gameDAO.getGamesFromMemory());
        GameData actualGame = listResult.games().iterator().next();

        assertEquals(2, actualGame.gameID());
        assertEquals(null, actualGame.whiteUsername());
        assertEquals(null, actualGame.blackUsername());
        assertEquals("banana", actualGame.gameName());
    }

    @Test
    void testListGamesException() throws UnauthorizedException, BadRequestException, UsernameTakenException {
        assertThrows(UnauthorizedException.class, () ->
                gameService.listGames(new ListRequest("")));
    }

    @Test
    void testJoinGame() throws UnauthorizedException, BadRequestException, UsernameTakenException, AlreadyTakenException {
        registerValidUser();
        String authToken = authDAO.getAuths().get(0).authToken();
        gameService.createGame(new CreateRequest(authToken, "banana"));
        gameDAO.updateGame(1, ChessGame.TeamColor.WHITE, "jonbob");

        assertEquals("jonbob", gameDAO.getGame(1).whiteUsername());
        assertEquals(1, gameDAO.getGame(1).gameID());
    }

    @Test
    void testJoinGameFail(){
        assertThrows(UnauthorizedException.class, () ->
                gameService.joinGame(new JoinRequest("", ChessGame.TeamColor.BLACK, 1)));
    }


}
