package com.aihuan.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.bean.ChatReceiveGiftBean;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.mob.MobCallback;
import com.aihuan.common.mob.MobShareUtil;
import com.aihuan.common.mob.ShareData;
import com.aihuan.common.presenter.GiftAnimViewHolder;
import com.aihuan.common.utils.DateFormatUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DownloadUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.dialog.ChatGiftDialogFragment;
import com.aihuan.im.presenter.CheckChatPresenter;
import com.aihuan.video.R;
import com.aihuan.video.bean.VideoBean;
import com.aihuan.video.event.VideoDeleteEvent;
import com.aihuan.video.event.VideoShareEvent;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;
import com.aihuan.video.utils.VideoLocalUtil;
import com.aihuan.video.utils.VideoStorge;
import com.aihuan.video.views.VideoScrollViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by cxf on 2019/2/28.
 */

public abstract class AbsVideoPlayActivity extends AbsVideoCommentActivity implements ChatGiftDialogFragment.ActionListener {

    protected VideoScrollViewHolder mVideoScrollViewHolder;
    private GiftAnimViewHolder mGiftAnimViewHolder;
    private Dialog mDownloadVideoDialog;
    private ClipboardManager mClipboardManager;
    private MobCallback mMobCallback;
    private MobShareUtil mMobShareUtil;
    private DownloadUtil mDownloadUtil;
    private ConfigBean mConfigBean;
    private VideoBean mShareVideoBean;
    protected String mVideoKey;
    private boolean mPaused;
    private ViewGroup mRoot;
    private View mChooseCallTypeView;
    private PopupWindow mPopupWindow;
    private CheckChatPresenter mCheckChatPresenter;

