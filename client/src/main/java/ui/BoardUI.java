package ui;


import chess.*;
import exceptions.ResponseException;

import static ui.EscapeSequences.*;

import java.nio.charset.StandardCharsets;
import java.io.PrintStream;
import java.util.*;

public class BoardUI {
    private ChessGame game;
    private ChessBoard board;
    private boolean squareIsWhite;

    private static final String[] WHITE_POSITION_LETTERS = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
    private static final  String[] BLACK_POSITION_LETTERS = {"   ", " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", "   "};
    private static final String[] WHITE_ROW_NUMS = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
    private static final String[] BLACK_ROW_NUMS = { " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 " };

    public void updateBoard(ChessGame game){
        this.game = game;
        this.board = game.getBoard();
    }

    public void drawBoard(ChessGame.TeamColor teamColor, ArrayList<ChessMove> highlightMoves){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

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

            printRow(out, row, index + 1, highlightMoves);
            //white: print row in normal order, starting with white
            //black: print row in reverse order, starting with white

            setLightGray(out);
            out.print(rowLabel);
            out.print(RESET_BG_COLOR);
            out.println();
        }

        drawColLabels(out, colLetters);
    }

    public void drawMoves(ChessPosition position, ChessGame.TeamColor teamColor){
        ChessPiece piece = board.getPiece(position);
        System.out.println(position);
        if(piece == null){
            throw new ResponseException("Square Empty!");
        }

        Collection<ChessMove> moves = game.validMoves(position);
        drawBoard(teamColor, new ArrayList<>(moves));



        //I'M THINKING WE JUST STORE THIS COLLECTION AND IN THE DRAW FUNCTION WE HAVE A CHECK
        //TO SEE IF THE SQUARE POSITION IS IN THE COLLECTION AND THEN PRINT IT GREEN INSTEAD OF THE
        //NORMAL COLOR. THIS WILL MEAN ADDITIONAL FLAG VARIABLES IN DRAW METHOD TO CHECK
        //FOR NORMAL BOARD PRINTING OR PRINTING LEGAL MOVES AS WELL
    }

    private void printRow(PrintStream out, ArrayList<ChessPiece> row, int rowIndex, ArrayList<ChessMove> highlightMoves) {

        switchSquareColor(out); //used to keep first square the same color as the last square on the prior row
        int colIndex = 1;
        for(ChessPiece boardSquare : row){
            switchSquareColor(out);

            String square = getSquare(boardSquare); //retrievs proper square to print, either empty or with a chesspiece

            if (highlightMoves != null){
                for(ChessMove move : highlightMoves){
                    int moveRow = move.getEndPosition().getRow();
                    int moveCol = move.getEndPosition().getColumn();

                    if (move.getStartPosition().getRow() == rowIndex && move.getStartPosition().getColumn() == colIndex){
                        setYellow(out); //square of piece to be highlighted
                    }
                    else if (moveRow == rowIndex && moveCol == colIndex){ //legal moves of piece
                        if(squareIsWhite) setLightGreen(out); else setDarkGreen(out);
                    }
                }
            }

            //prints proper team of chesspiece
            if (boardSquare != null && boardSquare.getTeamColor() == ChessGame.TeamColor.WHITE){
                whitePiece(out);
            }
            else if (boardSquare != null && boardSquare.getTeamColor() == ChessGame.TeamColor.BLACK) {
                blackPiece(out);
            }

            out.print(square);
            colIndex++;
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

    private static void setLightGreen(PrintStream out){
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void setDarkGreen(PrintStream out){
        out.print(SET_BG_COLOR_DARK_GREEN);
    }

    private static void setYellow(PrintStream out){
        out.print(SET_BG_COLOR_YELLOW);
    }

    private static void blackPiece(PrintStream out){
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void whitePiece(PrintStream out){
        out.print(SET_TEXT_COLOR_MAGENTA);
    }
}
