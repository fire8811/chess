package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGames();
    Collection<GameData> getGamesFromMemory();
    int createGame(String gameName) throws UnauthorizedException, BadRequestException;
    boolean isColorAvailable(GameData game, ChessGame.TeamColor color);
    boolean findGame(int gameID);
    GameData getGame(Integer id);
    void updateGame(Integer gameID, ChessGame.TeamColor color, String username) throws BadRequestException, AlreadyTakenException;
}
