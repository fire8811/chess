package chess;


import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    //copy constructor
    public ChessBoard(ChessBoard oldBoard){
        this.board = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (oldBoard.board[i][j] != null){
                    this.board[i][j] = new ChessPiece(oldBoard.board[i][j]);
                }
                else {
                    this.board[i][j] = null;
                }
            }
        }
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j<8; j++){
                board[i][j] = null;
            }
        }

        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        ChessPiece.PieceType king = ChessPiece.PieceType.KING;
        ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;
        ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
        ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;

        //white rooks
        addPiece(new ChessPosition(1, 1), new ChessPiece(white, rook));
        addPiece(new ChessPosition(1, 8), new ChessPiece(white, rook));
        //white knight
        addPiece(new ChessPosition(1, 2), new ChessPiece(white, knight));
        addPiece(new ChessPosition(1, 7), new ChessPiece(white, knight));
        //white bishop
        addPiece(new ChessPosition(1, 3), new ChessPiece(white, bishop));
        addPiece(new ChessPosition(1, 6), new ChessPiece(white, bishop));
        //white queen
        addPiece(new ChessPosition(1, 4), new ChessPiece(white, queen));
        //white king
        addPiece(new ChessPosition(1, 5), new ChessPiece(white, king));
        //white pawns
        for (int i = 1; i < 9; i++){
            addPiece(new ChessPosition(2, i), new ChessPiece(white, pawn));
        }

        //black rooks
        addPiece(new ChessPosition(8, 1), new ChessPiece(black, rook));
        addPiece(new ChessPosition(8, 8), new ChessPiece(black, rook));
        //black knight
        addPiece(new ChessPosition(8, 2), new ChessPiece(black, knight));
        addPiece(new ChessPosition(8, 7), new ChessPiece(black, knight));
        //black bishop
        addPiece(new ChessPosition(8, 3), new ChessPiece(black, bishop));
        addPiece(new ChessPosition(8, 6), new ChessPiece(black, bishop));
        //black queen
        addPiece(new ChessPosition(8, 4), new ChessPiece(black, queen));
        //black king
        addPiece(new ChessPosition(8, 5), new ChessPiece(black, king));
        //black pawns
        for (int i = 1; i < 9; i++){
            addPiece(new ChessPosition(7, i), new ChessPiece(black, pawn));
        }
     }

     public ChessPiece[] getBoardRow(int index){
        return board[index];
     }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            sb.append(row + 1).append(" ");  // Print row numbers
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece == null) {
                    sb.append(". ");
                } else {
                    char symbol = getPieceSymbol(piece);
                    sb.append(symbol).append(" ");
                }
            }
            sb.append("\n");
        }
        sb.append("  a b c d e f g h");  // Column labels
        return sb.toString();
    }

    private char getPieceSymbol(ChessPiece piece) {
        char symbol;
        switch (piece.getPieceType()) {
            case KING: symbol = 'K'; break;
            case QUEEN: symbol = 'Q'; break;
            case ROOK: symbol = 'R'; break;
            case BISHOP: symbol = 'B'; break;
            case KNIGHT: symbol = 'N'; break;
            case PAWN: symbol = 'P'; break;
            default: symbol = '?';
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            symbol = Character.toLowerCase(symbol);
        }
        return symbol;
    }
}
