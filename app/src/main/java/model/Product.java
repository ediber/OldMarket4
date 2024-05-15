package model;

public class Product {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String change;
    private String imageUrl; // Optional based on your Firestore structure

    public Product() {
        // Firestore Data Model classes need a public no-argument constructor
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(String quantity) {
        int quant = Integer.parseInt(quantity);
        this.quantity = quant;
    }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
