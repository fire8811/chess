package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SqlGameDAO implements GameDAO, DatabaseCreator {
    public SqlGameDAO() throws SQLException, DataAccessException {
        configureDatabase(createGameSchema);
    }

    public void clearGames() {

    }

    public Collection<GameData> getGamesFromMemory() {
        return List.of();
    }

    public int createGame(String gameName) throws UnauthorizedException, BadRequestException {
        return 0;
    }

    public boolean isColorAvailable(GameData game, ChessGame.TeamColor color) {
        return false;
    }

    public boolean findGame(int gameID) {
        return false;
    }

    public GameData getGame(Integer id) {
        return null;
    }

    public void updateGame(Integer gameID, ChessGame.TeamColor color, String username) throws BadRequestException, AlreadyTakenException {

    }

    private String[] createGameSchema = {
        """
        CREATE TABLE IF NOT EXISTS games {
        `gameID` int NOT NULL AUTO_INCREMENT,
        `whiteUsername` varChar(256) DEFAULT NULL,
        `blackUsername` varChar(256) DEFAULT NULL,
        `gameName` varChar(256) NOT NULL,
        `chessGame` TEXT NOT NULL,
        PRIMARY KEY (`gameID`),
        INDEX(gameName),
        INDEX(whiteUsername),
        INDEX(blackUsername)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
