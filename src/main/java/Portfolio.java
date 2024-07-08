import entities.Item;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.misc.Cacher;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.protocol.packethandler.shockwave.packets.ShockPacketOutgoing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import parsers.OHInventoryItem;
import ui.InventoryEntry;
import utils.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static utils.Utils.*;

@ExtensionInfo(
        Title = "Portfolio",
        Description = "Inventory Portfolio for Habbo Origins",
        Version = "1.0",
        Author = "Thauan & Rimuru"
)
public class Portfolio extends ExtensionForm implements Initializable {
    public static Portfolio RUNNING_INSTANCE;
    public Label totalUniqueItemsLabel;
    public Label totalItemsLabel;
    public Label usernameLabel;
    public Button scanButton;

    @FXML
    private TableView<InventoryEntry> inventoryTableView;
    @FXML
    private TableColumn<InventoryEntry, String> imageColumn;
    @FXML
    private TableColumn<InventoryEntry, String> nameColumn;
    @FXML
    private TableColumn<InventoryEntry, String> quantityColumn;

    public static String habboUserName;
    public String roomName;
    public String roomId;
    public List<Item> inventoryItems = new ArrayList<>();
    public boolean scanning = false;
    public int firstItemId = -1;
    public int pageCount = 0;
    public int totalItems = 0;
    public int totalUniqueItems = 0;
    public Thread scanThread;


    @Override
    protected void onStartConnection() {
        System.out.println("Portfolio started!");
    }

