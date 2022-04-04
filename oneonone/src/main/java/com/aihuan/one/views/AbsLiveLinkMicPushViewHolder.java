package com.aihuan.one.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.interfaces.LivePushListener;

/**
 * Created by cxf on 2018/10/26.
 * 连麦推流小窗口基类
 */

public abstract class AbsLiveLinkMicPushViewHolder extends AbsViewHolder  {

    protected LivePushListener mLivePushListener;
    protected boolean mPaused;
    protected boolean mStartPush;

    public AbsLiveLinkMicPushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    public abstract  void startPush(String pushUrl) ;

    public abstract void release() ;

    public abstract void pause() ;

    public abstract void resume() ;

    public void setLivePushListener(LivePushListener livePushListener) {
        mLivePushListener = livePushListener;
    }
}
