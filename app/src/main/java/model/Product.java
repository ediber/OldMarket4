package model;

public class Product {
    private String user_id;
    private String productId;
    private String name;
    private String description;
    private int quantity;
    private String change;
    private String imageUrl; // Optional based on your Firestore structure
    private String phoneNumber; // Add phoneNumber field

    public Product() {
        // Firestore Data Model classes need a public no-argument constructor
    }

    // Getters and setters
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
    public String getChange() { return change; }
    public void setChange(String change) { this.change = change; }
    public String getUserId() { return user_id; }
    public void setUserId(String user_id) { this.user_id = user_id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
