package utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;

public class Utils {


    public static void getFurniData() throws Exception{
        String url = "https://www.habbo%s/gamedata/furnidata_json/1";
        JSONObject jsonObj = new JSONObject(IOUtils.toString(new URL(String.format(url, codeToDomainMap.get(host))).openStream(), StandardCharsets.UTF_8));
        floorJson = jsonObj.getJSONObject("roomitemtypes").getJSONArray("furnitype");
        floorJson.forEach(o -> {
            JSONObject item = (JSONObject) o;
            try {
                String itemName = item.get("name").toString();
                MarketTools.typeIdToNameFloor.put(item.getInt("id"), itemName);
            }catch (JSONException ignored) {}
        });

        wallJson = jsonObj.getJSONObject("wallitemtypes").getJSONArray("furnitype");
        wallJson.forEach(o -> {
            JSONObject item = (JSONObject) o;
            try {
                String itemName = item.get("name").toString();
                MarketTools.typeIdToNameWall.put(item.getInt("id"), itemName);
            }catch (JSONException ignored) {}
        });
    }
}
