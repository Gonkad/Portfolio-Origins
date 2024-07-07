package utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.TreeMap;

public class Utils {
    public static TreeMap<Integer, String> typeIdToNameFloor = new TreeMap<>();
    public static TreeMap<Integer, String> typeIdToNameWall = new TreeMap<>();
    public static JSONArray wallJson;
    public static JSONArray floorJson;

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
}
