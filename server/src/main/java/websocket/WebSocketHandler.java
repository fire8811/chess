package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlUserDAO;
import exceptions.DataAccessException;
//import org.eclipse.jetty.server.session.Session;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;

//server side
@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final SqlAuthDAO authDAO;

    public WebSocketHandler(){
        try{
            this.authDAO = new SqlAuthDAO();
        } catch(SQLException | DataAccessException e){
            throw new RuntimeException("authDAO failed to init becase: " + e.getMessage());
        }
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, SQLException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch(command.getCommandType()){
            case CONNECT -> connect(command, session);
        }
    }

    public void connect(UserGameCommand command, Session session) throws IOException, SQLException, DataAccessException {
        String username = getUsername(command.getAuthToken());

        if (command.getTeamColor() != null) {
            joinGame(command, session, username); //if teamColor field is not null it means it is a request to join the game as a player
        } else {
            observeGame(command, session, username);
        }


    }

    private void joinGame(UserGameCommand command, Session session, String username) throws SQLException, DataAccessException, IOException {
        String teamColor;
        int gameID = command.getGameID();

        if (command.getTeamColor() == ChessGame.TeamColor.WHITE){
            teamColor = "WHITE";
        }
        else {
            teamColor = "BLACK";
        }

        connections.add(username, session);
        drawBoard(command, session);

        String message = String.format("Player %s joined the game as %s", username, teamColor);

        sendServerNotification(username, message);
    }

    private void observeGame(UserGameCommand command, Session session, String username) throws IOException {
        String message = String.format("%s joined as an observer", username);

        connections.add(username, session);
        drawBoard(command, session);
        sendServerNotification(username, message);
    }

    private void drawBoard(UserGameCommand command, Session session){
        int id = command.getGameID();


    }

    private void sendServerNotification(String username, String message) throws IOException {
        //var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        //serverMessage.addMessage(message);
        var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        //System.out.println("Serialized: " + new Gson().toJson(notificationMessage));

        connections.broadcast(username, notificationMessage);
    }

    private String getUsername(String authToken) throws SQLException, DataAccessException {
        return authDAO.getUsername(authToken);
    }
}
