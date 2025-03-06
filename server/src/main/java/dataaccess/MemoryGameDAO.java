package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    //QUESTION FOR TA: is it bad to have GameData Object in data structure, meaning I'd have to break up its attributes into a list maybe?
    //like this: HashMap<Integer, List> (gameID: [rest of attributes in list here)
    private int gameID = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public Collection<GameData> getGamesFromMemory(){ //getter for testing
        return games.values();
    }

    public int createGame(String gameName) throws UnauthorizedException, BadRequestException {
        int id = gameID++;
        var game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);

        return id;
    }

    public void clearGames(){
        games.clear();
    }
}
