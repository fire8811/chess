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

    private Collection<ChessMove> getValidMoves(int advanceDirection, ChessPosition myPosition, ChessBoard board, Collection<ChessMove> pawnMoves){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //first move white
        if (myTeam == ChessGame.TeamColor.WHITE) {
            if (row == 2 && (board.getPiece(new ChessPosition(row + (2 * advanceDirection), col)) == null)) { //white first move
                var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                pawnMoves.add(jumpTwoSquares);
            }
        }

        //first move black
        if (myTeam == ChessGame.TeamColor.BLACK) {
            if (row == 2 && (board.getPiece(new ChessPosition(row + (2 * advanceDirection), col)) == null)) { //white first move
                var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                pawnMoves.add(jumpTwoSquares);
            }
        }

        var potentialAdvance = new ChessPosition(row + advanceDirection, col);
        var potentialCaptureLeft = new ChessPosition(row + advanceDirection, col - 1);
        var potentialCaptureRight = new ChessPosition(row + advanceDirection, col + 1);

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


    return pawnMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> pawnMoves = new ArrayList<>();

        if (myTeam == ChessGame.TeamColor.WHITE) {
            getValidMoves(1 , myPosition, board, pawnMoves);
        }
        else if (myTeam == ChessGame.TeamColor.BLACK) {
            getValidMoves(-1, myPosition, board, pawnMoves);
        }

        return pawnMoves;
    }
}
