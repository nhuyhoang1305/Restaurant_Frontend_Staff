package com.uet.restaurant_staff.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Order {
    @SerializedName("OrderId")
    @Expose
    private int orderId;

    @SerializedName("OrderFBID")
    @Expose
    private String orderFBID;

    @SerializedName("OrderPhone")
    @Expose
    private String orderPhone;

    @SerializedName("OrderName")
    @Expose
    private String orderName;

    @SerializedName("OrderAddress")
    @Expose
    private String orderAddress;

    @SerializedName("OrderStatus")
    @Expose
    private int orderStatus;

    @SerializedName("OrderDate")
    @Expose
    private Date orderDate;

    @SerializedName("RestaurantId")
    @Expose
    private int restaurantId;

    @SerializedName("TransactionId")
    @Expose
    private String transactionId;

    @SerializedName("COD")
    @Expose
    private boolean cod;

    @SerializedName("TotalPrice")
    @Expose
    private Double totalPrice;

    @SerializedName("NumOfItem")
    @Expose
    private int numOfItem;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumOfItem() {
        return numOfItem;
    }

    public void setNumOfItem(int numOfItem) {
        this.numOfItem = numOfItem;
    }
}
