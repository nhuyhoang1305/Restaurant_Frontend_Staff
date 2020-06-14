package com.uet.restaurant_staff.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.uet.restaurant_staff.Model.Order;
import com.uet.restaurant_staff.Model.RestaurantOwner;
import com.uet.restaurant_staff.R;

public class Common {
    public static final String API_RESTAURANT_ENDPOINT = "http://192.168.0.105:3000/";
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

    public static void showNotification(Context context, int notiId, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, notiId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "nhuyhoang_staff_restaurant";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My restaurant Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Restaurant Staff App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification mNotification = builder.build();
        notificationManager.notify(notiId, mNotification);


    }


    public static String getTopicChannel(int restaurantId) {
        return new StringBuilder("Restaurant_").append(restaurantId).toString();
    }

    public static int convertStringToStatus(String toString) {
        switch (toString) {
            case "Placed":
                return 0;
            case "Shipping":
                return 1;
            case "Shipped":
                return 2;
            case "Cancelled":
                return -1;
            default:
                return -1;
        }
    }
}
