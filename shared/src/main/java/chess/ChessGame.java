package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor whosTurn;
    private ChessBoard gameBoard;

    private boolean whiteInCheck = false;
    private boolean blackInCheck = false;
    private boolean whiteInCheckmate = false;
    private boolean blackInCheckmate = false;
    private boolean whiteInStalemate = false;
    private boolean blackInStalemate = false;

    public ChessGame() {
        this.whosTurn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whosTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whosTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceToMove = gameBoard.getPiece(startPosition);
        Collection<ChessMove> moves = pieceToMove.pieceMoves(gameBoard, startPosition);

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!squareNotEmpty(move.getStartPosition())){ //check if starting square has a piece
            throw new InvalidMoveException(); //if not throw exception
        }

        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if(getTeamTurn() == pieceToMove.getTeamColor() && validMoves.contains(move) && !isInCheck(getTeamTurn())){
            gameBoard.addPiece(move.getStartPosition(), null);

            if (move.getPromotionPiece() != null){
                var promotionPiece = new ChessPiece(getTeamTurn(), move.getPromotionPiece());
                gameBoard.addPiece(move.getEndPosition(), promotionPiece);
            }
            else {
                gameBoard.addPiece(move.getEndPosition(), pieceToMove);
            }

            if (getTeamTurn() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            }
            else {
                setTeamTurn(TeamColor.WHITE);
            }

        } else{
            throw new InvalidMoveException();
        }
    }

    public boolean squareNotEmpty(ChessPosition square){ //check if square contains a piece
        if (gameBoard.getPiece(square) != null) {
            return true;
        }
        return false;
    }

    private boolean doesNotCauseCheck(ChessMove move, ChessPiece pieceToMove){
        ChessBoard potentialBoard = gameBoard;
        //TODO: impliment by making the potential move and then use same logic as isInCheck to see if the move causes isInCheck to change
        return false;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean seeIfCheck(ChessBoard board, TeamColor teamColor){
        ChessPosition kingPosition = getKingPosition(board, teamColor); //get position of king

        for(int row = 1; row < 9; row++){ //iterate through all enemy pieces' moves and see if they can kill the king
            for(int col = 1; col < 9; col++){
                ChessPosition square = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(square);

                if (piece != null && piece.getTeamColor() != teamColor) {  //square contains enemy piece
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, square); //get collection of that piece's moves

                }

            }
        }
        return false;
    }

    //finds the position of the given team's king
    private ChessPosition getKingPosition(ChessBoard board, TeamColor myTeam) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                var square = new ChessPosition(row, col);

                if (squareNotEmpty(square)){
                    ChessPiece piece = board.getPiece(square);
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == myTeam)
                        return square; //return position of myTeam's king
                }
            }
        }
        return null;
    }

    public boolean canKillKing(ChessPosition kingPosition, Collection<ChessMove> enemyMoves){
        for (ChessMove move: enemyMoves){
            if(move.getEndPosition() == kingPosition){
                return true;
            }
        }
        return false;
    }

    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE){

            return whiteInCheck;
        }
        else {
            return blackInCheck;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE){
            return whiteInCheckmate;
        }
        else {
            return blackInCheckmate;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE){
            return whiteInStalemate;
        }
        else {
            return blackInStalemate;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board; //create board with pieces
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
