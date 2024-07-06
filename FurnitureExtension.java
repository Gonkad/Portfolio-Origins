import gearth.extensions.ExtensionBase;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;

import java.util.ArrayList;
import java.util.List;

public class FurnitureExtension extends ExtensionBase {

    private List<Furniture> furnitureList = new ArrayList<>();

    public static void main(String[] args) {
        new FurnitureExtension(args).run();
    }

    public FurnitureExtension(String[] args) {
        super(args);
    }

    @Override
    protected void initExtension() {
        intercept(HMessage.Direction.TOCLIENT, "FurnitureData", this::onFurnitureData);
    }

    private void onFurnitureData(HMessage hMessage) {
        HPacket packet = hMessage.getPacket();

        // Example parsing logic, adjust according to actual packet structure
        int furnitureCount = packet.readInt();
        for (int i = 0; i < furnitureCount; i++) {
            int id = packet.readInt();
            String name = packet.readString();
            Furniture furniture = new Furniture(id, name);
            furnitureList.add(furniture);
        }

        displayFurnitureList();
    }

    private void displayFurnitureList() {
        System.out.println("Your Furniture:");
        for (Furniture furniture : furnitureList) {
            System.out.println("ID: " + furniture.getId() + ", Name: " + furniture.getName());
        }
    }

    class Furniture {
        private int id;
        private String name;

        public Furniture(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}