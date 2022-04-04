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
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.PhotoBean;

/**
 * Created by cxf on 2019/5/10.
 */

public class MyPhotoAdapter extends RefreshAdapter<PhotoBean> {

    private View.OnClickListener mOnClickListener;
    private String mVideoReject;//审核失败
    private String mVideoVerify;//审核中

    public MyPhotoAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                PhotoBean bean = mList.get(position);
                if (bean != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, position);
                }
            }
        };
        mVideoVerify = WordUtil.getString(R.string.video_status_verify);
        mVideoReject = WordUtil.getString(R.string.video_status_reject);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_photo_my, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mStatus;
        TextView mWatchNum;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mStatus = itemView.findViewById(R.id.status);
            mWatchNum = itemView.findViewById(R.id.watch_num);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(PhotoBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mImg);
            mWatchNum.setText(bean.getViews());
            if (bean.getStatus() == 1) {
                if (mStatus.getVisibility() == View.VISIBLE) {
                    mStatus.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mStatus.getVisibility() != View.VISIBLE) {
                    mStatus.setVisibility(View.VISIBLE);
                }
                if (bean.getStatus() == 0) {
                    mStatus.setText(mVideoVerify);
                } else {
                    mStatus.setText(mVideoReject);
                }
            }
        }

    }
}
