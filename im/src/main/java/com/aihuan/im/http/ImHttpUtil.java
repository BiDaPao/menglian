package com.aihuan.im.http;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.http.HttpClient;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.StringUtil;

/**
 * Created by cxf on 2019/2/26.
 */

public class ImHttpUtil {

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 私信聊天页面用于获取用户信息
     */
    public static void getImUserInfo(String uids, HttpCallback callback) {
        HttpClient.getInstance().get("Im.GetMultiInfo", ImHttpConsts.GET_IM_USER_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("uids", uids)
                .execute(callback);
    }

    /**
     * 获取系统消息列表
     */
    public static void getSystemMessageList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Im.GetSysNotice", ImHttpConsts.GET_SYSTEM_MESSAGE_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 判断自己有没有被对方拉黑，聊天的时候用到
     */
    public static void checkIm(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Im.Check", ImHttpConsts.CHECK_IM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("sex", CommonAppConfig.getInstance().getUserBean().getSex())
                .execute(callback);
    }

    /**
     * 付费发送消息
     */
    public static void chargeSendIm(String touid,HttpCallback callback) {
        HttpClient.getInstance().get("Im.BuyIm", ImHttpConsts.CHARGE_SEND_IM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid",touid)
                .execute(callback);
    }


    /**
     * 获取礼物列表，同时会返回剩余的钱
     */
    public static void getGiftList(HttpCallback callback) {
        HttpClient.getInstance().get("Gift.getGiftList", ImHttpConsts.GET_GIFT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     */
    public static void sendGift(String liveUid, String sessionId, int giftId, String giftCount, HttpCallback callback) {
        HttpClient.getInstance().get("Gift.SendGift", ImHttpConsts.SEND_GIFT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("showid", sessionId)
                .params("giftid", giftId)
                .params("nums", giftCount)
                .execute(callback);
    }


    /**
     * 通话时候 用于检测两个用户间关系
     */
    public static void checkChatStatus(String toUid, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("token=", token, "&touid=", toUid, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.Checkstatus", ImHttpConsts.CHECK_CHAT_STATUS)
                .params("uid", uid)
                .params("token", token)
                .params("touid", toUid)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 观众向主播发起通话邀请，检测主播状态，同时获取自己的推拉流地址
     *
     * @param liveUid  主播的 id
     * @param type     通话类型
     * @param callback
     */
    public static void chatAudToAncStart(String liveUid, int type, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("liveuid=", liveUid, "&token=", token, "&type=", String.valueOf(type), "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.Checklive", ImHttpConsts.CHAT_AUD_TO_ANC_START)
                .params("uid", uid)
                .params("token", token)
                .params("liveuid", liveUid)
                .params("type", type)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 观众预约主播
     *
     * @param liveUid 主播的uid
     */
    public static void audSubscribeAnc(String liveUid, int type) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("liveuid=", liveUid, "&token=", token, "&type=", String.valueOf(type), "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Subscribe.SetSubscribe", ImHttpConsts.AUD_SUBSCRIBE_ANC)
                .params("uid", uid)
                .params("token", token)
                .params("liveuid", liveUid)
                .params("type", type)
                .params("sign", sign)
                .execute(CommonHttpUtil.NO_CALLBACK);
    }

    /**
     * 主播向观众发起通话邀请，检测观众状态，同时获取自己的推拉流地址
     */
    public static void chatAncToAudStart2(String toUid, int type, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("token=", token, "&touid=", toUid, "&type=", String.valueOf(type), "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.AnchorLaunch", ImHttpConsts.CHAT_ANC_TO_AUD_START_2)
                .params("uid", uid)
                .params("token", token)
                .params("touid", toUid)
                .params("type", type)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 首页直播
     */
    public static void getBanners(HttpCallback callback) {
        HttpClient.getInstance().get("Home.getBanners", ImHttpConsts.BANNERS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 亲密度等级
     */
    public static void getIntimacyLevel(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.IntimacyLevel", ImHttpConsts.INTIMACY_LEVEL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 设置备注
     */
    public static void settingRemark(String touid, String remark, HttpCallback callback) {
        HttpClient.getInstance().get("Im.SetUserRemark", ImHttpConsts.SET_REMARK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("toremark", remark)
                .execute(callback);
    }

    /**
     * 设置备注
     */
    public static void getRemark(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Im.GetUserRemark", ImHttpConsts.SET_REMARK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 图片内容安全鉴定 -- 后端调用七牛云
     *
     * @param content image: base64字符串
     */
    public static void getVerifyContent(String content, HttpCallback callback) {
        HttpClient.getInstance().get("Upload.GetVerifyContent", ImHttpConsts.GET_VERIFY)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("content", content)
                .execute(callback);
    }

    /**
     * 文本内容安全鉴定 -- 自定义功能
     *
     * @param content 发送文本
     */
    public static void checkImText(String content, HttpCallback callback) {
        HttpClient.getInstance().get("Im.KeywordCheck", ImHttpConsts.CHECK_TEXT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("content", content)
                .execute(callback);
    }
}