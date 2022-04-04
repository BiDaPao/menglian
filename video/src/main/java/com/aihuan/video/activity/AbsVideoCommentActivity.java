package com.aihuan.video.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.ImChatFacePagerAdapter;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.OnFaceClickListener;
import com.aihuan.common.utils.DownloadUtil;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;
import com.aihuan.video.R;
import com.aihuan.video.bean.VideoCommentBean;
import com.aihuan.video.dialog.VideoInputDialogFragment2;
import com.aihuan.video.views.VideoCommentViewHolder;
import com.aihuan.video.views.VideoCommentVoiceViewHolder;

import java.io.File;

/**
 * Created by cxf on 2019/3/11.
 */

public abstract class AbsVideoCommentActivity extends AbsActivity implements View.OnClickListener, OnFaceClickListener {
    private static final int AT_FRIEND_REQUEST_CODE = 100;
    protected ProcessResultUtil mProcessResultUtil;
    protected VideoCommentViewHolder mVideoCommentViewHolder;
    protected VideoInputDialogFragment2 mVideoInputDialogFragment;
    private View mFaceView;//表情面板
    private int mFaceHeight;//表情面板高度


    protected VideoCommentVoiceViewHolder mVideoCommentVoiceViewHolder;
    private String mCurVideoId;
    private String mCurVideoUid;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    protected DownloadUtil mDownloadUtil;
    @Override
    protected void main() {
        mProcessResultUtil = new ProcessResultUtil(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send) {
            if (mVideoInputDialogFragment != null) {
                mVideoInputDialogFragment.sendComment();
            }
        }
    }
    public void forwardAtFriend(String videoId, String videoUid) {
        if (TextUtils.isEmpty(videoId) || TextUtils.isEmpty(videoUid)) {
            return;
        }
        mCurVideoId = videoId;
        mCurVideoUid = videoUid;
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.hideSoftInput();
        }
        RouteUtil.forwardAtFriend(this, AT_FRIEND_REQUEST_CODE);
    }
    public void showVoiceViewHolder(final String videoId, final String videoUid, final VideoCommentBean videoCommentBean) {
        if (mProcessResultUtil == null) {
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean bean) {
                if (mVideoInputDialogFragment != null) {
                    mVideoInputDialogFragment.hideSoftInput();
                }
                if (mVideoCommentVoiceViewHolder == null) {
                    mVideoCommentVoiceViewHolder = new VideoCommentVoiceViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
                }
                mVideoCommentVoiceViewHolder.setVideoId(videoId);
                mVideoCommentVoiceViewHolder.setVideoUid(videoUid);
                mVideoCommentVoiceViewHolder.setVideoCommentBean(videoCommentBean);
                mVideoCommentVoiceViewHolder.addToParent();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == AT_FRIEND_REQUEST_CODE && resultCode == RESULT_OK) {
            if (intent != null) {
                String toUid = intent.getStringExtra(Constants.TO_UID);
                String toName = intent.getStringExtra(Constants.TO_NAME);
                if (mVideoInputDialogFragment != null) {
                    mVideoInputDialogFragment.addSpan(toUid, toName);
                    mVideoInputDialogFragment.showSoftInputDelay();
                } else {
                    boolean dark = mVideoCommentViewHolder != null && mVideoCommentViewHolder.isShowed();
                    openCommentInputWindow(false, mCurVideoId, mCurVideoUid, null, dark, toUid, toName);
                }
            }
        }
    }

    /**
     * 打开评论输入框
     */
    public void openCommentInputWindow(boolean openFace, String videoId, String videoUid, VideoCommentBean bean) {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        VideoInputDialogFragment2 fragment = new VideoInputDialogFragment2();
        fragment.setVideoInfo(videoId, videoUid);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        fragment.setArguments(bundle);
        mVideoInputDialogFragment = fragment;
        fragment.show(getSupportFragmentManager(), "VideoInputDialogFragment");
    }


    /**
     * 打开评论输入框
     */
    public void openCommentInputWindow(boolean openFace, String videoId, String videoUid, VideoCommentBean bean, boolean dark, String atUid, String atName) {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        VideoInputDialogFragment2 fragment = new VideoInputDialogFragment2();
        fragment.setDarkStyle(dark);
        fragment.setVideoInfo(videoId, videoUid);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        bundle.putString(Constants.TO_UID, atUid);
        bundle.putString(Constants.TO_NAME, atName);
        fragment.setArguments(bundle);
        mVideoInputDialogFragment = fragment;
        fragment.show(getSupportFragmentManager(), "VideoInputDialogFragment");
    }


    public View getFaceView() {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        return mFaceView;
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceHeight = v.getMeasuredHeight();
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }

    /**
     * 显示评论
     */
    public void openCommentWindow(String videoId, String videoUid) {
        if (mVideoCommentViewHolder == null) {
            mVideoCommentViewHolder = new VideoCommentViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
            mVideoCommentViewHolder.addToParent();
        }
        mVideoCommentViewHolder.setVideoInfo(videoId, videoUid);
        mVideoCommentViewHolder.showBottom();
    }

    /**
     * 隐藏评论
     */
    public void hideCommentWindow() {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.hideBottom();
        }
        mVideoInputDialogFragment = null;
    }

    /**
     * 刷新评论
     */
    public void refreshComment() {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.refreshComment();
        }
    }

    /**
     * 播放语音评论
     */
    public void playCommentVoice(VideoCommentBean commentBean) {
        if (commentBean == null || !commentBean.isVoice()) {
            return;
        }
        String voiceLink = commentBean.getVoiceLink();
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
                    WordUtil.getString(R.string.video_play_error);
                }
            });
        }
    }

    /**
     * 停止播放语音评论
     */
    public void stopCommentVoice(VideoCommentBean commentBean) {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
        if (mContext instanceof AbsVideoPlayActivity) {
            ((AbsVideoPlayActivity) mContext).setMute(false);
        }
    }
    private void playVoiceFile(File file) {
        if (mContext instanceof AbsVideoPlayActivity) {
            ((AbsVideoPlayActivity) mContext).setMute(true);
        }
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    if (mVideoCommentViewHolder != null) {
                        mVideoCommentViewHolder.stopVoiceAnim();
                    }
                    if (mContext instanceof AbsVideoPlayActivity) {
                        ((AbsVideoPlayActivity) mContext).setMute(false);
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(file.getAbsolutePath());
    }

    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceClick(str, faceImageRes);
        }
    }

    @Override
    public void onFaceDeleteClick() {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceDeleteClick();
        }
    }

    public void release() {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.release();
        }
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        mVideoCommentViewHolder = null;
        mVideoInputDialogFragment = null;
        mProcessResultUtil = null;
    }

    public void releaseVideoInputDialog() {
        mVideoInputDialogFragment = null;
    }

}
