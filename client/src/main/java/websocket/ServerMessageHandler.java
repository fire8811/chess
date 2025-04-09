package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void displayMessage(ServerMessage message);
}
