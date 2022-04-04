package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.Constants;
import com.aihuan.one.R;

/**
 * Created by cxf on 2018/10/9.
 * 主播直播间逻辑
 */

public class ChatAnchorViewHolder extends AbsChatLiveViewHolder {

    public ChatAnchorViewHolder(Context context, ViewGroup parentView, int chatType) {
        super(context, parentView, chatType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_anchor;
    }

    @Override
    public void init() {
        super.init();
        if (mChatType == Constants.CHAT_TYPE_VOICE) {
            findViewById(R.id.space).setVisibility(View.VISIBLE);
        }
    }

}
