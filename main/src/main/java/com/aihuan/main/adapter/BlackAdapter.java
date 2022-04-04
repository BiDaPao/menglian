package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

/**
 * Created by cxf on 2018/9/29.
 */

public class BlackAdapter extends RefreshAdapter<UserBean> {

    private static final int HEAD = -1;
    private View.OnClickListener mClickListener;
    private String mIdString;

    public BlackAdapter(Context context) {
        super(context);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                UserBean bean = (UserBean) tag;
                CommonHttpUtil.setBlack(bean.getId());
            }
        };
        mIdString = WordUtil.getString(R.string.search_id);
    }

    public void removeItem(String toUid) {
        if (TextUtils.isEmpty(toUid)) {
            return;
        }
        int position = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (toUid.equals(mList.get(i).getId())) {
                position = i;
                break;
            }
        }
        if (position >= 0 && position < mList.size()) {
            mList.remove(position);
            notifyItemRemoved(position + 1);
            notifyItemRangeChanged(position + 1, getItemCount());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return 0;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadVh(mInflater.inflate(R.layout.item_empty_head, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_black, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1));
        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public int getListSize(){
        return mList.size();
    }


    class HeadVh extends RecyclerView.ViewHolder {

        public HeadVh(View itemView) {
            super(itemView);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mLevelAnchor;
        TextView mID;
        View mBtnCancel;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mID = itemView.findViewById(R.id.id_val);
            mBtnCancel = itemView.findViewById(R.id.btn_cancel);
            mBtnCancel.setOnClickListener(mClickListener);
        }

        void setData(UserBean bean) {
            mBtnCancel.setTag(bean);
            ImgLoader.displayAvatar(mContext, bean.getAvatarThumb(), mAvatar);
            mName.setText(bean.getUserNiceName());
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevelAnchor);
            }
            mID.setText(StringUtil.contact(mIdString, bean.getId()));
        }

    }
}
