package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.main.R;
import com.aihuan.main.bean.VipItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/5/11.
 */

public class VipItemAdapter extends RecyclerView.Adapter<VipItemAdapter.Vh> {

    private List<VipItemBean> mList;
    private LayoutInflater mInflater;

    public VipItemAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
        mList.add(new VipItemBean(R.mipmap.o_vip_item_0, R.string.vip_item_0_1, R.string.vip_item_0_2));
        mList.add(new VipItemBean(R.mipmap.o_vip_item_1, R.string.vip_item_1_1, R.string.vip_item_1_2));
        mList.add(new VipItemBean(R.mipmap.o_vip_item_2, R.string.vip_item_2_1, R.string.vip_item_2_2));
        mList.add(new VipItemBean(R.mipmap.o_vip_item_3, R.string.vip_item_3_1, R.string.vip_item_3_2));
        mList.add(new VipItemBean(R.mipmap.o_vip_item_4, R.string.vip_item_4_1, R.string.vip_item_4_2));
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_vip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mTip1;
        TextView mTip2;

        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mTip1 = itemView.findViewById(R.id.tip_1);
            mTip2 = itemView.findViewById(R.id.tip_2);
        }

        void setData(VipItemBean bean) {
            mIcon.setImageResource(bean.getIcon());
            mTip1.setText(bean.getTip1());
            mTip2.setText(bean.getTip2());
        }
    }
}
