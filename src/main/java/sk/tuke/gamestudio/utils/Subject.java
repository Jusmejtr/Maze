package sk.tuke.gamestudio.utils;

public interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notif();
}
