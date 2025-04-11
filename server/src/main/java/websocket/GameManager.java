package websocket;

import chess.*;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exceptions.DataAccessException;
import service.GameService;

public class GameManager {
    private ChessGame game;
    private final GameService service;

    public GameManager(int gameID, GameService gameService){
        try {
            game = gameService.g;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public ChessGame getGame() {return game; }
    public ChessBoard getBoard() {return game.getBoard();}

    public void makeMove(String startPositionString, String endPositionString){
        ChessPosition startPosition = createChessPosition(startPositionString);
        ChessPosition endPosition = createChessPosition(endPositionString);

        //TODO: figure out how to pass in pawn promotion pieces
        ChessMove move = new ChessMove(startPosition, endPosition, null);

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("GAME ERROR: " + e.getMessage());
        }

    }

    private ChessPosition createChessPosition(String position){
        char colLetter = position.charAt(0);
        int colInt = colLetter - 'a' + 1; ///use unicode value of 'a' to get proper column number
        int row = position.indexOf(1);

        System.out.println("ROW: " + row + " COL: " + colInt);
        return new ChessPosition(row, colInt);
    }

}
