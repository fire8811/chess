package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    Object game;

    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
        game = "";
    }
}
