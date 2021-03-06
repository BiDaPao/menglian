package com.aihuan.dynamic.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DateFormatUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.MediaRecordUtil;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.dynamic.activity.AbsDynamicCommentActivity;
import com.aihuan.dynamic.activity.DynamicDetailsActivity;
import com.aihuan.dynamic.bean.DynamicCommentBean;
import com.aihuan.dynamic.event.DynamicCommentEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.video.upload.FileUploadBean;
import com.aihuan.video.upload.FileUploadCallback;
import com.aihuan.video.upload.FileUploadQnImpl;
import com.aihuan.video.upload.FileUploadStrategy;
import com.aihuan.video.upload.FileUploadTxImpl;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/7/3.
 */

public class DynamicCommentVoiceViewHolder extends AbsViewHolder implements View.OnClickListener {

    private static final String TAG = "DynamicCommentVoiceViewHolder";
    private View mRecordTip;
    private TextView mBtnRecord;
    private String mPress;
    private String mPressString;
    private String mUnPress;
    private int mCancelHeight;
    private MediaRecordUtil mMediaRecordUtil;
    private File mRecordVoiceFile;//????????????
    private long mRecordVoiceDuration;//????????????
    private Handler mHandler;
    private boolean mIsRecording;
    private FileUploadStrategy mUploadStrategy;
    private List<FileUploadBean> mFileUpLoadList;
    private String mDynamicId;
    private String mDynamicUid;
    private DynamicCommentBean mDynamicCommentBean;
    private Dialog mLoading;
    private boolean mIsUploading;


