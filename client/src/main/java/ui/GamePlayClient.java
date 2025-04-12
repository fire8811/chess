package ui;

import chess.ChessGame;
import exceptions.ResponseException;
import serverfacade.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GamePlayClient implements Client, ServerMessageHandler {
    private final ServerFacade server;
    private final String url;
    private final  StageManager stageManager;
    private ChessGame chessGame;

    public GamePlayClient(String url, ServerFacade server, StageManager stageManager) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
        chessGame = new ChessGame();
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length); //parameters that follow the command (like a username)

            return switch(cmd){
                default -> help();
                //TODO: add make move command
            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    private void makeMove() {

    }

    private String help() {
        return "You are in the gameplay client. Update coming soon";
    }

    @Override
    public void displayMessage(ServerMessage message) {
        System.out.println("BoardMessage Received");
    }
}
