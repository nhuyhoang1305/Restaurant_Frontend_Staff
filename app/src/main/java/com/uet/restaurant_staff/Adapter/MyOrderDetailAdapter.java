package com.uet.restaurant_staff.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uet.restaurant_staff.Model.Addon;
import com.uet.restaurant_staff.Model.OrderDetail;
import com.uet.restaurant_staff.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private final String TAG = MyOrderDetailAdapter.class.getSimpleName();
    Context mContext;
    List<OrderDetail> mOrderDetailList;

    public MyOrderDetailAdapter(Context context, List<OrderDetail> orderDetailList) {
        mContext = context;
        mOrderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0 ? new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order_detail, parent, false))
                :
                new MyViewHolderAddon(LayoutInflater.from(mContext).inflate(R.layout.item_order_detail_addon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Picasso.get().load(mOrderDetailList.get(position).getImage()).into(myViewHolder.img_food_image);
            myViewHolder.tv_food_name.setText(mOrderDetailList.get(position).getName());
            myViewHolder.tv_food_quantity.setText(new StringBuilder("Quantity: ").append(mOrderDetailList.get(position).getQuantity()));
            myViewHolder.tv_food_size.setText(new StringBuilder("Size: ").append(mOrderDetailList.get(position).getSize()));
        }
        else if (holder instanceof MyViewHolderAddon) {
            MyViewHolderAddon myViewHolder = (MyViewHolderAddon) holder;
            Picasso.get().load(mOrderDetailList.get(position).getImage()).into(myViewHolder.img_food_image);
            myViewHolder.tv_food_name.setText(mOrderDetailList.get(position).getName());
            myViewHolder.tv_food_quantity.setText(new StringBuilder("Quantity: ").append(mOrderDetailList.get(position).getQuantity()));
            myViewHolder.tv_food_size.setText(new StringBuilder("Size: ").append(mOrderDetailList.get(position).getSize()));
            //Log.d(TAG, mOrderDetailList.get(position).getAddOn());
            //myViewHolder.tv_food_size.setText("TEST XEM CÓ BỊ GÌ KHÔNGGGGG");
            //myViewHolder.tv_add_on.setText("TEST XEM CÓ BỊ GÌ KHÔNGGGGG");
            List<Addon> addons = new Gson().fromJson(mOrderDetailList.get(position).getAddOn(),
                    new TypeToken<List<Addon>>(){}.getType());
            StringBuilder add_on_text = new StringBuilder();
            for (Addon addon: addons) {
                add_on_text.append(addon.getName()).append("\n");
            }


            myViewHolder.tv_add_on.setText(add_on_text);
        }
    }

    @Override
    public int getItemCount() {
        return mOrderDetailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mOrderDetailList.get(position).getAddOn().toLowerCase().equals("none") ||
                mOrderDetailList.get(position).getAddOn().toLowerCase().equals("normal"))
            return 0;
        else
            return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.tv_food_name)
        TextView tv_food_name;
        @BindView(R.id.tv_food_quantity)
        TextView tv_food_quantity;
        @BindView(R.id.tv_food_size)
        TextView tv_food_size;

        Unbinder mUnbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mUnbinder = ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderAddon extends RecyclerView.ViewHolder {

        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.tv_food_name)
        TextView tv_food_name;
        @BindView(R.id.tv_food_quantity)
        TextView tv_food_quantity;
        @BindView(R.id.tv_food_size)
        TextView tv_food_size;
        @BindView(R.id.tv_add_on)
        TextView tv_add_on;

        Unbinder mUnbinder;

        public MyViewHolderAddon(@NonNull View itemView) {
            super(itemView);

            mUnbinder = ButterKnife.bind(this, itemView);
        }
    }

}
