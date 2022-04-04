package com.aihuan.one.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatAudienceActivity;

/**
 * Created by cxf on 2018/10/9.
 * 观众直播间逻辑
 */

public class ChatAudienceViewHolder extends AbsChatLiveViewHolder {

    private boolean mOpenCamera;
    private ImageView mCameraIcon;
    private TextView mCameraText;
    private Drawable mCameraDrawableClose;
    private Drawable mCameraDrawableOpen;
    private String mCameraOpenString;
    private String mCameraCloseString;

    public ChatAudienceViewHolder(Context context, ViewGroup parentView, int chatType) {
        super(context, parentView, chatType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_audience;
    }

    @Override
    public void init() {
        super.init();
        if (mChatType == Constants.CHAT_TYPE_VIDEO) {
            findViewById(R.id.btn_camera_close).setOnClickListener(this);
        } else {
            findViewById(R.id.btn_camera_close).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.btn_gift).setOnClickListener(this);
        findViewById(R.id.btn_charge).setOnClickListener(this);
        mOpenCamera = true;
        mCameraIcon = (ImageView) findViewById(R.id.camera_icon);
        mCameraText = (TextView) findViewById(R.id.camera_text);
        mCameraDrawableClose = ContextCompat.getDrawable(mContext, R.mipmap.o_chat_camera_0);
        mCameraDrawableOpen = ContextCompat.getDrawable(mContext, R.mipmap.o_chat_camera_1);
        mCameraOpenString = WordUtil.getString(R.string.chat_live_camera_open);
        mCameraCloseString = WordUtil.getString(R.string.chat_live_camera_close);
        showCameraIcon();
    }


    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.btn_camera_close) {
            toggleCameraOpen();
        } else if (i == R.id.btn_gift) {
            ((ChatAudienceActivity) mContext).openGiftWindow();
        } else if (i == R.id.btn_charge) {
            ((ChatAudienceActivity) mContext).openChargeWindow();
        }
    }

    private void toggleCameraOpen() {
        mOpenCamera = !mOpenCamera;
        showCameraIcon();
        ((ChatAudienceActivity) mContext).toggleCameraOpen(mOpenCamera);
    }

    private void showCameraIcon() {
        if (mCameraIcon != null) {
            mCameraIcon.setImageDrawable(mOpenCamera ? mCameraDrawableClose : mCameraDrawableOpen);
        }
        if (mCameraText != null) {
            mCameraText.setText(mOpenCamera ? mCameraCloseString : mCameraOpenString);
        }
    }

}
