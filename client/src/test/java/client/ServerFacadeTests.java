package client;

import chess.ChessGame;
import model.RegisterRequest;
import model.*;
import org.junit.jupiter.api.*;
import exceptions.*;
import server.Server;
import serverfacade.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    public RegisterResult registerValidUser(){
        return serverFacade.registerUser(new RegisterRequest("jj", "jj1", "email"));
    }

    @BeforeAll
    public static void init() {
        server = new Server();

        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(String.format("http://127.0.0.1:%s", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void clearServer() {serverFacade.clearServer();}

    @Test
    public void registerUserTest() {
        RegisterResult result = registerValidUser();
        assertEquals("jj", result.username());
        assertNotEquals(null, result.authToken());
    }

    @Test
    public void registerUserFail() {
        //serverFacade.registerUser(new RegisterRequest("jj", "jj1", "email"));
        assertThrows(ResponseException.class, () -> serverFacade.registerUser(new RegisterRequest(null, "jj1", "email")));
    }

    @Test
    public void loginUser() {
        String authToken = getAuthToken();
        serverFacade.logoutUser(new LogoutRequest(authToken));

        LoginResult result = serverFacade.loginUser(new LoginRequest("jj", "jj1"));
        assertNotEquals(null, result.authToken());
    }

    @Test
    public void loginUserFail() {
        registerValidUser();
        assertThrows(ResponseException.class, () -> serverFacade.loginUser(new LoginRequest("jj", "badPassword")));
    }

    @Test
    public void logoutUser() {
        String authToken = getAuthToken();

        assertDoesNotThrow(()-> serverFacade.logoutUser(new LogoutRequest(authToken)));
    }

    @Test
    public void logoutUserFail() {
        registerValidUser();
        assertThrows(ResponseException.class, () -> serverFacade.logoutUser((new LogoutRequest("bad_authtoken"))));
    }

    @Test
    public void createGame(){
        String authToken = getAuthToken();
        assertDoesNotThrow(() -> serverFacade.createGame(new CreateRequest(authToken, "gameName")));
    }

    @Test
    public void createGameFail() {
        assertThrows(ResponseException.class, ()-> serverFacade.createGame(new CreateRequest("badAuthtoken", "gamename")));
    }

    @Test
    public void listGames(){
        String authToken = getAuthToken();
        serverFacade.createGame(new CreateRequest(authToken, "game"));

        ListResult gameList = serverFacade.listGames(new ListRequest(authToken));

        assertEquals(1, gameList.games().size());
    }
    
    @Test
    public void listGamesFail(){
        assertThrows(ResponseException.class, () -> serverFacade.listGames(new ListRequest("badToken")));
    }

    @Test
    public void joinGame(){
        String authToken = getAuthToken();
        CreateResult createResult = serverFacade.createGame(new CreateRequest(authToken, "a_game"));
        int gameId = createResult.gameID();

        assertDoesNotThrow(()-> serverFacade.joinGame(new JoinRequest(authToken, ChessGame.TeamColor.BLACK, gameId)));
    }

    @Test
    public void joinGameFail(){
        String authToken = getAuthToken();
        assertThrows(ResponseException.class, ()->serverFacade.joinGame(new JoinRequest(authToken, ChessGame.TeamColor.BLACK, 5)));
    }

    private String getAuthToken() {
        RegisterResult registerResult = registerValidUser(); //register the user and then log out user to test login
        String authToken = registerResult.authToken();
        return authToken;
    }


}
