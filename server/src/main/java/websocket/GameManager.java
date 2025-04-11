package websocket;

import chess.*;

public class GameManager {
    private ChessGame game;

    public GameManager(ChessGame game){
        this.game = game;
    }

    public ChessGame getGame() {return game; }

    public void makeMove(String startPositionString, String endPositionString){
        ChessPosition startPosition = createPosition(startPositionString);
        ChessPosition endPosition = createPosition(endPositionString);

        //TODO: figure out how to pass in pawn promotion pieces
        ChessMove move = new ChessMove(startPosition, endPosition, null);

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("GAME ERROR: " + e.getMessage());
        }

    }

    private ChessPosition createPosition(String position){
        char colLetter = position.charAt(0);
        int colInt = colLetter - 'a' + 1; ///use unicode value of 'a' to get proper column number
        int row = position.indexOf(1);

        System.out.println("ROW: " + row + " COL: " + colInt);
        return new ChessPosition(row, colInt);
    }

}
