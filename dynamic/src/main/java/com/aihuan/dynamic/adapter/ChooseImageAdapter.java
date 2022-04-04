package com.aihuan.dynamic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dynamic.R;
import com.aihuan.common.Constants;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.upload.UploadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 * 聊天时候选择图片的Adapter
 */

public class ChooseImageAdapter extends RecyclerView.Adapter<ChooseImageAdapter.Vh> {

    private Context mContext;
    private static final int POSITION_NONE = -1;
    private List<UploadBean> mList;
    private LayoutInflater mInflater;
    private int mSelectedPosition;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private View.OnClickListener mOnClickListener;
    private SparseArray<UploadBean> mSelectList;
    private int mSelectNum;

    public ChooseImageAdapter(Context context, List<UploadBean> list,int selectedNum) {
        mContext = context;
        mList = list;
        mSelectList=new SparseArray<>();
        mSelectNum=selectedNum;
        mInflater = LayoutInflater.from(context);
        mSelectedPosition = POSITION_NONE;
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
                if (position == mSelectedPosition) {
                    mSelectList.remove(position);
                    mList.get(mSelectedPosition).setChecked(false);
                    notifyItemChanged(mSelectedPosition, Constants.PAYLOAD);
                    mSelectedPosition=POSITION_NONE;
                    return;
                }
                if (mSelectList.size()+mSelectNum<Constants.DYNAMIC_IMG_MAX_NUM) {
                    mSelectList.put(position,mList.get(position));
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                }else {
                    ToastUtil.show(WordUtil.getString(R.string.img_select_tip));
                }
                mSelectedPosition = position;
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



    public List<UploadBean> getSelectedFile() {
        List<UploadBean> uploadBeanList=new ArrayList<>();
        for (int i = 0; i <mSelectList.size() ; i++) {
            UploadBean bean=mSelectList.valueAt(i);
            bean.setType(UploadBean.IMG);
            uploadBeanList.add(bean);
        }
        return uploadBeanList;
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

        void setData(UploadBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.display(mContext, bean.getOriginFile(), mCover);
            }
            mImg.setImageDrawable(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }

}
