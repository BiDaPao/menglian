package com.aihuan.one.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.pay.AliCallback;
import com.aihuan.common.pay.PayPresenter;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.dialog.ChatGiftDialogFragment;
import com.aihuan.im.event.ChatLiveImEvent;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.utils.ChatLiveImUtil;
import com.aihuan.one.R;
import com.aihuan.one.dialog.ChatChargeDialogFragment;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.aihuan.one.views.ChatAudInviteViewHolder;
import com.aihuan.one.views.ChatAudienceViewHolder;
import com.aihuan.one.views.ChatEndAudienceViewHolder;
import com.aihuan.one.views.ChatLivePlayTxViewHolder;
import com.aihuan.one.views.ChatLivePushTxViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2018/10/10.
 */

@Route(path = RouteUtil.MAIN_CHAT_AUDIENCE)
public class ChatAudienceActivity extends ChatBaseActivity implements ChatGiftDialogFragment.ActionListener {

    private static final String TAG = "ChatAudienceActivity";

    private ChatAudInviteViewHolder mAudInviteViewHolder;
    private ChatAudienceViewHolder mAudienceViewHolder;
    private ChatAudienceParam mChatAudienceParam;
    private String mAnchorUid;//?????????id
    private int mChatType;//????????????
    private String mPrice;//?????????????????????
    private String mAudiencePlayUrl;//?????????????????????
    private String mAudiencePushUrl;//?????????????????????
    private boolean mAudienceActive;//??????????????????????????????
    private HttpCallback mTimeChargeCallback;
    private PayPresenter mPayPresenter;
    private boolean mMatch;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {//????????????????????????
            L.e(mTag, "???????????????------>????????????");
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, mTag);
            wl.acquire();
            wl.release();
        }
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        if (flag) {
            L.e(mTag, "????????????------>  " + flag);
            //mNeedNotification = true;
        }

        init(intent);
    }

    @Override
    protected void main() {
        super.main();
        init(getIntent());
    }

    public void init(Intent intent) {
        ChatAudienceParam param = intent.getParcelableExtra(Constants.CHAT_PARAM_AUD);
        if (param == null) {
            return;
        }
        mChatAudienceParam = param;
        mAnchorUid = param.getAnchorID();
        mChatType = param.getChatType();
        mSessionId = param.getSessionId();
        mPrice = param.getAnchorPrice();
        mAudiencePlayUrl = param.getAudiencePlayUrl();
        mAudiencePushUrl = param.getAudiencePushUrl();
        mAudienceActive = param.isAudienceActive();
        mMatch = param.isMatch();

        if (mEndViewHolder != null) {
            mEndViewHolder.removeFromParent();
            mEndViewHolder = null;
        }
        if (!mMatch) {
            mAudInviteViewHolder = new ChatAudInviteViewHolder(mContext, mRoot);
            mInviteViewHolder = mAudInviteViewHolder;
            mAudInviteViewHolder.addToParent();
            mAudInviteViewHolder.subscribeActivityLifeCycle();
            mAudInviteViewHolder.showData(param,
                    String.format(WordUtil.getString(R.string.chat_live_price), StringUtil.contact(mPrice, mCoinName)),
                    mSessionId, mChatType, mAudienceActive);

        } else {
            mAnchorUid = param.getAnchorID();
        }
        mChatStatus = STATUS_WAITING;

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
        //notify(param.getAnchorName());

        if (mMatch) {
            startChat();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatLiveImEvent(ChatLiveImEvent e) {
        switch (e.getAction()) {
            case ChatLiveImUtil.ACTION_ANC_ACCPET://??????????????????
                onChatAnchorAccpet(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_ANC_REFUSE://??????????????????
                onChatAnchorRefuse(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_ANC_PUSH://??????????????????
                onChatAnchorPushSuccess(e.getAnchorPlayUrl(), e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_ANC_HANG_UP://??????????????????
                onChatAnchorHangUp(e.getSenderId());
                break;
            case ChatLiveImUtil.ACTION_ANC_CANCEL://??????????????????
                onChatAnchorCancel(e.getSenderId());
                break;

        }
    }


    /**
     * ????????????????????????
     */
    private void hangUpWaiting() {
        mChatStatus = STATUS_END;
        OneHttpUtil.chatAudienceHangUp(mAnchorUid, mSessionId, Constants.CHAT_HANG_TYPE_WAITING, null);
        if (mAudienceActive) {
            ChatLiveImUtil.chatAudToAncCancel(mAnchorUid, mChatType);
        } else {
            ChatLiveImUtil.chatAudienceRefuse(mAnchorUid, mChatType);
        }
        closeActivity();
    }

    /**
     * ????????????????????????????????????????????????
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
                    OneHttpUtil.chatAudienceAccpet(mAnchorUid, mSessionId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                ChatLiveImUtil.chatAudienceAccpet(mAnchorUid);
                                JSONObject obj = JSON.parseObject(info[0]);
                                mAudiencePushUrl = obj.getString("push");
                                mAudiencePlayUrl = obj.getString("pull");
                                startChat();
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
     * ?????????????????????????????????????????????????????????
     */
    private void onChatAnchorPushSuccess(String anchorPlayUrl, String senderId) {
        if (!TextUtils.isEmpty(anchorPlayUrl) && !TextUtils.isEmpty(senderId) && senderId.equals(mAnchorUid)) {
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
            mPlayViewHolder.startPlay(anchorPlayUrl);
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     */
    private void onChatAnchorHangUp(String senderId) {
        if (!TextUtils.isEmpty(senderId) && senderId.equals(mAnchorUid)) {
            mChatStatus = STATUS_END;
            endChat();
        }
    }


    /**
     * ??????????????????????????????????????????????????????
     */
    private void onChatAnchorAccpet(String senderId) {
        if (mChatStatus == STATUS_WAITING && !TextUtils.isEmpty(senderId) && senderId.equals(mAnchorUid)) {
            startChat();
        }
    }

    /**
     * ????????????
     */
    private void startChat() {
        mChatStatus = STATUS_CHAT;
        hideInviteViewHolder();
        if (mAudienceViewHolder == null) {
            mAudienceViewHolder = new ChatAudienceViewHolder(mContext, mContainerBottom, mChatType);
            mAudienceViewHolder.addToParent();
            mAudienceViewHolder.subscribeActivityLifeCycle();
        }
        setAnchorAvatar(mChatAudienceParam.getAnchorAvatar());
        setAnchorName(mChatAudienceParam.getAnchorName());
        if (mPushViewHolder == null) {
            mPushViewHolder = new ChatLivePushTxViewHolder(mContext, mContainerPush);
            mPushViewHolder.setBig(false);
            mPushViewHolder.addToParent();
            mPushViewHolder.subscribeActivityLifeCycle();
            mPushViewHolder.setLivePushListener(this);
        }
        mPushViewHolder.startPush(mAudiencePushUrl, mChatType == Constants.CHAT_TYPE_VOICE);
        startChatTimeChange();
    }


    /**
     * ???????????????????????????
     */
    @Override
    public void onPushStart() {
        if (mChatStatus == STATUS_CHAT) {
            ChatLiveImUtil.chatAudiencePushSuccess(mAnchorUid, mAudiencePlayUrl);
        }
    }

    /**
     * ????????????????????????
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
        OneHttpUtil.chatAudienceHangUp(mAnchorUid, mSessionId, Constants.CHAT_HANG_TYPE_CHAT, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    String chatDuration = String.format(WordUtil.getString(R.string.chat_duration_2), StringUtil.getDurationText3(mTotalChatSecondTime * 1000));
                    L.e(TAG, "????????????--------->  " + chatDuration);
                    ChatLiveImUtil.chatAudienceHangUp(mAnchorUid, mChatType, chatDuration);
                    endChat();
                }
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    private void onChatAnchorCancel(String senderId) {
        if (mChatStatus != STATUS_END && !TextUtils.isEmpty(senderId) && senderId.equals(mAnchorUid)) {
            mChatStatus = STATUS_END;
            ToastUtil.show(R.string.chat_to_cancel);
            closeActivity();
        }
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

    @Override
    protected void showEndViewHolder() {
//        String chatDuration = StringUtil.contact(WordUtil.getString(R.string.chat_end_duration), StringUtil.getDurationText4(mTotalChatSecondTime * 1000));
//        ChatEndAudienceViewHolder viewHolder = new ChatEndAudienceViewHolder(mContext, mRoot, mAnchorUid, chatDuration);
//        mEndViewHolder = viewHolder;
//        viewHolder.addToParent();
//        viewHolder.loadData();
        finish();
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @Override
    protected void onAudienceTimeCharge() {
        if (mTimeChargeCallback == null) {
            mTimeChargeCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        UserBean u = CommonAppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setLevel(obj.getIntValue("level"));
                            u.setCoin(obj.getString("coin"));
                        }
                        if (obj.getIntValue("istips") == 1) {
                            //?????????????????????
                            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.chat_coin_not_enough), obj.getString("tips"), true, new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    openChargeWindow();
                                }
                            });
                        }
                    } else {
                        ToastUtil.show(msg);
                        String chatDuration = String.format(WordUtil.getString(R.string.chat_duration_2), StringUtil.getDurationText3(mTotalChatSecondTime * 1000));
                        L.e(TAG, "????????????--------->  " + chatDuration);
                        ChatLiveImUtil.chatAudienceHangUp(mAnchorUid, mChatType, chatDuration);
                        endChat();
                    }
                }
            };
        }
        OneHttpUtil.timeCharge(mAnchorUid, mSessionId, mTimeChargeCallback);
    }

    @Override
    protected void onWaitEnd() {
        OneHttpUtil.chatAudienceHangUp(mAnchorUid, mSessionId, Constants.CHAT_HANG_TYPE_WAIT_END, null);
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.chat_not_response), new DialogUitl.SimpleCallback2() {
            @Override
            public void onCancelClick() {
                closeActivity();
            }

            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                ImHttpUtil.audSubscribeAnc(mAnchorUid, mChatType);
                ToastUtil.show(R.string.chat_subcribe_success);
                closeActivity();
            }
        });
    }

    /**
     * ???????????????????????????????????????
     */
    private void onChatAnchorRefuse(String senderId) {
        if (mChatStatus == STATUS_WAITING && !TextUtils.isEmpty(senderId) && senderId.equals(mAnchorUid)) {
            mChatStatus = STATUS_END;
            ToastUtil.show(R.string.chat_to_refuse);
            closeActivity();
        }
    }


    private void hideInviteViewHolder() {
        if (mAudInviteViewHolder != null) {
            mAudInviteViewHolder.hide();
            mAudInviteViewHolder = null;
        }
        mInviteViewHolder = null;
    }

    /**
     * ????????????????????????
     */
    public void toggleCameraOpen(boolean open) {
        ChatLiveImUtil.chatAudienceCamera(open, mAnchorUid);
        if (mPushViewHolder != null) {
            if (open) {
                mPushViewHolder.hideCameraCover();
            } else {
                mPushViewHolder.showCameraCover();
            }
        }
    }

    /**
     * ??????????????????
     */
    public void openGiftWindow() {
        ChatGiftDialogFragment fragment = new ChatGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mAnchorUid);
        bundle.putString(Constants.CHAT_SESSION_ID, mSessionId);
        fragment.setArguments(bundle);
        fragment.setActionListener(this);
        fragment.show(((ChatAudienceActivity) mContext).getSupportFragmentManager(), "ChatGiftDialogFragment");
    }

    /**
     * ??????????????????
     */
    public void openChargeWindow() {
        if (mPayPresenter == null) {
            mPayPresenter = new PayPresenter(this);
            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
            mPayPresenter.setPayCallback(new AliCallback() {
                @Override
                public void onSuccess() {
                    if (mPayPresenter != null) {
                        mPayPresenter.checkPayResult();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
        ChatChargeDialogFragment fragment = new ChatChargeDialogFragment();
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(((ChatAudienceActivity) mContext).getSupportFragmentManager(), "ChatChargeDialogFragment");
    }

    /**
     * ????????????
     */
    public void pausePlay() {
        if (mPlayViewHolder != null) {
            mPlayViewHolder.pausePlay();
        }
    }

    /**
     * ????????????
     */
    public void resumePlay() {
        if (mPlayViewHolder != null) {
            mPlayViewHolder.resumePlay();
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
        OneHttpUtil.cancel(OneHttpConsts.CHAT_AUDIENCE_ACCPET);
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
        L.e("LiveAudienceActivity-------onDestroy------->");
    }


    @Override
    public void onChargeClick() {
        openChargeWindow();
    }
}
