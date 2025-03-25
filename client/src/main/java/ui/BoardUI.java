package ui;


import chess.ChessGame;
import static ui.EscapeSequences.*;

import java.nio.charset.StandardCharsets;
import java.io.PrintStream;

public class BoardUI {
    private static ChessGame.TeamColor teamColor;
    private static char[] WHITE_LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};
    private static char[] BLACK_LETTERS = {'g', 'f', 'e', 'd', 'c', 'b', 'a'};

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

    public void drawWhiteBoard(PrintStream out){
       // draw
    }

    public void drawBlackBoard(PrintStream out) {

    }

    private static void setGray(PrintStream out){
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out){ //chessquare
        out.print(SET_BG_COLOR_BLUE);
    }

    private static void setLightGray(PrintStream out){ //chess square alternate color
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    private static void blackPiece(PrintStream out){
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void whitePiece(PrintStream out){
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void blackVoid(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
