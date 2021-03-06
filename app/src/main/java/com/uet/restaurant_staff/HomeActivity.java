package com.uet.restaurant_staff;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uet.restaurant_staff.Adapter.MyOrderAdapter;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Interface.ILoadMore;
import com.uet.restaurant_staff.Model.Order;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ILoadMore {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private IRestaurantAPI mIMyRestaurantAPI;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private AlertDialog mDialog;
    private TextView txt_owner_name, txt_restaurant_name;
    @BindView(R.id.rv_order)
    RecyclerView rv_order;

    private LayoutAnimationController mLayoutAnimationController;
    private int maxData = 0;
    private MyOrderAdapter mAdapter;
    private List<Order> mOrderList;

    @Override
    protected void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        txt_owner_name = headerView.findViewById(R.id.txt_owner_name);
        txt_restaurant_name = headerView.findViewById(R.id.txt_restaurant_name);

        if (Common.currentRestaurantOwner != null && !TextUtils.isEmpty(Common.currentRestaurantOwner.getName())){
            txt_owner_name.setText(Common.currentRestaurantOwner.getName());
        }

        if (Common.currentRestaurantOwner != null && Common.currentRestaurantOwner.getRestaurantId() > 0){
            String restaurant_name = "";
            switch (Common.currentRestaurantOwner.getRestaurantId()){
                case 1: {
                    restaurant_name = "Restaurant A";
                    break;
                }
                case 2: {
                    restaurant_name = "Restaurant B";
                    break;
                }
                case 3: {
                    restaurant_name = "Restaurant C";
                    break;
                }
                case 4: {
                    restaurant_name = "Restaurant D";
                    break;
                }
                case 5: {
                    restaurant_name = "Restaurant E";
                    break;
                }
                case 6: {
                    restaurant_name = "Restaurant F";
                    break;
                }
                case 7: {
                    restaurant_name = "Restaurant G";
                    break;
                }
                default: {
                    restaurant_name = "Can't find restaurant name";
                    break;
                }
            }
            txt_restaurant_name.setText(restaurant_name);
        }


        init();
        initView();

        subscribeToTopic(Common.getTopicChannel(Common.currentRestaurantOwner.getRestaurantId()));

        getMaxOrder();
    }

    private void subscribeToTopic(String topicChannel) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(topicChannel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Subscribe failed! You may not receive new order notification ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // Show or not show it up to you
                            Toast.makeText(HomeActivity.this, "Subscribe success!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this, "Failed: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getMaxOrder() {
        Log.d(TAG, "getMaxOrder: called!!");
        mDialog.show();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", Common.buildJWT(Common.API_KEY));
        mCompositeDisposable.add(mIMyRestaurantAPI.getMaxOrder(headers,
                String.valueOf(Common.currentRestaurantOwner.getRestaurantId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(maxOrderModel -> {

                    if (maxOrderModel.isSuccess()) {
                        maxData = maxOrderModel.getResult().get(0).getMaxRowNum();
                        mDialog.dismiss();

                        getAllOrder(0, 10, false);
                    }

                }, throwable -> {
                    mDialog.dismiss();
                    Toast.makeText(this, "[GET MAX ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void getAllOrder(int from, int to, boolean isRefresh) {
        Log.d(TAG, "getAllOrder: called!!");
        mDialog.show();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", Common.buildJWT(Common.API_KEY));
        mCompositeDisposable.add(mIMyRestaurantAPI.getOrder(headers,
                String.valueOf(Common.currentRestaurantOwner.getRestaurantId()), from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderModel -> {

                    if (orderModel.isSuccess()) {
                        if (orderModel.getResult().size() > 0) {
                            if (mAdapter == null) {
                                mOrderList = new ArrayList<>();
                                mOrderList = orderModel.getResult();
                                mAdapter = new MyOrderAdapter(this, mOrderList, rv_order);
                                mAdapter.setILoadMore(this);

                                rv_order.setAdapter(mAdapter);
                                rv_order.setLayoutAnimation(mLayoutAnimationController);
                            }
                            else {
                                if (!isRefresh){
                                    mOrderList.remove(mOrderList.size() - 1);
                                    mOrderList = orderModel.getResult();
                                    mAdapter.addItem(mOrderList);
                                }
                                else{
                                    mOrderList = new ArrayList<>();
                                    mOrderList = orderModel.getResult();
                                    mAdapter = new MyOrderAdapter(this, mOrderList, rv_order);
                                    mAdapter.setILoadMore(this);

                                    rv_order.setAdapter(mAdapter);
                                    rv_order.setLayoutAnimation(mLayoutAnimationController);
                                }
                            }
                        }

                        mDialog.dismiss();

                    }

                }, throwable -> {
                    mDialog.dismiss();
                    Toast.makeText(this, "[GET ORDER]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void init() {
        Log.d(TAG, "init: called!!");
        mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mIMyRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IRestaurantAPI.class);
    }

    private void initView() {
        Log.d(TAG, "initView: called!!");
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_order.setLayoutManager(layoutManager);
        rv_order.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        mLayoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Restaurant Order");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getAllOrder(0, 10, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hot_food) {
            Intent intent = new Intent(HomeActivity.this, HotFoodActivity.class);
            startActivity(intent);
        } else if (id == R.id.listOrder) {

        } else if (id == R.id.update_infomation) {
            startActivity(new Intent(HomeActivity.this, UpdateInfoActivity.class));
        } else if (id == R.id.logOut) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        Log.d(TAG, "logOut: called!!");
        //Alert dialog to confirm
        AlertDialog confimrDialog = new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có thực sự muốn đăng xuất?")
                .setNegativeButton("CANCLE", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("OK", (dialog12, which) -> {
                    Common.currentOrder = null;
                    Common.currentRestaurantOwner = null;
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //clear all previous activity
                    startActivity(intent);
                    finish();
                }).create();
        confimrDialog.show();
    }

    @Override
    public void onLoadMore() {
        if (mAdapter.getItemCount() < maxData) {
            // Add null to show loading progressbar
            mOrderList.add(null);
            mAdapter.notifyItemInserted(mOrderList.size()-1);

            // Get next 10 item
            getAllOrder(mAdapter.getItemCount()+1, mAdapter.getItemCount()+10, false);

            mAdapter.notifyDataSetChanged();
            mAdapter.setLoaded();
        }
        else {
            Toast.makeText(this, "Max Data to load", Toast.LENGTH_SHORT).show();
        }
    }
}
