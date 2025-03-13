package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int gameID = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData getGame(Integer id){ //method used for testing
        return games.get(id);
    }

    public Collection<GameData> listGames(){
        return games.values();
    }

    public int createGame(String gameName) throws UnauthorizedException, BadRequestException {
        int id = gameID++;
        var game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);

        return id;
    }

    public boolean findGame(int gameID){
        return games.containsKey(gameID);
    }

    public boolean isColorAvailable(GameData game, ChessGame.TeamColor color){
        if (game.whiteUsername() == null && color == ChessGame.TeamColor.WHITE){
            return true;
        }
        else if (game.blackUsername() == null && color == ChessGame.TeamColor.BLACK){
            return true;
        }
        return false;
    }

    public void updateGame(Integer gameID, ChessGame.TeamColor color, String username) throws BadRequestException, AlreadyTakenException {
        GameData game = games.get(gameID);
        if (color != ChessGame.TeamColor.BLACK && color != ChessGame.TeamColor.WHITE){
            throw new BadRequestException("bad request");
        }
        if (!isColorAvailable(game, color)){
            throw new AlreadyTakenException("already taken");
        }

        if (color == ChessGame.TeamColor.BLACK){
            games.put(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(),
                    game.game()));
        }
        else if (color == ChessGame.TeamColor.WHITE){
            games.put(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(),
                    game.game()));
        }
        else {
            throw new BadRequestException("bad request");
        }
    }

    public void clearGames(){
        games.clear();
    }

}
