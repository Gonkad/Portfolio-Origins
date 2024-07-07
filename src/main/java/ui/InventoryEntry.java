package ui;

public class InventoryEntry {

    private String imageUrl;
    private String name;
    private Integer quantity;

    public InventoryEntry(String imageUrl, String name, Integer quantity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}