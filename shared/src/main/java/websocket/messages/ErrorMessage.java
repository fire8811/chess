package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;
    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public ServerMessageType getServerMessageType() { return serverMessageType; }
    public String getErrorMessage() { return errorMessage; }
}

