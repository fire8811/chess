package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    private ChessGame.TeamColor myTeam;
    public BishopMovesCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }
    private Collection<ChessMove> validMovesOneDirection(int rowIncrement, int colIncrement,
                                        ChessPosition myPosition, ChessBoard board,
                                        Collection<ChessMove> bishopMoves) {

        int row = myPosition.getRow() + rowIncrement;
        int col = myPosition.getColumn() + colIncrement;

        while (row <= 8 && col <= 8 && col >= 1 && row >= 1){

            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                bishopMoves.add(chessMove);
            }
            else {
                break;
            }
            row = row + rowIncrement;
            col = col + colIncrement;
        }

        return bishopMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();

        validMovesOneDirection(1, 1, myPosition, board, bishopMoves);
        validMovesOneDirection(1, -1, myPosition, board, bishopMoves);
        validMovesOneDirection(-1, 1, myPosition, board, bishopMoves);
        validMovesOneDirection(-1, -1, myPosition, board, bishopMoves);

        return bishopMoves;
    }
}
