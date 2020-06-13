package com.uet.restaurant_staff.Model;

import com.uet.restaurant_staff.Common.Common;

public class OrderDetail {
    private int orderId;
    private int itemId;
    private int quantity;
    private String size;
    private String addOn;
    private String name;
    private String description;
    private String image;

    public String convert(String _image){
        String words[] = _image.split("/");
        return new StringBuilder().append(Common.API_RESTAURANT_ENDPOINT)
                .append(words[3]).toString();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAddOn() {
        return addOn;
    }

    public void setAddOn(String addOn) {
        this.addOn = addOn;
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

    public String getImage() {
        return convert(image);
    }

    public void setImage(String image) {
        this.image = image;
    }
}
