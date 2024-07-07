package utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class Utils {
    public static TreeMap<Integer, String> typeIdToNameFloor = new TreeMap<>();
    public static TreeMap<Integer, String> typeIdToNameWall = new TreeMap<>();
    public static JSONArray wallJson;
    public static JSONArray floorJson;
    public static Map<String, String> postersConfig = new HashMap<>();

    public static void getFurniData() throws Exception{
        String url = "https://origins.habbo.com/gamedata/furnidata_json/0";
        JSONObject jsonObj = new JSONObject(IOUtils.toString(new URL(String.format(url, "", StandardCharsets.UTF_8))));

        floorJson = jsonObj.getJSONObject("roomitemtypes").getJSONArray("furnitype");
        floorJson.forEach(o -> {
            JSONObject item = (JSONObject) o;
            try {
                String itemName = item.get("name").toString();
                typeIdToNameFloor.put(item.getInt("id"), itemName);
            }catch (JSONException ignored) {}
        });

        wallJson = jsonObj.getJSONObject("wallitemtypes").getJSONArray("furnitype");
        wallJson.forEach(o -> {
            JSONObject item = (JSONObject) o;
            try {
                String itemName = item.get("name").toString();
                typeIdToNameWall.put(item.getInt("id"), itemName);
            }catch (JSONException ignored) {}
        });
    }

    public static void getExternalTexts() throws Exception {
        String urlString = "https://origins.habbo.com/gamedata/external_texts/1";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            ResourceBundle bundle = new PropertyResourceBundle(reader);
            postersConfig = new HashMap<>();
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                postersConfig.put(key, bundle.getString(key));
            }
        }
        connection.disconnect();
    }
}
