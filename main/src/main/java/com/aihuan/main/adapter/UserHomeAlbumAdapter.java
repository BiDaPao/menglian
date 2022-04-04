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
import com.aihuan.main.bean.PhotoBean;

/**
 * Created by cxf on 2019/5/10.
 */

public class UserHomeAlbumAdapter extends RefreshAdapter<PhotoBean> {

    private View.OnClickListener mOnClickListener;

    public UserHomeAlbumAdapter(Context context) {
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
    }

    /**
     * 更新是否能看
     */
    public void updateCanSee(String photoId, int canSee) {
        if (TextUtils.isEmpty(photoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            PhotoBean bean = mList.get(i);
            if (photoId.equals(bean.getId())) {
                bean.setCansee(canSee);
               // notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 更新观看数量
     */
    public void updateViewNum(String photoId, String watchNum) {
        if (TextUtils.isEmpty(photoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            PhotoBean bean = mList.get(i);
            if (photoId.equals(bean.getId())) {
                bean.setViews(watchNum);
                notifyItemChanged(i);
                break;
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_photo_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mWatchNum;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mWatchNum = itemView.findViewById(R.id.watch_num);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(PhotoBean bean, int position) {
            itemView.setTag(position);
            mWatchNum.setText(bean.getViews());
            if (bean.isPrivate()) {
                ImgLoader.displayBlur(mContext, bean.getThumb(), mImg);
            } else {
                ImgLoader.display(mContext, bean.getThumb(), mImg);
            }
        }

    }
}
