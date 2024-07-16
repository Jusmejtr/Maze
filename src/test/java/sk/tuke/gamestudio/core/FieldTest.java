package sk.tuke.gamestudio.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    @Test
    public void CreatingFieldWithValidRowCount(){
        Field field = new Field(7,7);
        assertEquals(7, field.getRowCount());
    }
    @Test
    public void CreatingFieldWithInvalidRowCount(){
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Field(8,7));
        assertEquals("Invalid number of rows", exception.getMessage());
    }
    @Test
    public void CreatingFieldWithTooLowRowCount(){
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Field(4,7));
        assertEquals("Invalid number of rows", exception.getMessage());
    }
    @Test
    public void CreatingFieldWithValidColumnCount(){
        Field field = new Field(7,9);
        assertEquals(9, field.getColumnCount());
    }
    @Test
    public void CreatingFieldWithInvalidColumnCount(){
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Field(7,8));
        assertEquals("Invalid number of columns", exception.getMessage());
    }
}