package sk.tuke.gamestudio.core;

public class Finish {
    private Field field;
    private Player player;

    public Finish(Field field){
        this.field = field;
        player = field.getPlayer();
        createFinishPosition(player.getRow(), player.getColumn());
    }

    private void createFinishPosition(int row, int column){//crete finish in other corner of the maze
        if(row == 1 && column == 1){//upper left player
            field.getTile(field.getRowCount() - 2, field.getColumnCount() - 2).setState(TileState.FINISH);
        }else if(row == 1 && column == field.getColumnCount()-2){//upper right player
            field.getTile(field.getRowCount() - 2, 1).setState(TileState.FINISH);
        }else if(row == field.getRowCount()-2 && column == 1){//lower left player
            field.getTile(1, field.getColumnCount()-2).setState(TileState.FINISH);
        }else if(row == field.getRowCount()-2 && column == field.getColumnCount()-2){//lower right player
            field.getTile(1, 1).setState(TileState.FINISH);
        }
    }

}
