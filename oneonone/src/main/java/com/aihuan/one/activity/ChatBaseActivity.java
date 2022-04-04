package com.aihuan.one.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

//import com.aihuan.beauty.interfaces.BeautyViewHolder;
//import com.aihuan.beauty.views.DefaultBeautyViewHolder;
//import com.aihuan.beauty.views.TiBeautyViewHolder;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatReceiveGiftBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.presenter.GiftAnimViewHolder;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.im.bean.ImMessageBean;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.one.R;
import com.aihuan.one.interfaces.LivePushListener;
import com.aihuan.one.views.AbsChatEndViewHolder;
import com.aihuan.one.views.AbsChatInviteViewHolder;
import com.aihuan.one.views.AbsChatLivePlayViewHolder;
import com.aihuan.one.views.AbsChatLivePushViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.TiPanelLayout;

/**
 * Created by cxf on 2018/10/7.
 */

public abstract class ChatBaseActivity extends AbsActivity implements LivePushListener, View.OnClickListener {

    protected static final byte STATUS_WAITING = 0;//等待中
    protected static final byte STATUS_CHAT = 1;//通话中
    protected static final byte STATUS_END = 2;//通话结束
    protected static final long WAIT_TIME = 60000;//接听时候的等待时间 1分钟

    protected AbsChatInviteViewHolder mInviteViewHolder;
    protected AbsChatLivePushViewHolder mPushViewHolder;
    protected AbsChatLivePlayViewHolder mPlayViewHolder;
    protected AbsChatEndViewHolder mEndViewHolder;
    //    private BeautyViewHolder mLiveBeautyViewHolder;
    protected GiftAnimViewHolder mGiftAnimViewHolder;

    protected ViewGroup mRoot;
    protected FrameLayout mContainerPlayBack;
    protected FrameLayout mContainerPlayFront;
    protected FrameLayout mContainerPush;
    protected FrameLayout mContainerBottom;
    protected View mVoiceView;
    private ImageView mAvatar;//主播头像
    private TextView mName;//主播名字
    private TextView mChatTimeTextView;//通话时长TextView
    protected long mTotalChatSecondTime;//累计通话时长的秒数
    protected long mNextTimeMillis;//下次计时的时间点
    protected TimeHandler mTimeHandler;
    protected String mCoinName;//钻石名称
    protected byte mChatStatus = -1;
    protected int mWindowChangeCount;
    private boolean mPaused;
    protected String mSessionId;//通话的会话id
    protected String mTag;
    //protected boolean mNeedNotification;
    private ProcessResultUtil mProcessResultUtil;

