package com.aihuan.common;

/**
 * Created by cxf on 2018/6/7.
 */

public class Constants {
    public static final String URL = "url";
    public static final String PAYLOAD = "payload";
    public static final String TO_UID = "toUid";
    public static final String TO_NAME = "toName";
    public static final String FROM = "from";
    public static final String TIP = "tip";
    public static final String FIRST_LOGIN = "firstLogin";
    public static final String USER_BEAN = "userBean";
    public static final String LIVE_DANMU_PRICE = "danmuPrice";
    public static final String COIN_NAME = "coinName";
    public static final String LIVE_UID = "liveUid";
    public static final String FOLLOW = "follow";
    public static final String BLACK = "black";
    public static final String IM_FROM_HOME = "imFromUserHome";
    public static final String DIAMONDS = "云币";
    public static final String VOTES = "映票";
    public static final String PAY_ALI_NOT_ENABLE = "支付宝未接入";
    public static final String PAY_WX_NOT_ENABLE = "微信支付未接入";
    public static final String PAY_ALL_NOT_ENABLE = "未开启支付";
    public static final String PAY_TYPE_ALI = "ali";
    public static final String PAY_TYPE_WX = "wx";
    public static final String PAY_TYPE_COIN = "coin";

    public static final String MATCH_PRICE = "matchPrice";
    public static final String MATCH_PRICE_VIP = "matchPriceVip";

    public static final String PAY_BUY_COIN_ALI = "Charge.getAliOrder";
    public static final String PAY_BUY_COIN_WX = "Charge.getWxOrder";
    public static final String PAY_VIP_COIN_ALI = "Vip.GetAliOrder";
    public static final String PAY_VIP_COIN_WX = "Vip.GetWxOrder";

    public static final String PACKAGE_NAME_ALI = "com.eg.android.AlipayGphone";//支付宝的包名
    public static final String PACKAGE_NAME_WX = "com.tencent.mm";//微信的包名
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";//QQ的包名
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ADDRESS = "address";
    public static final String SCALE = "scale";
    public static final String SELECT_IMAGE_PATH = "selectedImagePath";
    public static final String COPY_PREFIX = "copy://";
    public static final String SHARE_PREFIX = "shareagent://";

    public static final String GIF_GIFT_PREFIX = "gif_gift_";
    public static final String LINK = "link";
    public static final String REPORT = "report";
    public static final String SAVE = "save";
    public static final String DELETE = "delete";

    //主播在线类型
    public static final int LINE_TYPE_OFF = 0;//离线
    public static final int LINE_TYPE_DISTURB = 1;//勿扰
    public static final int LINE_TYPE_CHAT = 2;//在聊
    public static final int LINE_TYPE_ON = 3;//在线


    //提现账号类型，1表示支付宝，2表示微信，3表示银行卡
    public static final int CASH_ACCOUNT_ALI = 1;
    public static final int CASH_ACCOUNT_WX = 2;
    public static final int CASH_ACCOUNT_BANK = 3;
    public static final String CASH_ACCOUNT_ID = "cashAccountID";
    public static final String CASH_ACCOUNT = "cashAccount";
    public static final String CASH_ACCOUNT_TYPE = "cashAccountType";
    //提现支付宝账号信息
    public static final String CASH_ALI_ACCOUNT_AVATAR = "cashAliAvatar";
    public static final String CASH_ALI_ACCOUNT_NICK = "cashAliNick";

    public static final String VIDEO_HOME = "videoHome";
    public static final String VIDEO_USER = "videoUser_";
    public static final String VIDEO_KEY = "videoKey";
    public static final String VIDEO_POSITION = "videoPosition";
    public static final String VIDEO_SINGLE = "videoSingle";
    public static final String VIDEO_PAGE = "videoPage";
    public static final String VIDEO_BEAN = "videoBean";
    public static final String VIDEO_ID = "videoId";
    public static final String VIDEO_COMMENT_BEAN = "videoCommnetBean";
    public static final String VIDEO_FACE_OPEN = "videoOpenFace";
    public static final String VIDEO_FACE_HEIGHT = "videoFaceHeight";
    public static final String VIDEO_DURATION = "videoDuration";
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_FROM_RECORD = "videoFromRecord";
    public static final String VIDEO_MUSIC_ID = "videoMusicId";
    public static final String VIDEO_HAS_BGM = "videoHasBgm";
    public static final String VIDEO_MUSIC_NAME_PREFIX = "videoMusicName_";
    public static final String VIDEO_SAVE_TYPE = "videoSaveType";
    public static final int VIDEO_SAVE_SAVE_AND_PUB = 1;//保存并发布
    public static final int VIDEO_SAVE_SAVE = 2;//仅保存
    public static final int VIDEO_SAVE_PUB = 3;//仅发布

    public static final String MOB_QQ = "qq";
    public static final String MOB_QZONE = "qzone";
    public static final String MOB_WX = "wx";
    public static final String MOB_WX_PYQ = "wchat";
    public static final String MOB_FACEBOOK = "facebook";
    public static final String MOB_TWITTER = "twitter";
    public static final String MOB_PHONE = "phone";

