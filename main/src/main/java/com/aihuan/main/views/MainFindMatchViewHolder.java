package com.aihuan.main.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.pay.AliCallback;
import com.aihuan.common.pay.PayPresenter;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.one.activity.MatchAnchorActivity;
import com.aihuan.one.activity.MatchAudienceActivity;
import com.aihuan.one.dialog.ChatChargeDialogFragment;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2019/5/7.
 */

public class MainFindMatchViewHolder extends AbsMainViewHolder implements View.OnClickListener {

    private GifImageView mGifImageView;
    private TextView mBtnVideo;
    private TextView mBtnVoice;
    private TextView mPrice;
    private TextView mPriceVip;
    private TextView mQueueCount;
    private int mType;
    private Drawable mRadioBg;
    private int mCheckedColor;
    private int mUnCheckedColor;
    private String mCoinName;
    private PayPresenter mPayPresenter;
    private boolean mHasAuth;
    private String mQueueCountString;
    private HttpCallback mMatchInfoCallback;
    private HttpCallback mMatchCheckCallback;
    private String mVideoPrice;
    private String mVideoPriceVip;
    private String mVoicePrice;
    private String mVoicePriceVip;

    public MainFindMatchViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_find_macth;
    }

    @Override
    public void init() {
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mQueueCountString = WordUtil.getString(R.string.match_queue_count);
        mRadioBg = ContextCompat.getDrawable(mContext, R.drawable.bg_main_find_check);
        mCheckedColor = ContextCompat.getColor(mContext, R.color.white);
        mUnCheckedColor = ContextCompat.getColor(mContext, R.color.textColor);
        mType = Constants.CHAT_TYPE_VIDEO;
        mGifImageView = (GifImageView) findViewById(R.id.gif);
        mQueueCount = (TextView) findViewById(R.id.queue_count);
        mBtnVideo = (TextView) findViewById(R.id.btn_video);
        mBtnVoice = (TextView) findViewById(R.id.btn_voice);
        showType();
        mBtnVideo.setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);
        findViewById(R.id.btn_match).setOnClickListener(this);
        mPrice = (TextView) findViewById(R.id.price);
        mPriceVip = (TextView) findViewById(R.id.price_vip);
    }

    /**
     * 选择 视频 语音 类型
     */
    private void showType() {
        if (mType == Constants.CHAT_TYPE_VIDEO) {
            mBtnVideo.setBackground(mRadioBg);
            mBtnVideo.setTextColor(mCheckedColor);
            mBtnVoice.setBackground(null);
            mBtnVoice.setTextColor(mUnCheckedColor);
        } else if (mType == Constants.CHAT_TYPE_VOICE) {
            mBtnVideo.setBackground(null);
            mBtnVideo.setTextColor(mUnCheckedColor);
            mBtnVoice.setBackground(mRadioBg);
            mBtnVoice.setTextColor(mCheckedColor);
        }
    }

    private void showPrice() {
        if (mType == Constants.CHAT_TYPE_VIDEO) {
            if (mPrice != null) {
                mPrice.setText(mVideoPrice);
                mPriceVip.setText(mVideoPriceVip);
            }
        } else if (mType == Constants.CHAT_TYPE_VOICE) {
            if (mPrice != null) {
                mPrice.setText(mVoicePrice);
                mPriceVip.setText(mVoicePriceVip);
            }
        }
    }


    /**
     * 显示排队人数
     */
    private void showQueueCount(String count) {
        if (mQueueCount != null) {
            mQueueCount.setText(StringUtil.contact(mQueueCountString, count));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_video) {
            clickVideo();

        } else if (i == R.id.btn_voice) {
            clickVoice();
        } else if (i == R.id.btn_match) {
            clickMatch();
        }
    }

    public void choice(int type) {
        if (type == 1) {
            clickVideo();
        } else {
            clickVoice();
        }
    }

    private void clickVideo() {
        if (mType == Constants.CHAT_TYPE_VIDEO) {
            return;
        }
        mType = Constants.CHAT_TYPE_VIDEO;
        showType();
        showPrice();
    }


    private void clickVoice() {
        if (mType == Constants.CHAT_TYPE_VOICE) {
            return;
        }
        mType = Constants.CHAT_TYPE_VOICE;
        showType();
        showPrice();
    }

    private void clickMatch() {
        //openChargeWindow();
        if (mHasAuth) {
            if (mType == Constants.CHAT_TYPE_VIDEO) {
                if (CommonAppConfig.getInstance().getUserSwitchVideo()) {
                    MatchAnchorActivity.forward(mContext, mType);
                } else {
                    ToastUtil.show(R.string.match_tip_1);
                }
            } else {
                if (CommonAppConfig.getInstance().getUserSwitchVoice()) {
                    MatchAnchorActivity.forward(mContext, mType);
                } else {
                    ToastUtil.show(R.string.match_tip_2);
                }
            }
        } else {
            matchCheck();
        }
    }


    private void matchCheck() {
        if (mMatchCheckCallback == null) {
            mMatchCheckCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (mType == Constants.CHAT_TYPE_VIDEO) {
                            MatchAudienceActivity.forward(mContext, mVideoPrice, mVideoPriceVip, Constants.CHAT_TYPE_VIDEO);
                        } else {
                            MatchAudienceActivity.forward(mContext, mVoicePrice, mVoicePriceVip, Constants.CHAT_TYPE_VOICE);
                        }
                    } else if (code == 1008) {
                        ToastUtil.show(R.string.chat_coin_not_enough);
                        openChargeWindow();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            };
        }
        OneHttpUtil.matchCheck(mType, mMatchCheckCallback);
    }


    /**
     * 打开充值窗口
     */
    public void openChargeWindow() {
        if (mPayPresenter == null) {
            mPayPresenter = new PayPresenter((AbsActivity) mContext);
            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
            mPayPresenter.setPayCallback(new AliCallback() {
                @Override
                public void onSuccess() {
                    if (mPayPresenter != null) {
                        mPayPresenter.checkPayResult();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
        ChatChargeDialogFragment fragment = new ChatChargeDialogFragment();
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatChargeDialogFragment");
    }


    @Override
    public void loadData() {
        if (mMatchInfoCallback == null) {
            mMatchInfoCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mHasAuth = obj.getIntValue("isauth") == 1;
                        if (mGifImageView != null && mGifImageView.getDrawable() == null) {
                            mGifImageView.setImageResource(mHasAuth ? R.mipmap.o_find_match_bg_2 : R.mipmap.o_find_match_bg_1);
                        }
                        mVideoPrice = String.format(WordUtil.getString(R.string.match_price), obj.getString("video"), mCoinName);
                        mVideoPriceVip = String.format(WordUtil.getString(R.string.match_price_vip), obj.getString("video_vip"), mCoinName);
                        mVoicePrice = String.format(WordUtil.getString(R.string.match_price), obj.getString("voice"), mCoinName);
                        mVoicePriceVip = String.format(WordUtil.getString(R.string.match_price_vip), obj.getString("voice_vip"), mCoinName);
                        showPrice();
                    }
                }
            };
        }
        OneHttpUtil.getMatchInfo(mMatchInfoCallback);
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_MATCH_INFO);
        super.onDestroy();
    }
}
