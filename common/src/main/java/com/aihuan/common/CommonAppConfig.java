package com.aihuan.common;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.bean.UserItemBean;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.VersionUtil;
import com.aihuan.common.utils.WordUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2017/8/4.
 */

public class CommonAppConfig {

    //域名
    public static final String HOST = getMetaDataString("SERVER_HOST");
    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = CommonAppContext.sInstance.getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "menglian";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_PATH = DCMI_PATH + "/" + DIR_NAME + "/video/";
    public static final String VIDEO_RECORD_TEMP_PATH = VIDEO_PATH + "recordParts";
    //下载贴纸的时候保存的路径
    public static final String VIDEO_TIE_ZHI_PATH = DCMI_PATH + "/" + DIR_NAME + "/tieZhi/";
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = DCMI_PATH + "/" + DIR_NAME + "/camera/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";

    public static final String VOICE_PATH = INNER_PATH + "/voice/";

    //
    public static final String IMAGE_PATH = INNER_PATH + "/image/";
    //QQ登录是否与PC端互通
    public static final boolean QQ_LOGIN_WITH_PC = false;
    //当前app是否是云豹自己的产品
    public static final boolean APP_IS_YUNBAO_SELF = false;
    //腾讯IM appId    update----10.24
    public static final int TX_IM_APP_Id = 1400624178;

    public static final String APP_VERSION = VersionUtil.getVersion();//app版本号
    public static final String SYSTEM_MODEL = android.os.Build.MODEL;//手机型号
    public static final String SYSTEM_RELEASE = android.os.Build.VERSION.RELEASE;//手机系统版本号


    private static CommonAppConfig sInstance;

    private CommonAppConfig() {

    }

