package com.aihuan.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

/**
 * Created by cxf on 2018/9/29.
 */

public class SearchAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mClickListener;
    private String mIdString;
    private String mFanString;

    public SearchAdapter(Context context) {
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
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
        mIdString = WordUtil.getString(R.string.search_id);
        mFanString = WordUtil.getString(R.string.search_fans);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mLevelAnchor;
        TextView mID;
        TextView mFans;
        View mVip;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mID = itemView.findViewById(R.id.id_val);
            mFans = itemView.findViewById(R.id.fans);
            mVip = itemView.findViewById(R.id.vip);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(UserBean bean) {
            itemView.setTag(bean);
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevelAnchor);
            }
            mID.setText(StringUtil.contact(mIdString, bean.getId()));
            mFans.setText(StringUtil.contact(mFanString, StringUtil.toWan(bean.getFans())));
            if (bean.isVip()) {
                if (mVip.getVisibility() != View.VISIBLE) {
                    mVip.setVisibility(View.VISIBLE);
                }
            } else {
                if (mVip.getVisibility() == View.VISIBLE) {
                    mVip.setVisibility(View.INVISIBLE);
                }
            }
        }

    }
}
