package ui;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BoardUI {
    private ChessBoard board;
    private ChessGame.TeamColor teamColor;
    private boolean squareIsWhite;

    private static final String[] WHITE_POSITION_LETTERS = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
    private static final  String[] BLACK_POSITION_LETTERS = {"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
    private static final String[] WHITE_ROW_NUMS = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
    private static final String[] BLACK_ROW_NUMS = { " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 " };

    public void updateBoard(ChessBoard board){
        this.board = board;
    }

    public void drawBoard(ChessGame.TeamColor teamColor){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.teamColor = teamColor;

        String[] colLetters = teamColor == ChessGame.TeamColor.WHITE ? WHITE_POSITION_LETTERS : BLACK_POSITION_LETTERS;
        String[] rowNums = teamColor == ChessGame.TeamColor.WHITE ? WHITE_ROW_NUMS : BLACK_ROW_NUMS;

        drawColLabels(out, colLetters);
        squareIsWhite = false;

        for(String rowLabel : rowNums){
            int index = Integer.parseInt(rowLabel.trim()) - 1; //extract number out of row string " 1 " -> 1
            setLightGray(out); //print black base pieces
            out.print(rowLabel);

            board.getBoardRow(index);
            ArrayList<ChessPiece> row = getRow(index, teamColor);

            printRow(out, row);
            //white: print row in normal order, starting with white
            //black: print row in reverse order, starting with white

            setLightGray(out);
            out.print(rowLabel);
            out.print(RESET_BG_COLOR);
            out.println();
        }

        drawColLabels(out, colLetters);
    }

    private void printRow(PrintStream out, ArrayList<ChessPiece> row) {

        switchSquareColor(out); //used to keep first square the same color as the last square on the prior row
        for(ChessPiece boardSquare : row){
            switchSquareColor(out);

            String square = getSquare(boardSquare); //retrievs proper square to print, either empty or with a chesspiece

            //prints proper team of chesspiece
            if (boardSquare != null && boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE){
                whitePiece(out);
            }
            else if (boardSquare != null && boardSquare.getTeamColor() == ChessGame.TeamColor.BLACK) {
                blackPiece(out);
            }

            out.print(square);
        }
    }

    private String getSquare(ChessPiece boardSquare) {
        if (boardSquare == null){
            return "   "; //empty square
        }
        switch (boardSquare.getPieceType()){
            case KING -> {
               return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            }
            case QUEEN -> {
                return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            }
            case BISHOP -> {
                return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            }
            case KNIGHT -> {
                return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            }
            case ROOK -> {
                return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            }
            case PAWN -> {
                return boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            }
            case null, default -> {
                return " X ";
            }
        }
    }

    private ArrayList<ChessPiece> getRow(int index, ChessGame.TeamColor teamColor) {
        ArrayList<ChessPiece> row = new ArrayList<>(Arrays.asList(board.getBoardRow(index)));

        if (teamColor == ChessGame.TeamColor.BLACK){ //reverse order of list if teamcolor is black for correct board orientation
            Collections.reverse(row);
        }
        
        return row;
    }

    private void switchSquareColor(PrintStream out){
        if(squareIsWhite){
            setBlue(out);
            squareIsWhite = false;
        } else {
            setWhite(out);
            squareIsWhite = true;
        }
    }

    private void drawColLabels(PrintStream out, String[] letters){
        setLightGray(out);
        for (String letter : letters) {
            out.print(letter);
        }

        out.print(RESET_BG_COLOR);
        out.println();
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

    private static void blackPiece(PrintStream out){
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void whitePiece(PrintStream out){
        out.print(SET_TEXT_COLOR_MAGENTA);
    }
}
