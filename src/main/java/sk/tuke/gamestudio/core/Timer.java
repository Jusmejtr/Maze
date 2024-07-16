package sk.tuke.gamestudio.core;

public class Timer {
    private long startingTime;
    private long endTime;
    public Timer(){
        this.startingTime = System.currentTimeMillis();
    }
    public void stopTimer(){
        this.endTime = System.currentTimeMillis();
    }
    public int getMinutes(){
        long elapsedTime = System.currentTimeMillis() - startingTime;
        return (int) elapsedTime / 1000 / 60;
    }
    public int getSeconds(){
        long elapsedTime = System.currentTimeMillis() - startingTime;
        return (int) elapsedTime / 1000;
    }
    public int getEndTimeSeconds(){
        return (int) (endTime - startingTime) / 1000;
    }
    public int getEndTimeMinutes(){
        return (int) (endTime - startingTime) / 1000 / 60;
    }
}
