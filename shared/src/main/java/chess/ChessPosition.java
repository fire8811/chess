package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        else if (this == obj){ //obj and instance point to same object
            return true;
        }
        else if (this.getClass() != obj.getClass()){ //obj is a different class type
            return false;
        }
        else {
            ChessPosition objAsChessPosition = (ChessPosition) obj;
            return (objAsChessPosition.row == this.row) && (objAsChessPosition.col == this.col);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), col);
    }

    @Override public String toString(){
        return "ChessPosition {row= " + this.row + " col= " + this.col + " }";
    }
}
