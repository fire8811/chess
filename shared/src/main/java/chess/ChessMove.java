package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;

    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        else if (this == obj){ //obj and instance point to same object
            return true;
        }
        else if (this.getClass() != obj.getClass()){ //obj is a different class type
            return false;
        }
        else {
            ChessMove objAsMove = (ChessMove) obj;
            return (objAsMove.startPosition.equals(this.startPosition)) && (objAsMove.endPosition.equals(this.endPosition))
                    && (objAsMove.promotionPiece == this.promotionPiece);
        }
    }

    @Override public int hashCode() {
        int hash = 5;
        hash = 31 * hash + startPosition.hashCode();
        hash = 31 * hash + endPosition.hashCode();

        if (promotionPiece != null) {
            hash = 31 * hash + promotionPiece.hashCode();
        }

        return hash;
    }
}
