package websocket;

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
        connections.add(username, session);
        String message = String.format("%s joined the game", username);

        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.addMessage(message);
        connections.broadcast(username, serverMessage);
    }

    private String getUsername(String authToken) throws SQLException, DataAccessException {
        return authDAO.getUsername(authToken);
    }
}