    @Override
    protected void onShow() {
        Platform.runLater(() -> {
            usernameLabel.setText(habboUserName);
        });
        scan();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        imageColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getImageUrl()));

        imageColumn.setCellFactory(column -> new TableCell<InventoryEntry, String>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                setAlignment(Pos.CENTER);
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(imageUrl, true));
                    setGraphic(imageView);
                }
            }
        });

        nameColumn.setCellValueFactory(cellData -> Bindings.createStringBinding(() ->
                cellData.getValue() != null ? cellData.getValue().getName() : "<no name>"));

        quantityColumn.setCellValueFactory(cellData -> Bindings.createStringBinding(() ->
                cellData.getValue() != null ? String.valueOf(cellData.getValue().getQuantity()) : "0"));

        nameColumn.setCellFactory(column -> new TableCell<InventoryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                setAlignment(Pos.CENTER);
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        quantityColumn.setCellValueFactory(cellData -> {
            TableCell<InventoryEntry, String> cell = new TableCell<>();
            cell.textProperty().bind(Bindings.createStringBinding(() ->
                    cellData.getValue() != null ? String.valueOf(cellData.getValue().getQuantity()) : "0"));
            return cell.textProperty();
        });

        quantityColumn.setCellFactory(column -> new TableCell<InventoryEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                setAlignment(Pos.CENTER);
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        try {
            Utils.getFurniData();
            Utils.getExternalTexts();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setupCache();
    }

    @Override
    protected void initExtension() {
        RUNNING_INSTANCE = this;

        onConnect((host, port, APIVersion, versionClient, client) -> {
            if (!Objects.equals(versionClient, "SHOCKWAVE")) {
                System.exit(0);
            }

            new Thread(() -> {
                sendToServer(new ShockPacketOutgoing("{out:INFORETRIEVE}"));
            }).start();
        });

        intercept(HMessage.Direction.TOCLIENT, "USER_OBJ", this::onUserObject);
        intercept(HMessage.Direction.TOCLIENT, "FLATINFO", this::onFlatInfo);
        intercept(HMessage.Direction.TOCLIENT, "STRIPINFO_2", this::onInventory);
        intercept(HMessage.Direction.TOSERVER, "GETSTRIP", this::onPage);

    }


    void onUserObject(HMessage hMessage) {
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

    private void onPage(HMessage hMessage) {
        if(scanning) {
            hMessage.setBlocked(true);
        }
    }

    private void onInventory(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        OHInventoryItem[] inventoryPage = OHInventoryItem.parse(hPacket);
        if (scanning) {
            hMessage.setBlocked(true);
            for (OHInventoryItem inventoryItem : inventoryPage) {
                int scanIndex = inventoryItem.getIndexAtInventory();
                if (scanIndex == 0 && firstItemId != -1) {
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

    private void loadInventoryCache() {
        JSONObject cache = Cacher.getCacheContents();

        if (cache.has(habboUserName + "Inventory")) {
            JSONArray jsonArray = (JSONArray) Cacher.get(habboUserName + "Inventory");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject inventoryItem = jsonArray.getJSONObject(i);
                // Handle the inventory item as necessary
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

//    public void updateInventoryCache() {
//        JSONArray jsonInventory = new JSONArray();
//        for (String name : playerListView.getItems()) {
//            JSONObject jsonPlayer = new JSONObject();
//            jsonPlayer.put("name", name);
//            jsonInventory.put(jsonPlayer);
//        }
//        Cacher.put(roomId, jsonInventory);
//    }
//
//    public void clearList(ActionEvent actionEvent) {
//        Cacher.put(roomId, new JSONArray());
//        Platform.runLater(() -> playerListView.getItems().clear());
//    }

    public static void waitAFckingSec(int millisecActually) {
        try {
            Thread.sleep(millisecActually);
        } catch (InterruptedException ignored) {
        }
    }

    public void scanInventory() {
        while (scanning) {
            sendToServer(new ShockPacketOutgoing("AAnext"));
            waitAFckingSec(50);
        }
    }

    public void populateTable() {
        ObservableList<InventoryEntry> items = FXCollections.observableArrayList();

        for (Item inventoryItem : inventoryItems) {
            String imageUrl = getImageUrlForItem(inventoryItem);
            String name = !Objects.equals(inventoryItem.getLocalizedName(), "null") ? inventoryItem.getLocalizedName()
                    : (!Objects.equals(inventoryItem.getName(), "null") ? inventoryItem.getName() : inventoryItem.getClassName());
            Integer quantity = inventoryItem.getQuantity();

            InventoryEntry entry = new InventoryEntry(imageUrl, name, quantity);
            items.add(entry);
        }


        Platform.runLater(() -> {
            inventoryTableView.setItems(items);
            totalItemsLabel.setText(String.valueOf(totalItems));
            totalUniqueItemsLabel.setText(String.valueOf(totalUniqueItems));
        });
        scanning = false;
        scanButton.setDisable(false);
    }

    public void updateItemInInventoryList(OHInventoryItem hInventoryItem) {
        boolean itemFound = false;

        boolean isWallItem = Objects.equals(hInventoryItem.getItemType(), "I");


        String itemNameAndType = hInventoryItem.getClassName() + (Objects.equals(hInventoryItem.getItemType(), "I")
                ? "_" + hInventoryItem.getProps()
                : "");

        String className = hInventoryItem.getClassName();

        for (Item item : inventoryItems) {
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
            newItem.setClassName(className);
            totalUniqueItems++;
            if(isWallItem) {
                    String name = Utils.postersConfig.get(newItem.getName() + "_name");
                    if(name != null) {
                        int revision = 56783;
                        newItem.setRevision(revision);
                        newItem.setLocalizedName(name);
                        newItem.setWallItem(true);
                    }else {
                        newItem.setLocalizedName(itemNameAndType);
                    }
            } else {
                floorJson.forEach(o -> {
                    JSONObject apiItem = (JSONObject) o;
                    try {
                        String itemName = apiItem.get("classname").toString();
                        if(itemName.equals(className)) {
                            int revision = apiItem.getInt("revision");
                            if(revision != 0) {
                                newItem.setRevision(revision);
                                newItem.setLocalizedName(apiItem.get("name").toString());
                            }else {
                                newItem.setLocalizedName(newItem.getClassName());
                            }
                        }
                    }catch (JSONException ignored) {}
                });
            }

            inventoryItems.add(newItem);

        }

    }

    private String getImageUrlForItem(Item item) {
        if(item.isWallItem()) {
                return "https://images.habbo.com/dcr/hof_furni/" + item.getRevision()
                    + "/" + item.getName().replace("_", "")
                    + "_icon.png";
        }

        return "https://images.habbo.com/dcr/hof_furni/" + item.getRevision()
                + "/" + (item.getClassName() != null ? item.getClassName().replace("*", "_") : item.getName())
                + "_icon.png";
    }

    public void scan() {
        if(!scanning) {
            if(scanThread != null) {
                scanThread.interrupt();
            }
            inventoryItems.clear();
            scanButton.setDisable(true);
            totalItems = 0;
            totalUniqueItems = 0;
            firstItemId = -1;
            Platform.runLater(() -> {
                inventoryTableView.getItems().clear();
                totalItemsLabel.setText("0");
                totalUniqueItemsLabel.setText(String.valueOf("0"));
            });
            sendToServer(new ShockPacketOutgoing("AAnew"));
            scanning = true;
            scanThread = new Thread(this::scanInventory);
            scanThread.start();
        }
    }
}
