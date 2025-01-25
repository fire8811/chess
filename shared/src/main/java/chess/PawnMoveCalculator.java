package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMovesCalculator{
    private ChessGame.TeamColor myTeam;

    public PawnMoveCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private boolean canPromote(ChessPosition position){
        if ((position.getRow() == 8) && (myTeam == ChessGame.TeamColor.WHITE)){
            return true;
        }
        else if ((position.getRow() == 1) && (myTeam == ChessGame.TeamColor.BLACK)) {
            return true;
        }
        else {
            return false;
        }
    }

    private Collection<ChessMove> addPromotions(Collection<ChessMove> pawnMoves,
                                                ChessPosition myPosition, ChessPosition capturePosition){
        ChessPiece.PieceType[] promotionPieces = {ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT,
        ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN};

        for (ChessPiece.PieceType piece : promotionPieces){
            pawnMoves.add(new ChessMove(myPosition, capturePosition, piece));
        }

        return pawnMoves;
    }

    private boolean inBounds(ChessPosition position){
        return position.getRow() <= 8 && position.getRow() >= 1 && position.getColumn() <= 8 && position.getColumn() >= 1;
    }

    private Collection<ChessMove> getValidMoves(int advanceDirection, ChessPosition myPosition, ChessBoard board, Collection<ChessMove> pawnMoves){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //first move white
        if (myTeam == ChessGame.TeamColor.WHITE) {
            if (row == 2 && (board.getPiece(new ChessPosition(row + (2 * advanceDirection), col)) == null)) {
                if (board.getPiece(new ChessPosition(row + advanceDirection, col)) == null) {
                    var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                    pawnMoves.add(jumpTwoSquares);
                }
            }
        }

        //first move black
        if (myTeam == ChessGame.TeamColor.BLACK) {
            if (row == 7 && (board.getPiece(new ChessPosition(row + (2 * advanceDirection), col)) == null)) {
                if (board.getPiece(new ChessPosition(row + advanceDirection, col)) == null) {
                    var jumpTwoSquares = new ChessMove(myPosition, new ChessPosition(row - 2, col), null);
                    pawnMoves.add(jumpTwoSquares);
                }
            }
        }

        //check for spot ahead, capture left, capture right
        var potentialAdvance = new ChessPosition(row + advanceDirection, col);
        var potentialCaptureLeft = new ChessPosition(row + advanceDirection, col - 1);
        var potentialCaptureRight = new ChessPosition(row + advanceDirection, col + 1);

        //advance
        if (inBounds(potentialAdvance) && board.getPiece(potentialAdvance) == null){
            if (canPromote(potentialAdvance)){
                addPromotions(pawnMoves, myPosition, potentialAdvance);
            }
            else {
                var advanceOneSquare = new ChessMove(myPosition, potentialAdvance, null);
                pawnMoves.add(advanceOneSquare);
            }
        }
        //capture left
        if (inBounds(potentialCaptureLeft) && (board.getPiece(potentialCaptureLeft) != null) &&
                (board.getPiece(potentialCaptureLeft).getTeamColor() != myTeam)){

            if (canPromote(potentialCaptureLeft)) {
                addPromotions(pawnMoves, myPosition, potentialCaptureLeft);
            }
            else {
                var captureLeft = new ChessMove(myPosition, potentialCaptureLeft, null);
                pawnMoves.add(captureLeft);
            }

        }
        //capture right
        if (inBounds(potentialCaptureRight) && (board.getPiece(potentialCaptureRight) != null) &&
                (board.getPiece(potentialCaptureRight).getTeamColor() != myTeam)){
            if (canPromote(potentialCaptureRight)){
                addPromotions(pawnMoves, myPosition, potentialCaptureRight);
            }
            else {
                var captureRight = new ChessMove(myPosition, potentialCaptureRight, null);
                pawnMoves.add(captureRight);
            }
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
