package parsers;

import gearth.protocol.HPacket;

import java.util.Objects;

public class OHInventoryItem {

//    BLQB\puHS[2]Xpufridge[2]II#FFFFFF,#FFFFFF[2]_puIS[2][91]pusmall_chair_armas[2]II0,0,0[2]\quJS[2]Xqusmall_chair_armas[2]II0,0,0[2]\ruKS[2]Xrubar_armas[2]IInull[2][93]ruPAS[2]Yrubar_armas[2]IInull[2]^ruQAS[2]Zrufireplace_armas[2]JI#FFFFFF,#FFFFFF,#FFFFFF[2]^suRAS[2]Zsusmall_chair_armas[2]II0,0,0[2]_\nSAS[2][91]\ncarpet_standard*6[2]KQA#777777[2]fNtAPBS[2]bNtAplant_sunflower[2]IInull[2]P[91]

    private int itemIdNegative;
    private int indexAtInventory;
    private String itemType;
    private int furniId;
    private String className;
    private int dimX;
    private int dimY;
    private String colors;
    private String props;

    public OHInventoryItem(HPacket packet) {
        this.itemIdNegative = packet.readInteger();
        this.indexAtInventory = packet.readInteger();
        this.itemType = packet.readString();
        this.furniId = packet.readInteger();
        this.className = packet.readString();
        if(Objects.equals(itemType, "S")) {
            this.dimX = packet.readInteger();
            this.dimY = packet.readInteger();
            this.colors = packet.readString();
        }
        if(Objects.equals(itemType, "I")) {
            this.props = packet.readString();
        }
    }

//    BLQB\puHS[2]Xpufridge[2]II#FFFFFF,#FFFFFF[2]_puIS[2][91]pusmall_chair_armas[2]II0,0,0[2]\quJS[2]Xqusmall_chair_armas[2]II0,0,0[2]\ruKS[2]Xrubar_armas[2]IInull[2][93]ruPAS[2]Yrubar_armas[2]IInull[2]^ruQAS[2]Zrufireplace_armas[2]JI#FFFFFF,#FFFFFF,#FFFFFF[2]^suRAS[2]Zsusmall_chair_armas[2]II0,0,0[2]fNtASAS[2]bNtAplant_sunflower[2]IInull[2]dOtAPBS[2]`OtAtable_silo_small[2]II#ffffff,#ABD0D2[2]SZ

    public static OHInventoryItem[] parse(HPacket packet) {
        OHInventoryItem[] entities = new OHInventoryItem[packet.readInteger()];

        for(int i = 0; i < entities.length; ++i) {
            entities[i] = new OHInventoryItem(packet);
        }

        return entities;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getFurniId() {
        return furniId;
    }

    public void setFurniId(int furniId) {
        this.furniId = furniId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getDimX() {
        return dimX;
    }

    public void setDimX(int dimX) {
        this.dimX = dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public void setDimY(int dimY) {
        this.dimY = dimY;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }
}
