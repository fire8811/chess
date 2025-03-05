package service;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final AuthDAO auth;
    private final UserDAO users;
    private final GameDAO games;

    public UserService(AuthDAO auth, UserDAO users, GameDAO games){
        this.auth = auth;
        this.games = games;
        this.users = users;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();

        if(users.isUsernameFree(username)){
            UserData userData = new UserData(username, registerRequest.password(), registerRequest.email());
            AuthData authData = new AuthData(generateToken(), username);
            users.addUser(userData);
            auth.addAuthData(authData);

            return new RegisterResult(username, authData.authToken());
        }

        throw new DataAccessException("Username taken!");
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
