package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ClickUtil;
import com.aihuan.im.utils.ChatLiveImUtil;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatAudienceActivity;
import com.aihuan.one.activity.ChatBaseActivity;

/**
 * Created by cxf on 2019/4/19.
 */

public class ChatAudInviteViewHolder extends AbsChatInviteViewHolder implements View.OnClickListener {

    private ImageView mAvatar;
    private TextView mName;
    private ImageView mLevelAnchor;
    private TextView mPrice;
    private TextView mTip;
    private View mBtnCancel;
    private View mBtnAccept;

    public ChatAudInviteViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_invite_aud;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mPrice = (TextView) findViewById(R.id.price);
        mTip = (TextView) findViewById(R.id.tip);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnAccept = findViewById(R.id.btn_accept);
        mBtnCancel.setOnClickListener(this);
        mBtnAccept.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            ((ChatAudienceActivity) mContext).onBackPressed();
        } else if (i == R.id.btn_accept) {
            ((ChatAudienceActivity) mContext).accpetChat();
        }
    }

    /**
     * 观众向主播发起通话邀请
     *
     * @param param     主播的信息
     * @param price     通话价格
     * @param sessionId 通话会话ID
     * @param chatType  通话类型
     * @param active    是否是观众主动发起的
     */
    public void showData(ChatAudienceParam param, String price, String sessionId, int chatType, boolean active) {
        ImgLoader.display(mContext, param.getAnchorAvatar(), mAvatar);
        mName.setText(param.getAnchorName());
        LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(param.getAnchorLevel());
        if (levelBean != null) {
            ImgLoader.display(mContext, levelBean.getThumb(), mLevelAnchor);
        }
        mPrice.setText(price);
        if (active) {
            mTip.setText(R.string.chat_invite_tip_1);
            ChatLiveImUtil.chatAudToAncStart(param.getAnchorID(), sessionId, chatType);
            ((ChatBaseActivity) mContext).startWait();
        } else {
            mTip.setText(chatType == Constants.CHAT_TYPE_VIDEO ? R.string.chat_invite_tip_2 : R.string.chat_invite_tip_3);
            mBtnAccept.setVisibility(View.VISIBLE);
            playRingMusic();
            ((ChatBaseActivity) mContext).startRing();
        }
    }


}
