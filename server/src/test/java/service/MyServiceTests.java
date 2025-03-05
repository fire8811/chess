package service;

import dataaccess.*;

import model.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MyServiceTests {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final GameDAO gameDAO = new MemoryGameDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();

    static final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO, gameDAO);

    @Test
    void testRegister() throws BadRequestException, UsernameTakenException {
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userService.register(registerRequest);

        assertEquals(1, userDAO.getUsers().size());
        assertTrue(userDAO.getUsers().containsKey("jonbob"));
    }

    @Test
    void testRegisterNegative() throws BadRequestException, UsernameTakenException {
        assertThrows(BadRequestException.class, () ->
        userService.register(new RegisterRequest("jonbob", "oogabooga", "")));
    }

    @Test
    void test_clear() throws BadRequestException, UsernameTakenException{
        var registerRequest = new RegisterRequest("jonbob", "oogabooga", "email@email.com");
        userService.register(registerRequest);

        clearService.clear();
        assertEquals(0, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuths().size());
        assertEquals(0, gameDAO.getGamesFromMemory().size());
    }
}
