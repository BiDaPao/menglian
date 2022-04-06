package com.aihuan.one.http;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.http.HttpClient;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.StringUtil;

/**
 * Created by cxf on 2019/3/21.
 */

public class OneHttpUtil {

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }


    /**
     * 获取主播印象列表
     */
    public static void getAllImpress(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetUserLabel", OneHttpConsts.GET_ALL_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 给主播设置印象
     */
    public static void setImpress(String touid, String ImpressIDs, HttpCallback callback) {
        HttpClient.getInstance().get("User.setUserLabel", OneHttpConsts.SET_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("labels", ImpressIDs)
                .execute(callback);
    }


    /**
     * 获取直播间举报内容列表
     */
    public static void getLiveReportList(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getReportClass", OneHttpConsts.GET_LIVE_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报用户
     */
    public static void setReport(String touid, String content, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setReport", OneHttpConsts.SET_REPORT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", content)
                .execute(callback);
    }


    /**
     * 观众跟主播连麦时，获取自己的流地址
     */
    public static void getLinkMicStream(HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.RequestLVBAddrForLinkMic", OneHttpConsts.GET_LINK_MIC_STREAM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 主播连麦成功后，要把这些信息提交给服务器
     *
     * @param touid    连麦用户ID
     * @param pull_url 连麦用户播流地址
     */
    public static void linkMicShowVideo(String touid, String pull_url) {
        HttpClient.getInstance().get("Live.showVideo", OneHttpConsts.LINK_MIC_SHOW_VIDEO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("pull_url", pull_url)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }


    /**
     * 主播设置是否允许观众发起连麦
     */
    public static void setLinkMicEnable(boolean linkMicEnable, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.setMic", OneHttpConsts.SET_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("ismic", linkMicEnable ? 1 : 0)
                .execute(callback);
    }


    /**
     * 观众检查主播是否允许连麦
     */
    public static void checkLinkMicEnable(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.isMic", OneHttpConsts.CHECK_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("liveuid", liveUid)
                .execute(callback);
    }


    /**
     * 认证时获取形象标签列表
     */
    public static void getImpressList(HttpCallback callback) {
        HttpClient.getInstance().get("Auth.GetLabel", OneHttpConsts.GET_IMPRESS_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 提交认证信息
     *
     * @param thumb     封面图片
     * @param photos    形象图片，多张用逗号拼接
     * @param name      真实姓名
     * @param idCardNum 身份证号
     * @param sex       性别,1男2女
     * @param height    身高
     * @param weight    体重
     * @param xingZuo   星座
     * @param label     形象标签,多个标签用逗号拼接
     * @param province  所在城市-省
     * @param city      所在城市-市
     * @param district  所在城市-区
     * @param intro     个人介绍
     * @param sign      个性签名
     * @param callback
     */
    public static void setAuth(String thumb,
                               String photos,
                               String name,
                               String idCardNum,
                               int sex,
                               String birthday,
                               String height,
                               String weight,
                               String xingZuo,
                               String label,
                               String province,
                               String city,
                               String district,
                               String intro,
                               String sign,
                               String avatar,
                               String nick,
                               String accostText,
                               String voice,
                               int isAlipayAuth,
                               int voiceDuration,
                               HttpCallback callback) {
        HttpClient.getInstance().get("Auth.setAuth", OneHttpConsts.SET_AUTH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("thumb", thumb)
                .params("photos", photos)
                .params("name", name)
                .params("id_card", idCardNum)
                .params("sex", sex)
                .params("birthday", birthday)
                .params("height", height)
                .params("weight", weight)
                .params("constellation", xingZuo)
                .params("label", label)
                .params("province", province)
                .params("city", city)
                .params("district", district)
                .params("intr", intro)
                .params("signature", sign)
                .params("avatar", avatar)
                .params("user_nickname", nick)
                .params("accost_text", accostText)
                .params("f_voice", voice)
                .params("f_voice_duration", voiceDuration)
                .params("is_alipay_auth", 1)
                .execute(callback);
    }


    public static void getAuth(HttpCallback callback) {
        HttpClient.getInstance().get("Auth.getAuth", OneHttpConsts.GET_AUTH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取支付宝实人认证参数
     */
    public static void setAliAuth(String realName, String idCard, String bizCode, HttpCallback callback) {
        HttpClient.getInstance().get("Auth.SetAlipayAuth", OneHttpConsts.SET_ALIPAY_AUTH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("name", realName)
                .params("id_card", idCard)
                .params("biz_code", bizCode)
                .execute(callback);
    }

    /**
     * 获取支付宝实人认证参数
     */
    public static void checkAliAuthResult(String certifyId, HttpCallback callback) {
        HttpClient.getInstance().get("Auth.GetAlipayUserCertifyQuery", OneHttpConsts.GET_ALIPAY_AUTH_RESULT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("certify_id", certifyId)
                .execute(callback);
    }

    /**
     * 进行资料的认证（身份证和真实的姓名）
     */
    public  static  void setUserAuth(String userName ,String cardNumber ,HttpCallback callback){
        HttpClient.getInstance().get("User.setUserAuth",OneHttpConsts.setUserAuth)
                .params("uid",CommonAppConfig.getInstance().getUid())
                .params("token",CommonAppConfig.getInstance().getToken())
                .params("is_alipay_auth","1")
                .execute(callback);
    }




    /**
     * 主播向观众赴约，检测观众状态，同时获取自己的推拉流地址
     *
     * @param subscribeId 预约的 id
     * @param callback
     */
    public static void chatAncToAudStart(String subscribeId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("subscribeid=", subscribeId, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.ToAppointment", OneHttpConsts.CHAT_ANC_TO_AUD_START)
                .params("uid", uid)
                .params("token", token)
                .params("subscribeid", subscribeId)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 主播向观众发起通话邀请，检测观众状态，同时获取自己的推拉流地址
     */
    public static void chatAncToAudStart2(String toUid, String type, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("token=", token, "&touid=", toUid, "&type=", String.valueOf(type), "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.AnchorLaunch", OneHttpConsts.CHAT_ANC_TO_AUD_START_2)
                .params("uid", uid)
                .params("token", token)
                .params("touid", toUid)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 观众挂断通话
     *
     * @param liveUid       主播的 id
     * @param chatSessionId 通话的会话id
     * @param hangType      挂断类型 0等待中挂断 1等待结束后主播无响应挂断 2通话中挂断
     */
    public static void chatAudienceHangUp(String liveUid, String chatSessionId, String hangType, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("liveuid=", liveUid, "&showid=", chatSessionId, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.UserHang", OneHttpConsts.CHAT_AUDIENCE_HANG_UP)
                .params("uid", uid)
                .params("token", token)
                .params("liveuid", liveUid)
                .params("showid", chatSessionId)
                .params("hangtype", hangType)
                .params("sign", sign)
                .execute(callback != null ? callback : CommonHttpUtil.NO_CALLBACK);

    }


    /**
     * 主播挂断通话
     *
     * @param touid         通话对象的 id
     * @param chatSessionId 通话的会话id
     */
    public static void chatAnchorHangUp(String touid, String chatSessionId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("showid=", chatSessionId, "&token=", token, "&touid=", touid, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.AnchorHang", OneHttpConsts.CHAT_ANCHOR_HANG_UP)
                .params("uid", uid)
                .params("token", token)
                .params("touid", touid)
                .params("showid", chatSessionId)
                .params("sign", sign)
                .execute(callback != null ? callback : CommonHttpUtil.NO_CALLBACK);

    }

    /**
     * 主播同意通话
     *
     * @param touid         通话对象的 id
     * @param chatSessionId 通话的会话id
     */
    public static void chatAnchorAccpet(String touid, String chatSessionId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("showid=", chatSessionId, "&token=", token, "&touid=", touid, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.AnchorAnswer", OneHttpConsts.CHAT_ANCHOR_ACCPET)
                .params("uid", uid)
                .params("token", token)
                .params("touid", touid)
                .params("showid", chatSessionId)
                .params("sign", sign)
                .execute(callback);

    }

    /**
     * 观众同意通话
     *
     * @param liveUid       主播的 id
     * @param chatSessionId 通话的会话id
     */
    public static void chatAudienceAccpet(String liveUid, String chatSessionId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("liveuid=", liveUid, "&showid=", chatSessionId, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Live.UserAnswer", OneHttpConsts.CHAT_AUDIENCE_ACCPET)
                .params("uid", uid)
                .params("token", token)
                .params("liveuid", liveUid)
                .params("showid", chatSessionId)
                .params("sign", sign)
                .execute(callback);

    }


    /**
     * 通话结束时候获取评价标签列表
     */
    public static void getChatEvaList(HttpCallback callback) {
        HttpClient.getInstance().get("Label.GetEvaluate", OneHttpConsts.GET_CHAT_EVA_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 通话结束时观众对主播进行评价
     */
    public static void setChatEvaList(String liveUid, String evaIds) {
        HttpClient.getInstance().get("Label.SetEvaluate", OneHttpConsts.SET_CHAT_EVA_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("evaluateids", evaIds)
                .execute(CommonHttpUtil.NO_CALLBACK);
    }

    /**
     * 获取某人的评价列表
     */
    public static void getUserEvaList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Label.GetEvaluateList", OneHttpConsts.GET_USER_EVA_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取某人的评价列表
     */
    public static void getUserEvaCalc(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Label.GetEvaluateCount", OneHttpConsts.GET_USER_EVA_CALC)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 计时收费的时候，观众每隔一分钟请求这个接口进行扣费
     *
     * @param liveUid 主播的uid
     */
    public static void timeCharge(String liveUid, String sessionId, HttpCallback callback) {
        HttpClient.getInstance().get("Live.TimeCharge", OneHttpConsts.TIME_CHARGE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("showid", sessionId)
                .execute(callback);
    }


    /**
     * 我预约的主播列表
     */
    public static void getMySubscribeList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Subscribe.GetMeto", OneHttpConsts.GET_MY_SUBCRIBE_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 预约我的列表
     */
    public static void getSubscribeMeList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Subscribe.GetTome", OneHttpConsts.GET_SUBCRIBE_ME_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取预约的个数
     */
    public static void getSubscribeNums(HttpCallback callback) {
        HttpClient.getInstance().get("Subscribe.GetSubscribeNums", OneHttpConsts.GET_SUBSCRIBE_NUMS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 观众在匹配前进行检测
     */
    public static void matchCheck(int type, HttpCallback callback) {
        HttpClient.getInstance().get("Match.Check", OneHttpConsts.MATCH_CHECK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type)
                .execute(callback);
    }


    /**
     * 获取匹配信息
     */
    public static void getMatchInfo(HttpCallback callback) {
        HttpClient.getInstance().get("Match.GetMatch", OneHttpConsts.GET_MATCH_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 主播匹配
     */
    public static void matchAnchor(int type, HttpCallback callback) {
        HttpClient.getInstance().get("Match.AnchorMatch", OneHttpConsts.MATCH_ANCHOR)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type)
                .execute(callback);
    }


    /**
     * 主播取消匹配
     */
    public static void matchAnchorCancel() {
        HttpClient.getInstance().get("Match.AnchorCancel", OneHttpConsts.MATCH_ANCHOR_CANCEL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(CommonHttpUtil.NO_CALLBACK);
    }


    /**
     * 用户匹配
     */
    public static void matchAudience(int type, HttpCallback callback) {
        HttpClient.getInstance().get("Match.UserMatch", OneHttpConsts.MATCH_AUDIENCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type)
                .execute(callback);
    }

    /**
     * 用户取消匹配
     */
    public static void matchAudienceCancel() {
        HttpClient.getInstance().get("Match.UserCancel", OneHttpConsts.MATCH_AUDIENCE_CANCEL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(CommonHttpUtil.NO_CALLBACK);
    }


}
