package com.uet.restaurant_staff.Common;

import com.uet.restaurant_staff.Model.Order;
import com.uet.restaurant_staff.Model.RestaurantOwner;

public class Common {
    public static final String API_RESTAURANT_ENDPOINT = "https://192.168.43.144:3000/";
    public static String API_KEY = "1305";
    public static final String REMEMBER_FBID = "REMEMBER_FBID";
    public static final String API_KEY_TAG = "API_KEY";
    public static final String NOTIFIC_TITLE = "title";
    public static final String NOTIFIC_CONTENT = "content";

    public static RestaurantOwner currentRestaurantOwner;
    public static Order currentOrder;

    public static String buildJWT(String apiKey){
        return new StringBuilder("Bearer")
                .append(" ")
                .append(apiKey).toString();
    }

    public static String convertStatusToString(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Cancelled";
        }
    }

    public static int convertStatusToIndex(int orderStatus) {
        if (orderStatus == -1) {
            return 3;
        }
        else {
            return orderStatus;
        }
    }


}
