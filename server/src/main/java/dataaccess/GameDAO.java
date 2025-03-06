package dataaccess;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface GameDAO {
    void clearGames();
    Collection<GameData> getGamesFromMemory();
    int createGame(String gameName) throws UnauthorizedException, BadRequestException;
}
