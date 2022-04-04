package com.aihuan.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
 * 首页 推荐
 */

public class MainHomeRecommendAdapter extends RefreshAdapter<ChatLiveBean> {

    private static final int HEAD = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private View.OnClickListener mOnClickListener;
    private OnAccostClick mOnAccostClick;
    private View mHeadView;
    private Drawable mVideoDrawable;
    private Drawable mVoiceDrawable;
    private String mPriceSuffix;

    public MainHomeRecommendAdapter(Context context) {
        super(context);
        mHeadView = mInflater.inflate(R.layout.item_main_home_live_head, null, false);
//        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(177)));
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

    public void setOnAccostClick(OnAccostClick listener) {
        this.mOnAccostClick = listener;
    }

    public View getHeadView() {
        return mHeadView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        } //else
//            if (position % 2 == 0) {
//            return RIGHT;
//        }
        return LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        }
//        else if (viewType == LEFT) {
//            return new Vh(mInflater.inflate(R.layout.item_main_home_live_left, parent, false));
//        }
        return new Vh(mInflater.inflate(R.layout.item_main_home_live_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1), position - 1);
        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder {

        public HeadVh(View itemView) {
            super(itemView);
        }
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
