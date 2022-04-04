package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.main.R;
import com.aihuan.main.bean.VipBuyBean;

import java.util.List;

/**
 * Created by cxf on 2019/5/11.
 */

public class VipBuyAdapter extends RecyclerView.Adapter<VipBuyAdapter.Vh> {

    private LayoutInflater mInflater;
    private List<VipBuyBean> mList;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private int mCheckedColor;
    private int mUnCheckedColor;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private OnItemClickListener<VipBuyBean> mOnItemClickListener;

    public VipBuyAdapter(Context context, List<VipBuyBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_vip_item_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_vip_item_0);
        mCheckedColor = ContextCompat.getColor(context, R.color.global);
        mUnCheckedColor = ContextCompat.getColor(context, R.color.gray1);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position != mCheckedPosition) {
                    mList.get(mCheckedPosition).setChecked(false);
                    notifyItemChanged(mCheckedPosition);
                    VipBuyBean bean = mList.get(position);
                    bean.setChecked(true);
                    notifyItemChanged(position);
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<VipBuyBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_vip_buy, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public Vh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setOnClickListener(mOnClickListener);
        }

        void setData(VipBuyBean bean, int position) {
            itemView.setTag(position);
            mTextView.setText(bean.getName());
            if (bean.isChecked()) {
                mTextView.setBackground(mCheckedDrawable);
                mTextView.setTextColor(mCheckedColor);
            } else {
                mTextView.setBackground(mUnCheckedDrawable);
                mTextView.setTextColor(mUnCheckedColor);
            }
        }
    }
}
