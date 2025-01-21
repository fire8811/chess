package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();

        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;

        while (row <= 8 && col <= 8){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                bishopMoves.add(chessMove);
            }
            else {
                break;
            }
            row = row + 1;
            col = col + 1;
        }

        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() - 1;

        while (row <= 8 && col >= 1){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                bishopMoves.add(chessMove);
            }
            else {
                break;
            }
            row = row + 1;
            col = col - 1;
        }

        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() - 1;

        while (row >= 1 && col >= 1){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                bishopMoves.add(chessMove);
            }
            else {
                break;
            }
            row = row - 1;
            col = col - 1;
        }

        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() + 1;

        while (row >= 1 && col <= 8){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                bishopMoves.add(chessMove);
            }
            else {
                break;
            }
            row = row - 1;
            col = col + 1;
        }

        return bishopMoves;
    }
}
