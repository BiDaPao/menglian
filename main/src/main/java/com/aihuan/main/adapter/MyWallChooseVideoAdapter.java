package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.video.bean.VideoBean;

import java.util.List;

/**
 * Created by cxf on 2019/5/10.
 * 背景墙选择视频
 */

public class MyWallChooseVideoAdapter extends RefreshAdapter<VideoBean> {

    private View.OnClickListener mOnClickListener;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private int mCheckPosition = -1;

    public MyWallChooseVideoAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position != mCheckPosition) {
                    if (mCheckPosition >= 0 && mCheckPosition < mList.size()) {
                        mList.get(mCheckPosition).setChecked(false);
                        notifyItemChanged(mCheckPosition, Constants.PAYLOAD);
                    }
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    mCheckPosition = position;
                }
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.o_checked_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.o_checked_0);
    }

    public VideoBean getCheckedPhoto() {
        if (mCheckPosition >= 0 && mCheckPosition < mList.size()) {
            return mList.get(mCheckPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_video_wall_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position, Constants.PAYLOAD);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mTitle;
        ImageView mCheck;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mCheck = itemView.findViewById(R.id.check);
            mTitle = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mImg);
                mTitle.setText(bean.getTitle());
            }
            mCheck.setBackground(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }

    }
}
