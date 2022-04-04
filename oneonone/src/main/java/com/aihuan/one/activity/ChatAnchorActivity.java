package com.aihuan.one.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.event.ChatLiveImEvent;
import com.aihuan.im.utils.ChatLiveImUtil;
import com.aihuan.one.R;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.aihuan.one.views.ChatAncInviteViewHolder;
import com.aihuan.one.views.ChatAnchorViewHolder;
import com.aihuan.one.views.ChatEndAnchorViewHolder;
import com.aihuan.one.views.ChatLivePlayTxViewHolder;
import com.aihuan.one.views.ChatLivePushTxViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2018/10/7.
 * 主播直播间
 */
@Route(path = RouteUtil.MAIN_CHAT_ANCHOR)
public class ChatAnchorActivity extends ChatBaseActivity {

    private static final String TAG = "ChatAnchorActivity";

    private ChatAncInviteViewHolder mAncInviteViewHolder;
    private ChatAnchorViewHolder mChatAnchorViewHolder;
    private int mChatType;//通话类型
    private String mAudienceID;//观众的ID
    private String mAudienceAvatar;//观众的头像
    private String mAudienceName;//观众的名字
    private String mAnchorPushUrl;//主播的推流地址
    private String mAnchorPlayUrl;//主播的播流地址
    private boolean mAnchorHangUp;//是否是主播主动挂断的
    private boolean mAnchorActive;//是否是主播主动发起的聊天
    private boolean mMatch;//是否是匹配进来的

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {//判断屏幕有没有量
            L.e(mTag, "屏幕没有亮------>开始点亮");
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, mTag);
            wl.acquire();
            wl.release();
        }
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        if (flag) {
            L.e(mTag, "是否锁屏------>  " + flag);
            // mNeedNotification = true;
        }

        init(intent);
    }

    @Override
    protected void main() {
        super.main();
        init(getIntent());
    }

    private void init(Intent intent) {
        if (mChatStatus == STATUS_WAITING) {
            return;
        }
        ChatAnchorParam param = intent.getParcelableExtra(Constants.CHAT_PARAM_ANC);
        if (param == null) {
            return;
        }
        mChatType = param.getChatType();
        mSessionId = param.getSessionId();
        L.e("mSessionId------> " + mSessionId);
        mAnchorActive = param.isAnchorActive();//是否是主播主动发起的
        mAudienceID = param.getAudienceID();
        mAudienceName = param.getAudienceName();
        mAudienceAvatar = param.getAudienceAvatar();
        mMatch = param.isMatch();
        if (mEndViewHolder != null) {
            mEndViewHolder.removeFromParent();
            mEndViewHolder = null;
        }
        if (!mMatch) {
            mAncInviteViewHolder = new ChatAncInviteViewHolder(mContext, mRoot);
            mInviteViewHolder = mAncInviteViewHolder;
            mAncInviteViewHolder.addToParent();
            mAncInviteViewHolder.subscribeActivityLifeCycle();
            if (mAnchorActive) {
                mAnchorPushUrl = param.getAnchorPushUrl();
                mAnchorPlayUrl = param.getAnchorPlayUrl();
                mSessionId = param.getSessionId();
                mAncInviteViewHolder.showDataAncToAud(mAudienceID, mAudienceAvatar, mAudienceName, mSessionId, mChatType, param.getPrice());
            } else {
                mAncInviteViewHolder.showDataAndToAnc(mAudienceAvatar, mAudienceName, mChatType);
            }
            mChatStatus = STATUS_WAITING;
        } else {
            mAnchorPushUrl = param.getAnchorPushUrl();
            mAnchorPlayUrl = param.getAnchorPlayUrl();
            mSessionId = param.getSessionId();
        }

        if (mChatType == Constants.CHAT_TYPE_VOICE) {
            if (mVoiceView != null && mVoiceView.getVisibility() != View.VISIBLE) {
                mVoiceView.setVisibility(View.VISIBLE);
            }
            if (mContainerPlayFront != null) {
                mContainerPlayFront.setClickable(false);
            }
        } else {
            if (mVoiceView != null && mVoiceView.getVisibility() == View.VISIBLE) {
                mVoiceView.setVisibility(View.INVISIBLE);
            }
            if (mContainerPlayFront != null) {
                mContainerPlayFront.setClickable(true);
            }
        }
        mWindowChangeCount = 0;
        mTotalChatSecondTime = 0;
        mNextTimeMillis = 0;
        //notify(mAudienceName);

        if (mMatch) {
            startPush();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatLiveImEvent(ChatLiveImEvent e) {
        switch (e.getAction()) {
            case ChatLiveImUtil.ACTION_AUD_CANCEL://观众取消通话
                onChatAudToAncCancel(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_AUD_PUSH://观众推流成功
                onChatAudiencePushSuccess(e.getAudiencePlayUrl(), e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_AUD_HANG_UP://观众挂断通话
                onChatAudienceHangUp(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_CAMERA://观众打开或关闭摄像头
                onChatAudienceCamera(e.isAudienceCameraOpen(), e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_AUD_REFUSE://观众拒绝通话
                onChatAudienceRefuse(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_AUD_ACCPET://观众接听通话
                onChatAudienceAccpet(e.getSenderId());
                break;

        }
    }


    /**
     * 主播在等待中挂断，即主播拒绝通话
     */
    private void hangUpWaiting() {
        mChatStatus = STATUS_END;
        OneHttpUtil.chatAnchorHangUp(mAudienceID, mSessionId, null);
        if (mAnchorActive) {
            ChatLiveImUtil.chatAncToAudCancel(mAudienceID, mChatType);
        } else {
            ChatLiveImUtil.chatAnchorRefuse(mAudienceID, mChatType);
        }
        closeActivity();
    }

    /**
     * 主播同意通话
     */
    public void accpetChat() {
        String[] permissions =
                mChatType == Constants.CHAT_TYPE_VOICE ?
                        new String[]{
                                Manifest.permission.RECORD_AUDIO
                        } :
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                        };


        checkPermissions(permissions, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    if (mTimeHandler != null) {
                        mTimeHandler.removeCallbacksAndMessages(null);
                    }
                    if (mChatStatus != STATUS_WAITING) {
                        return;
                    }
                    OneHttpUtil.chatAnchorAccpet(mAudienceID, mSessionId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                ChatLiveImUtil.chatAnchorAccpet(mAudienceID);
                                JSONObject obj = JSON.parseObject(info[0]);
                                mAnchorPushUrl = obj.getString("push");
                                mAnchorPlayUrl = obj.getString("pull");
                                L.e("--mAnchorPushUrl---" + mAnchorPushUrl);
                                L.e("--mAnchorPlayUrl---" + mAnchorPlayUrl);
                                startPush();
                            }
                        }
                    });
                } else {
                    onBackPressed();
                }
            }
        });

    }

    /**
     * 主播开始推流
     */
    public void startPush() {
        mChatStatus = STATUS_CHAT;
        hideInviteViewHolder();
        if (mChatAnchorViewHolder == null) {
            mChatAnchorViewHolder = new ChatAnchorViewHolder(mContext, mContainerBottom, mChatType);
            mChatAnchorViewHolder.addToParent();
            mChatAnchorViewHolder.subscribeActivityLifeCycle();
        }
        setAnchorAvatar(mAudienceAvatar);
        setAnchorName(mAudienceName);
        if (mPushViewHolder == null) {
            mPushViewHolder = new ChatLivePushTxViewHolder(mContext, mContainerPush);
            mPushViewHolder.setBig(false);
            mPushViewHolder.addToParent();
            mPushViewHolder.subscribeActivityLifeCycle();
            mPushViewHolder.setLivePushListener(this);
        }
        mPushViewHolder.startPush(mAnchorPushUrl, mChatType == Constants.CHAT_TYPE_VOICE);
        startChatTimeChange();
    }

    /**
     * 推流成功的回调
     */
    @Override
    public void onPushStart() {
        ChatLiveImUtil.chatAnchorPushSuccess(mAudienceID, mAnchorPlayUrl);
    }

    /**
     * 挂断
     */
    @Override
    public void hangUpChat() {
        DialogUitl.showSimpleDialog(mContext,
                WordUtil.getString(mChatType == Constants.CHAT_TYPE_VIDEO ? R.string.chat_hang_up_tip_video : R.string.chat_hang_up_tip_voice),
                new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        doHangUpChat();
                    }
                }
        );
    }

    @Override
    public void doHangUpChat() {
        mChatStatus = STATUS_END;
        mAnchorHangUp = true;
        String chatDuration = String.format(WordUtil.getString(R.string.chat_duration_2), StringUtil.getDurationText3(mTotalChatSecondTime * 1000));
        L.e(TAG, "主播挂断--------->  " + chatDuration);
        ChatLiveImUtil.chatAnchorHangUp(mAudienceID, mChatType, chatDuration);
        endChat();
    }


    @Override
    public void changeWindowSize() {
        mWindowChangeCount++;
        boolean big = mWindowChangeCount % 2 == 1;
        if (mPushViewHolder != null) {
            mPushViewHolder.setBig(big);
        }
        if (mPlayViewHolder != null) {
            View playContentView = mPlayViewHolder.getContentView();
            ViewParent parent = playContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(playContentView);
            }
            if (big) {
                mContainerPlayFront.addView(playContentView);
            } else {
                mContainerPlayBack.addView(playContentView);
            }
        }
    }

    /**
     * 通话结束后显示通话信息
     */
    @Override
    protected void showEndViewHolder() {
        ChatEndAnchorViewHolder endAnchorViewHolder = new ChatEndAnchorViewHolder(mContext, mRoot, mAudienceID, mSessionId, mAnchorHangUp);
        mEndViewHolder = endAnchorViewHolder;
        endAnchorViewHolder.addToParent();
        endAnchorViewHolder.loadData();
    }

    @Override
    protected void onAudienceTimeCharge() {

    }

    @Override
    protected void onWaitEnd() {
        ToastUtil.show(R.string.chat_not_respone);
        closeActivity();
    }


    /**
     * 主播收到观众主动取消通话的消息
     */
    private void onChatAudToAncCancel(String senderId) {
        if (mChatStatus != STATUS_END && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            mChatStatus = STATUS_END;
            ToastUtil.show(R.string.chat_to_cancel);
            closeActivity();
        }
    }


    /**
     * 主播收到观众发过来的播流地址，开始播流
     */
    private void onChatAudiencePushSuccess(String andiencePlayUrl, String senderId) {
        if (!TextUtils.isEmpty(andiencePlayUrl) && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            mChatStatus = STATUS_CHAT;
            hideInviteViewHolder();
            if (mPlayViewHolder != null) {
                mPlayViewHolder.removeFromParent();
                mPlayViewHolder.release();
                mPlayViewHolder = null;
            }
            mPlayViewHolder = new ChatLivePlayTxViewHolder(mContext, mContainerPlayBack);
            mPlayViewHolder.addToParent();
            mPlayViewHolder.subscribeActivityLifeCycle();
            mPlayViewHolder.startPlay(andiencePlayUrl);
        }
    }


    /**
     * 主播在通话过程中，收到观众主动挂断通话的消息
     */
    private void onChatAudienceHangUp(String senderId) {
        if (mChatStatus == STATUS_CHAT && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            mChatStatus = STATUS_END;
            endChat();
        }
    }

    /**
     * 主播在通话过程中，收到观众打开和关闭摄像头的消息
     */
    private void onChatAudienceCamera(boolean openCamera, String senderId) {
        if (mChatStatus == STATUS_CHAT && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            if (mPlayViewHolder != null) {
                if (openCamera) {
                    mPlayViewHolder.hideCameraCover();
                } else {
                    mPlayViewHolder.showCameraCover();
                }
            }
        }
    }

    /**
     * 主播向观众发起通话，主播收到观众拒绝通话的消息
     */
    private void onChatAudienceRefuse(String senderId) {
        if (mChatStatus == STATUS_WAITING && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            mChatStatus = STATUS_END;
            ToastUtil.show(R.string.chat_to_refuse);
            closeActivity();
        }
    }

    /**
     * 主播向观众发起通话，主播收到观众同意通话的消息
     */
    private void onChatAudienceAccpet(String senderId) {
        if (mChatStatus == STATUS_WAITING && !TextUtils.isEmpty(senderId) && senderId.equals(mAudienceID)) {
            startPush();
        }
    }

    @Override
    public void onBackPressed() {
        if (mChatStatus == STATUS_WAITING) {
            hangUpWaiting();
        } else if (mChatStatus == STATUS_CHAT) {
            hangUpChat();
        } else if (mChatStatus == STATUS_END) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.CHAT_ANCHOR_ACCPET);
        super.onDestroy();
        L.e("LiveAnchorActivity-------onDestroy------->");
    }


    private void hideInviteViewHolder() {
        if (mAncInviteViewHolder != null) {
            mAncInviteViewHolder.hide();
            mAncInviteViewHolder = null;
        }
        mInviteViewHolder = null;
    }


}
