package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMovesCalculator{
    private ChessGame.TeamColor myTeam;

    public PawnMoveCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private boolean inBounds(ChessPosition position){
        return position.getRow() <= 8 && position.getRow() >= 1 && position.getColumn() <= 8 && position.getColumn() >= 1;
    }

    private Collection<ChessMove> getValidMoves(ChessPosition myPosition, ChessBoard board, Collection<ChessMove> pawnMoves){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (myTeam == ChessGame.TeamColor.WHITE){
            if (row == 2 && (board.getPiece(new ChessPosition(row+2, col)) == null)){ //white first move
                var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                pawnMoves.add(jumpTwoSquares);
            }

//            for (int col = myPosition.getColumn() - 1; col <= col + 1; col++){
//
//            }

            var potentialAdvance = new ChessPosition(row + 1, col);
            var potentialCaptureLeft = new ChessPosition(row + 1, col - 1);
            var potentialCaptureRight = new ChessPosition(row + 1, col + 1);

            if (inBounds(potentialAdvance) && board.getPiece(potentialAdvance) == null){
                var advanceOneSquare = new ChessMove(myPosition, potentialAdvance, null);
                pawnMoves.add(advanceOneSquare);


            }
            if (inBounds(potentialCaptureLeft) && (board.getPiece(potentialCaptureLeft) != null) &&
                    (board.getPiece(potentialCaptureLeft).getTeamColor() != myTeam)){
                var captureLeft = new ChessMove(myPosition, potentialCaptureLeft, null);
                pawnMoves.add(captureLeft);
            }
            if (inBounds(potentialCaptureRight) && (board.getPiece(potentialCaptureRight) != null) &&
                    (board.getPiece(potentialCaptureRight).getTeamColor() != myTeam)){
                var captureRight = new ChessMove(myPosition, potentialCaptureRight, null);
                pawnMoves.add(captureRight);
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
