package com.aihuan.dynamic.dialog;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dynamic.R;
import com.aihuan.common.Constants;
import com.aihuan.common.custom.DrawableTextView;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.utils.AudioRecorderEx;
import com.aihuan.common.utils.FilePathUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.DynamicPublishActivity;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;

import java.io.File;

/**
 * Created by debug on 2019/7/23.
 * 动态语音录制
 */

public class VoiceRecordDialogFragment extends AbsDialogFragment implements View.OnClickListener {
    private static final int MAX_RECORD_TIME = 60;
    private static final int VOICE_PLAYING = 1;
    private static final int VOICE_RECORD = 2;
    private View mBtnConfirm;
    private DrawableTextView mBtnPlay;
    private View mLlPlay;
    private TextView mTvRecordTime;
    private ProgressBar mProgressBar;
    private ImageView mIvRecord;
    private TextView mTvRecord;
    private View mRlRecord;
    private View mBtnDel;
    private boolean mIsRecording;//是否在录制
    private boolean mIsPlaying;//是否在试听
    private AudioRecorderEx mAudioRecorderEx;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private File mVoiceFile;
    private int mCurTime;
    private int mCurPlayTime;//试听时间
    private String mRecordStr;
    private String mPauseStr;
    private Drawable mDrawable;
    private Drawable mDrawable2;
    private AnimationDrawable mAnimationDrawable;
    private boolean mIsNeedMerge;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==VOICE_RECORD){
                mCurTime++;
                mProgressBar.setProgress(mCurTime);
                mTvRecordTime.setText(mCurTime + "s");
                if (mCurTime == MAX_RECORD_TIME) {
                    if (mLlPlay != null) {
                        mLlPlay.setVisibility(View.VISIBLE);
                    }
                    if (mIvRecord != null) {
                        mIvRecord.setVisibility(View.INVISIBLE);
                    }
                    if (mTvRecord != null) {
                        mTvRecord.setVisibility(View.INVISIBLE);
                    }
                    if (mBtnDel != null) {
                        mBtnDel.setVisibility(View.VISIBLE);
                    }
                    if (mBtnConfirm!=null){
                        mBtnConfirm.setVisibility(View.VISIBLE);
                    }
                    recordAndPause();
                } else {
                    mHandler.sendEmptyMessageDelayed(VOICE_RECORD, 1000);
                }
            }else if (msg.what==VOICE_PLAYING){
                mCurPlayTime++;
                if (mCurPlayTime>mCurTime){
                    mCurPlayTime=mCurTime;
                }
                mProgressBar.setProgress(mCurPlayTime);
                mHandler.sendEmptyMessageDelayed(VOICE_PLAYING, 1000);
            }

        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_voice_record;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnPlay = (DrawableTextView) findViewById(R.id.btn_play);
        mLlPlay = findViewById(R.id.ll_play);
        mTvRecordTime = (TextView) findViewById(R.id.time);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mIvRecord = (ImageView) findViewById(R.id.btn_record);
        mTvRecord = (TextView) findViewById(R.id.record_tip);
        mRlRecord = findViewById(R.id.rl_record);
        mBtnDel = findViewById(R.id.btn_del);
        mBtnConfirm.setOnClickListener(this);
        //mBtnPlay.setOnClickListener(this);
        mIvRecord.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        findViewById(R.id.ll_play).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        mProgressBar.setMax(MAX_RECORD_TIME);
        mRecordStr = WordUtil.getString(R.string.click_record);
        mPauseStr = WordUtil.getString(R.string.click_pause);
        mDrawable = mContext.getDrawable(R.mipmap.voice_play);
        mDrawable2 = mContext.getDrawable(R.mipmap.voice_pause);
        mAudioRecorderEx = AudioRecorderEx.getInstance(mContext);
        mAnimationDrawable= (AnimationDrawable) mIvRecord.getDrawable();
    }

    private void recordAndPause() {
        mCurPlayTime=0;
        if (!mIsRecording) {
            mAudioRecorderEx.setOutputFile(FilePathUtil.makeFilePath(mContext, AudioRecorderEx.AUDIO_DIR_PATH, System.currentTimeMillis() + AudioRecorderEx.AUDIO_SUFFIX_MP3));
            mAudioRecorderEx.prepare();
            mAudioRecorderEx.start();
            mIsRecording = true;
            mIsNeedMerge = true;
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(VOICE_RECORD, 1000);
            }
            if (mIvRecord != null) {
                mAnimationDrawable.start();
                mTvRecord.setText(mPauseStr);
            }
            if (mLlPlay != null) {
                mLlPlay.setVisibility(View.INVISIBLE);
            }
            if (mBtnDel != null) {
                mBtnDel.setVisibility(View.INVISIBLE);
            }
            if (mBtnConfirm!=null){
                mBtnConfirm.setVisibility(View.GONE);
            }
        } else {
            if (mCurTime< Constants.DYNAMIC_VOICE_MIN_TIME){
                ToastUtil.show(WordUtil.getString(R.string.im_record_audio_too_short));
                return;
            }
            if (mHandler != null) {
                mHandler.removeMessages(VOICE_RECORD);
            }
            mAudioRecorderEx.stop();
            mAudioRecorderEx.reset();
            mAnimationDrawable.stop();
            mTvRecord.setText(mRecordStr);
            if (mLlPlay != null) {
                mLlPlay.setVisibility(View.VISIBLE);
            }
            if (mBtnDel != null) {
                mBtnDel.setVisibility(View.VISIBLE);
            }
            if (mBtnConfirm!=null){
                mBtnConfirm.setVisibility(View.VISIBLE);
            }
            mIsRecording = false;
        }
    }

    private void playVoice() {
        if (mIsPlaying) {
            mIsPlaying = false;
            if (mRlRecord != null) {
                mRlRecord.setVisibility(View.VISIBLE);
            }
            if (mBtnDel != null) {
                mBtnDel.setVisibility(View.VISIBLE);
            }
            if (mVoiceMediaPlayerUtil != null) {
                mVoiceMediaPlayerUtil.pausePlay();
            }
            mBtnPlay.setLeftDrawable(mDrawable);
            mBtnPlay.setText(WordUtil.getString(R.string.voice_audition));
            mHandler.removeMessages(VOICE_PLAYING);
        } else {
            if (mIsNeedMerge){
                mVoiceFile = mAudioRecorderEx.mergeAudioFile();
                mIsNeedMerge=false;
            }
            mBtnPlay.setLeftDrawable(mDrawable2);
            mBtnPlay.setText(WordUtil.getString(R.string.voice_pause));
            if (mRlRecord != null) {
                mRlRecord.setVisibility(View.INVISIBLE);
            }
            if (mBtnDel != null) {
                mBtnDel.setVisibility(View.INVISIBLE);
            }
            if (mVoiceMediaPlayerUtil == null) {
                mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
                mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                    @Override
                    public void onPlayEnd() {
                        mIsPlaying = false;
                        if (mRlRecord != null) {
                            mRlRecord.setVisibility(View.VISIBLE);
                        }
                        if (mBtnDel != null) {
                            mBtnDel.setVisibility(View.VISIBLE);
                        }
                        mBtnPlay.setLeftDrawable(mDrawable);
                        mBtnPlay.setText(WordUtil.getString(R.string.voice_audition));
                        mHandler.removeMessages(VOICE_PLAYING);
                        mCurPlayTime=0;
                    }
                });
            }
            mIsPlaying = true;
            if (mVoiceMediaPlayerUtil.isPaused()) {
                mVoiceMediaPlayerUtil.resumePlay();
            } else {
                mVoiceMediaPlayerUtil.startPlay(mVoiceFile.getAbsolutePath());
            }
            mHandler.sendEmptyMessageDelayed(VOICE_PLAYING, 1000);
        }
    }

    private void confirm(){
        if (mIsNeedMerge){
            mVoiceFile = mAudioRecorderEx.mergeAudioFile();
            mIsNeedMerge=false;
        }
        ((DynamicPublishActivity)mContext).addVoiceInfo(mVoiceFile,mCurTime);
        dismiss();
    }

    private void del() {
        mCurTime=0;
        mAudioRecorderEx.reset();
        mAudioRecorderEx.clearData();
        mAudioRecorderEx.deleteMixRecorderFile(mVoiceFile);
        mProgressBar.setProgress(0);
        mAnimationDrawable.stop();
        mTvRecord.setText(mRecordStr);
        mTvRecordTime.setText(mCurTime + "s");
        if (mLlPlay!=null){
            mLlPlay.setVisibility(View.INVISIBLE);
        }
        if (mBtnConfirm!=null){
            mBtnConfirm.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            confirm();
        } else if (i == R.id.ll_play) {
            playVoice();
        } else if (i == R.id.btn_record) {
            recordAndPause();
        } else if (i == R.id.btn_del) {
            del();

        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler=null;
    }
}
