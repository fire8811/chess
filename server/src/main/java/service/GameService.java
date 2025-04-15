package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import model.*;

import java.sql.SQLException;

public class GameService {
    private final GameDAO games;
    private final AuthDAO auth;

    public GameService(AuthDAO auth, GameDAO games){
        this.auth = auth;
        this.games = games;
    }

    public ListResult listGames(ListRequest request) throws DataAccessException {
        String authToken = request.authToken();
        auth.authTokenExists(authToken); //verify authToken

        return new ListResult(games.listGames());
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException, SQLException {
        if (request.gameName() == null){
            throw new BadRequestException("bad request");
        }

        String authToken = request.authToken();
        auth.authTokenExists(authToken);

        int gameID = games.createGame(request.gameName());
        return new CreateResult((gameID));
    }

    public JoinResult joinGame(JoinRequest request) throws DataAccessException, SQLException {
        String authToken = request.authToken();
        auth.authTokenExists(authToken);
        String username = auth.getUsername(authToken);

        Integer gameID = request.gameID();
        if (gameID == null){
            throw new BadRequestException("bad request");
        }

        boolean gameExists = games.gameExists(gameID);
        if (gameExists){
            games.updateGame(gameID, request.playerColor(), username);
        }
        else {
            throw new BadRequestException("bad request");
        }

        return new JoinResult(request.playerColor(), gameID);
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        return ((SqlGameDAO) games).getGame(gameID);
    }

    public String getUsernameByColor(ChessGame.TeamColor color, int gameID) throws SQLException, AlreadyTakenException {
        return ((SqlGameDAO) games).getUsername(color, gameID);
    }

    public void removeUser(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        ((SqlGameDAO) games).leaveGame(gameID, color, null);
    }

    public void updateGameInDatabase(int gameID, ChessGame game){
        ((SqlGameDAO) games).updateChessGame(gameID, game);
    }
}
