package com.aihuan.im.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.ChargeSuccessBean;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.MatchSuccessEvent;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.im.event.ChatLiveImEvent;
import com.aihuan.im.event.SystemMsgEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2019/4/19.
 */

public class ChatLiveImUtil {

    private static final String IM_SYS_NOTICE = "sysnotice";//系统通知
    public static final String IM_CHAT_CALL = "call";//通话消息
    public static final String IM_CHAT_HANDLE = "livehandle";//通话中的操作
    public static final String IM_GIFT = "sendgift";//礼物
    public static final String IM_CHARGE = "charge";//充值

    public static final byte ACTION_AUD_START = 0;//观众发起通话
    public static final byte ACTION_AUD_CANCEL = 1;//观众取消通话
    public static final byte ACTION_ANC_START = 2;//主播发起通话
    public static final byte ACTION_ANC_CANCEL = 3;//主播取消通话
    public static final byte ACTION_ANC_ACCPET = 4;//主播接听通话
    public static final byte ACTION_ANC_REFUSE = 5;//主播拒绝通话
    public static final byte ACTION_AUD_ACCPET = 6;//观众接听通话
    public static final byte ACTION_AUD_REFUSE = 7;//观众拒绝通话
    public static final byte ACTION_ANC_HANG_UP = 8;//主播挂断通话
    public static final byte ACTION_AUD_HANG_UP = 9;//观众挂断通话
    public static final byte ACTION_AUD_PUSH = 10;//观众推流成功
    public static final byte ACTION_ANC_PUSH = 11;//主播推流成功
    public static final byte ACTION_MATCH = 12;//匹配成功
    public static final byte ACTION_CAMERA = 100;//观众打开或关闭摄像头

    public static final String METHOD = "method";
    private static final String ACTION = "action";
    private static final String SESSION_ID = "showid";
    private static final String CHAT_TYPE = "type";
    private static final String CONTENT = "content";
    private static final String AVATAR = "avatar";
    private static final String USER_NAME = "user_nickname";
    private static final String UID = "id";
    private static final String PULL = "pull";
    private static final String PUSH = "push";
    private static final String CHAT_PRICE = "total";
    private static final String LEVEL_ANCHOR = "level_anchor";


    public static void onNewMessage(JSONObject obj, String senderId) {
        if (obj == null) {
            return;
        }
        switch (obj.getString(METHOD)) {
            case IM_SYS_NOTICE:
                onSystemNotice();
                break;
            case IM_CHARGE:
                onChargeMsg(obj);
                break;
            case IM_CHAT_CALL:
                onChatCall(obj, senderId);
                break;
            case IM_CHAT_HANDLE:
                onChatHandle(obj, senderId);
                break;
        }
    }

