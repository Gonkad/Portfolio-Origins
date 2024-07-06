import entities.InventoryPage;
import entities.Player;
import gearth.encoding.VL64Encoding;
import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.misc.Cacher;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.protocol.packethandler.shockwave.packets.ShockPacketOutgoing;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONArray;
import org.json.JSONObject;
import parsers.OHEntity;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExtensionInfo(
        Title = "Portfolio",
        Description = "Inventory Portfolio for Habbo Origins",
        Version = "1.0",
        Author = "Thauan"
)

public class Portfolio extends ExtensionForm implements Initializable {
    public static Portfolio RUNNING_INSTANCE;
    public Button buttonUnban;
    public ListView<String> playerListView;
    public Label labelInfo;
    public Button buttonClearList;
    public Label labelRoomName;
    public static String habboUserName;
    public String roomName;
    public String roomId;

    @Override
    protected void onStartConnection() {
        System.out.println("Portfolio started!");
    }

    @Override
    protected void onShow() {
        new Thread(() -> {
            sendToServer(new ShockPacketOutgoing("AAnew", roomId));
            waitAFckingSec(50);
            sendToServer(new ShockPacketOutgoing("AAnext", roomId));
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCache();
    }

    @Override
    protected void initExtension() {
        RUNNING_INSTANCE = this;

        onConnect((host, port, APIVersion, versionClient, client) -> {
            if (!Objects.equals(versionClient, "SHOCKWAVE")) {
                System.exit(0);
            }
        });

        intercept(HMessage.Direction.TOCLIENT, "USER_OBJ", this::onUserObject);
        intercept(HMessage.Direction.TOCLIENT, "FLATINFO", this::onFlatInfo);

//        BLQB\puHS[2]Xpufridge[2]II#FFFFFF,#FFFFFF[2]_puIS[2][91]pusmall_chair_armas[2]II0,0,0[2]\quJS[2]Xqusmall_chair_armas[2]II0,0,0[2]\ruKS[2]Xrubar_armas[2]IInull[2][93]ruPAS[2]Yrubar_armas[2]IInull[2]^ruQAS[2]Zrufireplace_armas[2]JI#FFFFFF,#FFFFFF,#FFFFFF[2]^suRAS[2]Zsusmall_chair_armas[2]II0,0,0[2]_\nSAS[2][91]\ncarpet_standard*6[2]KQA#777777[2]fNtAPBS[2]bNtAplant_sunflower[2]IInull[2]P[91]

        intercept(HMessage.Direction.TOCLIENT, "FLATINFO", this::onFlatInfo);
        intercept(HMessage.Direction.TOCLIENT, "STRIPINFO_2", this::onInventory);

    }

    private void onInventory(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        InventoryPage[] inventoryPages = InventoryPage.parse(hPacket);

        for(InventoryPage inventoryPage : inventoryPages) {
            System.out.println(inventoryPage.getFurniName());
//            System.out.println(inventoryPage.getStuffData());
//            System.out.println(inventoryPage.getFurniName());
//            System.out.println(inventoryPage.getInteger1());
//            System.out.println(inventoryPage.getInteger3());
//            System.out.println(inventoryPage.getInteger4());
//            System.out.println(inventoryPage.getItemIdNegative());
        }
    }

    public static String decodeVL64String(String input) {
        Pattern pattern = Pattern.compile("[^0-9]*");
        Matcher matcher = pattern.matcher(input);
        StringBuilder finalString = new StringBuilder();
        String remainder = "";
        if (matcher.find()) {
            remainder = input.substring(matcher.end());
            input = matcher.group();
        }
        while (!input.isEmpty()) {
            int currentNumber = VL64Encoding.decode(input.getBytes(StandardCharsets.ISO_8859_1));
            finalString.append(currentNumber).append(" ");

            byte[] encodedPart = VL64Encoding.encode(currentNumber);
            String part = new String(encodedPart, StandardCharsets.UTF_8);
            input = input.replaceFirst(part, "");
        }
        finalString.append(remainder);
//        System.out.println(finalString.toString());
        return finalString.toString();
    }

    private void onFlatInfo(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        hPacket.readBoolean();
        roomId = hPacket.readString();
        hPacket.readString();
        roomName = hPacket.readString();
        String sanitizedRoomId = roomId.replaceAll("[^a-zA-Z]", "");
    }

    private void onUserObject(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        final byte[] dataRemainder = hPacket.readBytes(hPacket.length() - hPacket.getReadIndex());
        final String data = new String(dataRemainder, StandardCharsets.ISO_8859_1);

        String[] pairs = data.split("\r");

        String nameValue = null;

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("name")) {
                nameValue = keyValue[1];
                break;
            }
        }

        habboUserName = nameValue;
    }

    private void loadInventoryCache() {
        JSONObject cache = Cacher.getCacheContents();

        if(cache.has(habboUserName + "Inventory")) {
            JSONArray jsonArray = (JSONArray) Cacher.get(habboUserName + "Inventory");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject inventoryItem = jsonArray.getJSONObject(i);

            }
        }
    }

    private void setupCache() {
        File extDir = null;
        try {
            extDir = (new File(Portfolio.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile();
            if (extDir.getName().equals("Extensions")) {
                extDir = extDir.getParentFile();
            }
        } catch (URISyntaxException ignored) {
        }

        Cacher.setCacheDir(extDir + File.separator + "Cache");
    }

    public void updateInventoryCache() {
        JSONArray jsonInventory = new JSONArray();
        for (String name : playerListView.getItems()) {
            JSONObject jsonPlayer = new JSONObject();
            jsonPlayer.put("name", name);
            jsonInventory.put(jsonPlayer);
        }
        Cacher.put(roomId, jsonInventory);
    }

    public void clearList(ActionEvent actionEvent) {
        Cacher.put(roomId, new JSONArray());
        Platform.runLater(() -> {
            playerListView.getItems().clear();
        });
    }

    public static void waitAFckingSec(int millisecActually) {
        try {
            Thread.sleep(millisecActually);
        } catch (InterruptedException ignored) {
        }
    }
}
