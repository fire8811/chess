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
    private final GameService gameService;

    public GameManager(GameService gameService){
        this.gameService = gameService;
    }

    public void makeMove(String username, ChessMove move, int gameID){
        System.out.println("IN MAKE MOVE GM");
        System.out.println("USER IN MM: " + username);
        try {
            ChessGame game = gameService.getGame(gameID); //get game from database

            if(game.isGameOver()){
                throw new InvalidMoveException("The Game is Over!");
            }
            if (!verifyTurn(username, game, gameID)){
                throw new InvalidMoveException("It's not your turn!");
            };
            if (!verifyCorrectPiece(username, move, game, gameID)){
                throw new InvalidMoveException("Can't make this move!");
            }
            if(game.getBoard().getPiece(move.getStartPosition()) == null){
                throw new InvalidMoveException("Square is empty or doesn't exist!");
            }
            if(move.getPromotionPiece() != null && !checkIfCanPromote(username, move, game, gameID)){
                System.out.println("INVALID MOVE");
               throw new InvalidMoveException("You cannot promote with this move!");
            }
            if(move.getPromotionPiece() == null && checkIfCanPromote(username, move, game, gameID)){
                throw new InvalidMoveException("You must promote your pawn!");
            }

            System.out.println("GAME MM: " + game);
            game.makeMove(move);
            gameService.updateGameInDatabase(gameID, game); //update database after making move

        } catch (InvalidMoveException | DataAccessException | SQLException e) {
            throw new ResponseException("GAME ERROR: " + e.getMessage());
        }
    }

    private boolean checkIfCanPromote(String username, ChessMove move, ChessGame game, int gameID) throws InvalidMoveException {
        if (game.getBoard().getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN){
            if (username.equals(getWhiteUsername(gameID)) && move.getEndPosition().getRow() == 8) {return true;}
            else if (username.equals(getBlackUsername(gameID)) && move.getEndPosition().getRow() == 1) {return true;}
        }

        return false;

    }

    public boolean verifyTurn(String username, ChessGame game, int gameID) throws SQLException, AlreadyTakenException {
        String blackUsername = gameService.getUsernameByColor(ChessGame.TeamColor.BLACK, gameID);
        String whiteUsername = gameService.getUsernameByColor(ChessGame.TeamColor.WHITE, gameID);

        return (Objects.equals(username, whiteUsername) && game.getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                ((Objects.equals(username, blackUsername) && game.getTeamTurn() == ChessGame.TeamColor.BLACK));
    }

    private boolean verifyCorrectPiece(String username, ChessMove move, ChessGame game, int gameID) throws SQLException, AlreadyTakenException, InvalidMoveException {
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
        if (!game.isGameOver()){
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
           teamColor = ChessGame.TeamColor.WHITE;
           gameService.removeUser(gameID, teamColor);
       }
       else if (Objects.equals(username, blackUsername)) {
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

    public String getOppositeUsername(int gameID, String username){
        if(username.equals(getWhiteUsername(gameID))){
            return getBlackUsername(gameID);
        } else {
            return getWhiteUsername(gameID);
        }
    }

    public boolean checkmateCheck(int gameID, String username) {
        System.out.println("CHECKING CHECKMATE");
        ChessGame game = getGame(gameID);
        if(username.equals(getWhiteUsername(gameID))){
            return game.isInCheckmate(ChessGame.TeamColor.BLACK);
        } else {
            return game.isInCheckmate(ChessGame.TeamColor.WHITE);
        }
    }

    public boolean checkforCheck(int gameID, String username) {
        ChessGame game = getGame(gameID);
        if(username.equals(getWhiteUsername(gameID))){
            return game.isInCheck(ChessGame.TeamColor.BLACK);
        } else {
            return game.isInCheck(ChessGame.TeamColor.WHITE);
        }
    }
}
