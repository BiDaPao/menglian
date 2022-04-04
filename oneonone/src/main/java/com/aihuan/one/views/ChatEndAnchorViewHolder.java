package com.aihuan.one.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatBaseActivity;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;


/**
 * Created by cxf on 2019/4/21.
 */

public class ChatEndAnchorViewHolder extends AbsChatEndViewHolder {

    private String mAudienceID;//观众的ID
    private String mSessionId;//通话的会话id
    private TextView mChatDuration;
    private TextView mChatIncome;
    private TextView mGiftIncome;


    public ChatEndAnchorViewHolder(Context context, ViewGroup parentView, String audienceID, String sessionId, boolean anchorHangUp) {
        super(context, parentView, audienceID, sessionId, anchorHangUp);
    }

    @Override
    protected void processArguments(Object... args) {
        mAudienceID = (String) args[0];
        mSessionId = (String) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_end_anchor;
    }

    @Override
    public void init() {
        super.init();
        mChatDuration = (TextView) findViewById(R.id.chat_duration);
        mChatIncome = (TextView) findViewById(R.id.chat_income);
        mGiftIncome = (TextView) findViewById(R.id.gift_income);
    }

    @Override
    protected void confirmClick() {
        ((ChatBaseActivity) mContext).finish();
    }


    public void loadData() {
        OneHttpUtil.chatAnchorHangUp(mAudienceID, mSessionId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mChatDuration != null) {
                        mChatDuration.setText(obj.getString("length"));
                    }
                    if (mChatIncome != null) {
                        mChatIncome.setText(obj.getString("answertotal"));
                    }
                    if (mGiftIncome != null) {
                        mGiftIncome.setText(obj.getString("gifttotal"));
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.CHAT_ANCHOR_HANG_UP);
        super.onDestroy();
    }
}
