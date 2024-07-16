package sk.tuke.gamestudio.core;

import java.util.ArrayList;
import java.util.Random;

public class Field {
    private final Tile[][] tiles;
    private final int rowCount;
    private final int columnCount;
    private FieldState state = FieldState.PLAYING;
    private Player player;
    private Finish finish;
    private Timer timer;


    public Field(int rowCount, int columnCount) {
        checkMazeSize(rowCount, columnCount);

        this.tiles = new Tile[rowCount][columnCount];
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        generateMaze();
        spawnPlayer();
        spawnFinish();
        createTimer();
    }
    public Field(int rowCount, int columnCount, int player_row, int player_column){
        checkMazeSize(rowCount, columnCount);

        this.tiles = new Tile[rowCount][columnCount];
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        generateMaze();
        spawnPlayer(player_row, player_column);
        spawnFinish();
        createTimer();
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public FieldState getFieldState() {
        return state;
    }

    public Player getPlayer() {
        return player;
    }
    public Timer getTimer(){
        return timer;
    }

    public void setFieldState(FieldState state) {
        this.state = state;
    }

    private void generateMaze(){
        generateBorder();
        generatePaths();
    }
    private void checkMazeSize(int rowCount, int columnCount) {
        if (rowCount < 5 || rowCount % 2 == 0){
            throw new IllegalArgumentException("Invalid number of rows");
        }
        if(columnCount < 5 || columnCount % 2 == 0){
            throw new IllegalArgumentException("Invalid number of columns");
        }
    }
    
    private void generateBorder() {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if(row == 0 || column == 0 || row == getRowCount()-1 || column == getColumnCount()-1){
                    tiles[row][column] = new Wall();
                }
            }
        }
    }

    private void generatePaths() {
        /* Create walls in maze*/
        for (int row = 1; row < getRowCount()-1; row++) {
            for (int column = 1; column < getColumnCount()-1; column++) {
                if(row % 2 == 0 && column % 2 == 0){
                    tiles[row][column] = new Wall();//main wall
                    Direction direction = getRandomAvailableTile(row, column);//select one direction where wall will be added
                    if(direction == null) continue;
                    tiles[row + direction.getRow()][column + direction.getColumn()] = new Wall();
                }
            }
        }
        /* Fill empty space with Road */
        for (int row = 1; row < getRowCount()-1; row++) {
            for (int column = 1; column < getColumnCount()-1; column++) {
                if(getTile(row, column) == null){
                    tiles[row][column] = new Road();
                    tiles[row][column].setState(TileState.EMPTY);
                }
            }
        }
    }
    private void spawnPlayer(){
        player = new Player(this);
    }
    private void spawnPlayer(int row, int column){
        if(getTile(row, column) instanceof Wall) throw new IllegalArgumentException("Invalid position");
        player = new Player(this, row, column);
    }
    private void spawnFinish(){
        finish = new Finish(this);
    }

    private void createTimer(){
        timer = new Timer();
    }

    private Direction getRandomAvailableTile(int row, int column){
        ArrayList<Direction> directionList = new ArrayList<>();
        Random random = new Random();
        for(Direction direction : Direction.values()) {
            int newRow = row + direction.getRow();//new coordinates for cell
            int newColumn = column + direction.getColumn();
            if (getTile(newRow, newColumn) == null) {//add to array if not set
                directionList.add(direction);
            }
        }
        if(directionList.size() == 0) return null;//zero available neighbours cell
        int item = random.nextInt(directionList.size());//pick random neighbour cell
        return directionList.get(item);
    }

    public Tile getTile(int row, int column){
        if(tiles[row][column] == null) return null;
        return tiles[row][column];
    }
    public int getScore() {
        return state == FieldState.FINISHED ? ((rowCount * columnCount * 100) / timer.getEndTimeSeconds()) : 0;
    }

}
