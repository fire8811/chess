package ui;

import exceptions.ResponseException;
import model.RegisterRequest;
import model.*;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class PreLoginClient implements Client{
    private final ServerFacade server;
    private final String url;
    private final StageManager stageManager;

    public PreLoginClient(String url, ServerFacade server, StageManager stageManager) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length); //parameters that follow the command (like a username)

            return switch(cmd){
                case "register" -> register(params);
                case "login" -> login(params);
                default -> help();
            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException{
        if (params.length > 1){
            RegisterResult result = server.registerUser(new RegisterRequest(params[0], params[1], params[2]));
            stageManager.setStage(ClientStage.POSTLOGIN);

            String username = result.username();
            return String.format("Welcome, %s", username);
        }
        throw new ResponseException("correct command is 'register <YOUR USERNAME> <YOUR PASSWORD> <YOUR EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length > 1){
            LoginResult result = server.loginUser(new LoginRequest(params[0], params[1]));
            stageManager.setStage(ClientStage.POSTLOGIN);

            String username = result.username();
            return String.format("Welcome back, %s", username);
        }
        throw new ResponseException("unknown error when logging in");
    }

    public String help(){
        return """
               register <username> <password> <email> -self explanatory
               login <username> <password> -self explanatory
               quit -self explanatory
               help -shows this menu again. Hopefully you already knew that.
               """;
    }
}
