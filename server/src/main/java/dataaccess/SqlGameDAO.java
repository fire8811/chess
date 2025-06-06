package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import model.GameData;
import server.Server;
import service.GameService;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO implements GameDAO, DatabaseCreator {
    private GameService gameService;

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

    public boolean gameExists(int gameID) throws DataAccessException { //checks to see if the gameID is in the database
        try (var goodConnection = DatabaseManager.getConnection()){
            var command = "SELECT 1 FROM games WHERE gameID=?";
            try (var preparedStatement = goodConnection.prepareStatement(command)) {
                preparedStatement.setInt(1, gameID);
                try (var retrieved = preparedStatement.executeQuery()) {
                    if (retrieved.next()) { //gameID exists in table, return true;
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("Error when trying to find gameID in table:%s", e.getMessage()));
        }
        return false; //no gameID found in table;
    }

    private String getCorrectStatement(ChessGame.TeamColor color){
        if (color == ChessGame.TeamColor.WHITE){
            return "SELECT whiteUsername FROM games WHERE gameID=?";
        }
        else {
            return "SELECT blackUsername FROM games WHERE gameID=?";
        }
    }

    public String getUsername(ChessGame.TeamColor color, int gameID)
            throws SQLException, AlreadyTakenException { //returns the username of a given team color
        try (var goodConnect = DatabaseManager.getConnection()) {
            var statement = getCorrectStatement(color);

            try (var preparedStatement = goodConnect.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);

                try (var result = preparedStatement.executeQuery()){ //retrieve username stored for teamColor (null or taken)
                    if (result.next()){
                        return result.getString(1);

                    }
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    //adds a user to a given team color in a given chess match if the match exists and the color is avaliable
    public void updateGame(Integer gameID, ChessGame.TeamColor color, String username)
            throws DataAccessException, AlreadyTakenException {

        if (color != ChessGame.TeamColor.BLACK && color != ChessGame.TeamColor.WHITE){ //check if color request is valid
            throw new BadRequestException("bad request");
        }
        //retrieve teamColor and update with username if possible
        try (var goodConnect = DatabaseManager.getConnection()) {
            var statement = getCorrectStatement(color);

            try (var preparedStatement = goodConnect.prepareStatement(statement)){
                executeQuery(preparedStatement, goodConnect, gameID, color, username, false );
            }

        } catch (SQLException e) {
            throw new ResponseException(String.format("Error when trying to access data: %s", e.getMessage()));

        }
    }

    public void leaveGame(Integer gameID, ChessGame.TeamColor color, String username) throws BadRequestException {
        if (color != ChessGame.TeamColor.BLACK && color != ChessGame.TeamColor.WHITE){ //check if color request is valid
            throw new BadRequestException("bad request");
        }
        //retrieve teamColor and update with username if possible
        try (var goodConnect = DatabaseManager.getConnection()) {
            var statement = getCorrectStatement(color);

            try (var preparedStatement = goodConnect.prepareStatement(statement)){
                executeQuery(preparedStatement, goodConnect, gameID, color, username, true);
            }

        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(String.format("Error when trying to access data: %s", e.getMessage()));

        }
    }

    private String getLeaveStatement(ResultSet result, ChessGame.TeamColor color) throws AlreadyTakenException, SQLException {
        if (color == ChessGame.TeamColor.WHITE && (result.getString("whiteUsername") != null)) {
            return "UPDATE games SET whiteUsername=? WHERE gameID=?"; //update whiteUsername if free
        } else if (color == ChessGame.TeamColor.BLACK && (result.getString("blackUsername") != null)) {
            return "UPDATE games SET blackUsername=? WHERE gameID=?";
        } else {
            throw new ResponseException("color isn't taken!");
        }
    }

    public void updateChessGame(int gameID, ChessGame chessGame){
        try {
            if (gameExists(gameID)){
                var goodConnect = DatabaseManager.getConnection();
                var statement = "SELECT chessGame FROM games WHERE gameID=?";

                queryAndInsertGame(gameID, chessGame, goodConnect, statement);
            } else {
                System.out.println("Game with ID " + gameID + " does not exist.");
            }
        } catch (DataAccessException | SQLException e) {
            System.out.println("Error when updating game to database: " + e.getMessage());
        }
    }

    private void queryAndInsertGame(int gameID, ChessGame chessGame, Connection goodConnect, String statement) throws SQLException {
        try (var preparedStatement = goodConnect.prepareStatement(statement)){
            preparedStatement.setInt(1, gameID);
            try(var result = preparedStatement.executeQuery()){
                if (result.next()){
                    String command = "UPDATE games SET chessGame=? WHERE gameID=?";
                    insertGame(gameID, chessGame, goodConnect, command);
                }
            }
        }
    }

    private void insertGame(int gameID, ChessGame chessGame, Connection conn, String command) throws SQLException {
        var chessGameJSON = new Gson().toJson(chessGame); //serialize chessGame to JSON

        try(var preparedStatememnt = conn.prepareStatement(command)){
            preparedStatememnt.setString(1, chessGameJSON);
            preparedStatememnt.setInt(2, gameID);

            preparedStatememnt.executeUpdate();
        }
    }

    private void executeQuery(PreparedStatement preparedStatement, Connection goodConnect,
                              Integer gameID, ChessGame.TeamColor color, String username, boolean isLeaving)
            throws SQLException, AlreadyTakenException {

        preparedStatement.setInt(1, gameID);
        try (var result = preparedStatement.executeQuery()){ //retrieve teamColor status (null or taken)
            if (result.next()){
                String command = isLeaving ? getLeaveStatement(result, color) : getCommandStatement(result, color);
                insertUser(goodConnect, command, username, gameID);
            }
        }
    }

    private String getCommandStatement(ResultSet result, ChessGame.TeamColor color) throws AlreadyTakenException, SQLException {
        if(color == ChessGame.TeamColor.WHITE && (result.getString("whiteUsername") == null)){
            return "UPDATE games SET whiteUsername=? WHERE gameID=?"; //update whiteUsername if free
        }
        else if (color == ChessGame.TeamColor.BLACK && (result.getString("blackUsername") == null)){
            return "UPDATE games SET blackUsername=? WHERE gameID=?";
        }
        else {
            throw new AlreadyTakenException("color already taken");
        }
    }

    private void insertUser(Connection conn, String command, String username, int gameID) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(command)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResponseException(String.format("Error when trying to insert username into table: %s", e.getMessage()));
        }
    }

    private int updateTable(String statement, Object... params) throws DataAccessException, SQLException { //used for adding new games to table
        try (var goodConnect = DatabaseManager.getConnection()){
            try (var preparedStatement = goodConnect.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for (int i = 0; i < params.length; i++){
                    var param = params[i];
                    if (param instanceof String toInsert) {
                        preparedStatement.setString(i+1, toInsert);
                    }
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
        return 0;
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        gameExists(gameID);

        GameData gameData = getGameQueryResult(gameID);
        return gameData.game();
    }

    private GameData getGameQueryResult(int gameID) throws DataAccessException {
        try (var goodConnection = DatabaseManager.getConnection()){
            var command = "SELECT * FROM games WHERE gameID=?";
            try (var preparedStatement = goodConnection.prepareStatement(command)) {
                preparedStatement.setInt(1, gameID);
                try (var retrieved = preparedStatement.executeQuery()) {
                    if(retrieved.next()){
                        return readGame(retrieved);
                    }
                    else {
                        throw new DataAccessException("No game found with ID: " + gameID);
                    }
                }

            }
        } catch (SQLException | DataAccessException e) {
            System.out.println(String.format("Error when trying to find gameID in table:%s", e.getMessage()));
            throw new DataAccessException("Error when trying to find gameID in table: " + e.getMessage());
        }

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
