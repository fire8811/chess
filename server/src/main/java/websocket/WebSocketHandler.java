package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import exceptions.ResponseException;
import org.eclipse.jetty.server.Authentication;
import server.Server;
import com.google.gson.Gson;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlUserDAO;
import exceptions.DataAccessException;
//import org.eclipse.jetty.server.session.Session;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

//server side
@WebSocket
public class WebSocketHandler { //create one instance of the class and always make the same instane. Check the DAO instance
    //have handler get references to services in constructor
    private final ConnectionManager connections = new ConnectionManager();
    //private GameManager gameManager;
    private HashMap<Integer, GameManager> gameManagerList = new HashMap<>();
    private final GameService gameService;
    private final UserService userService;
    public WebSocketHandler(GameService gameService, UserService userService){
        this.gameService = gameService;
        this.userService = userService;
        //gameManager = new GameManager();
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, SQLException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            command = new Gson().fromJson(message, MakeMoveCommand.class);
        }
        System.out.println("\nIncoming JSON: " + message);
        switch(command.getCommandType()){
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove((MakeMoveCommand) command, session);
            case RESIGN -> resign(command, session);
            case LEAVE -> leave(command, session);
        }
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        try {
            String username = getUsername(command.getAuthToken(), session);
            var gameManager = gameManagerList.get(command.getGameID());

            gameManager.leave(command.getGameID(), username);
            gameManagerList.put(command.getGameID(), gameManager);

            sendServerNotification(username, String.format("%s left the game", username), command.getGameID());
            connections.remove(username, command.getGameID());

        } catch (SQLException | DataAccessException | IOException | ResponseException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "ERROR: " + e.getMessage() + "or an other issue");

            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        try{
            String username = getUsername(command.getAuthToken(), session);
            var gameManager = gameManagerList.get(command.getGameID());

            //resign attempt as observer throws error
            if(!username.equals(gameManager.getWhiteUsername(command.getGameID()))
                    && (!username.equals(gameManager.getBlackUsername(command.getGameID())))){
                throw new ResponseException("Cannot resign as an observer!");
            }

            gameManager.resign(command.getGameID());
            gameManagerList.put(command.getGameID(), gameManager);

            var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("GAME OVER: %s resigned!", username));

            System.out.println("RESIGN");
            connections.broadcastAll(notificationMessage, command.getGameID());

        } catch (SQLException | IOException | DataAccessException | ResponseException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " +
                    e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        System.out.println("IN MAKE MOVE: " + command);
        try {
            String username = getUsername(command.getAuthToken(), session);
            System.out.println("USERNAME: " + username);
            var gameManager = gameManagerList.get(command.getGameID());
            //System.out.println("gameManagerList keys: " + gameManagerList.keySet());
            System.out.println("MM GAME MANAGER: " + gameManager);
            //System.out.println("GAMEHASH MM: " + gameManagerList.get(command.getGameID()).getGame());
            //System.out.println("CHESSGAME BEING USED: " + gameManagerList.get(command.getGameID()).getGame());


            gameManager.makeMove(username, command.getMove(), command.getGameID());
            gameManagerList.put(command.getGameID(), gameManager); //put updated game manager back into game manager list
            System.out.println("CHESSMOVE SUCCESS");

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameManager.getGame(command.getGameID()), command.getTeamColor());
            System.out.println("MAKE MOVE BROADCAST");
            connections.broadcastAll(loadGameMessage, command.getGameID());


            String notification = String.format("%s moved %s to %s", username,
                    command.getStartString(), command.getEndString());

            System.out.println("MOVE SUCCESS");
            sendServerNotification(username, notification, command.getGameID());


        } catch (RuntimeException | SQLException | DataAccessException e) {

            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage());
            System.out.println("MOVE ERROR: " + errorMessage);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, SQLException, DataAccessException {
        try {
            String username = getUsername(command.getAuthToken(), session);

            if (!command.getObserverStatus()) {
                joinGame(command, session, username);
            } else {
                observeGame(command, session, username);
            }
        } catch (SQLException | DataAccessException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void joinGame(UserGameCommand command, Session session, String username) throws SQLException, DataAccessException, IOException {
        System.out.println("IN JOIN");
        String teamColorString = "";
        int gameID = command.getGameID();

        try {
            gameManagerList.putIfAbsent(gameID, new GameManager(gameService));
            var gameManager = gameManagerList.get(gameID);

            if (command.getTeamColor() == ChessGame.TeamColor.WHITE){
                teamColorString = "WHITE";
            }
            else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
                teamColorString = "BLACK";
            }

            connections.add(username, session, gameID);
            System.out.println("AFTER CONNECT");

            sendGame(session, command.getTeamColor(), command.getGameID());

            String message = String.format("Player %s joined the game as %s", username, teamColorString);

            sendServerNotification(username, message, command.getGameID());

        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void observeGame(UserGameCommand command, Session session, String username) throws IOException {
        String message = String.format("%s joined as an observer", username);

        connections.add(username, session, command.getGameID());
        sendGame(session, ChessGame.TeamColor.WHITE, command.getGameID());
        sendServerNotification(username, message, command.getGameID());
    }

    private void sendGame(Session session, ChessGame.TeamColor teamColor, int gameID) throws IOException {
        var gameManager = gameManagerList.get(gameID);
        ChessGame game = gameManager.getGame(gameID);
        var loadGameMessage = new LoadGameMessage(game, teamColor);


        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }

    private void sendServerNotification(String username, String message, int gameID) throws IOException {
        //var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        //serverMessage.addMessage(message);
        var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        //System.out.println("Serialized: " + new Gson().toJson(notificationMessage));

        connections.broadcast(username, notificationMessage, gameID);
    }

    private String getUsername(String authToken, Session session) throws SQLException, DataAccessException, IOException {
        return userService.getUsername(authToken);
    }
}
