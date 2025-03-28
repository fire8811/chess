package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    public ChessPiece(ChessPiece oldPiece){
        this.pieceColor = oldPiece.pieceColor;
        this.pieceType = oldPiece.pieceType;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /*
         This method is similar to ChessGame.validMoves, except it does not honor whose turn it is or check if
         the king is being attacked. This method does account for enemy and friendly pieces blocking movement
         paths. The pieceMoves method will need to take into account the type of piece, and the location of other
         pieces on the board.
         */
        switch (pieceType){
            case BISHOP -> {
                var bishopMoves = new BishopMovesCalculator(pieceColor);
                return bishopMoves.pieceMoves(board, myPosition);
            }
            case ROOK -> {
                var rookMoves = new RookMovesCalculator(pieceColor);
                return rookMoves.pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                var queenMoves = new QueenMovesCalculator(pieceColor);
                return queenMoves.pieceMoves(board, myPosition);
            }
            case KING -> {
                var kingMoves = new KingMovesCalculator(pieceColor);
                return kingMoves.pieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                var knightMoves = new KnightMoveCalculator(pieceColor);
                return knightMoves.pieceMoves(board, myPosition);
            }
            case PAWN -> {
                var pawnMoves = new PawnMoveCalculator(pieceColor);
                return pawnMoves.pieceMoves(board, myPosition);
            }

            default -> throw new RuntimeException("Not implemented");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, getPieceType());
    }
}
