package com.aihuan.one.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.one.activity.ChatBaseActivity;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
//import com.aihuan.beauty.bean.FilterBean;
//import com.aihuan.beauty.interfaces.DefaultBeautyEffectListener;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.utils.BitmapUtil;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.one.R;

import cn.tillusory.sdk.bean.TiRotation;

/**
 * Created by cxf on 2018/10/7.
 * 腾讯云直播推流
 */

public class ChatLivePushTxViewHolder extends AbsChatLivePushViewHolder implements ITXLivePushListener {

    private static final String TAG = "ChatLivePushTxViewHolder";
    private View mRoot;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private String mBgmPath;//背景音乐路径
    private boolean mBig;
    private boolean mPushStarted;

    /**
     * 默认美颜参数
     */
    //todo --- tillusory start ---
    private int mBeautyLevel = 0;            // 美颜等级
    private int mBeautyStyle = TXLiveConstants.BEAUTY_STYLE_SMOOTH; // 美颜样式
    private int mWhiteningLevel = 0;            // 美白等级
    private int mRuddyLevel = 0;            // 红润等级


    public ChatLivePushTxViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        mRoot = findViewById(R.id.root);
        mBig = true;
        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(15);//视频帧率
        mLivePushConfig.setVideoEncodeGop(1);//GOP大小
        mLivePushConfig.setVideoBitrate(1800);
        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280);
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//自动对焦
        //mLivePushConfig.setANS(true);//噪声抑制
        mLivePushConfig.enableAEC(true);//消除回声
        mLivePushConfig.setAudioSampleRate(48000);
        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setMirror(true);
        mLivePusher.setPushListener(this);
        mLivePusher.setBGMVolume(1f);
        mLivePusher.setMicVolume(4f);
        mLivePusher.setBGMNofify(new TXLivePusher.OnBGMNotify() {
            @Override
            public void onBGMStart() {

            }

            @Override
            public void onBGMProgress(long l, long l1) {

            }

            @Override
            public void onBGMComplete(int i) {
                if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null) {
                    mLivePusher.playBGM(mBgmPath);
                }
            }
        });
//        mBeautyLevel =mTiSDKManager.getFaceNumber();
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int i, int i1, int i2) {
                    if (mTiSDKManager != null) {
                        return mTiSDKManager.renderTexture2D(i, i1, i2, TiRotation.CLOCKWISE_ROTATION_0, false);
                    }
                    return 0;
                }

                @Override
                public void onDetectFacePoints(float[] floats) {

                }

                @Override
                public void onTextureDestoryed() {
                }
            });
//             设置美颜
//            mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
            if (mContext instanceof ChatBaseActivity) {
                ((ChatBaseActivity) mContext).initBeauty();
            }
        }
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.camera_preview);
    }

//    public DefaultBeautyEffectListener getDefaultEffectListener() {
//        return new DefaultBeautyEffectListener() {
//
//            @Override
//            public void onFilterChanged(FilterBean bean) {
//                if (bean == null || mLivePusher == null) {
//                    return;
//                }
//                if (mFilterBmp != null) {
//                    mFilterBmp.recycle();
//                }
//                int filterSrc = bean.getFilterSrc();
//                if (filterSrc != 0) {
//                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
//                    if (bitmap != null) {
//                        mFilterBmp = bitmap;
//                        mLivePusher.setFilter(bitmap);
//                    } else {
//                        mLivePusher.setFilter(null);
//                    }
//                } else {
//                    mLivePusher.setFilter(null);
//                }
//            }
//
//            @Override
//            public void onMeiBaiChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mMeiBaiVal != v) {
//                        mMeiBaiVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//
//            @Override
//            public void onMoPiChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mMoPiVal != v) {
//                        mMoPiVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//
//            @Override
//            public void onHongRunChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mHongRunVal != v) {
//                        mHongRunVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//        };
//    }

    @Override
    public void setBig(boolean big) {
        if (mRoot == null || mBig == big) {
            return;
        }
        mBig = big;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRoot.getLayoutParams();
        if (big) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            params.rightMargin = 0;
            params.gravity = Gravity.NO_GRAVITY;
        } else {
            params.width = DpUtil.dp2px(120);
            params.height = DpUtil.dp2px(160);
            params.topMargin = DpUtil.dp2px(35);
            params.rightMargin = DpUtil.dp2px(5);
            params.gravity = Gravity.RIGHT;
        }
        mRoot.requestLayout();
    }


    /**
     * 切换镜头
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            mLivePusher.setMirror(mCameraFront);
        }
    }

    /**
     * 关麦
     */
    @Override
    public void setMute(boolean mute) {
        if (mLivePusher != null) {
            mLivePusher.setMute(mute);
        }
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl   推流地址
     * @param pureVoice 是否是纯音频推流
     */
    @Override
    public void startPush(String pushUrl, boolean pureVoice) {
        if (mLivePusher != null) {
            if (pureVoice) {
                if (mLivePushConfig != null) {
                    mLivePushConfig.enablePureAudioPush(true);
                    mLivePusher.setConfig(mLivePushConfig);
                }
            } else {
                if (mLivePushConfig != null) {
                    mLivePushConfig.enablePureAudioPush(false);
                    mLivePusher.setConfig(mLivePushConfig);
                }
                if (mTXCloudVideoView != null) {
                    mLivePusher.startCameraPreview(mTXCloudVideoView);
                }
            }
            int i = mLivePusher.startPusher(pushUrl);
            if (i == -5) {
                L.e(TAG, "startRTMPPush: license 校验失败");
            }
            mPushStarted = true;
        }
    }

    @Override
    public void stopPush() {
        try {
            if (mPushStarted) {
                mPushStarted = false;
                if (mLivePusher != null && mLivePusher.isPushing()) {
                    mLivePusher.stopBGM();
                    mLivePusher.stopPusher();
                    mLivePusher.stopCameraPreview(true);
                }
            }
        } catch (Exception e) {
            L.e("---e----" + e.toString());
        }

    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    public void startBgm(String path) {
        if (mLivePusher != null) {
            boolean result = mLivePusher.playBGM(path);
            if (result) {
                mBgmPath = path;
            }
        }
    }

    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
        mBgmPath = null;
    }


    @Override
    public void release() {
        try {
            stopPush();
            if (mLivePusher != null) {
                mLivePusher.setVideoProcessListener(null);
                mLivePusher.setBGMNofify(null);
                mLivePusher.setPushListener(null);
            }
            mLivePusher = null;
            if (mLivePushConfig != null) {
                mLivePushConfig.setPauseImg(null);
            }
            mLivePushConfig = null;
        } catch (Exception e) {

        }
        super.release();
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        L.e(TAG, "--e--" + e);
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);
        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "网络断开，推流失败------>");
        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            L.e(TAG, "mStearm--->推流成功");
            if (mLivePushListener != null) {
                mLivePushListener.onPushStart();
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }


}
