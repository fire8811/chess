package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(ServerMessageType type, String message){
        super(type);
        this.message = message;
        super.addMessage(message);
    }
}
