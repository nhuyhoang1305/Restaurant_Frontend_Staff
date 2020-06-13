package com.uet.restaurant_staff.Services;

import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private IRestaurantAPI mIRestaurantAPI;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        mIRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IRestaurantAPI.class);
        mCompositeDisposable = new CompositeDisposable();
        Paper.init(this);
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        // Here we will update token
        // To update token, we need FBID
        // But this is services, so Common.currentUser will null
        // So, we need save signed FBID by Paper and get it back when we need

        String fbid = Paper.book().read(Common.REMEMBER_FBID);
        String apiKey = Paper.book().read(Common.API_KEY_TAG);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", Common.buildJWT(Common.API_KEY));
        mCompositeDisposable.add(mIRestaurantAPI.updateTokenToServer(headers, newToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenModel -> {

                    // Do nothing

                }, throwable -> {
                    Toast.makeText(this, "[REFRESH TOKEN]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Get notification object from FCM
        // Because we want to retrieve notification while app killed, so we must use Data payload
        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {
            Common.showNotification(this,
                    new Random().nextInt(),
                    dataRecv.get(Common.NOTIFIC_TITLE),
                    dataRecv.get(Common.NOTIFIC_CONTENT),
                    null);
        }
    }
}
