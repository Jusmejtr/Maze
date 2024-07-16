package sk.tuke.gamestudio.core;

import java.util.Random;

public class Player {
    private Field field;
    private int row;
    private int column;
    public Player(Field field){
        this.field = field;
        this.row = pickRandomRow();
        this.column = pickRandomColumn();
        createPlayer(row, column);
    }
    public Player(Field field, int row, int column){
        this.field = field;
        this.row = row;
        this.column = column;
        createPlayer(row, column);
    }
    private int pickRandomRow(){
        Random random = new Random();
        int x = random.nextInt(100);
        if(x % 2 == 0){
            return row = 1;
        }else{
            return row = field.getRowCount()-2;
        }
    }
    private int pickRandomColumn(){
        Random random = new Random();
        int y = random.nextInt(100);
        if(y % 2 == 0){
            return column = 1;
        }else{
            return column = field.getColumnCount()-2;
        }
    }

    public void createPlayer(int row, int column) {
        if(row < 1 || row > field.getRowCount()-2 || column < 1 || column > field.getColumnCount()-2) throw new IllegalArgumentException("Invalid position");
        if(field.getTile(row, column) instanceof Road){
            field.getTile(row, column).setState(TileState.PLAYER);
        }
    }
    public void move(Direction direction){
        Tile newPosition = field.getTile(row + direction.getRow(), column + direction.getColumn());
        if(newPosition instanceof Wall) return;
        if(hitFinish(row + direction.getRow(), column + direction.getColumn())){
            field.setFieldState(FieldState.FINISHED);
            field.getTimer().stopTimer();
        }
        field.getTile(row, column).setState(TileState.EMPTY);
        newPosition.setState(TileState.PLAYER);
        row = row + direction.getRow();
        column = column + direction.getColumn();
    }
    private boolean hitFinish(int row, int column){
        if(field.getTile(row, column).getState() == TileState.FINISH) return true;
        return false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
