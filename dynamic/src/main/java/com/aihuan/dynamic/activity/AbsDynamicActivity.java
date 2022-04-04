package com.aihuan.dynamic.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DownloadUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.custorm.VideoPlayView;
import com.aihuan.dynamic.dialog.DynamicLookImgDialogFragment;
import com.aihuan.dynamic.event.DynamicDelEvent;
import com.aihuan.dynamic.event.DynamicLikeEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.dynamic.inter.VoicePlayCallBack;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by debug on 2019/7/24.
 */

public abstract class AbsDynamicActivity extends AbsActivity {
    //视频
    protected ViewGroup mCurContainer;
    protected FrameLayout mOriginContainer;
    protected VideoPlayView mPlayView;
    //列表语言
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    protected DownloadUtil mDownloadUtil;
    protected VoicePlayCallBack mVoicePlayCallBack;
    private Handler mHandler;
    protected int mSumVoiceTime;
    protected int mPlayVoiceTime;
    protected TextView mTvVoiceTime;

    @Override
    protected void main() {
        super.main();
        mCurContainer = findViewById(R.id.play_container);
//        mCurContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCurContainer.setClickable(false);
//                if (mPlayView != null) {
//                    mPlayView.pausePlay();
//                }
//                if (mCurContainer.getChildCount() > 0) {
//                    View playGroup = mCurContainer.getChildAt(0);
//                    mCurContainer.removeView(playGroup);
//                    if (mOriginContainer != null) {
//                        mOriginContainer.addView(playGroup);
//                        if (mPlayView != null) {
//                            mPlayView.resizeVideo(false);
//                            mPlayView.setVideoSize(false);
//                            mPlayView.resumePlay();
//                        }
//                    }
//                }
//            }
//        });
//        mCurContainer.setClickable(false);
    }

