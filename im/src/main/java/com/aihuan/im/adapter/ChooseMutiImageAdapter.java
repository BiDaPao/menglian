package com.aihuan.im.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aihuan.common.Constants;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.im.R;
import com.aihuan.im.bean.ChatChooseImageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 * 选择多张图片的Adapter
 */

public class ChooseMutiImageAdapter extends RecyclerView.Adapter<ChooseMutiImageAdapter.Vh> {

    private Context mContext;
    private List<ChatChooseImageBean> mList;
    private ArrayList<String> mSelectedPathList;
    private LayoutInflater mInflater;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private View.OnClickListener mOnClickListener;
    private int mCheckedCount;

    public ChooseMutiImageAdapter(Context context, List<ChatChooseImageBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_checked);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_checked_none);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }

                int position = (int) tag;
                if (position >= 0 && position < mList.size()) {
                    ChatChooseImageBean bean = mList.get(position);
                    if (bean.isChecked()) {
                        bean.setChecked(false);
                        mCheckedCount--;
                        if (mCheckedCount < 0) {
                            mCheckedCount = 0;
                        }
                    } else {
                        if (mCheckedCount >= 6) {
                            ToastUtil.show(R.string.choose_image_max);
                            return;
                        }
                        bean.setChecked(true);
                        mCheckedCount++;
                    }
                    notifyItemChanged(position, Constants.PAYLOAD);
                }
            }
        };
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_chat_choose_img, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    public ArrayList<String> getSelectedPathList() {
        if (mSelectedPathList != null) {
            mSelectedPathList.clear();
        }
        for (ChatChooseImageBean bean : mList) {
            if (bean.isChecked()) {
                if (mSelectedPathList == null) {
                    mSelectedPathList = new ArrayList<>();
                }
                mSelectedPathList.add(bean.getImageFile().getAbsolutePath());
            }
        }
        return mSelectedPathList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ChatChooseImageBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.display(mContext, bean.getImageFile(), mCover);
            }
            mImg.setImageDrawable(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }

}
