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
import com.aihuan.main.R;
import com.aihuan.video.bean.VideoBean;

/**
 * Created by cxf on 2018/12/14.
 */

public class VideoHomeAdapter extends RefreshAdapter<VideoBean> {

    private View.OnClickListener mOnClickListener;

    public VideoHomeAdapter(Context context) {
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
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_video_home, parent, false));
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

    /**
     * 购买视频成功
     */
    public void onVideoChargeSuccess(String videoId, String href) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            VideoBean bean = mList.get(i);
            if (videoId.equals(bean.getId())) {
                bean.setCansee(1);
                bean.setHref(href);
                //notifyItemChanged(i);
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTitle;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position) {
            itemView.setTag(position);
            mTitle.setText(bean.getTitle());
            if (bean.isPrivate()) {
                ImgLoader.displayBlur(mContext, bean.getThumb(), mImg);
            } else {
                ImgLoader.display(mContext, bean.getThumb(), mImg);
            }
        }

    }
}
