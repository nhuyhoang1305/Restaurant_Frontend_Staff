package com.uet.restaurant_staff.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantOwnerModel {
    @SerializedName("success")
    private boolean success;
    private String message;
    @SerializedName("result")
    private List<RestaurantOwner> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RestaurantOwner> getResult() {
        return result;
    }

    public void setResult(List<RestaurantOwner> result) {
        this.result = result;
    }
}
