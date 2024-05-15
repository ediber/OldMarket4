package model;

import java.io.Serializable;

public class Product___old extends BaseEntity implements Serializable {


    private String name;
    private String description;
    //private String categoryId;
    private String change;
    private int quantity;
    private String picture;
    private String pictureUrl;
    private String ownerId;

    public Product___old() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //public String getCategoryId() {
    //    return categoryId;
    //}

    //public void setCategoryId(String categoryId) {
    //    this.categoryId = categoryId;
    //}

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
