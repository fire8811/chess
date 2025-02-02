package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    private ChessGame.TeamColor myTeam;

    public QueenMovesCalculator(ChessGame.TeamColor myTeam){
        this.myTeam = myTeam;
    }

    private Collection<ChessMove> validMovesOneDirection(int rowIncrement, int colIncrement,
                                                         ChessPosition myPosition, ChessBoard board,
                                                         Collection<ChessMove> queenMoves) {

        int row = myPosition.getRow() + rowIncrement;
        int col = myPosition.getColumn() + colIncrement;

        while (row <= 8 && col <= 8 && col >= 1 && row >= 1){

            var potentialPosition = new ChessPosition(row, col);

            if (board.getPiece(potentialPosition) == null){ //if chess square is empty create move and add to move list
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                queenMoves.add(chessMove);
            }
            else if (board.getPiece(potentialPosition).getTeamColor() != myTeam){ //chess square contains enemy piece
                var chessMove = new ChessMove(myPosition, potentialPosition, null);
                queenMoves.add(chessMove);
                break; //no more valid moves for this direction, so exit loop
            }
            else {
                break;
            }
            row = row + rowIncrement;
            col = col + colIncrement;
        }

        return queenMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> queenMoves = new ArrayList<>();

        validMovesOneDirection(1, 1, myPosition, board, queenMoves);
        validMovesOneDirection(1, -1, myPosition, board, queenMoves);
        validMovesOneDirection(-1, 1, myPosition, board, queenMoves);
        validMovesOneDirection(-1, -1, myPosition, board, queenMoves);
        validMovesOneDirection(1, 0, myPosition, board, queenMoves);
        validMovesOneDirection(-1, 0, myPosition, board, queenMoves);
        validMovesOneDirection(0, 1, myPosition, board, queenMoves);
        validMovesOneDirection(0, -1, myPosition, board, queenMoves);



        return queenMoves;
    }
}
