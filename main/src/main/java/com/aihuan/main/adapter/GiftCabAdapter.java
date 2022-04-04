package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.GiftCabBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class GiftCabAdapter extends RefreshAdapter<GiftCabBean> {

    private String mPrefix;

    public GiftCabAdapter(Context context) {
        super(context);
        mPrefix = "x";
    }

    public GiftCabAdapter(Context context, List<GiftCabBean> list){
        super(context,list);
        mPrefix = "x";
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_gift_cab, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;
        TextView mCount;


        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
            mCount = itemView.findViewById(R.id.count);
        }

        void setData(GiftCabBean bean) {
            ImgLoader.display(mContext, bean.getThumb(), mIcon);
            mName.setText(bean.getName());
            mCount.setText(StringUtil.contact(mPrefix, bean.getNum()));
        }
    }
}
