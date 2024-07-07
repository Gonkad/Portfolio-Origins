package entities;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Item {
    private String name;
    private int quantity;
    private ArrayList<Integer> ids;
    private int revision;
    private String className;
    private String localizedName;
    private boolean isWallItem = false;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Integer> ids) {
        this.ids = ids;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean isWallItem() {
        return isWallItem;
    }

    public void setWallItem(boolean wallItem) {
        isWallItem = wallItem;
    }
}
