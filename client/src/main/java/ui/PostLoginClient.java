package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exceptions.BadRequestException;
import exceptions.ResponseException;
import model.*;
import serverfacade.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PostLoginClient implements Client {
    private final ServerFacade server;
    private final String url;
    private final StageManager stageManager;
    ArrayList<GameData> games;
    private final ServerMessageHandler serverMessageHandler;
    private WebSocketFacade ws;
    private BoardUI boardUI;

    public PostLoginClient(String url, ServerFacade server, StageManager stageManager,
                           ServerMessageHandler serverMessageHandler) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
        this.serverMessageHandler = serverMessageHandler;
        boardUI = null;
        WebSocketFacade.initWS(url, serverMessageHandler);
        ws = WebSocketFacade.getWS();
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
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
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
        return String.format("Game %s created", params[0], gameID);
    }

    public String listGames() {
        ListResult result = server.listGames(new ListRequest(stageManager.getAuthToken()));
        this.games = (ArrayList<GameData>) result.games();

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

    public String joinGame(String ... params)  {
        if (params.length != 2){
            throw new ResponseException("bad data given!");
        }
        int gameToJoin = Integer.parseInt(params[0]);
        ChessGame.TeamColor teamToJoin;
        String teamAsString = "";

        if (params[1].equalsIgnoreCase("WHITE") || params[1].equalsIgnoreCase("W")){
            teamToJoin = ChessGame.TeamColor.WHITE;
            teamAsString = "WHITE";
        }
        else if (params[1].equalsIgnoreCase("BLACK") || params[1].equalsIgnoreCase("B")) {
            teamToJoin = ChessGame.TeamColor.BLACK;
            teamAsString = "BLACK";
        }
        else {
            teamToJoin = null;
        }

        try{
            JoinResult result = server.joinGame(new JoinRequest(stageManager.getAuthToken(), teamToJoin, this.games.get(gameToJoin-1).gameID()));

            ws.joinGame(gameToJoin, stageManager.getAuthToken(), teamToJoin);
            System.out.print(String.format("JOINED GAME %d AS %s\n", result.gameID(), teamAsString));
            //new BoardUI().drawBoard(teamToJoin);

            stageManager.setGameID(gameToJoin);
            stageManager.setStage(ClientStage.IN_GAME);
            stageManager.setTeamColor(teamToJoin);
            return "\n";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseException(SET_TEXT_COLOR_RED + "Team is full or the game doesn't exist\n");
        }

    }

    public String observeGame(String ... params){
        String gameNum = params[0];

        ws.observeGame(Integer.valueOf(gameNum), stageManager.getAuthToken());
        stageManager.setTeamColor(ChessGame.TeamColor.WHITE);

        boardUI = new BoardUI();
        stageManager.setGameID(Integer.valueOf(gameNum));
        stageManager.setStage(ClientStage.IN_GAME);

        return String.format("OBSERVING GAME %s\n", gameNum);
    }
}
