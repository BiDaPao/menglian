package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ClickUtil;
import com.aihuan.im.utils.ChatLiveImUtil;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatAnchorActivity;
import com.aihuan.one.activity.ChatBaseActivity;

/**
 * Created by cxf on 2019/4/19.
 */

public class ChatAncInviteViewHolder extends AbsChatInviteViewHolder implements View.OnClickListener {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mTip;
    private View mBtnCancel;
    private View mBtnAccept;

    public ChatAncInviteViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_invite_anc;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mTip = (TextView) findViewById(R.id.tip);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnAccept = findViewById(R.id.btn_accept);
        mBtnCancel.setOnClickListener(this);
        mBtnAccept.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            ((ChatAnchorActivity) mContext).onBackPressed();
        } else if (i == R.id.btn_accept) {
            ((ChatAnchorActivity) mContext).accpetChat();
        }
    }

    /**
     * 主播向观众发起赴约
     */
    public void showDataAncToAud(String audienceId, String avatar, String name, String sessionId, int chatType, String price) {
        ImgLoader.display(mContext, avatar, mAvatar);
        mName.setText(name);
        mTip.setText(R.string.chat_invite_tip_1);
        ChatLiveImUtil.chatAncToAudStart(audienceId, sessionId, chatType, price);
        ((ChatBaseActivity) mContext).startWait();
    }

    /**
     * 观众向主播发起的
     */
    public void showDataAndToAnc(String avatar, String name, int chatType) {
        ImgLoader.display(mContext, avatar, mAvatar);
        mName.setText(name);
        mTip.setText(chatType == Constants.CHAT_TYPE_VIDEO ? R.string.chat_invite_tip_2 : R.string.chat_invite_tip_3);
        mBtnAccept.setVisibility(View.VISIBLE);
        playRingMusic();
        ((ChatBaseActivity) mContext).startRing();
    }
}
