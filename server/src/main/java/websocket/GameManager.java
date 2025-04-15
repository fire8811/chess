package websocket;

import chess.*;
import dataaccess.AuthDAO;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import service.GameService;
import server.Server;

import java.sql.SQLException;
import java.util.Objects;

public class GameManager {
    private ChessGame game;
    private boolean gameOver = false;
    private String blackUsername;
    private String whiteUsername;
    private GameService gameService;
    private boolean whiteResigned = false;
    private boolean blackResigned = false;

    public GameManager(int gameID){
        try {
            game = Server.gameService.getGame(gameID);

            blackUsername = Server.gameService.getUsernameByColor(ChessGame.TeamColor.BLACK, gameID);
            whiteUsername = Server.gameService.getUsernameByColor(ChessGame.TeamColor.WHITE, gameID);
        } catch (DataAccessException | SQLException e) {
            throw new ResponseException("ERROR GAMEMANAGER INIT: " + e.getMessage());
        }

    }

    public ChessBoard getBoard() {return game.getBoard();}
    public ChessGame getGame() {return game; }

    public void makeMove(ChessMove move){
        try {
            if (gameOver){
                throw new ResponseException("Game has ended!");
            }
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ResponseException("GAME ERROR: " + e.getMessage());
        }
    }

    public boolean verifyTurn(String username){
        return (Objects.equals(username, whiteUsername) && game.getTeamTurn() == ChessGame.TeamColor.WHITE) ||
                ((Objects.equals(username, blackUsername) && game.getTeamTurn() == ChessGame.TeamColor.BLACK));
    }

    public boolean verifyCorrectPiece(String username, ChessMove move){
        return (Objects.equals(username, whiteUsername) && game.getMovePieceColor(move) == ChessGame.TeamColor.WHITE) ||
                (Objects.equals(username, blackUsername) && game.getMovePieceColor(move) == ChessGame.TeamColor.BLACK);
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void resign(ChessGame.TeamColor teamColor){
        if (teamColor == ChessGame.TeamColor.WHITE){
            whiteResigned = true;
            gameOver = true;
        }
        else if (teamColor == ChessGame.TeamColor.BLACK){
            blackResigned = true;
            gameOver = true;
        }
        else if (gameOver){
            throw new ResponseException("Game has already ended!");
        }
    }

    public boolean isWhiteResigned() {
        return whiteResigned;
    }

    public boolean isBlackResigned() {
        return blackResigned;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
