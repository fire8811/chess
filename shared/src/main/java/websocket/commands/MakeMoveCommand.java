package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private  ChessMove move;
    private String startString;
    private String endString;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move,
                           String startMove, String endMove) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.startString = startMove;
        this.endString = endMove;
    }

    public ChessMove getMove(){
        return move;
    }

    public String getStartString() {
        return startString;
    }

    public String getEndString() {
        return endString;
    }
}
