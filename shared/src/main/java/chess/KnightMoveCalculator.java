package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMovesCalculator {
    private ChessGame.TeamColor myTeam;

    public KnightMoveCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private Collection<ChessMove> getValidMoves(int rowIncrement, int colIncrement,ChessPosition myPosition,
                                                ChessBoard board, Collection<ChessMove> knightMoves){
        int row = myPosition.getRow() + rowIncrement;
        int col = myPosition.getColumn() + colIncrement;

         if (row <= 8 && row >= 1 && col <= 8 && col >= 1){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                knightMoves.add(chessMove);
            }
            else if (board.getPiece(potentialPosition).getTeamColor() != myTeam){ //chess square contains enemy piece
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                knightMoves.add(chessMove);
            }
        }
        return knightMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> knightMoves = new ArrayList<>();

        getValidMoves(2, 1, myPosition, board, knightMoves);
        getValidMoves(1, 2, myPosition, board, knightMoves);
        getValidMoves(-1, 2, myPosition, board, knightMoves);
        getValidMoves(-2, 1, myPosition, board, knightMoves);

        getValidMoves(-2, -1, myPosition, board, knightMoves);
        getValidMoves(-1, -2, myPosition, board, knightMoves);
        getValidMoves(1, -2, myPosition, board, knightMoves);
        getValidMoves(2, -1, myPosition, board, knightMoves);

        return knightMoves;

    }
}
