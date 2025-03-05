package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    //QUESTION FOR TA: is it bad to have GameData Object in data structure, meaning I'd have to break up its attributes into a list maybe?
    //like this: HashMap<Integer, List> (gameID: [rest of attributes in list here)
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public HashMap<Integer, GameData> getGamesFromMemory(){ //getter for testing
        return games;
    }

    public void clearGames(){
        games.clear();
    }
}
