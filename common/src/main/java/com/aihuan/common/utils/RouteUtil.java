package com.aihuan.common.utils;

import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.UserBean;

/**
 * Created by cxf on 2019/2/25.
 */

public class RouteUtil {
    public static final String PATH_LAUNCHER = "/app/LauncherActivity";
    public static final String PATH_LOGIN_INVALID = "/main/LoginInvalidActivity";
    public static final String PATH_USER_HOME = "/main/UserHomeActivity";
    public static final String PATH_MAIN = "/main/MainActivity";
    public static final String PATH_COIN = "/main/MyCoinActivity";
    public static final String PATH_VIP = "/main/VipActivity";
    public static final String PATH_IMPRESS = "/main/ImpressActivity";
    public static final String PATH_IMPRESS_CALC = "/main/ImpressCalcActivity";
    public static final String MAIN_CHAT_AUDIENCE = "/oneonone/ChatAudienceActivity";
    public static final String MAIN_CHAT_ANCHOR = "/oneonone/ChatAnchorActivity";

    public static final String PATH_AT_FRIEND = "/main/AtFriendActivity";
    public static final String PATH_VIDEO_PUBLISH = "/video/VideoPublishActivity";
    /**
     * 启动页
     */
    public static void forwardLauncher() {
        ARouter.getInstance().build(PATH_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation();
    }

    /**
     * 登录过期
     */
    public static void forwardLoginInvalid(String tip) {
        ARouter.getInstance().build(PATH_LOGIN_INVALID)
                .withString(Constants.TIP, tip)
                .navigation();
    }

    /**
     * 跳转到个人主页
     */
    public static void forwardUserHome(String toUid) {
        ARouter.getInstance().build(PATH_USER_HOME)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }

    /**
     * 跳转到充值页面
     */
    public static void forwardMyCoin() {
        ARouter.getInstance().build(PATH_COIN).navigation();
    }

    /**
     * 跳转到主播印象页面
     */
    public static void forwardImpress(String toUid) {
        ARouter.getInstance().build(PATH_IMPRESS)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }

    /**
     * 跳转到印象统计页面
     */
    public static void forwardImpressCalc(String toUid) {
        ARouter.getInstance().build(PATH_IMPRESS_CALC)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }


    /**
     * 打开观众直播间
     */
    public static void forwardAudienceActivity(ChatAudienceParam param) {
        ARouter.getInstance().build(MAIN_CHAT_AUDIENCE)
                .withParcelable(Constants.CHAT_PARAM_AUD, param)
                .navigation();
    }

    /**
     * 打开主播直播间
     */
    public static void forwardAnchorActivity(ChatAnchorParam param) {
        ARouter.getInstance().build(MAIN_CHAT_ANCHOR)
                .withParcelable(Constants.CHAT_PARAM_ANC, param)
                .navigation();
    }


    public static void forwardMainActivity(ChatAnchorParam param) {
        ARouter.getInstance().build(PATH_MAIN)
                .withInt(Constants.CHAT_PARAM_TYPE, Constants.CHAT_PARAM_TYPE_ANC)
                .withParcelable(Constants.CHAT_PARAM_ANC, param)
                .navigation();
    }

    public static void forwardMainActivity(ChatAudienceParam param) {
        ARouter.getInstance().build(PATH_MAIN)
                .withInt(Constants.CHAT_PARAM_TYPE, Constants.CHAT_PARAM_TYPE_AUD)
                .withParcelable(Constants.CHAT_PARAM_AUD, param)
                .navigation();
    }


    /**
     * 跳转到VIP
     */
    public static void forwardVip() {
        ARouter.getInstance().build(PATH_VIP)
                .navigation();
    }
    /**
     * 跳转到召唤好友
     */
    public static void forwardAtFriend(AbsActivity activity, int requestCode) {
        ARouter.getInstance().build(PATH_AT_FRIEND).navigation(activity, requestCode);
    }

    /**
     * 发布视频
     * @param videoPath
     */
    public static void forwardVideoPublish(String videoPath) {
        ARouter.getInstance().build(PATH_VIDEO_PUBLISH)
                .withString(Constants.VIDEO_PATH, videoPath)
                .withInt(Constants.VIDEO_SAVE_TYPE, Constants.VIDEO_SAVE_PUB)
                .withInt(Constants.VIDEO_MUSIC_ID, 0)
                .navigation();
    }
}