    @Override
    protected void main() {
        super.main();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mRoot = findViewById(R.id.root);
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });
    }


    /**
     * 复制视频链接
     */
    public void copyLink(VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("text", videoBean.getHref());
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }

    /**
     * 分享页面链接
     */
    public void shareVideoPage(String type, VideoBean videoBean) {
        if (videoBean == null || mConfigBean == null) {
            return;
        }
        if (mMobCallback == null) {
            mMobCallback = new MobCallback() {

                @Override
                public void onSuccess(Object data) {
                    if (mShareVideoBean == null) {
                        return;
                    }
                    VideoHttpUtil.setVideoShare(mShareVideoBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0 && mShareVideoBean != null) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                EventBus.getDefault().post(new VideoShareEvent(mShareVideoBean.getId(), obj.getString("nums")));
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                }

                @Override
                public void onError() {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFinish() {

                }
            };
        }
        mShareVideoBean = videoBean;
        ShareData data = new ShareData();
        data.setTitle(mConfigBean.getVideoShareTitle());
        data.setDes(mConfigBean.getVideoShareDes());
        data.setImgUrl(videoBean.getUserBean().getAvatarThumb());
        String webUrl = HtmlConfig.SHARE_VIDEO + videoBean.getId();
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(type, data, mMobCallback);
    }


    /**
     * 下载视频
     */
    public void downloadVideo(final VideoBean videoBean) {
        if (mProcessResultUtil == null || videoBean == null || TextUtils.isEmpty(videoBean.getHref())) {
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, new CommonCallback<Boolean>() {

            @Override
            public void callback(Boolean result) {
                if (!result) {
                    return;
                }
                mDownloadVideoDialog = DialogUitl.loadingDialog(mContext);
                mDownloadVideoDialog.show();
                if (mDownloadUtil == null) {
                    mDownloadUtil = new DownloadUtil();
                }
                String fileName = "YB_VIDEO_" + videoBean.getTitle() + "_" + DateFormatUtil.getCurTimeString() + ".mp4";
                mDownloadUtil.download(videoBean.getTag(), CommonAppConfig.VIDEO_PATH, fileName, videoBean.getHref(), new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        ToastUtil.show(R.string.video_download_success);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                        String path = file.getAbsolutePath();
                        try {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(path);
                            String d = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            if (StringUtil.isInt(d)) {
                                long duration = Long.parseLong(d);
                                VideoLocalUtil.saveVideoInfo(mContext, path, duration);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(R.string.video_download_failed);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                    }
                });
            }
        });
    }

    /**
     * 删除视频
     */
    public void deleteVideo(final VideoBean videoBean) {
        VideoHttpUtil.videoDelete(videoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mVideoScrollViewHolder != null) {
                        EventBus.getDefault().post(new VideoDeleteEvent(videoBean.getId()));
                        mVideoScrollViewHolder.deleteVideo(videoBean);
                    }
                }
            }
        });
    }


    public boolean isPaused() {
        return mPaused;
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    public void release() {
        super.release();
        VideoHttpUtil.cancel(VideoHttpConsts.SET_VIDEO_SHARE);
        VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_DELETE);
        VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_PUBLIC);
        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
            mDownloadVideoDialog.dismiss();
        }
        if (mVideoScrollViewHolder != null) {
            mVideoScrollViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        VideoStorge.getInstance().removeDataHelper(mVideoKey);
        mDownloadVideoDialog = null;
        mVideoScrollViewHolder = null;
        mMobShareUtil = null;
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        mGiftAnimViewHolder = null;
    }


    public void setVideoScrollViewHolder(VideoScrollViewHolder videoScrollViewHolder) {
        mVideoScrollViewHolder = videoScrollViewHolder;
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow(String toUid) {
        ChatGiftDialogFragment fragment = new ChatGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, toUid);
        bundle.putString(Constants.CHAT_SESSION_ID, "0");
        fragment.setArguments(bundle);
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "ChatGiftDialogFragment");
    }


    /**
     * 显示礼物动画
     */
    public void showGift(ChatReceiveGiftBean bean) {
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(mContext, mRoot);
            mGiftAnimViewHolder.addToParent();
        }
        mGiftAnimViewHolder.showGiftAnim(bean);
    }


    @Override
    public void onChargeClick() {
        RouteUtil.forwardMyCoin();
    }


    /**
     * 点击通话
     */
    public void chatClick(UserBean u) {
        if (u == null) {
            return;
        }
        if (u.openDisturb()) {
            ToastUtil.show(R.string.user_home_disturb);
            return;
        }
        boolean openVideo = u.openVideo();
        boolean openVoice = u.openVoice();
        if (openVideo && openVoice) {
            String coinName = CommonAppConfig.getInstance().getCoinName();
            chooseCallType(u, StringUtil.contact(u.getVideoPrice(), coinName),
                    StringUtil.contact(u.getVoicePrice(), coinName));
        } else if (openVideo) {
            chatAudToAncStart(u, Constants.CHAT_TYPE_VIDEO);
        } else if (openVoice) {
            chatAudToAncStart(u, Constants.CHAT_TYPE_VOICE);
        } else {
            ToastUtil.show(R.string.user_home_close_all);
        }

    }

    /**
     * 选择通话类型
     */
    private void chooseCallType(final UserBean u, String videoPrice, String voicePrice) {
        if (mChooseCallTypeView == null) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_user_home_choose_call, null, false);
            mChooseCallTypeView = v;
            TextView priceVideo = v.findViewById(R.id.price_video);
            TextView priceVoice = v.findViewById(R.id.price_voice);
            priceVideo.setText(String.format(WordUtil.getString(R.string.user_home_price_video), videoPrice));
            priceVoice.setText(String.format(WordUtil.getString(R.string.user_home_price_voice), voicePrice));
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_video) {
                        chatAudToAncStart(u, Constants.CHAT_TYPE_VIDEO);
                    } else if (i == R.id.btn_voice) {
                        chatAudToAncStart(u, Constants.CHAT_TYPE_VOICE);
                    }
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            };
            v.findViewById(R.id.btn_video).setOnClickListener(clickListener);
            v.findViewById(R.id.btn_voice).setOnClickListener(clickListener);
            v.findViewById(R.id.btn_cancel).setOnClickListener(clickListener);
        }
        mPopupWindow = new PopupWindow(mChooseCallTypeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.bottomToTopAnim);
        mPopupWindow.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 观众向主播发起通话邀请
     *
     * @param type 通话类型
     */
    private void chatAudToAncStart(UserBean u, int type) {
        if (u == null) {
            return;
        }
        if (mCheckChatPresenter == null) {
            mCheckChatPresenter = new CheckChatPresenter(mContext);
        }
        mCheckChatPresenter.chatAudToAncStart(u.getId(), type, u);
    }

    /**
     * 让播放器静音
     */
    public void setMute(boolean mute) {
        if (mVideoScrollViewHolder != null) {
            mVideoScrollViewHolder.setMute(mute);
        }
    }

}
