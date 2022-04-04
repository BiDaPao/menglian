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
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.main.R;
import com.aihuan.main.bean.SubcribeAncBean;

/**
 * Created by cxf on 2019/4/23.
 */

public class SubcribeAncAdapter extends RefreshAdapter<SubcribeAncBean> {

    private View.OnClickListener mClickListener;
    private View.OnClickListener mToClickListener;
    private ActionListener mActionListener;

    public SubcribeAncAdapter(Context context) {
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
                SubcribeAncBean bean = (SubcribeAncBean) tag;
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };

        mToClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                SubcribeAncBean bean = (SubcribeAncBean) tag;
                if (mActionListener != null) {
                    mActionListener.onToSubcribeClick(bean);
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_subcribe_anc, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mLevel;
        TextView mCoin;
        View mBtnToSubcribe;
        View mVip;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mLevel = itemView.findViewById(R.id.level);
            mCoin = itemView.findViewById(R.id.coin);
            mVip = itemView.findViewById(R.id.vip);
            mBtnToSubcribe = itemView.findViewById(R.id.btn_subcribe_to);
            mBtnToSubcribe.setOnClickListener(mToClickListener);
            //itemView.setOnClickListener(mClickListener);
        }

        void setData(SubcribeAncBean bean) {
            itemView.setTag(bean);
            mBtnToSubcribe.setTag(bean);
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
            mCoin.setText(bean.getCoin());
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
        void onItemClick(SubcribeAncBean u);

        void onToSubcribeClick(SubcribeAncBean u);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
