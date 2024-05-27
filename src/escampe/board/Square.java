package escampe.board;

import escampe.board.types.SquareType;

public class Square {
    private final SquareType type;
    private final int column;
    private final int line;

    public Square(int line, int column, SquareType type){
        this.column = column;
        this.line = line;
        this.type = type;
    }


    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public SquareType getType() {
        return type;
    }

    @Override
    public String toString(){
        var letter = 'A' + column;
        var line = getLine();

        return String.valueOf(letter) + line;
    }
}

