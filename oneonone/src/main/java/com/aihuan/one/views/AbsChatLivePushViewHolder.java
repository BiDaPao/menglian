package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

//import com.aihuan.beauty.interfaces.BeautyEffectListener;
//import com.aihuan.beauty.interfaces.DefaultBeautyEffectListener;
//import com.aihuan.beauty.interfaces.TiBeautyEffectListener;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.R;
import com.aihuan.one.interfaces.LivePushListener;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.sdk.bean.TiRockEnum;

/**
 * Created by cxf on 2018/12/22.
 */

public abstract class AbsChatLivePushViewHolder extends AbsViewHolder {

    private View mCameraCover;//摄像头覆盖物，用来关闭摄像头
    protected LivePushListener mLivePushListener;
    protected boolean mCameraFront;//是否是前置摄像头
    protected boolean mFlashOpen;//闪光灯是否开启了
    protected boolean mPaused;
//    protected BeautyEffectListener mEffectListener;//萌颜的效果监听
    protected TiSDKManager mTiSDKManager;//各种萌颜效果控制器

    public AbsChatLivePushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsChatLivePushViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    public void init() {
        mCameraFront = true;
        mCameraCover = findViewById(R.id.camera_cover);
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
            initBeauty();
        }
    }

    /**
     * 初始化萌颜
     */
    private void initBeauty() {
        try {
            mTiSDKManager = TiSDKManager.getInstance();
            mTiSDKManager.setBeautyEnable(true);
            mTiSDKManager.setFaceTrimEnable(true);
            ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
            if (configBean != null) {
                mTiSDKManager.setSkinWhitening(configBean.getBeautyMeiBai());//美白
                mTiSDKManager.setSkinBlemishRemoval(configBean.getBeautyMoPi());//磨皮
                mTiSDKManager.setSkinSaturation(configBean.getBeautyBaoHe());//饱和
                mTiSDKManager.setSkinTenderness(configBean.getBeautyFenNen());//粉嫩
                mTiSDKManager.setEyeMagnifying(configBean.getBeautyBigEye());//大眼
                mTiSDKManager.setChinSlimming(configBean.getBeautyFace());//瘦脸
            } else {
                mTiSDKManager.setSkinWhitening(0);//美白
                mTiSDKManager.setSkinBlemishRemoval(0);//磨皮
                mTiSDKManager.setSkinSaturation(0);//饱和
                mTiSDKManager.setSkinTenderness(0);//粉嫩
                mTiSDKManager.setEyeMagnifying(0);//大眼
                mTiSDKManager.setChinSlimming(0);//瘦脸
            }
            mTiSDKManager.setSticker("");
//            mTiSDKManager.setFilterEnum(TiFilterEnum.NO_FILTER);
        }
        catch (Exception e) {
            mTiSDKManager = null;
            ToastUtil.show(R.string.beauty_init_error);
        }
    }

    /**
     * 设置推流监听
     */
    public void setLivePushListener(LivePushListener livePushListener) {
        mLivePushListener = livePushListener;
    }

    /**
     * 获取美颜效果监听
     */
//    public BeautyEffectListener getEffectListener() {
//        return mEffectListener;
//    }

    /**
     * 开始推流
     */
    public abstract void startPush(String pushUrl, boolean pureVoice);

    /**
     * 停止推流
     */
    public abstract void stopPush();

    /**
     * 切换闪光灯
     */
    public abstract void toggleFlash();

    /**
     * 切换摄像头
     */
    public abstract void toggleCamera();

    /**
     * 关麦
     */
    public abstract void setMute(boolean mute);

//    protected abstract DefaultBeautyEffectListener getDefaultEffectListener();

    public abstract void setBig(boolean big);


    @Override
    public void release() {
        super.release();
//        if (mTiSDKManager != null) {
//            mTiSDKManager.destroy();
//        }
//        mTiSDKManager = null;
        mLivePushListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    public void showCameraCover() {
        if (mCameraCover != null && mCameraCover.getVisibility() != View.VISIBLE) {
            mCameraCover.setVisibility(View.VISIBLE);
        }
    }

    public void hideCameraCover() {
        if (mCameraCover != null && mCameraCover.getVisibility() == View.VISIBLE) {
            mCameraCover.setVisibility(View.INVISIBLE);
        }
    }

}
