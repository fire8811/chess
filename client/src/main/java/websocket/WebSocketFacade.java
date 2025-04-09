package websocket;

import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//client side
public class WebSocketFacade {
    Session session;
    ServerMessageHandler serverMessageHandler;
    //I'll need to serialize and send the game command to the server which then deserializes it
    //I then need to be able to parse serverMessages received here and broadcast them

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException { //used in join or observe game
        try{
            url = url.replace("http", "ws");
            URI uri = new URI(url+ "w/s");
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


}
