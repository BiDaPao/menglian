package com.menglian.live;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mob.MobSDK;
//import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.utils.L;
import com.aihuan.im.utils.ImMessageUtil;

import cn.tillusory.sdk.TiSDK;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    public static AppContext sInstance;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        //腾讯云鉴权url
//        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/96c55a5faee5138f70be0dbc346995a8/TXLiveSDK.licence";
//        //腾讯云鉴权key
//        String ugcKey = "7410cc8a1fc4cb13e1bd2af4b8ba0aa9";
//        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);
        L.setDeBug(BuildConfig.DEBUG);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersionName());
        //初始化ShareSdk
        MobSDK.init(this);
        //初始化极光推送
        //ImPushUtil.getInstance().init(this);
        //初始化IM
        ImMessageUtil.getInstance().init();
        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
    }

    /**
     * 初始化萌颜
     */
    public void initBeautySdk(String beautyKey) {
        if (!TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                mBeautyInited = true;
                TiSDK.initSDK(beautyKey, this);
                CommonAppConfig.getInstance().setTiBeautyEnable(true);
                L.e("萌颜初始化------->");
            }
        } else {
            CommonAppConfig.getInstance().setTiBeautyEnable(false);
        }

    }

}
