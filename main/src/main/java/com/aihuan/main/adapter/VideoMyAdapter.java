package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.video.bean.VideoBean;

/**
 * Created by cxf on 2018/12/14.
 */

public class VideoMyAdapter extends RefreshAdapter<VideoBean> {

    private View.OnClickListener mOnClickListener;
    private String mVideoReject;//审核失败
    private String mVideoVerify;//审核中

    public VideoMyAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                VideoBean bean = mList.get(position);
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
        return new Vh(mInflater.inflate(R.layout.item_video_home_my, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    /**
     * 删除视频
     */
    public void deleteVideo(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (videoId.equals(mList.get(i).getId())) {
                notifyItemRemoved(i);
                break;
            }
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mTitle;
        TextView mStatus;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mTitle = itemView.findViewById(R.id.title);
            mStatus = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mImg);
            mTitle.setText(bean.getTitle());
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
