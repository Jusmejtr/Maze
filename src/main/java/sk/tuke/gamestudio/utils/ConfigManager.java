package sk.tuke.gamestudio.utils;

import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ConfigManager implements Subject{
    private List<Observer> observers;
    private int perPage;// items show per page
    private int imageSize;
    public ConfigManager(){
        this.observers = new LinkedList<>();
    }

    @Override
    public void subscribe(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notif() {
        for (Observer observer : this.observers){
            observer.update(this);
        }
    }
    public int getResultPerPage(){
        return perPage;
    }
    public int getImageSize(){
        return imageSize;
    }

    public JSONObject loadConfig(){
        JSONObject data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/botConfig.json")));
            data = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadValue(data);
        return data;
    }
    public void updateConfig(String key, Integer value){
        JSONObject data = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/botConfig.json")));
            data = new JSONObject(content);
            data.put(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter file = new FileWriter("src/main/resources/botConfig.json")) {
            file.write(data.toString());
            file.flush();
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void loadValue(JSONObject data) {
        this.perPage = data.getInt("resultPerPage");
        this.imageSize = data.getInt("imageSize");
    }
}
