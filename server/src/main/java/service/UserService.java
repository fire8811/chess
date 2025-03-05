package service;
import dataaccess.*;
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

    public void checkIfValidRegisterRequest(RegisterRequest request) throws BadRequestException{
        if (request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()){
            throw new BadRequestException("bad request");
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws UsernameTakenException {

        String username = registerRequest.username();

        if(users.isUsernameFree(username)){
            UserData userData = new UserData(username, registerRequest.password(), registerRequest.email());
            AuthData authData = new AuthData(generateToken(), username);
            users.addUser(userData);
            auth.addAuthData(authData);

            return new RegisterResult(username, authData.authToken());
        }
        else {
            throw new UsernameTakenException("already taken");
        }


    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
