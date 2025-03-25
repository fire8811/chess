package ui;

import exceptions.ResponseException;
import model.*;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

public class PostLoginClient implements Client {
    private final ServerFacade server;
    private final String url;
    private final StageManager stageManager;

    public PostLoginClient(String url, ServerFacade server, StageManager stageManager) {
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
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "quit", "q" -> "quit";
                default -> help();

            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    public String help(){
        return """
               create <GAME NAME> -create a new chess game
               join <GAME ID> [WHITE/BLACK] -self explanatory
               observe <GAME ID> -self explanatory
               list - show all games
               logout -self explanatory
               quit -hopefully you know what this means
               help -show this menu again
               """;
    }

    public String logout() {
        server.logoutUser(new LogoutRequest(stageManager.getAuthToken()));

        stageManager.deleteToken();
        stageManager.setStage(ClientStage.PRELOGIN);
        return "goodbye!";
    }

    public String createGame(String...params) {
        CreateResult result = server.createGame(new CreateRequest(stageManager.getAuthToken(), params[0]));
        int gameID = result.gameID();
        return String.format("Game %s created with game id %d", params[0], gameID);
    }

    public String listGames() {
        ListResult result = server.listGames(new ListRequest(stageManager.getAuthToken()));
        Collection<GameData> games = result.games();

        String returnString = "LIST OF GAMES\n";
        int i = 1;

        for (GameData game: games){
            String whiteUsername = !(game.whiteUsername() == null) ? game.whiteUsername() : "<free to join>";
            String blackUsername = !(game.blackUsername() == null) ? game.blackUsername() : "<free to join>";
            returnString += String.format("%d - NAME: %s | WHITE: %s | BLACK: %s\n", i, game.gameName(), whiteUsername, blackUsername);
            i++;
        }

        return returnString;
    }


}
