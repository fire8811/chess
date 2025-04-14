package websocket.commands;

public class MakeMoveCommand extends UserGameCommand {
    String start;
    String end;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, String start, String end) {
        super(commandType, authToken, gameID);
        this.start = start;
        this.end = end;
    }
}
