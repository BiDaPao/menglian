package com.aihuan.common;

/**
 * Created by cxf on 2018/10/15.
 */

public class HtmlConfig {

    //登录即代表同意服务和隐私条款
    public static final String LOGIN_PRIVCAY = CommonAppConfig.HOST + "/appapi/page/detail?id=1";
    //个人主页分享链接
    public static final String SHARE_HOME_PAGE = CommonAppConfig.HOST + "/index.php?g=Appapi&m=home&a=index&touid=";
    //提现记录
    public static final String CASH_RECORD = CommonAppConfig.HOST + "/appapi/cash/index?";
    //支付宝充值回调地址
    public static final String ALI_PAY_COIN_URL = CommonAppConfig.HOST + "/Appapi/Pay/notify_ali";
    //支付宝购买VIP回调地址
    public static final String ALI_PAY_VIP_URL = CommonAppConfig.HOST + "/Appapi/Vipback/notify_ali";
    //视频分享地址
    public static final String SHARE_VIDEO = CommonAppConfig.HOST + "/Appapi/Video/share?id=";
    //钱包明细 支出
    public static final String WALLET_EXPAND = CommonAppConfig.HOST + "/appapi/record/expend";
    //钱包明细 收入
    public static final String WALLET_INCOME = CommonAppConfig.HOST + "/appapi/record/income";
    //充值协议
    public static final String CHARGE_PRIVCAY = CommonAppConfig.HOST + "/appapi/page/detail?id=3";
    //关于我们
    public static final String ABOUT_US = CommonAppConfig.HOST + "/appapi/page/lists";
    //全民赚钱
    public static final String MAKE_MONEY = CommonAppConfig.HOST + "/appapi/Agent/share?code=";

    //提现协议
    public static final String WITHDDRAW_MONEY = CommonAppConfig.HOST + "/appapi/page/detail?id=10";

    //在线客服连接
    public static final String SERVICE_URL = "http://kefu.hnchengyun.cn/index/index/home?business_id=4&groupid=0&special=4&theme=7571f9";

}
