package com.aihuan.one.views;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aihuan.common.Constants;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.im.event.ImCensorMsgEvent;
import com.aihuan.im.event.SystemMsgEvent;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatBaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2018/10/9.
 */

public abstract class AbsChatLiveViewHolder extends AbsViewHolder implements View.OnClickListener {

    private boolean mMute;
    private ImageView mMuteIcon;
    private Drawable mMuteDrawable;
    private Drawable mUnMuteDrawable;
    protected int mChatType;

    public AbsChatLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsChatLiveViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }


    @Override
    protected void processArguments(Object... args) {
        mChatType = (int) args[0];
    }

    @Override
    public void init() {
        if (mChatType == Constants.CHAT_TYPE_VIDEO) {
            findViewById(R.id.btn_beauty).setOnClickListener(this);
            findViewById(R.id.btn_camera_switch).setOnClickListener(this);
        } else {
            findViewById(R.id.btn_beauty).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_camera_switch).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.btn_mute).setOnClickListener(this);
        findViewById(R.id.btn_hang_up).setOnClickListener(this);
        mMuteIcon = (ImageView) findViewById(R.id.mute_icon);
        mMuteDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_chat_mute_1);
        mUnMuteDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_chat_mute_0);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImCensorMsgEvent(ImCensorMsgEvent e) {
        L.e("检测到违规： " + e.getContent());
        //系统发送消息，检测当前视频出现违规内容
        ToastUtil.show(e.getContent());
        ((ChatBaseActivity) mContext).doHangUpChat();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_beauty) {
            ((ChatBaseActivity) mContext).beauty();
        } else if (i == R.id.btn_mute) {
            toggleMute();
        } else if (i == R.id.btn_hang_up) {
            ((ChatBaseActivity) mContext).hangUpChat();
        } else if (i == R.id.btn_camera_switch) {
            ((ChatBaseActivity) mContext).switchCamera();
        }
    }


    private void toggleMute() {
        mMute = !mMute;
        ((ChatBaseActivity) mContext).setMute(mMute);
        if (mMuteIcon != null) {
            mMuteIcon.setImageDrawable(mMute ? mMuteDrawable : mUnMuteDrawable);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
