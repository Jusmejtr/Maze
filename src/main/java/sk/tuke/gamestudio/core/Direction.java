package sk.tuke.gamestudio.core;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int row;
    private final int column;
    Direction(int x, int y) {
        this.row = x;
        this.column = y;
    }
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
