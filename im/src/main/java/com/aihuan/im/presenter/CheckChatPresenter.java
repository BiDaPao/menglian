package com.aihuan.im.presenter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.R;
import com.aihuan.im.http.ImHttpConsts;
import com.aihuan.im.http.ImHttpUtil;


/**
 * Created by cxf on 2017/9/29.
 */

public class CheckChatPresenter {

    private HttpCallback mAudToAncCallback;//观众邀请主播的回调
    private Context mContext;

    public CheckChatPresenter(Context context) {
        mContext = context;
    }

    /**
     * 观众向主播发起通话邀请
     *
     * @param liveUid    主播的uid
     * @param chatType   通话类型
     * @param toUserBean 主播的userBean
     */
    public void chatAudToAncStart(String liveUid, final int chatType, final UserBean toUserBean) {
        if (TextUtils.isEmpty(liveUid) || toUserBean == null) {
            return;
        }
        if (mAudToAncCallback == null) {
            mAudToAncCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            ChatAudienceParam param = new ChatAudienceParam();
                            param.setAnchorID(toUserBean.getId());
                            param.setAnchorName(toUserBean.getUserNiceName());
                            param.setAnchorAvatar(toUserBean.getAvatar());
                            param.setAnchorLevel(toUserBean.getLevelAnchor());
                            param.setSessionId(obj.getString("showid"));
                            param.setAudiencePlayUrl(obj.getString("pull"));
                            param.setAudiencePushUrl(obj.getString("push"));
                            param.setAnchorPrice(obj.getString("total"));
                            param.setChatType(obj.getIntValue("type"));
                            param.setAudienceActive(true);
                            RouteUtil.forwardAudienceActivity(param);
                        }
                    } else if (code == 800) {
                        if (mContext != null) {
                            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.chat_not_response), new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    ImHttpUtil.audSubscribeAnc(toUserBean.getId(), chatType);
                                    ToastUtil.show(R.string.chat_subcribe_success);
                                }
                            });
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            };
        }
        ImHttpUtil.chatAudToAncStart(liveUid, chatType, mAudToAncCallback);
    }


    public void cancel() {
        ImHttpUtil.cancel(ImHttpConsts.CHAT_AUD_TO_ANC_START);
    }

}
