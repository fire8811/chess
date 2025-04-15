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

//server side
@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private GameManager gameManager;

    public WebSocketHandler(){
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, SQLException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            command = new Gson().fromJson(message, MakeMoveCommand.class);
        }
        System.out.println("Incoming JSON: " + message);
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
            gameManager.leave(command.getGameID(), username);

            sendServerNotification(username, String.format("%s left the game", username));
            connections.remove(username);

        } catch (SQLException | DataAccessException | IOException | ResponseException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "ERROR: " + e.getMessage() + "or an other issue");

            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        try{
            String username = getUsername(command.getAuthToken(), session);

            //resign attempt as observer throws error
            if(!username.equals(gameManager.getWhiteUsername()) && (!username.equals(gameManager.getBlackUsername()))){
                throw new ResponseException("Cannot resign as an observer!");
            }

            if(gameManager.isGameOver()){
                throw new ResponseException("The game has already ended!");
            }

            if (username == gameManager.getWhiteUsername()){
                gameManager.resign(ChessGame.TeamColor.WHITE);
            }
            else {
                gameManager.resign(ChessGame.TeamColor.BLACK);
            }

            var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("GAME OVER: %s resigned!", username));
            connections.broadcastAll(notificationMessage);

        } catch (SQLException | IOException | DataAccessException | ResponseException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " +
                    e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            String username = getUsername(command.getAuthToken(), session);

            if (!gameManager.verifyCorrectPiece(username, command.getMove())){
                throw new RuntimeException("Can't make this move!");
            }

            gameManager.makeMove(command.getMove());

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameManager.getGame(), command.getTeamColor());
            connections.broadcastAll(loadGameMessage);


            String notification = String.format("%s moved %s to %s", username,
                    command.getStartString(), command.getEndString());

            sendServerNotification(username, notification);


        } catch (RuntimeException | SQLException | DataAccessException e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, SQLException, DataAccessException {
        try {
            String username = getUsername(command.getAuthToken(), session);
            //TODO:
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
        String teamColorString = "";
        int gameID = command.getGameID();

        try {
            gameManager = new GameManager(gameID);

            if (command.getTeamColor() == ChessGame.TeamColor.WHITE){
                teamColorString = "WHITE";
            }
            else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
                teamColorString = "BLACK";
            }

            connections.add(username, session);

            sendGame(session, command.getTeamColor());

            String message = String.format("Player %s joined the game as %s", username, teamColorString);

            sendServerNotification(username, message);

        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void observeGame(UserGameCommand command, Session session, String username) throws IOException {
        String message = String.format("%s joined as an observer", username);

        connections.add(username, session);
        sendGame(session, ChessGame.TeamColor.WHITE);
        sendServerNotification(username, message);
    }

    private void sendGame(Session session, ChessGame.TeamColor teamColor) throws IOException {
        ChessGame game = gameManager.getGame();
        var loadGameMessage = new LoadGameMessage(game, teamColor);


        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }

    private void sendServerNotification(String username, String message) throws IOException {
        //var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        //serverMessage.addMessage(message);
        var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        //System.out.println("Serialized: " + new Gson().toJson(notificationMessage));

        connections.broadcast(username, notificationMessage);
    }

    private String getUsername(String authToken, Session session) throws SQLException, DataAccessException, IOException {
        return Server.userService.getUsername(authToken);
    }
}
