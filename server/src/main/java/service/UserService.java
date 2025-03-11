package service;
import dataaccess.*;
import model.*;

import java.sql.SQLException;
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

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }

    public void checkIfValidRegisterRequest(RegisterRequest request) throws BadRequestException{
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("bad request");
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, SQLException {
        checkIfValidRegisterRequest(registerRequest);

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

//    public LoginResult login(LoginRequest request) throws UnauthorizedException {
//        String username = request.username();
//        String password = request.password();
//
//        users.findUser(username, password);
//        AuthData authData = new AuthData(generateToken(), username);
//        auth.addAuthData(authData);
//
//        return new LoginResult(username, authData.authToken());
//    }
//

//
//    public void logout(LogoutRequest request) throws UnauthorizedException {
//        String authToken = request.authToken();
//
//        auth.deleteAuthToken(authToken);
//    }
}