    //点赞
    public void addLike(final DynamicBean dynamicBean) {
        if (dynamicBean.getUid().equals(CommonAppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.video_comment_cannot_self));
            return;
        }
        DynamicHttpUtil.addLike(dynamicBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    EventBus.getDefault().post(new DynamicLikeEvent(dynamicBean.getId(), obj.getIntValue("islike"), obj.getString("nums")));
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    //删除动态
    public void delDynamic(final DynamicBean dynamicBean) {
        DynamicHttpUtil.delDynamic(dynamicBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new DynamicDelEvent(dynamicBean.getId()));
                }
                ToastUtil.show(msg);
            }
        });
    }

    //设置 举报和删除
    public void setting(final DynamicBean dynamicBean) {
        Integer[] integers;
        if (dynamicBean.getUid().equals(CommonAppConfig.getInstance().getUid())) {
            integers = new Integer[]{R.string.del_dynamic_tip};
        } else {
            integers = new Integer[]{R.string.report};
        }
        DialogUitl.showStringArrayDialog(mContext, integers, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.report) {
                    DynamicReportActivity.forward(mContext, dynamicBean.getId());
                } else if (tag == R.string.del_dynamic_tip) {
                    DialogUitl.showSimpleDialog3(mContext, WordUtil.getString(R.string.del_dynamic_tip2), "", WordUtil.getString(R.string.delete), new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            delDynamic(dynamicBean);
                        }
                    });
                }
            }
        });
    }

    //查看图片
    public void lookImgs(ArrayList<String> list, int selectPos) {
        DynamicLookImgDialogFragment dynamicImgsDialogFragment = new DynamicLookImgDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.DYNAMIC_IMG_LIST, list);
        bundle.putInt(Constants.DYNAMIC_IMG_CUR_POS, selectPos);
        bundle.putString(Constants.DYNAMIC_UID, "");
        dynamicImgsDialogFragment.setArguments(bundle);
        dynamicImgsDialogFragment.show(getSupportFragmentManager(), "LookDynamicImgsDialogFragment");
    }

    //查看图片
    public void lookImgs(ArrayList<String> list, int selectPos, CommonCallback<Boolean> commonCallback) {
        DynamicLookImgDialogFragment dynamicImgsDialogFragment = new DynamicLookImgDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.DYNAMIC_IMG_LIST, list);
        bundle.putInt(Constants.DYNAMIC_IMG_CUR_POS, selectPos);
        bundle.putString(Constants.DYNAMIC_UID, "");
        dynamicImgsDialogFragment.setArguments(bundle);
        dynamicImgsDialogFragment.setCommonCallback(commonCallback);
        dynamicImgsDialogFragment.show(getSupportFragmentManager(), "LookDynamicImgsDialogFragment");
    }

    //视频放大播
    public void addPlayerGroup(FrameLayout container, View videoGroup, VideoPlayView playView) {
        mOriginContainer = container;
        mPlayView = playView;
        ViewParent parent = videoGroup.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(videoGroup);
        }
        playView.setVideoSize(true);
        mCurContainer.addView(videoGroup);
        mCurContainer.setClickable(true);
        playView.resizeVideo(true);
        playView.forceResumePlay();
    }

    //返回时是否要先返回大视频
    public boolean isBackVideo() {
        return mCurContainer.getChildCount() > 0;
    }

    public void videoBack() {
        if (mCurContainer.getChildCount() > 0) {
            mCurContainer.setClickable(false);
            if (mPlayView != null) {
                mPlayView.pausePlay();
            }
            View playGroup = mCurContainer.getChildAt(0);
            mCurContainer.removeView(playGroup);
            if (mOriginContainer != null) {
                mOriginContainer.addView(playGroup);
                if (mPlayView != null) {
                    mPlayView.setVideoSize(false);
                    mPlayView.resizeVideo(false);
                    mPlayView.resumePlay();
                }
            }
        }
    }


    //列表播语音时的回调
    public void setVoicePlayCallBack(VoicePlayCallBack voicePlayCallBack) {
        mVoicePlayCallBack = voicePlayCallBack;
    }

    /**
     * 语音
     *
     * @param length      时长
     * @param tvVoiceTime 显示时长的
     */
    public void setVoiceInfo(int length, TextView tvVoiceTime) {
        mSumVoiceTime = length;
        mPlayVoiceTime = length;
        mTvVoiceTime = tvVoiceTime;
    }

    /**
     * 播放语音
     */
    public void playVoice(String voiceLink) {
        if (TextUtils.isEmpty(voiceLink)) {
            return;
        }
        String fileName = MD5Util.getMD5(voiceLink);
        String path = CommonAppConfig.VOICE_PATH + fileName;
        File file = new File(path);
        if (file.exists()) {
            playVoiceFile(file);
        } else {
            if (mDownloadUtil == null) {
                mDownloadUtil = new DownloadUtil();
            }
            mDownloadUtil.download("voice", CommonAppConfig.VOICE_PATH, fileName, voiceLink, new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    playVoiceFile(file);
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.show(WordUtil.getString(R.string.video_play_error));
                    if (mVoicePlayCallBack != null) {
                        mVoicePlayCallBack.onPlayAutoEnd();
                    }
                }
            });
        }
    }

    /**
     * 停止播放语音
     */
    public void stopVoice() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
        if (mPlayView != null) {
            mPlayView.setMute(false);
        }
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
        if (mVoicePlayCallBack != null) {
            mVoicePlayCallBack.onPlayEnd();
        }
        mPlayVoiceTime = mSumVoiceTime;
        if (mTvVoiceTime != null) {
            mTvVoiceTime.setText(mPlayVoiceTime + "s");
        }
    }

    /**
     * 语音暂停
     */
    public void pauseVoice() {
        if (mVoiceMediaPlayerUtil != null) {
            if (mVoiceMediaPlayerUtil.isStarted()) {
                mVoiceMediaPlayerUtil.pausePlay();
                if (mPlayView != null) {
                    mPlayView.setMute(false);
                }
                if (mVoicePlayCallBack != null) {
                    mVoicePlayCallBack.onPlayPause();
                }
            }
        }
        if (mHandler != null) {
            mHandler.removeMessages(0);
        }
    }


    // 语音继续播
    public void resumeVoice(String path) {
        if (mVoiceMediaPlayerUtil == null) {
            return;
        }
        if (mVoiceMediaPlayerUtil.isPaused()) {
            mVoiceMediaPlayerUtil.resumePlay();
        } else {
            mVoiceMediaPlayerUtil.startPlay(path);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
        if (mVoicePlayCallBack != null) {
            mVoicePlayCallBack.onPlayResume();
        }

    }


    // 播语音
    private void playVoiceFile(File file) {
        if (mPlayView != null) {
            mPlayView.setMute(true);
        }
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    mPlayVoiceTime--;
                    if (mPlayVoiceTime > 0) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(0, 1000);
                        }
                        if (mTvVoiceTime != null) {
                            mTvVoiceTime.setText(mPlayVoiceTime + "s");
                        }
                    } else {
                        mPlayVoiceTime = mSumVoiceTime;
                    }

                }
            };
        }
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    L.e("---show-onPlayAutoEnd-");
                    mPlayVoiceTime = mSumVoiceTime;
                    if (mTvVoiceTime != null) {
                        mTvVoiceTime.setText(mPlayVoiceTime + "s");
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(0);
                    }
                    if (mPlayView != null) {
                        mPlayView.setMute(false);
                    }
                    if (mVoicePlayCallBack != null) {
                        mVoicePlayCallBack.onPlayAutoEnd();
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(file.getAbsolutePath());
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
        if (mVoicePlayCallBack != null) {
            mVoicePlayCallBack.onPlayStart();
        }
        if (mTvVoiceTime != null) {
            mTvVoiceTime.setText(mSumVoiceTime + "s");
        }
    }

    /**
     * 播语音的时候设置视频静音
     */
    public void setMute(boolean mute) {
        if (mPlayView != null) {
            mPlayView.setMute(mute);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseVoice();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_ADDLIKE);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_DEL);
    }
}
