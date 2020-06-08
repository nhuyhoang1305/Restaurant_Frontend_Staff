package com.uet.restaurant_staff;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    private final String TAG = SplashScreenActivity.class.getSimpleName();
    private IRestaurantAPI mIRestaurantAPI;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private AlertDialog mDialog;

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //Get token
                        FirebaseInstanceId.getInstance()
                                .getInstanceId()
                                .addOnFailureListener(e -> Toast.makeText(SplashScreenActivity.this, "[GET TOKEN]" + e.getMessage(), Toast.LENGTH_SHORT).show()
                                )
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        if (user != null){
                                            Paper.book().write(Common.REMEMBER_FBID, user.getUid());

                                            mDialog.show();
                                            mCompositeDisposable.add(mIRestaurantAPI.getKey(user.getUid())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(getKeyModel -> {
                                                        if (getKeyModel.isSuccess()){
                                                            //Write jwt to variable
                                                            Common.API_KEY = getKeyModel.getToken();
                                                            //Log.d(TAG, "API_KEY: " + Common.API_KEY);
                                                            //After we have account, we will get fbid and update token
                                                            Map<String, String> headers = new HashMap<>();
                                                            headers.put("Authorization", Common.buildJWT(Common.API_KEY));
                                                            Log.d(TAG, "TOKEN_TASK: " + task.getResult().getToken());
                                                            mCompositeDisposable.add(mIRestaurantAPI.updateTokenToServer(headers,
                                                                    task.getResult().getToken())
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(tokenModel -> {
                                                                        if (!tokenModel.isSuccess()){
                                                                            Toast.makeText(SplashScreenActivity.this, "[UPDATE TOKEN ERROR]" + tokenModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        mCompositeDisposable.add(mIRestaurantAPI.getRestaurantOwner(headers)
                                                                                .subscribeOn(Schedulers.io())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .subscribe(userModel -> {
                                                                                            if (userModel.isSuccess()){ // if user avaiable in db
                                                                                                Common.currentRestaurantOwner = userModel.getResult().get(0);
                                                                                                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                            else{
                                                                                                Intent intent = new Intent(SplashScreenActivity.this, UpdateInfoActivity.class);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                            mDialog.dismiss();
                                                                                        },
                                                                                        throwable -> {
                                                                                            mDialog.dismiss();
                                                                                            Toast.makeText(SplashScreenActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }));
                                                                    }, throwable -> {
                                                                        mDialog.dismiss();
                                                                        Toast.makeText(SplashScreenActivity.this, "[UPDATE TOKEN] " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        Log.d(TAG, "[UPDATE TOKEN] " + throwable.getMessage());
                                                                    }));

                                                        }
                                                        else{
                                                            mDialog.dismiss();
                                                            Toast.makeText(SplashScreenActivity.this, getKeyModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, throwable -> {
                                                        mDialog.dismiss();
                                                        Toast.makeText(SplashScreenActivity.this, "Can't get Json Web Token " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Can't get Json Web Token " + throwable.getMessage());
                                                    }));
                                        }
                                        else {
                                            Toast.makeText(SplashScreenActivity.this, "Chưa đăng nhập!!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                            finish();
                                        }

                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(SplashScreenActivity.this, "Bạn cần cấp quyền để truy cập ứng dụng!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    private void init() {
        Paper.init(this);
        mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mIRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IRestaurantAPI.class);
    }
}