    /**
     * 收到系统消息
     */
    private static void onSystemNotice() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, true);
        EventBus.getDefault().post(new SystemMsgEvent());
    }

    /**
     * 收到充值消息
     */
    private static void onChargeMsg(JSONObject obj) {
        if (CommonAppConfig.getInstance().isLaunched()) {
            EventBus.getDefault().post(obj.toJavaObject(ChargeSuccessBean.class));
        }
    }


    /**
     * 聊天通话消息
     */
    private static void onChatCall(JSONObject obj, String senderId) {
        if (obj == null) {
            return;
        }
        switch (obj.getIntValue(ACTION)) {
            case ACTION_AUD_START://观众发起通话
                onChatAudToAncStart(obj, senderId);
                break;
            case ACTION_AUD_CANCEL://观众取消通话
                onChatAudToAncCancel(senderId);
                break;
            case ACTION_ANC_ACCPET://主播同意接听通话
                onChatAnchorAccpet(senderId);
                break;
            case ACTION_ANC_REFUSE://主播拒绝通话
                onChatAnchorRefuse(senderId);
                break;
            case ACTION_ANC_PUSH://主播推流成功
                onChatAnchorPushSuccess(obj.getString(PULL), senderId);
                break;
            case ACTION_AUD_PUSH://观众推流成功
                onChatAudiencePushSuccess(obj.getString(PULL), senderId);
                break;
            case ACTION_AUD_HANG_UP://观众挂断通话
                onChatAudienceHangUp(senderId);
                break;
            case ACTION_ANC_HANG_UP://主播挂断通话
                onChatAnchorHangUp(senderId);
                break;
            case ACTION_ANC_START://主播发起通话
                onChatAncToAndStart(obj, senderId);
                break;
            case ACTION_AUD_REFUSE://观众拒绝通话
                onChatAudienceRefuse(senderId);
                break;
            case ACTION_ANC_CANCEL://主播取消通话
                onChatAnchorCancel(senderId);
                break;
            case ACTION_AUD_ACCPET://观众接听通话
                onChatAudienceAccpet(senderId);
                break;
            case ACTION_MATCH://匹配成功
                onMatchSuccess(obj);
                break;
        }
    }

    /**
     * 聊天通话消息,观众打开或关闭摄像头
     */
    private static void onChatHandle(JSONObject obj, String senderId) {
        if (obj == null) {
            return;
        }
        onChatAudienceCamera(obj.getByteValue(ACTION) == 2, senderId);
    }


    /**
     * 观众向主播发起通话
     *
     * @param toUid         对方的id 即主播的id
     * @param chatSessionId 通话的会话id
     * @param chatType      通话类型
     */
    public static void chatAudToAncStart(String toUid, String chatSessionId, int chatType) {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_START);
        obj.put(UID, u.getId());
        obj.put(USER_NAME, u.getUserNiceName());
        obj.put(AVATAR, u.getAvatar());
        obj.put(SESSION_ID, chatSessionId);
        obj.put(CHAT_TYPE, chatType);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }


    /**
     * 主播向观众发起通话
     *
     * @param toUid         对方的id 即观众的id
     * @param chatSessionId 通话的会话id
     * @param chatType      通话类型
     * @param price         通话价格
     */
    public static void chatAncToAudStart(String toUid, String chatSessionId, int chatType, String price) {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_START);
        obj.put(UID, u.getId());
        obj.put(USER_NAME, u.getUserNiceName());
        obj.put(AVATAR, u.getAvatar());
        obj.put(LEVEL_ANCHOR, u.getLevelAnchor());
        obj.put(SESSION_ID, chatSessionId);
        obj.put(CHAT_TYPE, chatType);
        obj.put(CHAT_PRICE, price);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }

    /**
     * 观众向主播发起通话，主播收到观众主动发起的通话邀请
     */
    private static void onChatAudToAncStart(JSONObject obj, String senderId) {
        String audienceId = obj.getString(UID);
        if (!TextUtils.isEmpty(audienceId) && audienceId.equals(senderId)) {
            ChatAnchorParam param = new ChatAnchorParam();
            param.setSessionId(obj.getString(SESSION_ID));
            param.setChatType(obj.getIntValue(CHAT_TYPE));
            param.setAudienceID(audienceId);
            param.setAudienceAvatar(obj.getString(AVATAR));
            param.setAudienceName(obj.getString(USER_NAME));
            param.setAnchorActive(false);
            if (CommonAppConfig.getInstance().isLaunched()) {
                RouteUtil.forwardAnchorActivity(param);
            } else {
                RouteUtil.forwardMainActivity(param);
            }
        }
    }


    /**
     * 主播向观众发起通话，观众收到主播主动发起的通话邀请
     */
    private static void onChatAncToAndStart(JSONObject obj, String senderId) {
        String anchorId = obj.getString(UID);
        if (!TextUtils.isEmpty(anchorId) && anchorId.equals(senderId)) {
            ChatAudienceParam param = new ChatAudienceParam();
            param.setSessionId(obj.getString(SESSION_ID));
            param.setChatType(obj.getIntValue(CHAT_TYPE));
            param.setAnchorID(anchorId);
            param.setAnchorAvatar(obj.getString(AVATAR));
            param.setAnchorName(obj.getString(USER_NAME));
            param.setAnchorLevel(obj.getIntValue(LEVEL_ANCHOR));
            param.setAnchorPrice(obj.getString(CHAT_PRICE));
            param.setAudienceActive(false);
            if (CommonAppConfig.getInstance().isLaunched()) {
                RouteUtil.forwardAudienceActivity(param);
            } else {
                RouteUtil.forwardMainActivity(param);
            }
        }
    }

    /**
     * 观众向主播发起通话，观众主动取消通话
     *
     * @param toUid 对方的id 即主播的id
     */
    public static void chatAudToAncCancel(String toUid, int chatType) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_CANCEL);
        obj.put(CHAT_TYPE, chatType);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }

    /**
     * 观众向主播发起通话，主播收到观众主动取消通话的消息
     */
    private static void onChatAudToAncCancel(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_AUD_CANCEL, senderId));
    }

    /**
     * 主播向观众发起通话，主播主动取消通话
     *
     * @param toUid 对方的id 即观众的id
     */
    public static void chatAncToAudCancel(String toUid, int chatType) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_CANCEL);
        obj.put(CHAT_TYPE, chatType);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }

    /**
     * 主播向观众发起通话，观众收到主播主动取消通话的消息
     */
    private static void onChatAnchorCancel(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_ANC_CANCEL, senderId));
    }

    /**
     * 观众向主播发起通话，主播拒绝通话
     */
    public static void chatAnchorRefuse(String toUid, int chatType) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_REFUSE);
        obj.put(CHAT_TYPE, chatType);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }

    /**
     * 观众向主播发起通话，观众收到主播拒绝通话的消息
     */
    private static void onChatAnchorRefuse(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_ANC_REFUSE, senderId));
    }

    /**
     * 主播向观众发起通话，观众拒绝通话
     */
    public static void chatAudienceRefuse(String toUid, int chatType) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_REFUSE);
        obj.put(CHAT_TYPE, chatType);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }

    /**
     * 主播向观众发起通话，主播收到观众拒绝通话的消息
     */
    private static void onChatAudienceRefuse(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_AUD_REFUSE, senderId));
    }

    /**
     * 观众向主播发起通话，主播同意通话
     */
    public static void chatAnchorAccpet(String toUid) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_ACCPET);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }

    /**
     * 观众向主播发起通话，观众收到主播同意通话的消息
     */
    private static void onChatAnchorAccpet(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_ANC_ACCPET, senderId));
    }

    /**
     * 主播向观众发起通话，观众同意通话
     */
    public static void chatAudienceAccpet(String toUid) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_ACCPET);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }

    /**
     * 主播向观众发起通话，主播收到观众同意通话的消息
     */
    private static void onChatAudienceAccpet(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_AUD_ACCPET, senderId));
    }

    /**
     * 观众推流成功，把播放地址发给主播
     *
     * @param playUrl 观众的播放地址
     */
    public static void chatAudiencePushSuccess(String toUid, String playUrl) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_PUSH);
        obj.put(PULL, playUrl);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }

    /**
     * 主播收到观众推流成功，发过来的播流地址
     *
     * @param andiencePlayUrl 观众的播放地址
     */
    private static void onChatAudiencePushSuccess(String andiencePlayUrl, String senderId) {
        ChatLiveImEvent e = new ChatLiveImEvent(ACTION_AUD_PUSH, senderId);
        e.setAudiencePlayUrl(andiencePlayUrl);
        EventBus.getDefault().post(e);
    }


    /**
     * 主播推流成功，把播放地址发给观众
     *
     * @param playUrl 观众的播放地址
     */
    public static void chatAnchorPushSuccess(String toUid, String playUrl) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_PUSH);
        obj.put(PULL, playUrl);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }


    /**
     * 观众收到主播推流成功，发过来的播流地址
     *
     * @param anchorPlayUrl 主播的播放地址
     */
    private static void onChatAnchorPushSuccess(String anchorPlayUrl, String senderId) {
        ChatLiveImEvent e = new ChatLiveImEvent(ACTION_ANC_PUSH, senderId);
        e.setAnchorPlayUrl(anchorPlayUrl);
        EventBus.getDefault().post(e);
    }


    /**
     * 观众在通话过程中，主动挂断通话
     *
     * @param toUid 对方的id 即主播的id
     */
    public static void chatAudienceHangUp(String toUid, int chatType, String chatDuration) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_AUD_HANG_UP);
        obj.put(CHAT_TYPE, chatType);
        obj.put(CONTENT, chatDuration);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }


    /**
     * 主播在通话过程中，收到观众主动挂断通话的消息
     */
    private static void onChatAudienceHangUp(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_AUD_HANG_UP, senderId));
    }


    /**
     * 主播在通话过程中，主动挂断通话
     *
     * @param toUid 对方的id 即观众的id
     */
    public static void chatAnchorHangUp(String toUid, int chatType, String chatDuration) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_CALL);
        obj.put(ACTION, ACTION_ANC_HANG_UP);
        obj.put(CHAT_TYPE, chatType);
        obj.put(CONTENT, chatDuration);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString());
    }

    /**
     * 观众在通话过程中，收到主播主动挂断通话的消息
     */
    private static void onChatAnchorHangUp(String senderId) {
        EventBus.getDefault().post(new ChatLiveImEvent(ACTION_ANC_HANG_UP, senderId));
    }


    /**
     * 观众在通话过程中，打开和关闭摄像头
     *
     * @param openCamera true打开 false关闭
     * @param toUid      对方的id 即主播的id
     */
    public static void chatAudienceCamera(boolean openCamera, String toUid) {
        JSONObject obj = new JSONObject();
        obj.put(METHOD, IM_CHAT_HANDLE);
        obj.put(ACTION, openCamera ? 2 : 1);
        ImMessageUtil.getInstance().sendCustomMessage(toUid, obj.toJSONString(), false);
    }

    /**
     * 主播在通话过程中，收到观众打开和关闭摄像头的消息
     */
    private static void onChatAudienceCamera(boolean openCamera, String senderId) {
        ChatLiveImEvent e = new ChatLiveImEvent(ACTION_CAMERA, senderId);
        e.setAudienceCameraOpen(openCamera);
        EventBus.getDefault().post(e);
    }

    /**
     * 匹配成功
     */
    private static void onMatchSuccess(JSONObject obj) {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null || !CommonAppConfig.getInstance().isLaunched()) {
            return;
        }
        if (u.hasAuth()) {
            ChatAnchorParam param = new ChatAnchorParam();
            param.setSessionId(obj.getString(SESSION_ID));
            param.setChatType(obj.getIntValue(CHAT_TYPE));
            param.setAudienceID(obj.getString(UID));
            param.setAudienceAvatar(obj.getString(AVATAR));
            param.setAudienceName(obj.getString(USER_NAME));
            param.setAnchorPushUrl(obj.getString(PUSH));
            param.setAnchorPlayUrl(obj.getString(PULL));
            param.setAnchorActive(false);
            param.setMatch(true);
            RouteUtil.forwardAnchorActivity(param);
        } else {
            ChatAudienceParam param = new ChatAudienceParam();
            param.setSessionId(obj.getString(SESSION_ID));
            param.setChatType(obj.getIntValue(CHAT_TYPE));
            param.setAnchorID(obj.getString(UID));
            param.setAnchorAvatar(obj.getString(AVATAR));
            param.setAnchorName(obj.getString(USER_NAME));
            param.setAnchorLevel(obj.getIntValue(LEVEL_ANCHOR));
            param.setAudiencePushUrl(obj.getString(PUSH));
            param.setAudiencePlayUrl(obj.getString(PULL));
            param.setAudienceActive(false);
            param.setMatch(true);
            RouteUtil.forwardAudienceActivity(param);
        }
        EventBus.getDefault().post(new MatchSuccessEvent());
    }
}
