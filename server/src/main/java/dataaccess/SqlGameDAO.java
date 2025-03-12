package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SqlGameDAO implements GameDAO, DatabaseCreator {
    public SqlGameDAO() throws SQLException, DataAccessException {
        configureDatabase(createGameSchema);
    }

    public void clearGames() throws SQLException, DataAccessException {
        var command = "TRUNCATE games";
        updateTable(command);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var goodConnect = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games";
            try (var preparedStatement = goodConnect.prepareStatement(statement)) {
                try (var returnStatement = preparedStatement.executeQuery()){
                    while (returnStatement.next()){
                        result.add(readGame(returnStatement));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("Error when accessing games database: %s", e.getMessage()));
        }

        return result;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("chessGame");
        var chessGame = new Gson().fromJson(json, ChessGame.class);

        return new GameData(id, whiteUsername, blackUsername, gameName, chessGame);
    }

    public int createGame(String gameName) throws DataAccessException, SQLException {
        var command = "INSERT INTO games (gameName, chessGame) VALUES (?, ?)";
        ChessGame chessGameToAdd = new ChessGame();
        var chessGameJson = new Gson().toJson(chessGameToAdd);

        return updateTable(command, gameName, chessGameJson); //create game and return gameID int
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

    private int updateTable(String statement, Object... params) throws DataAccessException, SQLException {
        try (var goodConnect = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnect.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (int i = 0; i < params.length; i++){
                    var param = (String) params[i];
                    if (param instanceof String toInsert) preparedStatement.setString(i+1, toInsert);
                    //else if (param instanceof ChessGame)
                }
                preparedStatement.executeUpdate();

                var gameID = preparedStatement.getGeneratedKeys();
                if (gameID.next()){
                    return gameID.getInt(1); //return gameID
                }
            }
        }
        catch (SQLException e){
            throw new ResponseException(String.format("can't update database: %s, %s", statement, e.getMessage()));
        }
        throw new GeneralException("something bad happened");
    }

    private String[] createGameSchema = {
        """
        CREATE TABLE IF NOT EXISTS games (
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
