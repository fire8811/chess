package ui;

import chess.*;
import exceptions.ResponseException;
import serverfacade.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;

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
                case "moves" -> showLegalMoves(params);
                default -> help();
            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    private String showLegalMoves(String... params) {
        try{
            ChessPosition piecePosition = convertToChessPosition(params[0]);
            boardUI.drawMoves(piecePosition, stageManager.getTeamColor());
        } catch(Exception e){
            System.out.println(SET_TEXT_COLOR_RED + "Invalid Move!");
        }

        return "";
    }

    private String redrawBoard() {
        boardUI.drawBoard(teamColor, null);
        return "";
    }

    private String leaveGame() {
        ws.leaveGame(stageManager.getAuthToken(), stageManager.getGameID());

        stageManager.setStage(ClientStage.POSTLOGIN);
        return "bye";
    }

    private String resign() {
        Scanner in = new Scanner(System.in);
        System.out.print("Type 'yes' to confirm: ");
        String result = in.nextLine();

        if (result.toLowerCase().equals("yes") || result.toLowerCase().equals("y")){
            ws.resign(stageManager.getAuthToken(), stageManager.getGameID());
            return "You resigned!";
        }

        return "The game goes on...";
    }

    private String makeMove(String... params) {
        ChessPosition start = convertToChessPosition(params[0]);
        ChessPosition end = convertToChessPosition(params[1]);
        String token = stageManager.getAuthToken();
        int gameID = stageManager.getGameID();

        if (params.length > 2) {//promotion piece given
            try{
                ChessPiece.PieceType piece = convertToChessPiece(params[2]);
                ChessMove move = new ChessMove(start, end, piece);
                ws.makeMove(token, gameID, move, teamColor, params[0], params[1]);
            } catch (Exception e) {
                System.out.println(SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
            }

        }
        else{ //normal move
            ChessMove move = new ChessMove(start, end, null);
            ws.makeMove(token, gameID, move, teamColor, params[0], params[1]);
        }

        return "";
    }

    private ChessPiece.PieceType convertToChessPiece(String pieceString) {
        return switch(pieceString){
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> throw new IllegalStateException("Not a valid chess piece: " + pieceString); //TODO: this does the long error message thing. FIX
        };
    }

    private ChessPosition convertToChessPosition(String position) {
        try{
            int row = Integer.parseInt(position.substring(1));
            int col = position.charAt(0) - 'a' + 1;

            return new ChessPosition(row, col);
        } catch (Exception e){
            throw new ResponseException(SET_TEXT_COLOR_RED + "Invalid Chess Position!");
        }

    }

    public void drawBoard(ChessGame.TeamColor teamColor, ChessGame game){
        this.teamColor = teamColor;
        boardUI.updateBoard(game);
        boardUI.drawBoard(teamColor, null);
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
