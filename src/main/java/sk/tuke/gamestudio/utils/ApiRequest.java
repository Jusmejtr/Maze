package sk.tuke.gamestudio.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ApiRequest {
    public JSONArray getRequestJson(String urlAddress){
        JSONArray jsonArray = null;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(conn.getInputStream());
                String responseBody = scanner.useDelimiter("\\A").next();
                jsonArray = new JSONArray(responseBody);
            } else {
                System.out.println("HTTP GET request failed with response code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
    public int getRequestInteger(String urlAddress){
        int averageRating = 0;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(conn.getInputStream());
                averageRating = scanner.nextInt();
            } else {
                System.out.println("HTTP GET request failed with response code " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return averageRating;
    }

    public boolean postRequestJson(JSONObject data, String urlAddress){
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String jsonInputString = data.toString();

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                System.out.println("HTTP POST request failed with response code " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
