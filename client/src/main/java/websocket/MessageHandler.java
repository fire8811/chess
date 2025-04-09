package websocket;

import websocket.messages.ServerMessage;

public interface MessageHandler {
    void displayMessage(ServerMessage message);
}
