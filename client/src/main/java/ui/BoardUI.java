package ui;


import chess.ChessGame;
import static ui.EscapeSequences.*;

import java.nio.charset.StandardCharsets;
import java.io.PrintStream;

public class BoardUI {
    private static ChessGame.TeamColor teamColor;
    private static String[] WHITE_POSITION_LETTERS = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
    private static String[] BLACK_POSITION_LETTERS = {"   ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", " h ", "   "};
    private static String[] WHITE_BASE_PIECES = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING,
            WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

    private static String[] BLACK_BASE_PIECES = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN,
            BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};


    private static int BOARD_EDGE_SIZE = 10;

    public BoardUI(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }

    public void drawBoard(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (teamColor == ChessGame.TeamColor.WHITE){
            drawWhiteBoard(out);
        }
        else {
            drawBlackBoard(out);
        }
    }

    private void drawWhiteBoard(PrintStream out){
        drawColLabels(out, WHITE_POSITION_LETTERS);
        drawRowsWhite(out, WHITE_BASE_PIECES, BLACK_BASE_PIECES);
        drawColLabels(out, WHITE_POSITION_LETTERS);
    }

    private void drawBlackBoard(PrintStream out) {

    }

    private void drawColLabels(PrintStream out, String[] letters){
        setLightGray(out);
        for (int boardCol = 0; boardCol < letters.length; boardCol++){
            out.print(letters[boardCol]);
        }

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void drawRowsWhite(PrintStream out, String[] whitePieces, String[] blackPieces){
        setLightGray(out);
        out.print(" 8 ");
        setWhite(out);
        boolean isWhite = true;

        for (int i = 0; i < blackPieces.length; i++){
            out.print(BLACK_BASE_PIECES[i]);
            isWhite = switchSquareColor(out, isWhite);
        }

        setLightGray(out);
        out.print(" 8 ");

        out.print(RESET_BG_COLOR);
        out.println();

        setLightGray(out); //print out black pawns here
        out.print(" 7 ");
        setBlue(out);
        isWhite = false;

        for(int i = 0; i < blackPieces.length; i++){
            out.print(BLACK_PAWN);
            isWhite = switchSquareColor(out, isWhite);
        }

        setLightGray(out);
        out.print(" 7 ");


        for(int row = 8; row > 0; row--){
            setLightGray(out);
            String rowLabel = String.format(" %d ", row+1);

            for (int col=0; col < 10; col++){
                out.print(rowLabel);
                setWhite(out);
            }



        }
    }

    private boolean switchSquareColor(PrintStream out, boolean isWhite){
        if(isWhite){
            setBlue(out);
            isWhite = false;
        } else {
            setWhite(out);
            isWhite = true;
        }

        return isWhite;
    }

    private static void setLightGray(PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlue(PrintStream out){ //chessquare
        out.print(SET_BG_COLOR_BLUE);
    }

    private static void setWhite(PrintStream out){ //chess square alternate color
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void blackPieces(PrintStream out){
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void whitePieces(PrintStream out){
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void blackVoid(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
