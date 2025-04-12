package websocket;

import chess.*;
import dataaccess.AuthDAO;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exceptions.DataAccessException;
import service.GameService;
import server.Server;

import java.sql.SQLException;

public class GameManager {
    private ChessGame game;
    private GameService gameService;

    public GameManager(int gameID){
        try {
            game = Server.gameService.getGame(gameID);
        } catch (DataAccessException e) {
            System.out.println("ERROR GAMEMANAGER INIT: " + e.getMessage());
        }

    }

    public ChessBoard getBoard() {return game.getBoard();}
    public ChessGame getGame() {return game; }

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
