package com.aihuan.dynamic.http;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.http.HttpClient;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.video.http.VideoHttpConsts;

/**
 * Created by debug on 2019/7/23.
 */

public class DynamicHttpUtil {
    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }
    /**
     * 发布动态
     * @param title
     * @param thumb
     * @param video_thumb
     * @param href
     * @param voice
     * @param length
     * @param type 动态类型：0：纯文字；1：文字+图片；2：文字+视频；3：文字+音频
     * @param callback
     */
    public static void uploadDynamic(boolean isLocation,String title, String thumb,String video_thumb, String href, String voice, int length,int type, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        //,"&href=", href, "&video_thumb=", String.valueOf(video_thumb),"&thumb=", thumb
        String sign = MD5Util.getMD5(StringUtil.contact( "type=",String.valueOf(type) ,"&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Dynamic.setDynamic", DynamicHttpConsts.DYNAMIC_PUBLISH)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("lat",isLocation?CommonAppConfig.getInstance().getLat():0)
                .params("lng", isLocation?CommonAppConfig.getInstance().getLng():0)
                .params("city", isLocation?CommonAppConfig.getInstance().getCity():"")
                .params("title", title)
                .params("thumb", thumb)
                .params("video_thumb", video_thumb)
                .params("href", href)
                .params("voice", voice)
                .params("length", length)
                .params("type", type)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 动态列表最新
     * @param p
     * @param callback
     */
    public static void getDynamicList(int p,HttpCallback callback){
        HttpClient.getInstance().get("Dynamic.getDynamicList", DynamicHttpConsts.DYNAMIC_GETNEW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("p",p)
                .execute(callback);
    }

    /**
     * 用户关注的动态列表
     * @param p
     * @param callback
     */
    public static void getAttentionDynamic(int p,HttpCallback callback){
        HttpClient.getInstance().get("Dynamic.getAttentionDynamic", DynamicHttpConsts.DYNAMIC_GETATTEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p",p)
                .execute(callback);
    }

    /**
     * 点赞动态
     * @param dynamicId
     * @param callback
     */
    public static void addLike(String dynamicId,HttpCallback callback){
        String sign = MD5Util.getMD5(StringUtil.contact("dynamicid=", dynamicId,"&uid=", CommonAppConfig.getInstance().getUid(),"&",HttpClient.SALT));
        HttpClient.getInstance().get("Dynamic.addLike", DynamicHttpConsts.DYNAMIC_ADDLIKE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("dynamicid",dynamicId)
                .params("sign",sign)
                .execute(callback);
    }

    /**
     * 获取举报内容列表
     */
    public static void getDynamicReportList(HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.getReport", DynamicHttpConsts.DYNAMIC_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报接口
     */
    public static void dynamicReport(String dynamicId, String content, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5(StringUtil.contact("content=", content, "&dynamicid=", dynamicId,"&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Dynamic.setReport", DynamicHttpConsts.DYNAMIC_SET_REPORT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("dynamicid", dynamicId)
                .params("content", content)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 获取动态评论
     */
    public static void getDynamicCommentList(String dynamicId, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.GetComments", DynamicHttpConsts.DYNAMIC_GET_COMMENTS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("dynamicid", dynamicId)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 评论点赞
     */
    public static void setCommentLike(String commentid, HttpCallback callback) {
        String sign=MD5Util.getMD5(StringUtil.contact("commentid=",commentid,"&uid=",CommonAppConfig.getInstance().getUid(),"&", HttpClient.SALT));
        HttpClient.getInstance().get("Dynamic.addCommentLike", DynamicHttpConsts.DYNAMIC_COMMENTS_LIKE)
                .params("commentid", commentid)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 发表评论
     */
    public static void setComment(String toUid, String dynamicid, String content, String commentId, String parentId,String at_info, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.setComment", DynamicHttpConsts.DYNAMIC_SET_COMMENTS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", toUid)
                .params("dynamicid", dynamicid)
                .params("commentid", commentId)
                .params("parentid", parentId)
                .params("content", content)
                .params("at_info", at_info)
                .params("type", 0)
                .params("voice", "")
                .params("length", 0)
                .execute(callback);
    }

    /**
     * 发表语音评论
     */
    public static void setDynamicComment(String toUid, String dynamicid, String commentId, String parentId, String voiceLink, int voiceDuration, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.setComment", DynamicHttpConsts.DYNAMIC_SET_COMMENTS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", toUid)
                .params("dynamicid", dynamicid)
                .params("commentid", commentId)
                .params("parentid", parentId)
                .params("content", "")
                .params("at_info", "")
                .params("type", 1)
                .params("voice", voiceLink)
                .params("length", voiceDuration)
                .execute(callback);
    }
    /**
     * 获取评论回复
     */
    public static void getCommentReply(String commentid,String lastReplyId, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.getReplys", DynamicHttpConsts.DYNAMIC_GET_REPLYS)
                .params("commentid", commentid)
                .params("last_replyid", lastReplyId)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 用户删除动态
     * @param dynamicid
     * @param callback
     */
    public static void delDynamic(String dynamicid, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.delDynamic", DynamicHttpConsts.DYNAMIC_DEL)
                .params("dynamicid", dynamicid)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 个人主页、个人中心：动态列表
     * @param toUid
     * @param p
     * @param callback
     */
    public static void getHomeDynamicList(String toUid,int p, HttpCallback callback) {
        HttpClient.getInstance().get("Dynamic.getHomeDynamic", DynamicHttpConsts.DYNAMIC_GET_USERHOME)
                .params("p", p)
                .params("liveuid", toUid)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }
}
