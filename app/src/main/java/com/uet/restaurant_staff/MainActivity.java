package com.uet.restaurant_staff;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private static final int APP_REQUEST_CODE = 1305;
    private IRestaurantAPI mIRestaurantAPI;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private AlertDialog mDialog;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    public MainActivity() {
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;

    @OnClick(R.id.btn_sign_in)
    void loginUser(){
        Log.d(TAG, "loginUser: called");
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
        .setAvailableProviders(providers).build(), APP_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        
        init();

    }

    private void init() {
        Log.d(TAG, "init: called");
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuth = FirebaseAuth.getInstance();

        listener = firebaseAuth1 -> {
            FirebaseUser user = firebaseAuth1.getCurrentUser();

            if (user != null){ // user đã đăng nhập
                mDialog.show();

                mCompositeDisposable.add(mIRestaurantAPI.getKey(user.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getKeyModel -> {
                            if (getKeyModel.isSuccess()){
                                //Write jwt to variable
                                Common.API_KEY = getKeyModel.getToken();
                                //Log.d(TAG, "API_KEY: " + Common.API_KEY);
                                //Log.d(TAG, "TOKEN_TASK: " + task.getResult().getToken());

                                //Save FBID
                                Paper.book().write(Common.REMEMBER_FBID, user.getUid());

                                //https://firebase.google.com/docs/cloud-messaging/android/client?hl=vi
                                // we need to retrive the current token
                                FirebaseInstanceId.getInstance()
                                        .getInstanceId()
                                        .addOnFailureListener(e -> { Toast.makeText(this, "[GET TOKEN]", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnCompleteListener(task -> {
                                            Map<String, String> headers = new HashMap<>();
                                            headers.put("Authorization", Common.buildJWT(Common.API_KEY));
                                            mCompositeDisposable.add(mIRestaurantAPI.updateTokenToServer(headers,
                                                    task.getResult().getToken())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(tokenModel -> {
                                                        if (!tokenModel.isSuccess()){
                                                            Toast.makeText(this, "[UPDATE TOKEN]" + tokenModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "[UPDATE TOKEN]" + tokenModel.getMessage());
                                                        }
                                                        mCompositeDisposable.add(mIRestaurantAPI.getRestaurantOwner(headers)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(userModel -> {
                                                                    // nếu người dùng đã tồn tại trong db
                                                                    if (userModel.isSuccess()){
                                                                        Common.currentRestaurantOwner = userModel.getResult().get(0);
                                                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                                        finish();
                                                                    }
                                                                    else{ // nếu người dùng chưa có thông tin
                                                                        startActivity(new Intent(MainActivity.this, UpdateInfoActivity.class));
                                                                        finish();
                                                                    }
                                                                    mDialog.dismiss();
                                                                }, throwable -> {
                                                                    mDialog.dismiss();
                                                                    Toast.makeText(this, "[GET USER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, "[GET USER]" + throwable.getMessage());
                                                                }));
                                                    }, throwable -> {
                                                        mDialog.dismiss();
                                                        Toast.makeText(this, "[GET TOKEN ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "[GET TOKEN ERROR" + throwable.getMessage());
                                                    }));
                                        });

                            }
                            else{
                                mDialog.dismiss();
                                Toast.makeText(MainActivity.this, getKeyModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Can't get Json Web Token " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Can't get Json Web Token " + throwable.getMessage());
                        }));



            }
            else{
                loginUser();
            }
        };
        Paper.init(this);
        mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mIRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IRestaurantAPI.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }
            else{
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listener != null && firebaseAuth != null){
            firebaseAuth.addAuthStateListener(listener);
        }
    }

    @Override
    protected void onStop() {
        if (listener != null && firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }
}
