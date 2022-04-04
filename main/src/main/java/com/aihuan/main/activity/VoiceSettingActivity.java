package com.aihuan.main.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadStrategy;
import com.aihuan.common.upload.UploadVoiceImpl;
import com.aihuan.common.utils.DateFormatUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.utils.MediaRecordUtil;
import com.aihuan.main.R;
import com.aihuan.common.utils.MediaManager;
import com.alibaba.android.arouter.utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saint  2022/2/14 13:40
 * @DESC: 个人主页声音展示设置
 */
public class VoiceSettingActivity extends AbsActivity {

    private TextView tvDelete;
    private TextView btnRecord;
    private View layoutVoice;
    private TextView btnSubmit;
    private ImageView ivVoice;
    private AnimationDrawable animationDrawable;


    private TextView tvVoiceRecord;
    private TextView tvDuration;
    private String mPressSayString;
    private String mUnPressStopString;
    private MediaRecordUtil mMediaRecordUtil;
    private File mRecordVoiceFile;//录音文件
    private int mRecordVoiceDuration;//录音时长
    private Handler mHandler;

    //音频文件URL
    private String voiceUrl;

    private UploadStrategy mUploadStrategy;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice_setting;
    }

    @Override
    protected void main() {
        voiceUrl = getIntent().getStringExtra("voice_url");
        mRecordVoiceDuration = getIntent().getIntExtra("voice_duration", 0);
        setTitle(WordUtil.getString(R.string.voice_setting));

        mHandler = new Handler();
        btnSubmit = findViewById(R.id.btn_submit);
        tvDelete = findViewById(R.id.tv_delete);
        btnRecord = findViewById(R.id.btn_record);
        tvDuration = findViewById(R.id.tv_duration);
        layoutVoice = findViewById(R.id.layout_voice);
        ivVoice = findViewById(R.id.iv_voice);
        tvVoiceRecord = findViewById(R.id.tv_voice_record);

        if (mRecordVoiceDuration > 0) {
            tvDuration.setText(WordUtil.getString(R.string.voice_duraion, (int) mRecordVoiceDuration));
        }

        mPressSayString = WordUtil.getString(com.aihuan.im.R.string.im_press_say);
        mUnPressStopString = WordUtil.getString(com.aihuan.im.R.string.im_unpress_stop);
        tvVoiceRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecordVoice();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopRecordVoice();
                        break;
                }
                return true;
            }
        });

        layoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVoice();
            }
        });

        if (voiceUrl != null && !voiceUrl.isEmpty()) {
            btnRecord.setText(R.string.record_voice_agin);
        } else {
            btnRecord.setText(R.string.click_record_voice);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                tvVoiceRecord.setVisibility(View.VISIBLE);
            }
        });
    }

    private void playVoice() {
        if (mRecordVoiceFile != null) {
            MediaManager.getInstance().playSound(mRecordVoiceFile.getAbsolutePath(), new MediaManager.Callback() {
                @Override
                public void onCompletion(Boolean success) {

                    if (!success) {
                        ToastUtil.show("播放失败");
                    }
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                        ivVoice.setImageResource(R.mipmap.icon_voice_left_3);
                    }
                }
            });
            ivVoice.setImageResource(R.drawable.play_voice_message);
            animationDrawable = (AnimationDrawable) ivVoice.getDrawable();
            animationDrawable.start();
        } else if (!TextUtils.isEmpty(voiceUrl)) {
            MediaManager.getInstance().playSound(voiceUrl, new MediaManager.Callback() {
                @Override
                public void onCompletion(Boolean success) {

                    if (!success) {
                        ToastUtil.show("播放失败");
                    }
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                        ivVoice.setImageResource(R.mipmap.icon_voice_left_3);
                    }
                }
            });
            ivVoice.setImageResource(R.drawable.play_voice_message);
            animationDrawable = (AnimationDrawable) ivVoice.getDrawable();
            animationDrawable.start();
        } else {
            ToastUtil.show("请先录制语音");
        }
    }


    /**
     * 开始录音
     */
    public void startRecordVoice() {
        if (tvVoiceRecord == null) {
            return;
        }
        tvVoiceRecord.setText(mUnPressStopString);
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecordVoice();
                }
            }, 60000);
        }
    }

    /**
     * 结束录音
     */
    private void stopRecordVoice() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (tvVoiceRecord == null) {
            return;
        }
        tvVoiceRecord.setText(mPressSayString);
        mRecordVoiceDuration = (int) (mMediaRecordUtil.stopRecord() / 1000);
        L.e("语音录制时长：" + mRecordVoiceDuration);
        if (mRecordVoiceDuration < 2) {
            ToastUtil.show(WordUtil.getString(com.aihuan.im.R.string.im_record_audio_too_short));
            deleteVoiceFile();
            return;
        }
        btnRecord.setText(R.string.record_voice_agin);
        tvDuration.setText(WordUtil.getString(R.string.voice_duraion, (int) mRecordVoiceDuration));
    }

    /**
     * 删除录音文件
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
    }

    private void delete() {
        deleteVoiceFile();
        if (!TextUtils.isEmpty(voiceUrl)) {
            voiceUrl = "";
        }
        Intent intent = new Intent();
        intent.putExtra("voice_file", "");
        setResult(RESULT_OK, intent);
    }

    private void submit() {
        if (mRecordVoiceFile == null || mRecordVoiceDuration < 2) {
            ToastUtil.show("请先录制语音！");
            return;
        }
        UploadBean uploadBean = new UploadBean();
        uploadBean.setOriginFile(mRecordVoiceFile);
        L.e("上传文件开始--------->" + mRecordVoiceDuration);
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadVoiceImpl(mContext);
        }

        ArrayList<UploadBean> mUploadList = new ArrayList<>();

        mUploadList.add(uploadBean);
        mUploadStrategy.upload(mUploadList, false, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    L.e("上传文件完成---------> ");
                    if (list != null && list.size() > 0) {
                        voiceUrl = list.get(0).getRemoteFileName();
                        Intent intent = new Intent();
                        intent.putExtra("voice_url", voiceUrl);
                        intent.putExtra("voice_duration", mRecordVoiceDuration);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

}