    TiPanelLayout tiPanelLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_live;
    }

    @Override
    protected void main() {
        mTag = getClass().getSimpleName();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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

        mProcessResultUtil = new ProcessResultUtil(this);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mRoot = findViewById(R.id.root);
        mContainerPlayBack = findViewById(R.id.container_play_back);
        mContainerPlayFront = findViewById(R.id.container_play_front);
        mContainerPlayFront.setOnClickListener(this);
        mContainerPush = findViewById(R.id.container_push);
        mContainerBottom = findViewById(R.id.container_bottom);
        mVoiceView = findViewById(R.id.voice_view);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mChatTimeTextView = findViewById(R.id.time);
        EventBus.getDefault().register(this);
        ImMessageUtil.getInstance().setOpenChatActivity(true);
        tiPanelLayout = new TiPanelLayout(this).init(TiSDKManager.getInstance());
        addContentView(tiPanelLayout,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }


    public void checkPermissions(String[] permissions, CommonCallback<Boolean> commonCallback) {
        if (mProcessResultUtil != null) {
            mProcessResultUtil.requestPermissions(permissions, commonCallback);
        }
    }


    /**
     * 显示主播头像
     */
    public void setAnchorAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(mContext, url, mAvatar);
        }
    }

    /**
     * 显示用户名
     */
    public void setAnchorName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * 收到消息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageBean(ImMessageBean bean) {
        ChatReceiveGiftBean giftBean = bean.getGiftBean();
        if (giftBean == null) {
            return;
        }
        if (!TextUtils.isEmpty(mSessionId) && mSessionId.equals(giftBean.getSessionId())) {
            showGift(giftBean);
        }
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


    /**
     * 开始通话计时读秒
     */
    protected void startChatTimeChange() {
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        } else {
            mTimeHandler.removeCallbacksAndMessages(null);
        }
        mNextTimeMillis = SystemClock.uptimeMillis();
        mTotalChatSecondTime = 0;
        if (mTimeHandler != null) {
            mTimeHandler.sendEmptyMessage(TimeHandler.WHAT_CHAT_TIME_CHANGED);
        }
    }

    /**
     * 通话计时
     */
    private void onChatTimeChanged() {
        if (mChatTimeTextView != null) {
            mChatTimeTextView.setText(StringUtil.getDurationText2(mTotalChatSecondTime * 1000));
        }
        if (mTotalChatSecondTime % 60 == 0) {//60秒
            if (mChatStatus == STATUS_CHAT) {
                onAudienceTimeCharge();
            }
        }
        if (mTimeHandler != null) {
            mNextTimeMillis += 1000;
            mTotalChatSecondTime += 1;
            mTimeHandler.sendEmptyMessageAtTime(TimeHandler.WHAT_CHAT_TIME_CHANGED, mNextTimeMillis);
        }
    }

    @Override
    public void onPreviewStart() {

    }

    /**
     * 推流成功的回调
     */
    @Override
    public void onPushStart() {
        //子类各自实现
    }

    @Override
    public void onPushFailed() {
        ToastUtil.show(R.string.live_push_failed);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.container_play_front) {
            changeWindowSize();
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mPushViewHolder != null) {
            mPushViewHolder.toggleCamera();
        }
    }

    /**
     * 设置美颜
     */
    public void beauty() {
//        if (mLiveBeautyViewHolder == null) {
//            if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//                mLiveBeautyViewHolder = new TiBeautyViewHolder(mContext, mRoot);
//            } else {
//                mLiveBeautyViewHolder = new DefaultBeautyViewHolder(mContext, mRoot);
//            }
//            if (mPushViewHolder != null) {
//                mLiveBeautyViewHolder.setEffectListener(mPushViewHolder.getEffectListener());
//            }
//        }
//        mLiveBeautyViewHolder.show();
        if (tiPanelLayout != null) {
            tiPanelLayout.showBeautyView();
        }
    }

    public void initBeauty() {
        if (tiPanelLayout != null) {
            tiPanelLayout.initBeautyCache();
        }
    }

    /**
     * 关麦
     */
    public void setMute(boolean mute) {
        if (mPushViewHolder != null) {
            mPushViewHolder.setMute(mute);
        }
    }

    /**
     * 挂断
     */
    public abstract void hangUpChat();

    /**
     * 挂断
     */
    public abstract void doHangUpChat();

    /**
     * 改变窗口大小
     */
    public abstract void changeWindowSize();

    /**
     * 显示结束通话后的页面
     */
    protected abstract void showEndViewHolder();

    /**
     * 观众计时扣费
     */
    protected abstract void onAudienceTimeCharge();

    /**
     * 开始等待对方接听，超时进行预约
     */
    public void startWait() {
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        }
        mTimeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onWaitEnd();
            }
        }, WAIT_TIME);
    }

    /**
     * 对方发来通话邀请，开始响铃
     */
    public void startRing() {
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        }
        mTimeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeActivity();
            }
        }, WAIT_TIME);
    }

    /**
     * 等待时间结束
     */
    protected abstract void onWaitEnd();


    /**
     * 结束通话
     */
    protected void endChat() {
        if (mTimeHandler != null) {
            mTimeHandler.release();
        }
        mTimeHandler = null;
        if (mPushViewHolder != null) {
            mPushViewHolder.stopPush();
        }
        if (mPlayViewHolder != null) {
            mPlayViewHolder.stopPlay();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        mGiftAnimViewHolder = null;
        showEndViewHolder();
    }


    /**
     * 是否能够返回
     */
    protected boolean canBackPressed() {
//        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
//            mLiveBeautyViewHolder.hide();
//            return false;
//        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!canBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    protected void release() {
        EventBus.getDefault().unregister(this);
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        if (mTimeHandler != null) {
            mTimeHandler.release();
        }
        if (mInviteViewHolder != null) {
            mInviteViewHolder.release();
        }
        if (mPlayViewHolder != null) {
            mPlayViewHolder.release();
        }
        if (mPushViewHolder != null) {
            mPushViewHolder.release();
        }
        mInviteViewHolder = null;
        mGiftAnimViewHolder = null;
        mTimeHandler = null;
        mPlayViewHolder = null;
        mPushViewHolder = null;
        L.e("ChatBaseActivity--------release------>");
        ImMessageUtil.getInstance().setOpenChatActivity(false);
    }

    /**
     * 切后台时间到了
     */
    public void onPauseTimeReached() {
        if (mChatStatus == STATUS_CHAT) {
            doHangUpChat();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mChatStatus == STATUS_CHAT) {
            if (mTimeHandler != null) {
                mTimeHandler.sendEmptyMessageDelayed(TimeHandler.WHAT_PAUSE, WAIT_TIME);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mTimeHandler != null) {
                mTimeHandler.removeMessages(TimeHandler.WHAT_PAUSE);
            }
        }
        mPaused = false;
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    /**
     * 关闭activity
     */
    protected void closeActivity() {
        release();
        finish();
    }


    protected static class TimeHandler extends Handler {

        private ChatBaseActivity mChatBaseActivity;
        private static final int WHAT_CHAT_TIME_CHANGED = 0;//通话时间读秒

        private static final int WHAT_PAUSE = 1;//切后台

        public TimeHandler(ChatBaseActivity activity) {
            mChatBaseActivity = new WeakReference<>(activity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mChatBaseActivity != null) {
                switch (msg.what) {
                    case WHAT_CHAT_TIME_CHANGED:
                        mChatBaseActivity.onChatTimeChanged();
                        break;
                    case WHAT_PAUSE:
                        mChatBaseActivity.onPauseTimeReached();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mChatBaseActivity = null;
        }
    }

}