    public static final String LIVE_SDK = "liveSdk";
    public static final int LIVE_SDK_KSY = 0;//金山推流
    public static final int LIVE_SDK_TX = 1;//腾讯推流

    public static final int LINK_MIC_TYPE_NORMAL = 0;//观众与主播连麦
    public static final int LINK_MIC_TYPE_ANCHOR = 1;//主播与主播连麦

    public static final int MAIN_ME_WALLET = 1;//我的钱包
    public static final int MAIN_ME_AUTH = 2;//我要认证
    public static final int MAIN_ME_IMPRESS = 4;//我的印象
    public static final int MAIN_ME_GIFI_CAB = 5;//礼物柜
    public static final int MAIN_ME_VIDEO = 6;//视频接听
    public static final int MAIN_ME_VOICE = 7;//语音接听
    public static final int MAIN_ME_DISTURB = 8;//勿扰
    public static final int MAIN_ME_SETTING = 9;//设置
    public static final int MAIN_ME_VIP = 10;//会员中心
    public static final int MAIN_ME_WALL = 11;//背景墙
    public static final int MAIN_ME_MY_VIDEO = 12;//我的视频
    public static final int MAIN_ME_MY_ALBUM = 13;//我的相册
    public static final int MAIN_ME_MY_DYNAMIC = 3;//我的动态
    public static final int MAIN_ME_MY_MENGYAN = 101;//美颜设置
    public static final int MAIN_ME_MY_SERVICE = 102;//在线客服


    public static final String AUTH_STATUS = "authStatus";//认证状态
    public static final int AUTH_NONE = 0;//未认证
    public static final int AUTH_WAITING = 1;//认证中,等待审核
    public static final int AUTH_SUCCESS = 2;//认证成功
    public static final int AUTH_FAILED = 3;//认证失败
    public static final int AUTH_IMAGE_MAX_SIZE = 6;//MAX_SIZE 认证时候上传图片最大张数

    public static final String CHAT_TYPE = "chatType";//通话类型 1视频 2语音
    public static final byte CHAT_TYPE_VIDEO = 1;//通话类型 视频
    public static final byte CHAT_TYPE_VOICE = 2;//通话类型 语音
    public static final byte CHAT_TYPE_NONE = 0;//通话类型 全部

    public static final String CHAT_PARAM_AUD = "chatParamAud";
    public static final String CHAT_PARAM_ANC = "chatParamAnc";
    public static final String CHAT_PARAM_TYPE = "chatParamType";
    public static final String CHAT_SESSION_ID = "chatSessionId";
    public static final int CHAT_PARAM_TYPE_AUD = 1;
    public static final int CHAT_PARAM_TYPE_ANC = 2;


    public static final String CHAT_HANG_TYPE_WAITING = "0";//0等待中挂断
    public static final String CHAT_HANG_TYPE_WAIT_END = "1";//1等待结束后主播无响应挂断
    public static final String CHAT_HANG_TYPE_CHAT = "2";//2通话中挂断

    public static final String MAIN_SEX = "mainHomeSex";
    public static final byte MAIN_SEX_NONE = 0;
    public static final byte MAIN_SEX_MALE = 1;
    public static final byte MAIN_SEX_FAMALE = 2;

    public static final String IM_MSG_ADMIN = "admin";

    public static final String PHOTO_BEAN = "photoBean";
    public static final String WALL_BEAN = "wallBean";
    public static final String WALL_IMAGE_SIZE = "wallImageSize";
    public static final String WALL_IS_VIDEO = "wallIsVideo";
    public static final String WALL_ACTION = "wallAction";
    public static final String WALL_OLD_ID = "wallOldId";
    /*7.22*/
    public static final int DYNAMIC_IMG_MAX_NUM = 9;
    public static final int DYNAMIC_VOICE_MIN_TIME = 3;
    public static final String DYNAMIC_ID = "dynamicId";
    public static final String DYNAMIC_UID = "dynamicUid";
    public static final String DYNAMIC_BEAN = "dynamic_bean";
    public static final String DYNAMIC_FROM_USER_CENTER = "from_user_center";
    public static final int DYNAMIC_TYPE_TEXT = 0;
    public static final int DYNAMIC_TYPE_IMG = 1;
    public static final int DYNAMIC_TYPE_VIDEO = 2;
    public static final int DYNAMIC_TYPE_VOICE = 3;
    public static final String DYNAMIC_IMG_LIST = "img_list";
    public static final String DYNAMIC_IMG_CUR_POS = "img_pos";
    public static final String RANK_DEFAULT = "default_pos";
    public static final int DYNAMIC_VIDEO_PUBLISH = 1;
    public static final int VIDEO_PUBLISH = 2;
    public static final String DYNAMIC_IMG_SELECT = "imgSelect";


    public static final String USER_AVATAT = "avatar";
    public static final String USER_NICK_NAME = "name";
    public static final String USER_LOGIN_BY_THIRD = "login_by_third";

    //点击首页列表搭讪按钮点击进入聊天
    public static final String IS_ACCOST = "is_accost";

}
