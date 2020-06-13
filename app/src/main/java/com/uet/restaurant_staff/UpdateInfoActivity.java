package com.uet.restaurant_staff;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateInfoActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private IRestaurantAPI mIRestaurantAPI;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private AlertDialog mDialog;

    @BindView(R.id.edit_user_name)
    EditText edit_user_name;
    @BindView(R.id.edit_user_phone_number)
    EditText edit_user_phone_number;
    @BindView(R.id.btn_update)
    Button btn_update;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        Log.d(TAG, "onCreate: started !!");

        ButterKnife.bind(this);

        init();
        initView();
    }

    //Override back arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish(); // close this activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Log.d(TAG, "initView: called!!");
        toolbar.setTitle(getString(R.string.update_infomation));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_update.setOnClickListener(v -> {
            Log.d(TAG, "onClick: calledd!!");
            mDialog.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Common.buildJWT(Common.API_KEY));
                mCompositeDisposable.add(
                        mIRestaurantAPI.updateRestaurantOwner(headers,
                                edit_user_phone_number.getText().toString(),
                                edit_user_name.getText().toString(),
                                1,
                                1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(updateRestaurantOwnerModel -> {

                                            if (updateRestaurantOwnerModel.isSuccess()){
                                                // If user has been update, just refesh again
                                                mCompositeDisposable.add(
                                                        mIRestaurantAPI.getRestaurantOwner(headers)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(restaurantownerModel -> {

                                                                            if (restaurantownerModel.isSuccess()){
                                                                                Common.currentRestaurantOwner = restaurantownerModel.getResult().get(0);
                                                                                startActivity(new Intent(UpdateInfoActivity.this, HomeActivity.class));
                                                                                finish();
                                                                            }
                                                                            else{
                                                                                Toast.makeText(UpdateInfoActivity.this, "[GET USER RESULT]" + restaurantownerModel.getResult(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            mDialog.dismiss();
                                                                        },
                                                                        throwable -> {
                                                                            mDialog.dismiss();
                                                                            Toast.makeText(UpdateInfoActivity.this, "[GET USER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        })
                                                );
                                            }
                                            else{
                                                mDialog.dismiss();
                                                Toast.makeText(UpdateInfoActivity.this, "[UPDATE USER API RETURN]" + updateRestaurantOwnerModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        },
                                        throwable -> {
                                            mDialog.dismiss();
                                            Toast.makeText(UpdateInfoActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );
            }
            else{
                Toast.makeText(UpdateInfoActivity.this, "Chưa đăng nhập!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateInfoActivity.this, MainActivity.class));
                finish();
            }


        });

        if (Common.currentRestaurantOwner != null && !TextUtils.isEmpty(Common.currentRestaurantOwner.getName()))
            edit_user_name.setText(Common.currentRestaurantOwner.getName());
        if (Common.currentRestaurantOwner != null && !TextUtils.isEmpty(Common.currentRestaurantOwner.getUserPhone()))
            edit_user_phone_number.setText(Common.currentRestaurantOwner.getUserPhone());

    }

    private void init() {
        Log.d(TAG, "init: called!!");
        Paper.init(this);
        mDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        mIRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IRestaurantAPI.class);
    }
}
