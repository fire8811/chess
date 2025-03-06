package service;

import dataaccess.*;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MyServiceTests {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final GameDAO gameDAO = new MemoryGameDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();

    static final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO, gameDAO);

    void registerValidUser() throws BadRequestException, UsernameTakenException{
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userService.register(registerRequest);
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


}
