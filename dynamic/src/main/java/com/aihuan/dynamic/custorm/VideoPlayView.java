package com.aihuan.dynamic.custorm;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.dynamic.R;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.dynamic.bean.DynamicBean;

/**
 * Created by cxf on 2018/9/5.
 */

public class VideoPlayView extends RelativeLayout {
    private static final String TAG = "VideoPlayView";
    private Context mContext;
    private TXCloudVideoView mVideoView;
    private TXVodPlayer mPlayer;
    private boolean mDestoryed;
    private boolean mPaused;
    boolean mStarted;
    boolean mFirstFrame;
    boolean mScrollPausePlay;//是否滚动暂停了播放
    boolean mPausePlay;//是否被动暂停了播放
    boolean mVoicePlayPause;//语音播放是暂停播放
    private PlayEventListener mPlayEventListener;
    private int[] mLocation;
    private DynamicBean mDynamicBean;
    private int mHight;
    private int mWidth;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mBack;//切出去
    private boolean mIsLarge;
    private int mScreenHeight;
    private int mScreenWidth;


    public VideoPlayView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLocation = new int[2];
        mWidth = DpUtil.dp2px(115);
        mHight = DpUtil.dp2px(200);
        mScreenHeight = ScreenDimenUtil.getInstance().getScreenHeight();
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
    }

    public int[] getLocation() {
        this.getLocationOnScreen(mLocation);
        return mLocation;
    }


    public int getVideoWidth() {
        return mVideoWidth;
    }

    public void setPlayEventListener(PlayEventListener playEventListener) {
        mPlayEventListener = playEventListener;
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_play, this, false);
        addView(view);
        mVideoView = (TXCloudVideoView) view.findViewById(R.id.player);
        mVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        mPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig config = new TXVodPlayConfig();
        config.setCacheFolderPath(mContext.getCacheDir().getAbsolutePath());
        config.setMaxCacheItems(10);
        mPlayer.setConfig(config);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int e, Bundle bundle) {
                switch (e) {
                    case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                        if (!mDestoryed) {
                            if (!mBack) {
                                if (mFirstFrame && mPlayEventListener != null) {
                                    mPlayEventListener.onStartPlay();
                                }

                            } else {
                                mPlayer.pause();
                                if (mPlayEventListener != null) {
                                    mPlayEventListener.onPausePlay();
                                }

                            }
                        } else {
                            doDestroy();
                        }
                        break;
                    case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                    case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                        ToastUtil.show(mContext.getString(R.string.mp4_error));
                        break;
                    case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                        break;
                    case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                        if (!mDestoryed) {
                            mFirstFrame = true;
                            if (mPlayEventListener != null) {
                                mPlayEventListener.onFirstFrame();
                            }
                            if (mPaused || mScrollPausePlay || mPausePlay || mBack) {
                                mPlayer.pause();
                                if (mPlayEventListener != null) {
                                    mPlayEventListener.onPausePlay();
                                }
                            }
                        } else {
                            doDestroy();
                        }
                        break;
                    case TXLiveConstants.PLAY_EVT_PLAY_END:
                        onReplay();
                        break;
                    case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                        mVideoWidth = bundle.getInt("EVT_PARAM1", 0);
                        mVideoHeight = bundle.getInt("EVT_PARAM2", 0);
                        if (!mDestoryed && mVideoWidth > 0 && mVideoHeight > 0) {
                            videoSizeChanged();
                        }
                        break;
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });
        LayoutParams params = (LayoutParams) mVideoView.getLayoutParams();
        params.width = DpUtil.dp2px(115);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        mVideoView.setLayoutParams(params);
    }

    /**
     * 让播放器静音
     */
    public void setMute(boolean mute) {
        if (mPlayer != null) {
            mPlayer.setMute(mute);
        }
    }


    public void setVideoSize(boolean isLarge) {
        if (mPlayEventListener != null) {
            if (isLarge) {
                float rate = ((float) mVideoHeight) / mVideoWidth;
                mPlayEventListener.onVideoSize(mScreenWidth,  (int) (mScreenWidth*rate), true);
            } else {
                float rate = ((float) mVideoWidth) / mVideoHeight;
                mPlayEventListener.onVideoSize((int) (mHight * rate), mHight, true);
            }
        }
    }

    public void resizeVideo(boolean large) {
        if (mLargePlayCallback != null) {
            mLargePlayCallback.largePlay(large);
        }
        mIsLarge = large;
        if (mIsLarge){
            setBackgroundResource(R.color.black);
        }else {
            setBackgroundResource(R.color.transparent);
        }
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            LayoutParams params = (LayoutParams) mVideoView.getLayoutParams();
            if (large) {
                float rate = ((float) mVideoHeight) / mVideoWidth;
                params.width = mScreenWidth;
                params.height = (int) (mScreenWidth*rate);

            } else {
                float rate = ((float) mVideoWidth) / mVideoHeight;
                params.width = (int) (mHight * rate);
                params.height = mHight;
            }
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            mVideoView.setLayoutParams(params);
        }
    }

    /**
     * 加载视频成功后调用
     */
    private void videoSizeChanged() {
        LayoutParams params = (LayoutParams) mVideoView.getLayoutParams();
        if (mIsLarge) {
            float rate = ((float) mVideoHeight) / mVideoWidth;
            params.width = mScreenWidth;
            params.height = (int) (mScreenWidth*rate);
        } else {
            params.height = mHight;
            float rate = ((float) mVideoWidth) / mVideoHeight;
            params.width = (int) (mHight * rate);
        }
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        mVideoView.setLayoutParams(params);
        if (mPlayEventListener != null) {
            mPlayEventListener.onVideoSize(params.width, mHight, false);
        }
    }

    public void play() {
        mFirstFrame = false;
        mPausePlay = false;
        mScrollPausePlay = false;
        mVoicePlayPause = false;
        mVideoWidth = 0;
        mVideoHeight = 0;
        if (!mDestoryed && mPlayer != null && mDynamicBean != null) {
            String url = mDynamicBean.getHref();
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (mStarted) {
                mPlayer.stopPlay(false);
            }
            mPlayer.startPlay(url);
            mStarted = true;
        }

    }


    public void setDynamicBean(DynamicBean dynamicBean) {
        mDynamicBean = dynamicBean;

    }

    public void onDetchWindow() {
        mFirstFrame = false;
        mPausePlay = false;
        mScrollPausePlay = false;
        mVoicePlayPause = false;
        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /**
     * 循环播放
     */
    private void onReplay() {
        if (mStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    public void stopPlay() {
        if (mStarted) {
            mStarted = false;
            mPlayer.stopPlay(false);
        }
    }

    public void destroy() {
        if (!mDestoryed) {
            doDestroy();
        }
    }

    private void doDestroy() {
        mDestoryed = true;
        if (mPlayer != null) {
            mPlayer.stopPlay(true);
        }
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
    }


    public interface PlayEventListener {

        void onStartPlay();

        void onFirstFrame();

        void onResumePlay();

        void onPausePlay();

        void onPausePlay2();

        void onVideoSize(int width, int height, boolean onlyImgChange);

    }


    /**
     * 取消切后台，返回前台
     */
    public void onResume() {

        if (!mPausePlay && !mScrollPausePlay && mPaused && !mDestoryed && mPlayer != null) {
            mPaused = false;
            mPlayer.resume();
        }
    }


    /**
     * 切后台
     */
    public void onPause() {
        if (!mPausePlay && !mScrollPausePlay && !mPaused && !mDestoryed && mPlayer != null) {
            mPaused = true;
            mPlayer.pause();
        }
        if (mScrollPausePlay) {
            if (mPlayEventListener != null) {
                mPlayEventListener.onPausePlay2();
            }
        }
    }

    public void stop() {
        if (mStarted && mPlayer != null) {
            mPlayer.stopPlay(false);
        }
    }

    /**
     * 滚动暂停播放
     */
    public void scrollPausePlay() {
        if (mFirstFrame && !mPausePlay && !mScrollPausePlay) {
            mScrollPausePlay = true;
            mPlayer.pause();
        }
    }


    /**
     * 滚动时恢复播放
     */
    public void scrollResumePlay() {
        if (mFirstFrame && mScrollPausePlay) {
            mScrollPausePlay = false;
            mPlayer.resume();
            if (mPlayEventListener != null) {
                mPlayEventListener.onResumePlay();
            }
        }
    }

    /**
     * 被动暂停播放
     */
    public void pausePlay() {
        //mFirstFrame &&
        if ( !mScrollPausePlay && !mPausePlay) {
            mPlayer.pause();
            if (mPlayEventListener != null) {
                mPlayEventListener.onPausePlay();
            }
            mPausePlay = true;
        }
    }

    /**
     * 被动恢复播放
     */
    public void resumePlay() {
        if (mFirstFrame && !mScrollPausePlay && mPausePlay) {
            mPausePlay = false;
            mPlayer.resume();
            if (mPlayEventListener != null) {
                mPlayEventListener.onResumePlay();
            }
        }
    }

    /*
     *播语音时，暂停视频
     */
    public void voicePlayPause() {

        //!mPausePlay &&   !mVoicePlayPause
        if (mFirstFrame && !mScrollPausePlay ) {
            mVoicePlayPause = true;
            mPlayer.pause();
        }
    }

    /**
     * 语音暂停恢复视频播放
     */
    public void voicePauseResume() {
        if (mFirstFrame && !mScrollPausePlay && !mPausePlay && mVoicePlayPause) {
            mVoicePlayPause = false;
            mPlayer.resume();
        }
    }


    public void forceResumePlay() {
        if (mFirstFrame) {
            mPausePlay = false;
            mScrollPausePlay = false;
            mPlayer.resume();
            if (mPlayEventListener != null) {
                mPlayEventListener.onResumePlay();
            }
        }
    }


    public void setBack(boolean back) {
        mBack = back;
    }


    public interface LargePlayCallback {
        void largePlay(boolean large);
    }

    public LargePlayCallback mLargePlayCallback;

    public void setLargePlayCallback(LargePlayCallback largePlayCallback) {
        mLargePlayCallback = largePlayCallback;
    }
}
