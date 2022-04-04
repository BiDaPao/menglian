package com.aihuan.dynamic.custorm;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamic.R;
import com.aihuan.common.utils.L;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;

/**
 * Created by debug on 2019/7/26.
 */

public class VoicePlayView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private int mVoiceSumTime;
    private int mVoicePlayTime;
    private String mPlayUrl;
    private boolean mIsPlaying;
    private TextView mTvVoiceTime;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private AnimationDrawable mAnimationDrawable;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mVoicePlayTime--;
            if (mVoicePlayTime > 0) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
                mTvVoiceTime.setText(mVoicePlayTime + "s");
            } else {
                if (mVoicePlayTime == 0) {
                    mVoicePlayTime = mVoiceSumTime;
                }
            }
        }
    };
    private VoicePlayCallBack mVoicePlayCallBack;

    public interface VoicePlayCallBack {
        void play(VoicePlayView playView);
    }

    public VoicePlayView(@NonNull Context context) {
        this(context, null);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VoicePlayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_voice_group, this, false);
        addView(view);
        mTvVoiceTime = view.findViewById(R.id.voice_time);
        ImageView imageView = view.findViewById(R.id.voice_play);
        mAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
        imageView.setOnClickListener(this);
        L.e("---onFinishInflate-----");
    }

    @Override
    public void onClick(View v) {
        onClickVoice();
    }

    public void setVoiceInfo(int sumTime, String playUrl) {
        mVoiceSumTime = sumTime;
        mVoicePlayTime = mVoiceSumTime;
        mPlayUrl = playUrl;
        mTvVoiceTime.setText(mVoiceSumTime + "s");
    }

    public void setVoiceMediaPlayerUtil(VoiceMediaPlayerUtil voiceMediaPlayerUtil) {
        mVoiceMediaPlayerUtil = voiceMediaPlayerUtil;
    }

    public void setVoicePlayCallBack(VoicePlayCallBack voicePlayCallBack) {
        mVoicePlayCallBack = voicePlayCallBack;
    }

    private void onClickVoice() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mIsPlaying) {
            mIsPlaying = false;
            mVoiceMediaPlayerUtil.pausePlay();
            if (mAnimationDrawable != null) {
                mAnimationDrawable.stop();
            }
        } else {
            mIsPlaying = true;
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    mVoicePlayTime=mVoiceSumTime;
                    mTvVoiceTime.setText(mVoicePlayTime + "s");
                    mIsPlaying = false;
                    if (mHandler != null) {
                        mHandler.removeMessages(0);
                    }
                    if (mAnimationDrawable != null) {
                        mAnimationDrawable.stop();
                    }
                }
            });
            mAnimationDrawable.start();
            if (mVoiceMediaPlayerUtil.isPaused()) {
                mVoiceMediaPlayerUtil.resumePlay();
            } else {
                mVoiceMediaPlayerUtil.startPlay(mPlayUrl);
            }
            if (mVoicePlayCallBack != null) {
                mVoicePlayCallBack.play(this);
            }
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void resume(){
        if (!mIsPlaying&&mVoiceMediaPlayerUtil!=null){
            if (mVoiceMediaPlayerUtil.isPaused()){
                mVoiceMediaPlayerUtil.resumePlay();
                mIsPlaying=true;
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.start();
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        }

    }

    public void pause(){
        if (mIsPlaying){
            mVoiceMediaPlayerUtil.pausePlay();
            mIsPlaying=false;
        }
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    public void resetView() {
        mIsPlaying=false;
        mVoicePlayTime = mVoiceSumTime;
        mTvVoiceTime.setText(mVoiceSumTime + "s");
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }

    public void release() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }


    }
}
