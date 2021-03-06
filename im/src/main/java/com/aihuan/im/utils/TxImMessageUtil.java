package com.aihuan.im.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.aihuan.common.utils.MediaManager;
import com.aihuan.im.event.ImCensorMsgEvent;
import com.aihuan.im.event.ImUnReadIntimacyCountEvent;
import com.aihuan.im.event.SystemMsgEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMLocationElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.session.SessionWrapper;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.ChatReceiveGiftBean;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.TxImCacheUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.R;
import com.aihuan.im.bean.ChatInfoBean;
import com.aihuan.im.bean.ImMessageBean;
import com.aihuan.im.bean.ImMsgLocationBean;
import com.aihuan.im.bean.ImUserBean;
import com.aihuan.im.event.ImUnReadCountEvent;
import com.aihuan.im.event.ImUserMsgEvent;
import com.aihuan.im.interfaces.ImClient;
import com.aihuan.im.interfaces.SendMsgResultCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cxf on 2019/4/2.
 */

public class TxImMessageUtil implements ImClient, TIMMessageListener, TIMOfflinePushListener {

    private static final String TAG = "??????IM";
    public static final String PREFIX = "";
    //private Map<String, Long> mMap;
    private SimpleDateFormat mSimpleDateFormat;
    private String mImageString;
    private String mVoiceString;
    private String mLocationString;
    private String mCustomString;
    private String mChatString;
    private String mChatCancel1;
    private String mChatCancel2;
    private String mChatRefuse1;
    private String mChatRefuse2;
    private TIMCallBack mUnReadCountCallBack;
    private TIMCallBack mEmptyCallBack;
    private SendMsgResultCallback mSendMsgResultCallback;
    private TIMValueCallBack<TIMMessage> mSendCompleteCallback;
    private TIMValueCallBack<TIMMessage> mSendCustomCallback;
    private StringBuilder mStringBuilder;
    private String mGroupId;
    private SoundPool mSoundPool;
    private int mSoundId = -1;
    private boolean mOpenChatActivity;//?????????????????????activity
    private boolean mCloseChatMusic;//?????????????????????
    private Handler mHandler;


    public TxImMessageUtil() {
        //mMap = new HashMap<>();
        mStringBuilder = new StringBuilder();
        mSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        mImageString = WordUtil.getString(R.string.im_type_image);
        mVoiceString = WordUtil.getString(R.string.im_type_voide);
        mLocationString = WordUtil.getString(R.string.im_type_location);
        mCustomString = WordUtil.getString(R.string.im_type_custom);
        mChatString = WordUtil.getString(R.string.im_type_chat);
        mChatCancel1 = WordUtil.getString(R.string.im_type_chat_cancel_1);
        mChatCancel2 = WordUtil.getString(R.string.im_type_chat_cancel_2);
        mChatRefuse1 = WordUtil.getString(R.string.im_type_chat_refuse_1);
        mChatRefuse2 = WordUtil.getString(R.string.im_type_chat_refuse_2);
        mCloseChatMusic = SpUtil.getInstance().getBooleanValue(SpUtil.CHAT_MUSIC_CLOSE);
        mUnReadCountCallBack = new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                refreshAllUnReadMsgCount();
            }
        };
        mEmptyCallBack = new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ToastUtil.show(WordUtil.getString(R.string.net_work_broken));
            }
        };
    }


    /**
     * ???app????????????uid??????IM?????????uid
     */
    private String getImUid(String uid) {
        return PREFIX + uid;
    }

    /**
     * ???IM?????????uid??????app?????????uid
     */
    private String getAppUid(String from) {
        if (!TextUtils.isEmpty(from) && from.length() > PREFIX.length() && from.startsWith(PREFIX)) {
            return from.substring(PREFIX.length());
        }
        return "";
    }

    /**
     * ??????IM?????? ??????App?????????uid
     */
    private String getAppUid(TIMMessage msg) {
        if (msg == null) {
            return "";
        }
        String peer = msg.getConversation().getPeer();
        if (TextUtils.isEmpty(peer)) {
            return "";
        }
        return getAppUid(peer);
    }


    @Override
    public void init() {
        TIMManager timManager = TIMManager.getInstance();
        //??????????????????????????? ????????? SDK ????????????
        if (SessionWrapper.isMainProcess(CommonAppContext.sInstance)) {
            TIMSdkConfig config = new TIMSdkConfig(CommonAppConfig.TX_IM_APP_Id)
                    .enableLogPrint(false)
                    .setLogLevel(TIMLogLevel.OFF);

            timManager.init(CommonAppContext.sInstance, config);
        }

        //??????????????????
        TIMUserConfig userConfig = new TIMUserConfig();
        //???????????????????????????????????????
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                L.e(TAG, "????????????????????????---->");
                RouteUtil.forwardLoginInvalid(WordUtil.getString(R.string.login_status_Invalid));
            }

            @Override
            public void onUserSigExpired() {
                L.e(TAG, "??????????????????????????????????????????---->");
            }
        });
        //?????????????????????????????????
        userConfig.setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                L.e(TAG, "????????????---->");
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void onDisconnected(int code, String desc) {
                L.e(TAG, "????????????---->");
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }
            }

            @Override
            public void onWifiNeedAuth(String name) {
                L.e(TAG, "onWifiNeedAuth");
            }
        });