    public DynamicCommentVoiceViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_dynamic_voice;
    }

    @Override
    public void init() {
        mCancelHeight = ScreenDimenUtil.getInstance().getScreenHeight() - DpUtil.dp2px(41);
        mPressString = WordUtil.getString(R.string.im_press_say);
        mUnPress = WordUtil.getString(R.string.im_unpress_stop);
        findViewById(R.id.btn_hide).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mRecordTip = findViewById(R.id.record_tip);
        mBtnRecord = (TextView) findViewById(R.id.btn_record);
        mBtnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordStart();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (e.getRawY() < mCancelHeight) {
                            if (mIsRecording) {
                                recordCancel();
                            }
                        } else {
                            if (mIsRecording&&!mIsUploading) {
                                recordEnd();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void addToParent() {
        super.addToParent();
        if (mBtnRecord != null) {
            mBtnRecord.setText(mPress);
        }
        if (mRecordTip != null && mRecordTip.getVisibility() == View.VISIBLE) {
            mRecordTip.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_hide) {
            removeFromParent();
        } else if (i == R.id.btn_face) {
            ((DynamicDetailsActivity) mContext).openFace();
        }
    }

    /**
     * ????????????
     */
    private void recordStart() {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        if (mBtnRecord != null) {
            mBtnRecord.setText(mUnPress);
        }
        if (mRecordTip != null && mRecordTip.getVisibility() != View.VISIBLE) {
            mRecordTip.setVisibility(View.VISIBLE);
        }
        if (mContext instanceof AbsDynamicCommentActivity) {
            ((AbsDynamicCommentActivity) mContext).setMute(true);
        }
        if (mContext instanceof DynamicDetailsActivity) {
            ((DynamicDetailsActivity) mContext).pauseDynamicVoice();
        }
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH + "comment");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (mIsRecording) {
                        try {
                            if (mLoading != null) {
                                mLoading.show();
                            }
                        }catch (Exception e){
                        }
                        recordEnd();
                    }
                }
            };
        }
        mHandler.sendEmptyMessageDelayed(0, 60000);
        mIsRecording = true;

    }

    /**
     * ????????????
     */
    private void recordEnd() {
        L.e(TAG, "????????????----------->");
        if (mBtnRecord != null) {
            mBtnRecord.setText(mPress);
        }
        if (mRecordTip != null && mRecordTip.getVisibility() == View.VISIBLE) {
            mRecordTip.setVisibility(View.INVISIBLE);
        }
        mRecordVoiceDuration = mMediaRecordUtil.stopRecord();
        if (mRecordVoiceDuration < 2000) {
            ToastUtil.show(WordUtil.getString(R.string.im_record_audio_too_short));
            deleteVoiceFile();
            mIsRecording = false;
            mIsUploading = false;
        } else {
            if (mRecordVoiceFile != null && mRecordVoiceFile.length() > 0) {
                L.e(TAG, "????????????----------->");
                if (mLoading != null) {
                    mLoading.show();
                }
                mIsUploading=true;
                uploadVoiceFile();
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mContext instanceof AbsDynamicCommentActivity) {
            ((AbsDynamicCommentActivity) mContext).setMute(false);
        }
        if (mContext instanceof DynamicDetailsActivity) {
            ((DynamicDetailsActivity) mContext).resumeDynamicVoice();
        }
    }

    /**
     * ????????????
     */
    private void recordCancel() {
        L.e(TAG, "????????????----------->");
        if (mBtnRecord != null) {
            mBtnRecord.setText(mPress);
        }
        if (mRecordTip != null && mRecordTip.getVisibility() == View.VISIBLE) {
            mRecordTip.setVisibility(View.INVISIBLE);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mMediaRecordUtil.stopRecord();
        deleteVoiceFile();
        ToastUtil.show(R.string.video_comment_voice_tip_1);
        mIsRecording = false;
        if (mContext instanceof AbsDynamicCommentActivity) {
            ((AbsDynamicCommentActivity) mContext).setMute(false);
        }
        if (mContext instanceof DynamicDetailsActivity) {
            ((DynamicDetailsActivity) mContext).resumeDynamicVoice();
        }
    }

    /**
     * ??????????????????
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
    }

    public void release() {
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_SET_COMMENTS);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMediaRecordUtil != null) {
            mMediaRecordUtil.release();
        }
        mMediaRecordUtil = null;
        if (mUploadStrategy != null) {
            mUploadStrategy.cancel();
        }
        mUploadStrategy = null;
    }


    /**
     * ??????????????????
     */
    private void uploadVoiceFile() {
        if (mUploadStrategy == null) {
            ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
            //if (configBean.getVideoCloudType() == 1) {
            mUploadStrategy = new FileUploadQnImpl(configBean);
            //} else {
            // mUploadStrategy = new FileUploadTxImpl(configBean);
            //}
        }
        FileUploadBean bean = new FileUploadBean(mRecordVoiceFile);
        if (mFileUpLoadList == null) {
            mFileUpLoadList = new ArrayList<>();
        }
        mFileUpLoadList.clear();
        mFileUpLoadList.add(bean);
        mUploadStrategy.upload(mFileUpLoadList, new FileUploadCallback() {
            @Override
            public void onSuccess(List<FileUploadBean> resultUrlList) {
                String voiceLink = resultUrlList.get(0).getRemoteAccessUrl();
                sendComment(voiceLink);
            }

            @Override
            public void onFailure() {
                if (mLoading != null) {
                    mLoading.dismiss();
                }
                mIsRecording = false;
                mIsUploading = false;
            }
        });
    }


    /**
     * ????????????
     */
    public void sendComment(String voiceLink) {
        if (TextUtils.isEmpty(mDynamicId) || TextUtils.isEmpty(mDynamicUid) || mRecordVoiceDuration == 0) {
            return;
        }
        String toUid = mDynamicUid;
        String commentId = "0";
        String parentId = "0";
        if (mDynamicCommentBean != null) {
            toUid = mDynamicCommentBean.getUid();
            commentId = mDynamicCommentBean.getCommentId();
            parentId = mDynamicCommentBean.getId();
        }
        DynamicHttpUtil.setDynamicComment(toUid, mDynamicId, commentId, parentId, voiceLink, (int) (mRecordVoiceDuration / 1000), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String commentNum = obj.getString("comments");
                    EventBus.getDefault().post(new DynamicCommentEvent(mDynamicId, commentNum));
                    ToastUtil.show(msg);
                    removeFromParent();
                    ((AbsDynamicCommentActivity) mContext).refreshComment();
                }
                if (mLoading != null) {
                    mLoading.dismiss();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mLoading != null) {
                    mLoading.dismiss();
                }
                mIsRecording = false;
                mIsUploading = false;
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }

    public void setVideoId(String videoId) {
        mDynamicId = videoId;
    }

    public void setVideoUid(String videoUid) {
        mDynamicUid = videoUid;
    }

    public void setVideoCommentBean(DynamicCommentBean videoCommentBean) {
        mDynamicCommentBean = videoCommentBean;
        mPress = mPressString;
        if (videoCommentBean != null) {
            UserBean replyUserBean = videoCommentBean.getUserBean();//???????????????
            if (replyUserBean != null) {
                mPress = WordUtil.getString(R.string.video_comment_reply_2) + replyUserBean.getUserNiceName();
            }
        }
    }
}
