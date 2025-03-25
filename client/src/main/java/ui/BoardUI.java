package ui;


import chess.ChessGame;
import static ui.EscapeSequences.*;

import java.nio.charset.StandardCharsets;
import java.io.PrintStream;

public class BoardUI {
    private static ChessGame.TeamColor teamColor;
    private static final String[] WHITE_POSITION_LETTERS = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
    private static final  String[] BLACK_POSITION_LETTERS = {"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
    private static final String[] WHITE_BASE_PIECES = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING,
            WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};

    private static final String[] BLACK_FROM_WHITE = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING,
            BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}; //black's pieces from white's perspective

    private static final String[] BLACK_BASE_PIECES = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN,
            BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};

    private static final String[] WHITE_FROM_BLACK = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN,
            WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}; //white's pieces from black's perspective

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
        drawColLabels(out, BLACK_POSITION_LETTERS);
        drawRowsBlack(out, WHITE_BASE_PIECES, BLACK_BASE_PIECES);
        drawColLabels(out, BLACK_POSITION_LETTERS);
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
        setLightGray(out); //print black base pieces
        out.print(" 8 ");
        setWhite(out);
        boolean isWhite = true;

        for (int i = 0; i < blackPieces.length; i++){
            out.print(BLACK_FROM_WHITE[i]);
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
            isWhite = switchSquareColor(out, isWhite); //alternate square colors
        }

        setLightGray(out);
        out.print(" 7 ");
        out.print(RESET_BG_COLOR);
        out.println();
        //end of black pawn/pieces printing

        //print blank squares in middle of board
        for (String rowLabel : new String[]{" 6 ", " 5 ", " 4 ", " 3 "}){
            setLightGray(out);
            out.print(rowLabel);

            isWhite = getFirstSquareColor(out, isWhite);

            for (int col = 0; col < 8; col++){
                out.print("   "); //blank square
                isWhite = switchSquareColor(out, isWhite);

            }

            setLightGray(out);
            out.print(rowLabel);
            out.print(RESET_BG_COLOR);
            out.println();
        }

        //draw white pawns
        setLightGray(out);
        out.print(" 2 ");
        setWhite(out);
        isWhite = true;
        whitePieces(out);

        for(int i = 0; i < blackPieces.length; i++){
            out.print(WHITE_PAWN);
            isWhite = switchSquareColor(out, isWhite); //alternate square colors
        }

        setLightGray(out);
        out.print(" 2 ");
        out.print(RESET_BG_COLOR);
        out.println();
        //end of white pawn printing

        //print white base pieces
        setLightGray(out); //print black base pieces
        out.print(" 1 ");
        setBlue(out);
        isWhite = false;

        for (int i = 0; i < whitePieces.length; i++){
            whitePieces(out); // change to white piece color
            out.print(WHITE_BASE_PIECES[i]);
            isWhite = switchSquareColor(out, isWhite);
        }

        setLightGray(out);
        out.print(" 1 ");

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static boolean getFirstSquareColor(PrintStream out, boolean isWhite) {
        if(!isWhite){
            setWhite(out);
            isWhite = true;
        }
        else{
            setBlue(out);
            isWhite = false;
        }
        return isWhite;
    }

    private void drawRowsBlack(PrintStream out, String[] whitePieces, String[] blackPieces) {
        setLightGray(out); //print white base pieces
        out.print(" 1 ");
        setWhite(out);
        boolean isWhite = true;
        whitePieces(out);

        for (int i = 0; i < blackPieces.length; i++){
            out.print(WHITE_FROM_BLACK[i]);
            isWhite = switchSquareColor(out, isWhite);
        }

        setLightGray(out);
        out.print(" 1 ");

        out.print(RESET_BG_COLOR);
        out.println();

        setLightGray(out); //print out white pawns here
        out.print(" 2 ");
        setBlue(out);
        isWhite = false;
        whitePieces(out);

        for(int i = 0; i < whitePieces.length; i++){
            out.print(WHITE_PAWN);
            isWhite = switchSquareColor(out, isWhite); //alternate square colors
        }

        setLightGray(out);
        out.print(" 2 ");
        out.print(RESET_BG_COLOR);
        out.println();
        //end of black pawn/pieces printing

        //print blank squares in middle of board
        for (String rowLabel : new String[]{" 3 ", " 4 ", " 5 ", " 6 "}){
            setLightGray(out);
            out.print(rowLabel);

            isWhite = getFirstSquareColor(out, isWhite);

            for (int col = 0; col < 8; col++){
                out.print("   "); //blank square
                isWhite = switchSquareColor(out, isWhite);

            }

            setLightGray(out);
            out.print(rowLabel);
            out.print(RESET_BG_COLOR);
            out.println();
        }

        //draw black pawns
        setLightGray(out);
        out.print(" 7 ");
        setWhite(out);
        isWhite = true;
        blackPieces(out);

        for(int i = 0; i < blackPieces.length; i++){
            out.print(BLACK_PAWN);
            isWhite = switchSquareColor(out, isWhite); //alternate square colors
        }

        setLightGray(out);
        out.print(" 7 ");
        out.print(RESET_BG_COLOR);
        out.println();
        //end of white pawn printing

        //print white base pieces
        setLightGray(out); //print black base pieces
        out.print(" 8 ");
        setBlue(out);
        isWhite = false;

        for (int i = 0; i < whitePieces.length; i++){
            blackPieces(out); // change to white piece color
            out.print(BLACK_BASE_PIECES[i]);
            isWhite = switchSquareColor(out, isWhite);
        }

        setLightGray(out);
        out.print(" 8 ");

        out.print(RESET_BG_COLOR);
        out.println();
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
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

}
