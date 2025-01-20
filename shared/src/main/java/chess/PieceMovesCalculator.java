package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
/* perhaps use a while loop for bishop. start at starting position, and in each direction, add (or sub) 1 to both the x
and y cord. (Unless it's greater than 8, where you'd then stop). Then check if there's a piece using the board.getPiece method
if it returns null then it's a valid move! Otherwise, stop the loop/counting/subtracting up/down there and go in a different direction
*/