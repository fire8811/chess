package websocket;

import exceptions.ResponseException;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//client side
public class WebSocketFacade {
    Session session;
    MessageHandler messageHandler;
    //I'll need to serialize and send the game command to the server which then deserializes it
    //I then need to be able to parse serverMessages received here and broadcast them

    public WebSocketFacade(String url, MessageHandler messageHandler) throws ResponseException { //used in join or observe game
        try{
            url = url.replace("http", "ws");
            URI uri = new URI(url+ "w/s");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addM

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(e.getMessage());
        }

    }
}
