package dataaccess;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    void clearGames();
    HashMap<Integer, GameData> getGamesFromMemory();
}
