package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.aihuan.main.interfaces.OnAccostClick;
import com.aihuan.main.utils.MainIconUtil;
import com.aihuan.one.bean.ChatLiveBean;

/**
 * Created by cxf on 2018/9/26.
 */

public class MainHomeFollowAdapter extends RefreshAdapter<ChatLiveBean> {

    private View.OnClickListener mOnClickListener;
    private Drawable mVideoDrawable;
    private Drawable mVoiceDrawable;
    private String mPriceSuffix;
    private OnAccostClick mOnAccostClick;


    public MainHomeFollowAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
        mVideoDrawable = ContextCompat.getDrawable(context, R.mipmap.o_main_price_video);
        mVoiceDrawable = ContextCompat.getDrawable(context, R.mipmap.o_main_price_voice);
        mPriceSuffix = String.format(WordUtil.getString(R.string.main_price), CommonAppConfig.getInstance().getCoinName());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_live_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    public void setOnAccostClick(OnAccostClick listener) {
        this.mOnAccostClick = listener;
    }
    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mPriceIcon;
        ImageView mLevel;
        View mVideoIcon;
        View mVoiceIcon;
        TextView mPrice;
        TextView mName;
        ImageView mOnLine, ivAccost;
        TextView mInfo;

        public Vh(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mPriceIcon = itemView.findViewById(R.id.price_icon);
            mLevel = itemView.findViewById(R.id.level);
            mVideoIcon = itemView.findViewById(R.id.video);
            mVoiceIcon = itemView.findViewById(R.id.voice);
            mPrice = itemView.findViewById(R.id.price);
            mName = itemView.findViewById(R.id.name);
            mOnLine = itemView.findViewById(R.id.on_line);
            ivAccost = itemView.findViewById(R.id.iv_accost);
            mInfo = itemView.findViewById(R.id.city_age_height);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(final ChatLiveBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mName.setText(bean.getUserNiceName());
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
            mOnLine.setImageResource(MainIconUtil.getOnLineIcon1(bean.getOnLineStatus()));
            if (bean.isOpenVideo()) {
                if (mVideoIcon.getVisibility() != View.VISIBLE) {
                    mVideoIcon.setVisibility(View.VISIBLE);
                }
                mPriceIcon.setImageDrawable(mVideoDrawable);
                mPrice.setText(StringUtil.contact(bean.getPriceVideo(), mPriceSuffix));
            } else {
                if (mVideoIcon.getVisibility() == View.VISIBLE) {
                    mVideoIcon.setVisibility(View.GONE);
                }
                if (bean.isOpenVoice()) {
                    mPriceIcon.setImageDrawable(mVoiceDrawable);
                    mPrice.setText(StringUtil.contact(bean.getPriceVoice(), mPriceSuffix));
                } else {
                    mPriceIcon.setImageDrawable(mVideoDrawable);
                    mPrice.setText(StringUtil.contact(bean.getPriceVideo(), mPriceSuffix));
                }
            }
            if (bean.isOpenVoice()) {
                if (mVoiceIcon.getVisibility() != View.VISIBLE) {
                    mVoiceIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mVoiceIcon.getVisibility() == View.VISIBLE) {
                    mVoiceIcon.setVisibility(View.GONE);
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(bean.getCity());
            if (bean.getAge() > 0) {
                if (builder.length() > 0) {
                    builder.append(" | ");
                }
                builder.append(bean.getAge()).append("岁");
            }
            if (bean.getHeight() > 0) {
                if (builder.length() > 0) {
                    builder.append(" | ");
                }
                builder.append(bean.getHeight()).append("cm");
            }
            mInfo.setText(builder.toString());
            ivAccost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnAccostClick != null) {
                        mOnAccostClick.onAccostClick(bean);
                    }
                }
            });

        }
    }

}
