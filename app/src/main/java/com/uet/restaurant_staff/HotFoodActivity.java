package com.uet.restaurant_staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.uet.restaurant_staff.Common.Common;
import com.uet.restaurant_staff.Model.HotFood;
import com.uet.restaurant_staff.Retrofit.IRestaurantAPI;
import com.uet.restaurant_staff.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HotFoodActivity extends AppCompatActivity {

    private final String TAG = HotFoodActivity.class.getSimpleName();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private IRestaurantAPI mIRestaurantAPI;
    private List<PieEntry> entryList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.piechar)
    PieChart pieChart;

    @Override
    protected void onStop() {
        mCompositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_food);

        init();
        initView();

        loadChar();

    }

    private void loadChar() {
        entryList = new ArrayList<>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", Common.buildJWT(Common.API_KEY));
        mCompositeDisposable.add(mIRestaurantAPI.getHotFood(headers)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    hotFoodModel -> {
                        if (hotFoodModel.isSuccess()){
                            int i = 0;
                            for (HotFood hotFood : hotFoodModel.getResult()){
                                entryList.add(new PieEntry(Float.parseFloat(String.valueOf(hotFood.getPercent()))));
                                ++i;
                            }
                            PieDataSet dataSet = new PieDataSet(entryList, "Hotest Food");
                            PieData data = new PieData();
                            data.setDataSet(dataSet);
                            data.setValueTextSize(14f);
                            data.setValueFormatter(new PercentFormatter(pieChart));

                            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                            pieChart.setData(data);
                            pieChart.animateXY(2000, 2000);
                            pieChart.setUsePercentValues(true);
                            pieChart.getDescription().setEnabled(true);

                            pieChart.invalidate();
                        }
                        else{
                            Toast.makeText(this, "" + hotFoodModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    throwable -> {
                        Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            ));
    }

    private void init() {
        Log.d(TAG, "init: called!!");
        mIRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IRestaurantAPI.class);
    }

    private void initView() {
        Log.d(TAG, "initView: called!!");
        ButterKnife.bind(this);
        toolbar.setTitle("HOT FOOD");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
