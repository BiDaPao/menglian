package com.aihuan.common.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.R;
import com.aihuan.common.bean.ChatPriceTipBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class PriceTipAdapter extends RecyclerView.Adapter<PriceTipAdapter.Vh> {

    private Context mContext;
    private List<ChatPriceTipBean> mList;
    private LayoutInflater mInflater;
    private String mPrefix;

    public PriceTipAdapter(Context context, List<ChatPriceTipBean> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mPrefix = "≤ ";
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_price_tip, parent, false));
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

        private ImageView mLevel;
        private TextView mPrice;

        public Vh(View itemView) {
            super(itemView);
            mLevel = itemView.findViewById(R.id.level);
            mPrice = itemView.findViewById(R.id.price);
        }

        void setData(ChatPriceTipBean bean) {
            ImgLoader.display(mContext, bean.getThumb(), mLevel);
            mPrice.setText(StringUtil.contact(mPrefix, bean.getCoin()));
        }
    }
}