//        //???????????????????????????
//        userConfig.setGroupEventListener(new TIMGroupEventListener() {
//            @Override
//            public void onGroupTipsEvent(TIMGroupTipsElem elem) {
//                L.e(TAG, "??????????????????----->" + elem.getTipsType());
//            }
//        });
        //???????????????????????????
        userConfig.setRefreshListener(new TIMRefreshListener() {
            @Override
            public void onRefresh() {
                //L.e(TAG, "????????????---onRefresh----->");
            }

            @Override
            public void onRefreshConversation(List<TIMConversation> conversations) {
                // L.e(TAG, "????????????---onRefreshConversation---->size: " + conversations.size());
            }
        });
        timManager.setUserConfig(userConfig);
        timManager.addMessageListener(TxImMessageUtil.this);
        // ???????????????????????????
        timManager.setOfflinePushListener(this);
    }

    @Override
    public void loginImClient(String uid) {
        String sign = SpUtil.getInstance().getStringValue(SpUtil.TX_IM_USER_SIGN);
        if (TextUtils.isEmpty(sign)) {
            ToastUtil.show("??????IM??????????????? ???????????????");
            return;
        }
        TIMManager.getInstance().login(uid, sign, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                L.e(TAG, "???????????? : " + code + " errmsg: " + desc);
                CommonAppConfig.getInstance().setLoginIM(false);
                ToastUtil.show("IM ???????????????" + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                L.e(TAG, "??????????????????");
                CommonAppConfig.getInstance().setLoginIM(true);
                refreshAllUnReadMsgCount();
                ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
                if (configBean != null) {
                    String groupId = configBean.getTxImGroupId();
                    L.e(TAG, "??????ID------> " + groupId);
                    if (!TextUtils.isEmpty(groupId)) {
                        mGroupId = groupId;
                        TIMGroupManager.getInstance().applyJoinGroup(groupId, "login", new TIMCallBack() {
                            @java.lang.Override
                            public void onError(int code, String desc) {
                                L.e(TAG, "?????????????????? : " + code + " errmsg: " + desc);
                            }

                            @java.lang.Override
                            public void onSuccess() {
                                L.e(TAG, "????????????????????????");
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void logoutImClient() {
        TIMManager timManager = TIMManager.getInstance();
        timManager.logout(null);
        CommonAppConfig.getInstance().setLoginIM(false);
        L.e(TAG, "????????????--->");
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        for (TIMMessage msg : list) {
            onReceiveMessage(msg);
        }
        return true; //??????true????????????????????????????????????????????????????????????
    }


    private void onReceiveMessage(TIMMessage msg) {
        if (msg == null) {
            return;
        }
        L.e("IM????????? --- >" + msg.toString());
        //??????????????????????????????
        if ("@TIM#SYSTEM".equals(msg.getSender())) {
            if (msg.timestamp() < CommonAppConfig.getInstance().getLaunchTime()) {
                return;
            }
            if (msg.getElementCount() > 0) {
                TIMElem elem0 = msg.getElement(0);
                if (elem0 instanceof TIMGroupSystemElem) {
                    TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem0;
                    if (systemElem.getGroupId().equals(mGroupId)) {
                        String data = new String(systemElem.getUserData());
                        L.e(TAG, "????????????--------> " + data);
                        try {
                            ChatLiveImUtil.onNewMessage(JSON.parseObject(data), null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        }
        String uid = getAppUid(msg);
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int type = getMessageType(msg);
        if (type == 0) {
            String customMsgData = getCustomMsgData(msg);
            if (!TextUtils.isEmpty(customMsgData)) {
                L.e(TAG, "???????????????---> " + customMsgData);
                try {
                    JSONObject obj = JSON.parseObject(customMsgData);
                    L.e(TAG, "???????????????--obj-> " + obj);
                    //??????uid == admin  ???????????????????????????????????????????????????????????????  suggestion == 1 ?????????????????????
                    if (obj != null && Constants.IM_MSG_ADMIN.equals(uid)) {
                        int suggestion = obj.getIntValue("Suggestion");
                        if (suggestion == 1) {
                            String content = obj.getString("Content");
                            EventBus.getDefault().post(new ImCensorMsgEvent(content));
                            showMessage(uid, msg, type, null, null);
                            return;
                        }
                    }
                    if (obj != null) {
                        String method = obj.getString(ChatLiveImUtil.METHOD);
                        if (ChatLiveImUtil.IM_GIFT.equals(method)) {//??????????????????
                            type = ImMessageBean.TYPE_GIFT;
                            showMessage(uid, msg, type, obj.toJavaObject(ChatReceiveGiftBean.class), null);
                        } else if (ChatLiveImUtil.IM_CHAT_CALL.equals(method)) {
                            MediaManager.getInstance().stopPlayRecord();
                            ChatLiveImUtil.onNewMessage(obj, uid);
                            type = ImMessageBean.TYPE_CHAT;
                            ChatInfoBean bean = obj.toJavaObject(ChatInfoBean.class);
                            byte action = bean.getAction();
                            if (action == ChatLiveImUtil.ACTION_AUD_START
                                    || action == ChatLiveImUtil.ACTION_ANC_START
                                    || action == ChatLiveImUtil.ACTION_ANC_ACCPET
                                    || action == ChatLiveImUtil.ACTION_AUD_ACCPET
                                    || action == ChatLiveImUtil.ACTION_AUD_PUSH
                                    || action == ChatLiveImUtil.ACTION_ANC_PUSH
                                    || action == ChatLiveImUtil.ACTION_MATCH
                            ) {
                                removeMessage(msg);
                                return;
                            }
                            if (action == ChatLiveImUtil.ACTION_AUD_CANCEL || action == ChatLiveImUtil.ACTION_ANC_CANCEL) {
                                bean.setContent(mChatCancel2);
                            } else if (action == ChatLiveImUtil.ACTION_ANC_REFUSE || action == ChatLiveImUtil.ACTION_AUD_REFUSE) {
                                bean.setContent(mChatRefuse2);
                            }
                            showMessage(uid, msg, type, null, bean);
                        } else if (ChatLiveImUtil.IM_CHAT_HANDLE.equals(method)) {
                            ChatLiveImUtil.onNewMessage(obj, uid);
                            removeMessage(msg);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (type == 0) {
            return;
        }
//        boolean canShow = true;
//        Long lastTime = mMap.get(uid);
//        long curTime = System.currentTimeMillis();
//        if (lastTime != null) {
//            if (curTime - lastTime < 1000) {
//                //?????????????????????????????????????????????????????????1??????????????????????????????
//                canShow = false;
//            } else {
//                mMap.put(uid, curTime);
//            }
//        } else {
//            //??????sMap????????????????????????????????????????????????????????????????????????????????????
//            mMap.put(uid, curTime);
//        }
//        if (canShow) {
//            L.e(TAG, "????????????--->");
//            EventBus.getDefault().post(new ImMessageBean(uid, msg, type, false));
//            ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
//            imUserMsgEvent.setUid(uid);
//            imUserMsgEvent.setLastMessage(getMessageString(msg));
//            imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
//            imUserMsgEvent.setLastTime(getMessageTimeString(msg));
//            EventBus.getDefault().post(imUserMsgEvent);
//            refreshAllUnReadMsgCount();
//        }

        showMessage(uid, msg, type, null, null);
    }


    private void showMessage(String uid, TIMMessage msg, int type, ChatReceiveGiftBean giftBean, ChatInfoBean chatInfoBean) {
        ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
        bean.setGiftBean(giftBean);
        bean.setChatInfoBean(chatInfoBean);
        EventBus.getDefault().post(bean);

        ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
        imUserMsgEvent.setUid(uid);
        imUserMsgEvent.setLastMessage(getMessageString(msg));
        imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
        imUserMsgEvent.setLastTime(getMessageTimeString(msg));
        EventBus.getDefault().post(imUserMsgEvent);
        refreshAllUnReadMsgCount();

        if (type == ImMessageBean.TYPE_TEXT || type == ImMessageBean.TYPE_IMAGE || (type == ImMessageBean.TYPE_GIFT && !msg.isSelf())) {
            playRing();
        }
    }


    @Override
    public String getConversationUids() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        mStringBuilder.delete(0, mStringBuilder.length());
        if (list != null) {
            for (TIMConversation conversation : list) {
                if (conversation.getType() != TIMConversationType.C2C) {
                    continue;
                }
                String peer = conversation.getPeer();
                if (TextUtils.isEmpty(peer) || (!Constants.IM_MSG_ADMIN.equals(peer)) && peer.length() < 6) {
                    continue;
                }
                String from = getAppUid(peer);
                if (!TextUtils.isEmpty(from)) {
                    mStringBuilder.append(from);
                    mStringBuilder.append(",");
                }
            }
        }
        String s = mStringBuilder.toString();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Override
    public List<ImUserBean> getLastMsgInfoList(List<ImUserBean> list) {
        if (list == null) {
            return null;
        }
        TIMManager timManager = TIMManager.getInstance();
        for (ImUserBean bean : list) {
            TIMConversation conversation = timManager.getConversation(TIMConversationType.C2C, getImUid(bean.getId()));
            if (conversation != null) {
                bean.setHasConversation(true);
//                TIMConversationExt conExt = new TIMConversationExt(conversation);
//                TIMMessage msg = conExt.getLastMsg();
                TIMMessage msg =  conversation.getLastMsg();
                if (msg != null) {
                    bean.setLastTime(getMessageTimeString(msg));
                    bean.setLastTimeStamp(msg.timestamp());
                    bean.setUnReadCount((int) conversation.getUnreadMessageNum());
                    bean.setLastMessage(getMessageString(msg));
                }
            } else {
                bean.setHasConversation(false);
            }
        }
        return list;
    }

    /**
     * ???????????????????????????
     */
    private String getMessageTimeString(TIMMessage message) {
        return mSimpleDateFormat.format(new Date(message.timestamp() * 1000));
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????null
     */
    private String getCustomMsgData(TIMMessage msg) {
        if (msg == null || msg.getElementCount() <= 0) {
            return null;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0.getType() != TIMElemType.Custom) {
            return null;
        }
        TIMCustomElem elem = (TIMCustomElem) elem0;
        return new String(elem.getData());
    }

    /**
     * ?????????????????????
     */
    private int getMessageType(TIMMessage msg) {
        int type = 0;
        if (msg == null || msg.getElementCount() <= 0) {
            return type;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null) {
            return type;
        }
        TIMElemType elemType = elem0.getType();
        switch (elemType) {
            case Text://??????
                type = ImMessageBean.TYPE_TEXT;
                break;
            case Image://??????
                type = ImMessageBean.TYPE_IMAGE;
                break;
            case Sound://??????
                type = ImMessageBean.TYPE_VOICE;
                break;
            case Location://??????
                type = ImMessageBean.TYPE_LOCATION;
                break;
        }
        return type;
    }


    /**
     * ??????????????????????????????
     */
    private String getMessageString(TIMMessage msg) {
        String result = "";
        if (msg == null || msg.getElementCount() <= 0) {
            return result;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null) {
            return result;
        }
        TIMElemType elemType = elem0.getType();
        switch (elemType) {
            case Text://??????
                result = ((TIMTextElem) elem0).getText();
                break;
            case Image://??????
                result = mImageString;
                break;
            case Sound://??????
                result = mVoiceString;
                break;
            case Location://??????
                result = mLocationString;
                break;
            case Custom:
                String customMsgData = getCustomMsgData(msg);
                if (!TextUtils.isEmpty(customMsgData)) {
                    try {
                        JSONObject obj = JSON.parseObject(customMsgData);
                        //??????uid == admin  ???????????????????????????????????????????????????????????????  suggestion == 1 ?????????????????????
                        if (obj != null && Constants.IM_MSG_ADMIN.equals(msg.getSender())) {
                            int suggestion = obj.getIntValue("Suggestion");
                            if (suggestion == 1) {
                                result = obj.getString("Content");
                                break;
                            }
                        }

                        if (obj != null) {
                            String method = obj.getString(ChatLiveImUtil.METHOD);
                            if (ChatLiveImUtil.IM_GIFT.equals(method)) {//??????????????????
                                result = String.format(mCustomString, obj.getString("giftname"));
                            } else if (ChatLiveImUtil.IM_CHAT_CALL.equals(method)) {//??????????????????
                                result = mChatString;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        return result;
    }

    @Override
    public void getChatMessageList(String toUid, final CommonCallback<List<ImMessageBean>> callback) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
//        TIMConversationExt conExt = new TIMConversationExt(conversation);
//        conExt.getLocalMessage(50, //???????????????????????? 50 ?????????
        conversation.getLocalMessage(50, //???????????????????????? 50 ?????????
                null, //null ??????????????????????????????
                new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????? code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> list) {//??????????????????
                        if (list != null && list.size() > 0) {
                            List<ImMessageBean> result = new ArrayList<>();
                            for (int i = list.size() - 1; i >= 0; i--) {
                                TIMMessage msg = list.get(i);
                                String from = getAppUid(msg);
                                int type = getMessageType(msg);
                                if (type == 0) {
                                    String customMsgData = getCustomMsgData(msg);
                                    if (!TextUtils.isEmpty(customMsgData)) {
                                        try {
                                            JSONObject obj = JSON.parseObject(customMsgData);
                                            //??????uid == admin  ???????????????????????????????????????????????????????????????  suggestion == 1 ?????????????????????
                                            if (obj != null && Constants.IM_MSG_ADMIN.equals(from)) {
                                                int suggestion = obj.getIntValue("Suggestion");
                                                if (suggestion == 1) {
                                                    boolean self = msg.isSelf();
                                                    ImMessageBean bean = new ImMessageBean(from, msg, ImMessageBean.TYPE_TEXT, self);
                                                    if (self && msg.status() == TIMMessageStatus.SendFail) {
                                                        bean.setSendFail(true);
                                                    }
                                                    result.add(bean);
                                                }
                                            }
                                            if (obj != null) {
                                                String method = obj.getString(ChatLiveImUtil.METHOD);
                                                if (ChatLiveImUtil.IM_CHAT_HANDLE.equals(method)) {
                                                    continue;
                                                } else if (ChatLiveImUtil.IM_GIFT.equals(method)) {//??????????????????
                                                    type = ImMessageBean.TYPE_GIFT;
                                                    if (!TextUtils.isEmpty(from)) {
                                                        boolean self = msg.isSelf();
                                                        ImMessageBean bean = new ImMessageBean(from, msg, type, self);
                                                        bean.setGiftBean(obj.toJavaObject(ChatReceiveGiftBean.class));
                                                        if (self && msg.status() == TIMMessageStatus.SendFail) {
                                                            bean.setSendFail(true);
                                                        }
                                                        result.add(bean);
                                                    }
                                                } else if (ChatLiveImUtil.IM_CHAT_CALL.equals(method)) {
                                                    type = ImMessageBean.TYPE_CHAT;
                                                    if (!TextUtils.isEmpty(from)) {
                                                        boolean self = msg.isSelf();
                                                        ImMessageBean bean = new ImMessageBean(from, msg, type, self);
                                                        ChatInfoBean chatInfoBean = obj.toJavaObject(ChatInfoBean.class);
                                                        byte action = chatInfoBean.getAction();
                                                        if (action == ChatLiveImUtil.ACTION_AUD_START
                                                                || action == ChatLiveImUtil.ACTION_ANC_START
                                                                || action == ChatLiveImUtil.ACTION_ANC_ACCPET
                                                                || action == ChatLiveImUtil.ACTION_AUD_ACCPET
                                                                || action == ChatLiveImUtil.ACTION_AUD_PUSH
                                                                || action == ChatLiveImUtil.ACTION_ANC_PUSH
                                                                || action == ChatLiveImUtil.ACTION_MATCH
                                                        ) {
                                                            continue;
                                                        }
                                                        if (action == ChatLiveImUtil.ACTION_AUD_CANCEL || action == ChatLiveImUtil.ACTION_ANC_CANCEL) {
                                                            chatInfoBean.setContent(self ? mChatCancel1 : mChatCancel2);
                                                        } else if (action == ChatLiveImUtil.ACTION_ANC_REFUSE || action == ChatLiveImUtil.ACTION_AUD_REFUSE) {
                                                            chatInfoBean.setContent(self ? mChatRefuse1 : mChatRefuse2);
                                                        }
                                                        bean.setChatInfoBean(chatInfoBean);
                                                        if (self && msg.status() == TIMMessageStatus.SendFail) {
                                                            bean.setSendFail(true);
                                                        }
                                                        result.add(bean);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(from)) {
                                        boolean self = msg.isSelf();
                                        ImMessageBean bean = new ImMessageBean(from, msg, type, self);
                                        if (self && msg.status() == TIMMessageStatus.SendFail) {
                                            bean.setSendFail(true);
                                        }
                                        result.add(bean);
                                    }
                                }
                            }
                            if (callback != null) {
                                callback.callback(result);
                            }
                        }
                    }
                });
    }

    @Override
    public int getUnReadMsgCount(String uid) {
        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(uid));
        if (con != null) {
//            TIMConversationExt conExt = new TIMConversationExt(con);
//            return (int) conExt.getUnreadMessageNum();
            return (int) con.getUnreadMessageNum();
        }
        return 0;
    }

    @Override
    public void refreshAllUnReadMsgCount() {
        EventBus.getDefault().post(new ImUnReadCountEvent(getAllUnReadMsgCount()));
    }

    @Override
    public String getAllUnReadMsgCount() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        int unReadCount = 0;
        if (list != null && list.size() > 0) {
            for (TIMConversation conversation : list) {
                if (conversation == null) {
                    continue;
                }
                if (conversation.getType() != TIMConversationType.C2C) {
                    continue;
                }
                String peer = conversation.getPeer();
                if (TextUtils.isEmpty(peer)) {
                    continue;
                }
                String uid = getAppUid(peer);
                if (TextUtils.isEmpty(uid)) {
                    continue;
                }
//                TIMConversationExt conExt = new TIMConversationExt(conversation);
                unReadCount += (int) conversation.getUnreadMessageNum();
            }
        }
        L.e(TAG, "??????????????????----->" + unReadCount);
        String res = "";
        if (unReadCount > 99) {
            res = "99+";
        } else {
            if (unReadCount < 0) {
                unReadCount = 0;
            }
            res = String.valueOf(unReadCount);
        }
        return res;
    }

    @Override
    public void markAllMessagesAsRead(String toUid, boolean needRefresh) {
        if (!TextUtils.isEmpty(toUid)) {
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
            if (con != null) {
//                TIMConversationExt conExt = new TIMConversationExt(con);
//                conExt.setReadMessage(null, needRefresh ? mUnReadCountCallBack : mEmptyCallBack);
                con.setReadMessage(null, needRefresh ? mUnReadCountCallBack : mEmptyCallBack);
            }
        }
    }

    /**
     * ???????????????????????????  ?????????????????????
     */
    @Override
    public void markAllConversationAsRead() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            for (TIMConversation conversation : list) {
                if (conversation != null) {
//                    TIMConversationExt conExt = new TIMConversationExt(conversation);
//                    conExt.setReadMessage(null, mEmptyCallBack);
                    conversation.setReadMessage(null,mEmptyCallBack);
                }
            }
        }
        EventBus.getDefault().post(new ImUnReadCountEvent("0"));
        EventBus.getDefault().post(new ImUnReadIntimacyCountEvent(0));

    }

    @Override
    public ImMessageBean createTextMessage(String toUid, String content) {
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(content);
        if (msg.addElement(elem) != 0) {
            L.e(TAG, "????????????????????????----->");
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_TEXT, true);
    }

    @Override
    public ImMessageBean createImageMessage(String toUid, String path) {
        TIMMessage msg = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        if (msg.addElement(elem) != 0) {
            L.e(TAG, "????????????????????????----->");
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_IMAGE, true);
    }


    @Override
    public ImMessageBean createLocationMessage(String toUid, double lat, double lng, int scale, String address) {
        TIMMessage msg = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        elem.setLatitude(lat);
        elem.setLongitude(lng);
        elem.setDesc(address);
        if (msg.addElement(elem) != 0) {
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_LOCATION, true);
    }

    @Override
    public ImMessageBean createVoiceMessage(String toUid, File voiceFile, long duration) {
        TIMMessage msg = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(voiceFile.getAbsolutePath());
        elem.setDuration(duration / 1000);
        if (msg.addElement(elem) != 0) {
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_VOICE, true);
    }

    /**
     * ?????????????????????
     *
     * @param toUid
     * @param data  ??????????????????
     */
    @Override
    public void sendCustomMessage(String toUid, String data, final boolean save) {
        final TIMMessage customMsg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        if (customMsg.addElement(elem) != 0) {
            return;
        }
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
        if (conversation != null) {
            if (mSendCustomCallback == null) {
                mSendCustomCallback = new TIMValueCallBack<TIMMessage>() {//??????????????????
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????????---> code: " + code + " errmsg: " + desc);
                        if (!save) {
                            removeMessage(customMsg);
                        }
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//??????????????????
                        L.e(TAG, "?????????????????????????????????");
                        if (!save) {
                            removeMessage(customMsg);
                        }
                    }
                };
            }
            conversation.sendMessage(customMsg, mSendCustomCallback);
        }
    }

    @Override
    public void sendMessage(String toUid, ImMessageBean bean, SendMsgResultCallback callback) {
        if (bean == null || TextUtils.isEmpty(toUid)) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null) {
            return;
        }
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
        if (conversation != null) {
            mSendMsgResultCallback = callback;
            if (mSendCompleteCallback == null) {
                mSendCompleteCallback = new TIMValueCallBack<TIMMessage>() {//??????????????????
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "??????????????????---> code: " + code + " errmsg: " + desc);
                        if (mSendMsgResultCallback != null) {
                            mSendMsgResultCallback.onSendFinish(false);
                        }
                        mSendMsgResultCallback = null;
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//??????????????????
                        if (mSendMsgResultCallback != null) {
                            mSendMsgResultCallback.onSendFinish(true);
                        }
                        mSendMsgResultCallback = null;
                    }
                };
            }
            conversation.sendMessage(msg, mSendCompleteCallback);
        }

    }

    /**
     * ????????????
     */
    private boolean removeMessage(TIMMessage msg) {
        if (msg != null) {
            TIMMessageExt timMessageExt = new TIMMessageExt(msg);
            return timMessageExt.remove();
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param toUid
     * @param bean
     */
    @Override
    public void removeMessage(String toUid, ImMessageBean bean) {
        if (bean != null) {
            removeMessage(bean.getTimRawMessage());
        }
    }

    @Override
    public void removeAllConversation() {

    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    @Override
    public void removeAllMessage(String toUid) {
        if (!TextUtils.isEmpty(toUid)) {
            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
            if (conversation != null) {
//                TIMConversationExt conExt = new TIMConversationExt(conversation);
//                conExt.deleteLocalMessage(mUnReadCountCallBack);
                conversation.setReadMessage(null,mEmptyCallBack);
            }
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void removeConversation(String toUid) {
        if (!TextUtils.isEmpty(toUid)) {
            markAllMessagesAsRead(toUid, true);
            TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, getImUid(toUid));
        }
    }

    @Override
    public void refreshLastMessage(String uid, ImMessageBean bean) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        if (bean == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null) {
            return;
        }
        ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
        imUserMsgEvent.setUid(uid);
        imUserMsgEvent.setLastMessage(getMessageString(msg));
        imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
        imUserMsgEvent.setLastTime(getMessageTimeString(msg));
        EventBus.getDefault().post(imUserMsgEvent);
    }

    @Override
    public void setVoiceMsgHasRead(ImMessageBean bean, Runnable runnable) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg != null) {
                TIMMessageExt ext = new TIMMessageExt(msg);
                ext.setCustomInt(1);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    @Override
    public String getMessageText(ImMessageBean bean) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg == null || msg.getElementCount() <= 0) {
                return "";
            }
            TIMElem elem0 = msg.getElement(0);
            if (elem0 == null) {
                return "";
            }
            if (elem0.getType() == TIMElemType.Custom && Constants.IM_MSG_ADMIN.equals(msg.getSender())) {
                TIMCustomElem customElem = (TIMCustomElem) elem0;
                JSONObject obj = JSON.parseObject(new String(customElem.getData()));
                //??????uid == admin  ???????????????????????????????????????????????????????????????  suggestion == 1 ?????????????????????
                if (obj != null) {
                    int suggestion = obj.getIntValue("Suggestion");
                    if (suggestion == 1) {
                        return obj.getString("Content");
                    }
                }
                return new String(customElem.getExt());
            }
            if (elem0.getType() == TIMElemType.Text) {
                return ((TIMTextElem) elem0).getText();
            }
        }
        return "";
    }

    @Override
    public ImMsgLocationBean getMessageLocation(ImMessageBean bean) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg == null || msg.getElementCount() <= 0) {
                return null;
            }
            TIMElem elem0 = msg.getElement(0);
            if (elem0 == null) {
                return null;
            }
            if (elem0.getType() == TIMElemType.Location) {
                TIMLocationElem locationElem = (TIMLocationElem) elem0;
                return new ImMsgLocationBean(locationElem.getDesc(),
                        0, locationElem.getLatitude(), locationElem.getLongitude());
            }
        }
        return null;
    }

    @Override
    public void displayImageFile(final Context context, final ImMessageBean bean, CommonCallback<File> commonCallback) {
        if (bean == null || commonCallback == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null || msg.getElementCount() <= 0) {
            return;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null || elem0.getType() != TIMElemType.Image) {
            return;
        }
        TIMImageElem e = (TIMImageElem) elem0;
        String localPath = e.getPath();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                commonCallback.callback(file);
            }
            return;
        }
        List<TIMImage> imageList = e.getImageList();
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        TIMImage targetImage = null;
        for (TIMImage img : imageList) {
            if (img.getType() == TIMImageType.Original) {
                targetImage = img;
                break;
            }
        }
        if (targetImage != null) {
            TxImCacheUtil.getImageFile(targetImage.getUuid(), targetImage.getUrl(), commonCallback);
        }
    }

    @Override
    public void getVoiceFile(ImMessageBean bean, final CommonCallback<File> commonCallback) {
        if (bean == null || commonCallback == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null || msg.getElementCount() <= 0) {
            return;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null || elem0.getType() != TIMElemType.Sound) {
            return;
        }
        TIMSoundElem e = (TIMSoundElem) elem0;
        String localPath = e.getPath();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                commonCallback.callback(file);
            }
            return;
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File voiceFile = new File(dir, e.getUuid());
        if (voiceFile.exists()) {
            commonCallback.callback(voiceFile);
            return;
        }
        e.getSoundToFile(voiceFile.getAbsolutePath(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                if (voiceFile.exists()) {
                    commonCallback.callback(voiceFile);
                }
            }
        });

    }

    /**
     * ???????????????????????????
     */
    @Override
    public void handleNotification(TIMOfflinePushNotification timOfflinePushNotification) {
        L.e(TAG, "????????????--->");
    }

    /**
     * ?????????????????????
     */
    private void playRing() {
        if (mCloseChatMusic || mOpenChatActivity) {
            return;
        }
        if (mSoundPool == null) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0 && mSoundId != -1) {
                        soundPool.play(mSoundId, 1, 1, 1, 0, 1);
                    }
                }
            });
        }
        if (mSoundId == -1) {
            mSoundId = mSoundPool.load(CommonAppContext.sInstance, R.raw.msg_ring, 1);
        } else {
            mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
        }

    }

    @Override
    public void setOpenChatActivity(boolean openChatActivity) {
        mOpenChatActivity = openChatActivity;
    }

    @Override
    public void setCloseChatMusic(boolean closeChatMusic) {
        mCloseChatMusic = closeChatMusic;
    }
}
