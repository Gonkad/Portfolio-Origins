import entities.Item;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import parsers.OHInventoryItem;
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
import ui.InventoryEntry;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@ExtensionInfo(
        Title = "Portfolio",
        Description = "Inventory Portfolio for Habbo Origins",
        Version = "1.0",
        Author = "Thauan & Rimuru"
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
    public List<Item> inventoryItems = new ArrayList<>();
    public boolean scanning = false;
    public int firstItemId = -1;
    public int pageCount = 0;
    public int totalItems = 0;
    public TableView<InventoryEntry> inventoryTableView;
    public TableColumn<InventoryEntry, String> imageColumn;
    public TableColumn<InventoryEntry, String> nameColumn;
    public TableColumn<InventoryEntry, String> quantityColumn;

    @Override
    protected void onStartConnection() {
        System.out.println("Portfolio started!");
    }

    @Override
    protected void onShow() {
        sendToServer(new ShockPacketOutgoing("AAnew"));
        scanning = true;
        new Thread(this::scanInventory).start();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        imageColumn.setCellFactory(column -> new TableCell<InventoryEntry, String>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null) {
                    setGraphic(null);
                } else {
                    Image image = new Image(imageUrl, true); // Enable background loading
                    imageView.setImage(image);
                    imageView.setFitHeight(50); // Adjust this value as needed
                    imageView.setFitWidth(50); // Adjust this value as needed
                    setGraphic(imageView);
                }
            }
        });

        nameColumn.setCellValueFactory(cellData -> {
            TableCell<InventoryEntry, String> cell = new TableCell<>();
            cell.textProperty().bind(Bindings.createStringBinding(() ->
                    cellData.getValue() != null ? cellData.getValue().getName() : "<no name>"));
            return cell.textProperty();
        });

        quantityColumn.setCellValueFactory(cellData -> {
            TableCell<InventoryEntry, String> cell = new TableCell<>();
            cell.textProperty().bind(Bindings.createStringBinding(() ->
                    cellData.getValue() != null ? String.valueOf(cellData.getValue().getQuantity()) : "0"));
            return cell.textProperty();
        });
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
        intercept(HMessage.Direction.TOCLIENT, "STRIPINFO_2", this::onInventory);

    }

    private void onInventory(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        OHInventoryItem[] inventoryPage = OHInventoryItem.parse(hPacket);
        if (scanning) {
            for (OHInventoryItem inventoryItem : inventoryPage) {
                int scanIndex = inventoryItem.getIndexAtInventory();
                if (scanIndex == 0 && firstItemId != -1) {
                    scanning = false;
                    Platform.runLater(this::populateTable);
                    break;
                }
                updateItemInInventoryList(inventoryItem);
                totalItems++;
                if (scanIndex == 0) {
                    firstItemId = inventoryItem.getFurniId();
                }
            }
            pageCount++;
        }
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

        if (cache.has(habboUserName + "Inventory")) {
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

    public void scanInventory() {
        while(scanning) {
            sendToServer(new ShockPacketOutgoing("AAnext"));
            waitAFckingSec(50);
        }
        System.out.println(inventoryItems.toArray().length);
    }

    public void populateTable() {
        ObservableList<InventoryEntry> items = FXCollections.observableArrayList();

        for (Item inventoryItem : inventoryItems) {
            String imageUrl = getImageUrlForItem(inventoryItem); // Implement this method based on your needs
            String name = inventoryItem.getName(); // Assuming className is the name you want to display
            Integer quantity = inventoryItem.getQuantity(); // Assuming you have a method to get quantity

            InventoryEntry entry = new InventoryEntry(imageUrl, name, quantity);
            items.add(entry);
        }

        System.out.println("POPULATING TABLE");

        inventoryTableView.setItems(items);
    }

    public void updateItemInInventoryList(OHInventoryItem hInventoryItem) {
        boolean itemFound = false;

        String itemNameAndType = hInventoryItem.getClassName() + (Objects.equals(hInventoryItem.getItemType(), "I")
                ? "_" + hInventoryItem.getProps()
                : "");

        for (Item item : inventoryItems) {
            System.out.println(item.getName() + " " + item.getQuantity());
            if (Objects.equals(item.getName(), itemNameAndType)
                    && !item.getIds().contains(hInventoryItem.getFurniId())) {
                item.setQuantity(item.getQuantity() + 1);
                ArrayList<Integer> newIds = item.getIds();
                newIds.add(hInventoryItem.getFurniId());
                item.setIds(newIds);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            Item newItem = new Item(itemNameAndType);
            newItem.setQuantity(1);
            ArrayList<Integer> newIds = new ArrayList<>();
            newIds.add(hInventoryItem.getFurniId());
            newItem.setIds(newIds);
            inventoryItems.add(newItem);
        }
    }

    private String getImageUrlForItem(Item item) {
        return "http://example.com/" + item.getName() + ".png";
    }


}