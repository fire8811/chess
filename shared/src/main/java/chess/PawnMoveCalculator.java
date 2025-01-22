package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMovesCalculator{
    private ChessGame.TeamColor myTeam;

    public PawnMoveCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private Collection<ChessMove> getValidMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> pawnMoves){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (myTeam == ChessGame.TeamColor.WHITE){
            if (row == 2 && (board.getPiece(new ChessPosition(row+2, col)) == null)){ //white first move
                var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                pawnMoves.add(jumpTwoSquares);
            }

            var potentialAdvance = new ChessPosition(row + 1, col);

            if (board.getPiece(potentialAdvance) == null){
                var advanceOneSquare = new ChessMove(myPosition, potentialAdvance, null);
                pawnMoves.add(advanceOneSquare);
            }
        }

//        else if ((row == 7 && myTeam == ChessGame.TeamColor.BLACK)){ //black first pawn move
//            var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row - 2, col), null);
//            pawnMoves.add(jumpTwoSquares);
//        }
        return pawnMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> pawnMoves = new ArrayList<>();

        getValidMoves(myPosition, board, pawnMoves);
        return pawnMoves;
    }
}
