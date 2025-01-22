package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private ChessGame.TeamColor myTeam;

    public KingMovesCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private Collection<ChessMove> getValidMove(int rowIncrement, int colIncrement,ChessPosition myPosition,
                                                ChessBoard board, Collection<ChessMove> kingMoves){
        int row = myPosition.getRow() + rowIncrement;
        int col = myPosition.getColumn() + colIncrement;

        if (row <= 8 && row >= 1 && col <= 8 && col >= 1){
            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                kingMoves.add(chessMove);
            }
            else if (board.getPiece(potentialPosition).getTeamColor() != myTeam){ //chess square contains enemy piece
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                kingMoves.add(chessMove);
            }
        }
        return kingMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> kingMoves = new ArrayList<>();

        getValidMove(1, 0, myPosition, board, kingMoves);
        getValidMove(-1, 0, myPosition, board, kingMoves);
        getValidMove(0, 1, myPosition, board, kingMoves);
        getValidMove(0, -1, myPosition, board, kingMoves);

        //diagonal moves below
        getValidMove(1, 1, myPosition, board, kingMoves);
        getValidMove(1, -1, myPosition, board, kingMoves);
        getValidMove(-1, 1, myPosition, board, kingMoves);
        getValidMove(-1, -1, myPosition, board, kingMoves);

        return kingMoves;
    }

}
