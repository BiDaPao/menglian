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
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.SubcribeAudBean;

/**
 * Created by cxf on 2019/4/23.
 */

public class SubcribeAudAdapter extends RefreshAdapter<SubcribeAudBean> {

    private String mIdString;
    private View.OnClickListener mClickListener;

    public SubcribeAudAdapter(Context context) {
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
                SubcribeAudBean bean = (SubcribeAudBean) tag;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
        mIdString = WordUtil.getString(R.string.search_id);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_subcribe_aud, parent, false));
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
        View mVip;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mID = itemView.findViewById(R.id.id_val);
            mVip = itemView.findViewById(R.id.vip);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(SubcribeAudBean bean) {
            itemView.setTag(bean);
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevelAnchor);
            }
            mID.setText(StringUtil.contact(mIdString, bean.getUid()));
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
