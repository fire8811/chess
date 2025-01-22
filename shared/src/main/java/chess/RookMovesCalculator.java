package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    private ChessGame.TeamColor myTeam;

    public RookMovesCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private Collection<ChessMove> getHorizontalMoves(int increment, ChessPosition myPosition, ChessBoard board,
                                                     Collection<ChessMove> rookMoves){
        int row = myPosition.getRow();
        int col = myPosition.getColumn() + increment;

        while (row <= 8 && row >= 1 && col <= 8 && col >= 1){

            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){
                var validMove = new ChessMove(myPosition, potentialPosition, null);
                rookMoves.add(validMove);
            }
            else if (board.getPiece(potentialPosition).getTeamColor() != myTeam){
                var validMove = new ChessMove(myPosition, potentialPosition, null);
                rookMoves.add(validMove);
                break;
            }
            else{
                break;
            }

            col = col + increment;
        }
        return rookMoves;
    }

    private Collection<ChessMove> getVerticalMoves(int increment, ChessPosition myPosition, ChessBoard board,
                                                   Collection<ChessMove> rookMoves){
        int row = myPosition.getRow() + increment;
        int col = myPosition.getColumn();

        while (row <= 8 && row >= 1 && col <= 8 && col >= 1){

            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){
                var validMove = new ChessMove(myPosition, potentialPosition, null);
                rookMoves.add(validMove);
            }
            else if (board.getPiece(potentialPosition).getTeamColor() != myTeam){
                var validMove = new ChessMove(myPosition, potentialPosition, null);
                rookMoves.add(validMove);
                break;
            }
            else{
                break;
            }

            row = row + increment;
        }
        return rookMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> rookMoves = new ArrayList<>();

        getHorizontalMoves(1, myPosition, board, rookMoves);
        getHorizontalMoves(-1, myPosition, board, rookMoves);
        getVerticalMoves(1, myPosition, board, rookMoves);
        getVerticalMoves(-1, myPosition, board, rookMoves);

        return rookMoves;
    }

}
