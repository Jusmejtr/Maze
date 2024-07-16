package sk.tuke.gamestudio.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    public void CreatingPlayerWithValidCoordinates(){
        Field field = new Field(5,5,1,1);
        assertTrue(field.getTile(1,1).getState() == TileState.PLAYER);
    }
    @Test
    public void CreatingPlayerWithInvalidCoordinates(){
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Field(7,7,0,1));
        assertEquals("Invalid position", exception.getMessage());
    }

    @Test
    public void MovePlayerToValidDirection(){
        Field field = new Field(5,5,1,1);
        if(field.getTile(1,2) instanceof Wall){
            field.getPlayer().move(Direction.DOWN);
            assertTrue(field.getTile(2,1).getState() == TileState.PLAYER);
        }else{
            field.getPlayer().move(Direction.RIGHT);
            assertTrue(field.getTile(1,2).getState() == TileState.PLAYER);
        }
    }    @Test
    public void NumberOfPlayersMustBeOne(){
        Field field = new Field(5,5);
        int players = 0;
        for(int row = 0; row < field.getRowCount(); row++){
            for(int column = 0; column < field.getColumnCount(); column++){
                if(field.getTile(row, column).getState() == TileState.PLAYER){
                    players++;
                }
            }
        }
        assertEquals(1, players);
    }

    @Test
    public void NumberOfFinishMustBeOne(){
        Field field = new Field(5,5);
        int finish = 0;
        for(int row = 0; row < field.getRowCount(); row++){
            for(int column = 0; column < field.getColumnCount(); column++){
                if(field.getTile(row, column).getState() == TileState.FINISH){
                    finish++;
                }
            }
        }
        assertEquals(1, finish);
    }
}