package websocket;

import chess.*;
import dataaccess.AuthDAO;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import service.GameService;
import server.Server;

import java.sql.SQLException;
import java.util.Objects;

public class GameManager {
    //private ChessGame game;
    //private boolean gameOver = false;
    //private String blackUsername;
    //private String whiteUsername;

    private final GameService gameService;

    public GameManager(int gameID, GameService gameService){
        //System.out.println("IN GM CONSTRUCTOR, ID: " + this);
        //System.out.println("MAKING NEW GAME: " + gameID);
        this.gameService = gameService;
    }

    public void makeMove(String username, ChessMove move, int gameID){
        System.out.println("IN MAKE MOVE GM");
        System.out.println("USER IN MM: " + username);
        try {
            ChessGame game = gameService.getGame(gameID); //get game from database

            if (isGameOver(gameID)){
                System.out.println("GAME HAS ENDED");
                throw new ResponseException("Game has ended!");
            }

            if (!verifyCorrectPiece(username, move, game, gameID)){
                System.out.println("MAKEMOVE FAIL");
                throw new RuntimeException("Can't make this move!");
            }

            if (!verifyTurn(username, game, gameID)){
                throw new RuntimeException("It's not your turn!");
            };



            System.out.println("GAME MM: " + game);
            game.makeMove(move);
            gameService.updateGameInDatabase(gameID, game); //update database after making move

        } catch (InvalidMoveException | DataAccessException | SQLException e) {
            throw new ResponseException("GAME ERROR: " + e.getMessage());
        }
    }

    public boolean verifyTurn(String username, ChessGame game, int gameID) throws SQLException, AlreadyTakenException {
        String blackUsername = gameService.getUsernameByColor(ChessGame.TeamColor.BLACK, gameID);
        String whiteUsername = gameService.getUsernameByColor(ChessGame.TeamColor.WHITE, gameID);

        return (Objects.equals(username, whiteUsername) && game.getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                ((Objects.equals(username, blackUsername) && game.getTeamTurn() == ChessGame.TeamColor.BLACK));
    }

    private boolean verifyCorrectPiece(String username, ChessMove move, ChessGame game, int gameID) throws SQLException, AlreadyTakenException {
        String blackUsername = getBlackUsername(gameID);
        String whiteUsername = getWhiteUsername(gameID);
        System.out.println("USER: " + username);
        System.out.println("WHITE: " + whiteUsername);
        System.out.println("BLACK: " + blackUsername);

        return (Objects.equals(username, whiteUsername) && game.getMovePieceColor(move) == ChessGame.TeamColor.WHITE) ||
                (Objects.equals(username, blackUsername) && game.getMovePieceColor(move) == ChessGame.TeamColor.BLACK);
    }

    public String getWhiteUsername(int gameID) {
        try {
            return gameService.getUsernameByColor(ChessGame.TeamColor.WHITE, gameID);
        } catch (SQLException | AlreadyTakenException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public String getBlackUsername(int gameID) {
        try {
            return gameService.getUsernameByColor(ChessGame.TeamColor.BLACK, gameID);
        } catch (SQLException | AlreadyTakenException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void resign(int gameID){
        ChessGame game = getGame(gameID);
        if (!isGameOver(gameID)){
            game.setGameOver(true);
            gameService.updateGameInDatabase(gameID, game);
            
        } else {
            throw new ResponseException("Game has already ended!");
        }
    }

    public boolean isGameOver(int gameID) {
        return getGame(gameID).isGameOver();
    }

    public void leave(int gameID, String username) throws DataAccessException {
       ChessGame.TeamColor teamColor;
       String whiteUsername = getWhiteUsername(gameID);
       String blackUsername = getBlackUsername(gameID);

       if (Objects.equals(username, whiteUsername)) {
           whiteUsername = null;
           teamColor = ChessGame.TeamColor.WHITE;
           gameService.removeUser(gameID, teamColor);
       }
       else if (Objects.equals(username, blackUsername)) {
           blackUsername = null;
           teamColor = ChessGame.TeamColor.BLACK;
           gameService.removeUser(gameID, teamColor);
       }
    }

    public ChessGame getGame(Integer gameID) {
        try {
            return gameService.getGame(gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
