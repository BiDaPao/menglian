package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class FollowAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mClickListener;
    private View.OnClickListener mFollowClickListner;
    private ActionListener mActionListener;
    private String mIdString;
    private String mFanString;
    private String mFollowAddString;
    private String mFollowCancelString;
    private Drawable mFollowAddDrawable;
    private Drawable mFollowCancelDrawable;
    private int mFollowAddColor;
    private int mFollowCancelColor;


    public FollowAdapter(Context context) {
        super(context);
        mFollowAddString = WordUtil.getString(R.string.follow_add);
        mFollowCancelString = WordUtil.getString(R.string.follow_cancel);
        mFollowAddDrawable = ContextCompat.getDrawable(context, R.drawable.btn_follow_1);
        mFollowCancelDrawable = ContextCompat.getDrawable(context, R.drawable.btn_follow_0);
        mFollowAddColor = ContextCompat.getColor(context, R.color.global);
        mFollowCancelColor = 0xffb3b3b3;
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
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };
        mFollowClickListner = new View.OnClickListener() {

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
                if (mActionListener != null) {
                    mActionListener.onFollowClick(bean);
                }
            }
        };
        mIdString = WordUtil.getString(R.string.search_id);
        mFanString = WordUtil.getString(R.string.search_fans);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), payload);
    }

    public void updateItem(String id, int attention) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            UserBean bean = mList.get(i);
            if (bean != null && id.equals(bean.getId())) {
                bean.setAttent(attention);
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mLevelAnchor;
        TextView mID;
        TextView mFans;
        TextView mBtnFollow;
        View mVip;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mID = itemView.findViewById(R.id.id_val);
            mFans = itemView.findViewById(R.id.fans);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
            mVip = itemView.findViewById(R.id.vip);
            itemView.setOnClickListener(mClickListener);
            mBtnFollow.setOnClickListener(mFollowClickListner);
        }

        void setData(UserBean bean, Object payload) {
            itemView.setTag(bean);
            mBtnFollow.setTag(bean);
            if (payload == null) {
                ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
                if (levelBean != null) {
                    ImgLoader.display(mContext, levelBean.getThumb(), mLevelAnchor);
                }
                mID.setText(StringUtil.contact(mIdString, bean.getId()));
                mFans.setText(StringUtil.contact(mFanString, StringUtil.toWan(bean.getFans())));
            }
            if (bean.isFollowing()) {
                mBtnFollow.setText(mFollowCancelString);
                mBtnFollow.setBackground(mFollowCancelDrawable);
                mBtnFollow.setTextColor(mFollowCancelColor);
            } else {
                mBtnFollow.setText(mFollowAddString);
                mBtnFollow.setBackground(mFollowAddDrawable);
                mBtnFollow.setTextColor(mFollowAddColor);
            }
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


    public interface ActionListener {
        void onItemClick(UserBean userBean);

        void onFollowClick(UserBean userBean);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
