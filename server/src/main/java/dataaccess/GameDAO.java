package dataaccess;
import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    void clearGames() throws SQLException, DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException, SQLException;
    boolean findGame(int gameID) throws DataAccessException;
    void updateGame(Integer gameID, ChessGame.TeamColor color, String username) throws DataAccessException;
}
