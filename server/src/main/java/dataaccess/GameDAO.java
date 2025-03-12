package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    void clearGames() throws SQLException, DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException, SQLException;
    boolean isColorAvailable(GameData game, ChessGame.TeamColor color);
    boolean findGame(int gameID) throws DataAccessException;
    GameData getGame(Integer id);
    void updateGame(Integer gameID, ChessGame.TeamColor color, String username) throws BadRequestException, AlreadyTakenException;
}
