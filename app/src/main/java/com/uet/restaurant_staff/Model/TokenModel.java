package com.uet.restaurant_staff.Model;

public class TokenModel {
    private boolean success;
    private String message;
    private RestaurantToken result;

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

    public RestaurantToken getResult() {
        return result;
    }

    public void setResult(RestaurantToken result) {
        this.result = result;
    }
}

