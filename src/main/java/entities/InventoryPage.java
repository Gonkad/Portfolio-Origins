package entities;

import gearth.extensions.parsers.HEntityType;
import gearth.extensions.parsers.HInventoryItem;
import gearth.protocol.HPacket;
import parsers.OHEntity;

public class InventoryPage {

//    BLQB\puHS[2]Xpufridge[2]II#FFFFFF,#FFFFFF[2]_puIS[2][91]pusmall_chair_armas[2]II0,0,0[2]\quJS[2]Xqusmall_chair_armas[2]II0,0,0[2]\ruKS[2]Xrubar_armas[2]IInull[2][93]ruPAS[2]Yrubar_armas[2]IInull[2]^ruQAS[2]Zrufireplace_armas[2]JI#FFFFFF,#FFFFFF,#FFFFFF[2]^suRAS[2]Zsusmall_chair_armas[2]II0,0,0[2]_\nSAS[2][91]\ncarpet_standard*6[2]KQA#777777[2]fNtAPBS[2]bNtAplant_sunflower[2]IInull[2]P[91]

    private int numberOfItems;
    private int itemIdNegative;
    private int indexAtInventory;
    private int integer1;
    private int furniId;
    private String furniName;
    private int integer3;
    private int integer4;
    private String stuffData;

    public InventoryPage(HPacket packet) {
        this.itemIdNegative = packet.readInteger();
        System.out.println("itemIdNegative: " + itemIdNegative);
        this.indexAtInventory = packet.readInteger();
        System.out.println("indexAtInventory: " + indexAtInventory);
        this.integer1 = packet.readInteger();
        System.out.println("integer1: " + integer1);
        this.furniId = packet.readInteger();
        System.out.println("furniId: " + furniId);
        this.furniName = packet.readString();
        System.out.println("furniName: " + furniName);
        this.integer3 = packet.readInteger();
        System.out.println("integer3: " + integer3);
        this.integer4 = packet.readInteger();
        System.out.println("integer4: " + integer4);
        this.stuffData = packet.readString();
        System.out.println("stuffData: " + stuffData);
        System.out.println("---------------------------------------------------");
        HInventoryItem item = new HInventoryItem(stuffData);
    }

//    BLQB\puHS[2]Xpufridge[2]II#FFFFFF,#FFFFFF[2]_puIS[2][91]pusmall_chair_armas[2]II0,0,0[2]\quJS[2]Xqusmall_chair_armas[2]II0,0,0[2]\ruKS[2]Xrubar_armas[2]IInull[2][93]ruPAS[2]Yrubar_armas[2]IInull[2]^ruQAS[2]Zrufireplace_armas[2]JI#FFFFFF,#FFFFFF,#FFFFFF[2]^suRAS[2]Zsusmall_chair_armas[2]II0,0,0[2]fNtASAS[2]bNtAplant_sunflower[2]IInull[2]dOtAPBS[2]`OtAtable_silo_small[2]II#ffffff,#ABD0D2[2]SZ

    public static InventoryPage[] parse(HPacket packet) {
        InventoryPage[] entities = new InventoryPage[packet.readInteger()];

        for(int i = 0; i < entities.length; ++i) {
            entities[i] = new InventoryPage(packet);
        }

        return entities;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getItemIdNegative() {
        return itemIdNegative;
    }

    public void setItemIdNegative(int itemIdNegative) {
        this.itemIdNegative = itemIdNegative;
    }

    public int getIndexAtInventory() {
        return indexAtInventory;
    }

    public void setIndexAtInventory(int indexAtInventory) {
        this.indexAtInventory = indexAtInventory;
    }

    public int getInteger1() {
        return integer1;
    }

    public void setInteger1(int integer1) {
        this.integer1 = integer1;
    }

    public int getFurniId() {
        return furniId;
    }

    public void setFurniId(int furniId) {
        this.furniId = furniId;
    }

    public String getFurniName() {
        return furniName;
    }

    public void setFurniName(String furniName) {
        this.furniName = furniName;
    }

    public int getInteger3() {
        return integer3;
    }

    public void setInteger3(int integer3) {
        this.integer3 = integer3;
    }

    public int getInteger4() {
        return integer4;
    }

    public void setInteger4(int integer4) {
        this.integer4 = integer4;
    }

    public String getStuffData() {
        return stuffData;
    }

    public void setStuffData(String stuffData) {
        this.stuffData = stuffData;
    }
}
