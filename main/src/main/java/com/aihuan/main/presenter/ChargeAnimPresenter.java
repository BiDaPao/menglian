package com.aihuan.main.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.ChargeSuccessBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cxf on 2018/10/13.
 * 首页充值动画
 */

public class ChargeAnimPresenter {

    private Context mContext;
    private View mGroup;
    private TextView mName;
    private TextView mContent;
    private ImageView mAvatar;
    private ObjectAnimator mGifGiftTipShowAnimator;
    private ObjectAnimator mGifGiftTipHideAnimator;
    private ConcurrentLinkedQueue<ChargeSuccessBean> mQueue;
    private boolean mAnimating;
    private int mDp10;
    private int mDp500;
    private Handler mHandler;
    private String mCoinName;


    public ChargeAnimPresenter(Context context, View v) {
        mContext = context;
        mGroup = v.findViewById(R.id.group_charge_success);
        mName = v.findViewById(R.id.name_charge_success);
        mAvatar = v.findViewById(R.id.avatar_charge_success);
        mContent = v.findViewById(R.id.content_charge_success);
        mDp500 = DpUtil.dp2px(500);
        mGifGiftTipShowAnimator = ObjectAnimator.ofFloat(mGroup, "translationX", mDp500, 0);
        mGifGiftTipShowAnimator.setDuration(1000);
        mGifGiftTipShowAnimator.setInterpolator(new LinearInterpolator());
        mGifGiftTipShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }
            }
        });
        mDp10 = DpUtil.dp2px(10);
        mGifGiftTipHideAnimator = ObjectAnimator.ofFloat(mGroup, "translationX", 0);
        mGifGiftTipHideAnimator.setDuration(800);
        mGifGiftTipHideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mGifGiftTipHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating=false;
                if (mQueue != null) {
                    ChargeSuccessBean bean = mQueue.poll();
                    if (bean != null) {
                        showAnim(bean);
                    }
                }
            }
        });
        mQueue = new ConcurrentLinkedQueue<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mGifGiftTipHideAnimator.setFloatValues(0, -mDp10 - mGroup.getWidth());
                mGifGiftTipHideAnimator.start();
            }
        };
        mCoinName = CommonAppConfig.getInstance().getCoinName();
    }

    public void save(ChargeSuccessBean bean) {
        if (mQueue != null) {
            mQueue.offer(bean);
        }
    }

    public ChargeSuccessBean get() {
        if (mQueue != null) {
            return mQueue.poll();
        }
        return null;
    }

    public void showAnim(ChargeSuccessBean bean) {
        if (bean == null) {
            return;
        }
        if (mAnimating) {
            if (mQueue != null) {
                mQueue.offer(bean);
            }
        } else {
            mAnimating = true;
            ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getNickname());
            mContent.setText(String.format(WordUtil.getString(R.string.main_charge_tip), bean.getCoin(), mCoinName));
            mGifGiftTipShowAnimator.start();
        }
    }


    public void release() {
        if (mGifGiftTipHideAnimator != null) {
            mGifGiftTipHideAnimator.cancel();
            mGifGiftTipHideAnimator.removeAllListeners();
        }
        mGifGiftTipHideAnimator = null;
        if (mGifGiftTipShowAnimator != null) {
            mGifGiftTipShowAnimator.cancel();
            mGifGiftTipShowAnimator.removeAllListeners();
        }
        mGifGiftTipShowAnimator = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mQueue != null) {
            mQueue.clear();
        }
        mQueue = null;
    }

}
