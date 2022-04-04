package com.aihuan.main.views;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.aihuan.common.utils.L;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.main.R;

/**
 * Created by cxf on 2019/5/10.
 */

public class UserHomeWallVideoViewHolder extends AbsViewHolder implements ITXVodPlayListener {

    private TXCloudVideoView mTXCloudVideoView;
    private TXVodPlayer mPlayer;
    private String mCachePath;
    private TXVodPlayConfig mTXVodPlayConfig;
    private boolean mPaused;//生命周期暂停
    private ActionListener mActionListener;
    private View mLoading;
    private boolean mPassivePaused;//被动暂停

    public UserHomeWallVideoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_wall_video;
    }

    @Override
    public void init() {
        mCachePath = mContext.getCacheDir().getAbsolutePath();
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer = new TXVodPlayer(mContext);
        mTXVodPlayConfig = new TXVodPlayConfig();
        mTXVodPlayConfig.setMaxCacheItems(15);
        mPlayer.setConfig(mTXVodPlayConfig);
        mPlayer.setAutoPlay(true);
        mPlayer.setVodListener(this);
        mPlayer.setPlayerView(mTXCloudVideoView);
        mLoading = findViewById(R.id.loading);
    }

    /**
     * 播放器事件回调
     */
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://加载完成，开始播放的回调
                if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING: //开始加载的回调
                if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://获取到视频播放完毕的回调
                replay();
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://获取到视频首帧回调
                if (mActionListener != null) {
                    mActionListener.onFirstFrame();
                }
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取到视频宽高回调
                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
                break;
        }
    }


    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    /**
     * 开始播放
     */
    public void startPlay(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            return;
        }
        if (mTXVodPlayConfig == null) {
            mTXVodPlayConfig = new TXVodPlayConfig();
            mTXVodPlayConfig.setMaxCacheItems(15);
        }
        if (videoUrl.endsWith(".m3u8")) {
            mTXVodPlayConfig.setCacheFolderPath(null);
        } else {
            mTXVodPlayConfig.setCacheFolderPath(mCachePath);
        }
        mPlayer.setConfig(mTXVodPlayConfig);
        if (mPlayer != null) {
            mPlayer.startPlay(videoUrl);
        }
    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }


    /**
     * 获取到视频宽高回调
     */
    private void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
            int targetH = 0;
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
            } else {
                targetH = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (targetH != params.height) {
                params.height = targetH;
                params.gravity = Gravity.CENTER;
                mTXCloudVideoView.requestLayout();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (!mPassivePaused && mPlayer != null) {
            mPlayer.pause();
        }
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPaused) {
            if (!mPassivePaused && mPlayer != null) {
                mPlayer.resume();
            }
        }
        mPaused = false;
    }


    /**
     * 被动暂停播放
     */
    public void passivePause() {
        if(mPassivePaused){
            return;
        }
        if (mPlayer != null) {
            mPlayer.pause();
        }
        mPassivePaused = true;
    }


    /**
     * 被动恢复播放
     */
    public void passiveResume() {
        if (mPassivePaused) {
            if (mPlayer != null) {
                mPlayer.resume();
            }
        }
        mPassivePaused = false;
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
        mActionListener = null;
        super.onDestroy();
        L.e("UserHomeWallVideoViewHolder", "------->onDestroy");
    }


    public interface ActionListener {
        void onFirstFrame();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
