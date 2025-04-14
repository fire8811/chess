package ui;

import chess.*;
import exceptions.ResponseException;
import serverfacade.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GamePlayClient implements Client, ServerMessageHandler {
    private final ServerFacade server;
    private final String url;
    private final  StageManager stageManager;
    private ChessGame chessGame;
    private BoardUI boardUI;
    ChessGame.TeamColor teamColor;
    private int gameID;
    private WebSocketFacade ws;

    public GamePlayClient(String url, ServerFacade server, StageManager stageManager) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
        chessGame = new ChessGame();
        this.boardUI = new BoardUI();
        ws = WebSocketFacade.getWS();
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length); //parameters that follow the command (like a username)

            return switch(cmd){
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                default -> help();
                //TODO: add hihglight move command ALSO NOTE THAT OBSERVERS NEED TO BE IN THIS CLIENT. WILL ADD BOOLEAN TO PREVENT THEM FROM USING
                //TODO: CERTAIN GAMEPLAY COMMANDS
            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    private String redrawBoard() {
        boardUI.drawBoard(teamColor);
        return "";
    }

    private String leaveGame() {
        return "bye";
    }

    private String resign() {
        return "resign"; //spec says not to make player leave game. will prob need a gameState variable that is set to false to disable making moves
    }

    private String makeMove(String... params) {
        ChessPosition start = convertToChessPosition(params[0]);
        ChessPosition end = convertToChessPosition(params[1]);

        ChessMove move = new ChessMove(start, end, null);//TODO: implement promotion peices
        String token = stageManager.getAuthToken();
        int gameID = stageManager.getGameID();


        ws.makeMove(token, gameID, move, teamColor, params[0], params[1]);

        return "";
    }

    private ChessPosition convertToChessPosition(String position) {
        int row = Integer.parseInt(position.substring(1));
        int col = position.charAt(0) - 'a' + 1;

        return new ChessPosition(row, col);
    }

    public void drawBoard(ChessGame.TeamColor teamColor, ChessBoard board){
        this.teamColor = teamColor;
        boardUI.updateBoard(board);
        boardUI.drawBoard(teamColor);
    }

    private String help() {
        return """
               move <position> <desired position>: self explanatory
               redraw: redraws the game board
               leave: self explanatory
               resign: for when all hope of victory is lost
               moves <position>: shows all legal moves of the selected chess piece
               """;
    }


    @Override
    public void displayMessage(ServerMessage message) {
        System.out.println("BoardMessage Received");
    }
}