    public static CommonAppConfig getInstance() {
        if (sInstance == null) {
            synchronized (CommonAppConfig.class) {
                if (sInstance == null) {
                    sInstance = new CommonAppConfig();
                }
            }
        }
        return sInstance;
    }

    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private String mVersion;
    private String mVersionCode;
    private boolean mLoginIM;//IM是否登录了
    private Boolean mLaunched;//App是否启动了
    private Long mLaunchTime;//MainActivity打开的时间戳，极光IM用到
    private String mJPushAppKey;//极光推送的AppKey
    private List<UserItemBean> mUserItemList;//个人中心功能列表
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private String mTxMapAppKey;//腾讯定位，地图的AppKey
    private String mTxMapAppSecret;//腾讯地图的AppSecret
    private boolean mFrontGround;
    private int mAppIconRes;
    private String mAppName;
    private Boolean mTiBeautyEnable;//是否使用萌颜 true使用萌颜 false 使用基础美颜
    //个人中心开关
    private Boolean mUserSwitchDisturb;//勿扰
    private Boolean mUserSwitchVideo;//视频接听
    private Boolean mUserSwitchVoice;//语音接听
    private String mPriceVideo;//视频接听价格
    private String mPriceVoice;//语音接听价格
    private int mVideoPublishType;//1动态视频 2原来的视频上传

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance()
                    .getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                }
            } else {
                return "-1";
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }

    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            CommonHttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }

    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    /**
     * 区
     */
    public String getDistrict() {
        if (TextUtils.isEmpty(mDistrict)) {
            mDistrict = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_DISTRICT);
        }
        return mDistrict == null ? "" : mDistrict;
    }

    public void setUserBean(UserBean bean) {
        mUserBean = bean;
    }

    public UserBean getUserBean() {
        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            }
        }
        return mUserBean;
    }

    /**
     * 设置萌颜是否可用
     */
    public void setTiBeautyEnable(boolean tiBeautyEnable) {
        mTiBeautyEnable = tiBeautyEnable;
        SpUtil.getInstance().setBooleanValue(SpUtil.TI_BEAUTY_ENABLE, tiBeautyEnable);
    }

    public boolean isTiBeautyEnable() {
        if (mTiBeautyEnable == null) {
            mTiBeautyEnable = SpUtil.getInstance().getBooleanValue(SpUtil.TI_BEAUTY_ENABLE);
        }
        return mTiBeautyEnable;
    }

    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        L.e("登录成功", "uid------>" + uid);
        L.e("登录成功", "token------>" + token);
        mUid = uid;
        mToken = token;
        if (save) {
            Map<String, String> map = new HashMap<>();
            map.put(SpUtil.UID, uid);
            map.put(SpUtil.TOKEN, token);
            SpUtil.getInstance().setMultiStringValue(map);
        }
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        mLoginIM = false;
        SpUtil.getInstance().removeValue(
                SpUtil.UID, SpUtil.TOKEN, SpUtil.USER_INFO, SpUtil.TX_IM_USER_SIGN, SpUtil.IM_LOGIN
        );
    }


    /**
     * 设置位置信息
     *
     * @param lng      经度
     * @param lat      纬度
     * @param province 省
     * @param city     市
     */
    public void setLocationInfo(double lng, double lat, String province, String city, String district) {
        mLng = lng;
        mLat = lat;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.LOCATION_LNG, String.valueOf(lng));
        map.put(SpUtil.LOCATION_LAT, String.valueOf(lat));
        map.put(SpUtil.LOCATION_PROVINCE, province);
        map.put(SpUtil.LOCATION_CITY, city);
        map.put(SpUtil.LOCATION_DISTRICT, district);
        SpUtil.getInstance().setMultiStringValue(map);
    }

    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }


    public boolean isLoginIM() {
        return mLoginIM;
    }

    public void setLoginIM(boolean loginIM) {
        mLoginIM = loginIM;
    }

    /**
     * 获取版本号
     */
    public String getVersionName() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = CommonAppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(CommonAppContext.sInstance.getPackageName(), 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    /**
     * 获取版本号
     */
    public String getVersionCode() {
        if (TextUtils.isEmpty(mVersionCode)) {
            try {
                PackageManager manager = CommonAppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(CommonAppContext.sInstance.getPackageName(), 0);
                mVersionCode = String.valueOf(info.versionCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersionCode;
    }

    /**
     * 获取App名称
     */
    public String getAppName() {
        if (TextUtils.isEmpty(mAppName)) {
            int res = CommonAppContext.sInstance.getResources().getIdentifier("app_name", "string", "com.menglian.live");
            mAppName = WordUtil.getString(res);
        }
        return mAppName;
    }


    /**
     * 获取App图标的资源id
     */
    public int getAppIconRes() {
        if (mAppIconRes == 0) {
            mAppIconRes = CommonAppContext.sInstance.getResources().getIdentifier("ic_launcher", "mipmap", "com.menglian.live");
        }
        return mAppIconRes;
    }

    /**
     * 获取MetaData中的极光AppKey
     */
    public String getJPushAppKey() {
        if (mJPushAppKey == null) {
            mJPushAppKey = getMetaDataString("JPUSH_APPKEY");
        }
        return mJPushAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppKey
     *
     * @return
     */
    public String getTxMapAppKey() {
        if (mTxMapAppKey == null) {
            mTxMapAppKey = getMetaDataString("TencentMapSDK");
        }
        return mTxMapAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppSecret
     *
     * @return
     */
    public String getTxMapAppSecret() {
        if (mTxMapAppSecret == null) {
            mTxMapAppSecret = getMetaDataString("TencentMapAppSecret");
        }
        return mTxMapAppSecret;
    }


    public static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.sInstance.getPackageManager().getApplicationInfo(CommonAppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 个人中心功能列表
     */
    public List<UserItemBean> getUserItemList() {
        if (mUserItemList == null || mUserItemList.size() == 0) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                JSONObject obj = JSON.parseObject(userBeanJson);
                if (obj != null) {
                    setUserItemList(obj.getString("list"));
                }
            }
        }
        return mUserItemList;
    }


    public void setUserItemList(String listString) {
        if (!TextUtils.isEmpty(listString)) {
            try {
                mUserItemList = JSON.parseArray(listString, UserItemBean.class);
                for (UserItemBean bean : mUserItemList) {
                    int id = bean.getId();
                    if (id == Constants.MAIN_ME_DISTURB) {
                        bean.setRadioBtnChecked(getUserSwitchDisturb());
                    } else if (id == Constants.MAIN_ME_VIDEO) {
                        bean.setRadioBtnChecked(getUserSwitchVideo());
                        bean.setPriceText(getPriceVideo());
                    } else if (id == Constants.MAIN_ME_VOICE) {
                        bean.setRadioBtnChecked(getUserSwitchVoice());
                        bean.setPriceText(getPriceVoice());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }

        if (mLevelMap == null) {
            return null;
        }
        int size = mLevelMap.size();
        if (size == 0) {
            return null;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }

        if (mAnchorLevelMap == null) {
            return null;
        }
        int size = mAnchorLevelMap.size();
        if (size == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }

    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = CommonAppContext.sInstance.getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        if (mLaunched == null) {
            mLaunched = SpUtil.getInstance().getBooleanValue(SpUtil.APP_LAUNCHED);
        }
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
        SpUtil.getInstance().setBooleanValue(SpUtil.APP_LAUNCHED, launched);
    }


    public Long getLaunchTime() {
        if (mLaunchTime == null) {
            mLaunched = SpUtil.getInstance().getBooleanValue(SpUtil.APP_LAUNCHED);
        }
        return mLaunchTime;
    }

    public void setLaunchTime(Long launchTime) {
        SpUtil.getInstance().setLongValue(SpUtil.APP_LAUNCHED_TIME, launchTime);
        mLaunchTime = launchTime;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    /**
     * 勿扰开关
     */
    public boolean getUserSwitchDisturb() {
        if (mUserSwitchDisturb == null) {
            mUserSwitchDisturb = SpUtil.getInstance().getBooleanValue(SpUtil.USER_SWITCH_DISTURB);
        }
        return mUserSwitchDisturb;
    }

    /**
     * 勿扰开关
     */
    public void setUserSwitchDisturb(Boolean switchDisturb) {
        SpUtil.getInstance().setBooleanValue(SpUtil.USER_SWITCH_DISTURB, switchDisturb);
        mUserSwitchDisturb = switchDisturb;
    }

    /**
     * 视频开关
     */
    public boolean getUserSwitchVideo() {
        if (mUserSwitchVideo == null) {
            mUserSwitchVideo = SpUtil.getInstance().getBooleanValue(SpUtil.USER_SWITCH_VIDEO);
        }
        return mUserSwitchVideo;
    }

    /**
     * 视频开关
     */
    public void setUserSwitchVideo(Boolean switchVideo) {
        SpUtil.getInstance().setBooleanValue(SpUtil.USER_SWITCH_VIDEO, switchVideo);
        mUserSwitchVideo = switchVideo;
    }


    /**
     * 语音开关
     */
    public boolean getUserSwitchVoice() {
        if (mUserSwitchVoice == null) {
            mUserSwitchVoice = SpUtil.getInstance().getBooleanValue(SpUtil.USER_SWITCH_VOICE);
        }
        return mUserSwitchVoice;
    }

    /**
     * 语音开关
     */
    public void setUserSwitchVoice(Boolean switchVoice) {
        SpUtil.getInstance().setBooleanValue(SpUtil.USER_SWITCH_VOICE, switchVoice);
        mUserSwitchVoice = switchVoice;
    }

    /**
     * 视频接听价格
     */
    public String getPriceVideo() {
        if (TextUtils.isEmpty(mPriceVideo)) {
            mPriceVideo = SpUtil.getInstance().getStringValue(SpUtil.USER_PRICE_VIDEO);
        }
        return mPriceVideo;
    }

    /**
     * 视频接听价格
     */
    public void setPriceVideo(String priceVideo) {
        SpUtil.getInstance().setStringValue(SpUtil.USER_PRICE_VIDEO, priceVideo);
        mPriceVideo = priceVideo;
    }

    /**
     * 语音接听价格
     */
    public String getPriceVoice() {
        if (TextUtils.isEmpty(mPriceVoice)) {
            mPriceVoice = SpUtil.getInstance().getStringValue(SpUtil.USER_PRICE_VOICE);
        }
        return mPriceVoice;
    }

    /**
     * 语音接听价格
     */
    public void setPriceVoice(String priceVoice) {
        SpUtil.getInstance().setStringValue(SpUtil.USER_PRICE_VOICE, priceVoice);
        mPriceVoice = priceVoice;
    }

    /**
     * 获取视频价格列表
     */
    public List<ChatPriceBean> getVideoPriceList() {
        List<ChatPriceBean> list = null;
        try {
            String jsonStr = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(jsonStr)) {
                JSONObject obj = JSON.parseObject(jsonStr);
                if (obj != null) {
                    list = JSON.parseArray(obj.getString("videolist"), ChatPriceBean.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取语音价格列表
     */
    public List<ChatPriceBean> getVoicePriceList() {
        List<ChatPriceBean> list = null;
        try {
            String jsonStr = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(jsonStr)) {
                JSONObject obj = JSON.parseObject(jsonStr);
                if (obj != null) {
                    list = JSON.parseArray(obj.getString("voicelist"), ChatPriceBean.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getVideoPublishType() {
        return mVideoPublishType;
    }

    public void setVideoPublishType(int videoPublishType) {
        mVideoPublishType = videoPublishType;
    }
}
