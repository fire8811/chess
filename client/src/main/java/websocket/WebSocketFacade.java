package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

//client side
public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    //I'll need to serialize and send the game command to the server which then deserializes it
    //I then need to be able to parse serverMessages received here and broadcast them

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException { //used in join or observe game
        try{
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                    serverMessageHandler.displayMessage(message);
                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(Integer gameID, String authToken, ChessGame.TeamColor teamColor) throws ResponseException{
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            command.setTeamColor(teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void observeGame(Integer gameID, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }


}
