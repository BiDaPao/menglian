package com.aihuan.main.http;

import android.util.Log;

import com.aihuan.common.http.JsonBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.http.HttpClient;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.MD5Util;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.StringUtil;
import com.lzy.okgo.request.GetRequest;

import java.util.logging.Logger;

/**
 * Created by cxf on 2018/9/17.
 */

public class MainHttpUtil {
    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 手机号 验证码登录
     */
    public static void login(String phoneNum, String code, HttpCallback callback) {
        HttpClient.getInstance().get("Login.userLogin", MainHttpConsts.LOGIN)
                .params("user_login", phoneNum)
                .params("code", code)
                .execute(callback);
    }

    /**
     * 获取登录验证码接口
     */
    public static void getLoginCode(String phoneNum, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("mobile=", phoneNum, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.getCode", MainHttpConsts.GET_LOGIN_CODE)
                .params("mobile", phoneNum)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 第三方登录
     */
    public static void loginByThird(String openid, String nicename, String avatar, int flag, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("openid=", openid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.userLoginByThird", MainHttpConsts.LOGIN_BY_THIRD)
                .params("openid", openid)
                .params("nicename", nicename)
                .params("avatar", avatar)
                .params("type", flag)
                .params("source", 1)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 请求签到奖励
     */
    public static void requestBonus(HttpCallback callback) {
        HttpClient.getInstance().get("User.Bonus", MainHttpConsts.REQUEST_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取签到奖励
     */
    public static void getBonus(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBonus", MainHttpConsts.GET_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 用于用户首次登录设置分销关系
     */
    public static void setDistribut(String code, HttpCallback callback) {
        HttpClient.getInstance().get("User.setDistribut", MainHttpConsts.SET_DISTRIBUT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("code", code)
                .execute(callback);
    }

    /**
     * 首页直播
     */
    public static void getHot(int p, byte sex, byte chatType, HttpCallback callback) {
        GetRequest<JsonBean> params = HttpClient.getInstance().get("Home.getHot", MainHttpConsts.GET_HOT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("sex", sex);
        if (p==1){
                    params  .params("action_type",1);
                }else{
                    params  .params("action_type",0);
                }
                params
                .params("type", chatType)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 首页
     */
    public static void getFollow(int p, int sex, HttpCallback callback) {
        HttpClient.getInstance().get("Home.GetAttention", MainHttpConsts.GET_FOLLOW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .params("sex", sex)
                .execute(callback);
    }

    /**
     * 首页 附近
     */
    public static void getNear(int p, int sex, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getNearby", MainHttpConsts.GET_NEAR)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("lng", CommonAppConfig.getInstance().getLng())
                .params("lat", CommonAppConfig.getInstance().getLat())
                .params("p", p)
                .params("sex", sex)
                .execute(callback);
    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(String uid, String token, final CommonCallback<UserBean> commonCallback) {
        HttpClient.getInstance().get("User.getBaseInfo", MainHttpConsts.GET_BASE_INFO)
                .params("uid", uid)
                .params("token", token)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            CommonAppConfig appConfig = CommonAppConfig.getInstance();
                            appConfig.setUserBean(bean);
                            appConfig.setPriceVideo(obj.getString("video_value"));
                            appConfig.setPriceVoice(obj.getString("voice_value"));
                            appConfig.setUserSwitchDisturb(obj.getIntValue("isdisturb") == 1);
                            appConfig.setUserSwitchVideo(obj.getIntValue("isvideo") == 1);
                            appConfig.setUserSwitchVoice(obj.getIntValue("isvoice") == 1);
                            appConfig.setUserItemList(obj.getString("list"));
                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(CommonCallback<UserBean> commonCallback) {
        getBaseInfo(CommonAppConfig.getInstance().getUid(),
                CommonAppConfig.getInstance().getToken(),
                commonCallback);
    }

    /**
     * 用户个人主页信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.getUserHome", MainHttpConsts.GET_USER_HOME)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("liveuid", touid)
                .execute(callback);
    }


    /**
     * 搜索
     */
    public static void search(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.search", MainHttpConsts.SEARCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的关注列表
     *
     * @param touid 对方的uid
     */
    public static void getFollowList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getFollowsList", MainHttpConsts.GET_FOLLOW_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     *
     * @param touid 对方的uid
     */
    public static void getFansList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getFansList", MainHttpConsts.GET_FANS_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);

    }


    /**
     * 更新用户资料
     *
     * @param avatar 头像
     * @param name   昵称
     */
    public static void updateUserInfo(String avatar, String name, HttpCallback callback) {
        HttpClient.getInstance().get("User.UpUserInfo", MainHttpConsts.UPDATE_USER_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("avatar", avatar)
                .params("name", name)
                .execute(callback);
    }


    /**
     * 获取 我的收益 可提现金额数
     */
    public static void getProfit(HttpCallback callback) {
        HttpClient.getInstance().get("Cash.getProfit", MainHttpConsts.GET_PROFIT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取 提现账户列表
     */
    public static void getCashAccountList(HttpCallback callback) {
        HttpClient.getInstance().get("Cash.GetUserAccountList", MainHttpConsts.GET_USER_ACCOUNT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 添加 提现账户
     */
    public static void addCashAccount(String account, String name, String idNum, int type, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.SetUserAccount", MainHttpConsts.ADD_CASH_ACCOUNT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("account", account)
                .params("name", name)
                .params("id_num", idNum)
                .params("type", type)
                .execute(callback);
    }

    /**
     * 删除 提现账户
     */
    public static void deleteCashAccount(String accountId, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.delUserAccount", MainHttpConsts.DEL_CASH_ACCOUNT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("id", accountId)
                .execute(callback);
    }

    /**
     * 提现
     */
    public static void doCash(String votes, String accountId, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.setCash", MainHttpConsts.DO_CASH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .params("accountid", accountId)//账号ID
                .execute(callback);
    }


    /**
     * 获取自己收到的主播印象列表
     */
    public static void getMyImpress(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetMyLabel", MainHttpConsts.GET_MY_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 勿扰开关 0关 1开
     *
     * @param open
     * @param callback
     */
    public static void setDisturbSwitch(boolean open, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetDisturbSwitch", MainHttpConsts.SET_DISTURB_SWITCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("isdisturb", open ? 1 : 0)
                .execute(callback);
    }

    /**
     * 视频接听开关 0关 1开
     *
     * @param open
     * @param callback
     */
    public static void setVideoSwitch(boolean open, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetVideoSwitch", MainHttpConsts.SET_VIDEO_SWITCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("isvideo", open ? 1 : 0)
                .execute(callback);
    }


    /**
     * 语音接听开关 0关 1开
     *
     * @param open
     * @param callback
     */
    public static void setVoiceSwitch(boolean open, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetVoiceSwitch", MainHttpConsts.SET_VOICE_SWITCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("isvoice", open ? 1 : 0)
                .execute(callback);
    }


    /**
     * 设置视频价格
     */
    public static void setVideoPrice(String value, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetVideoValue", MainHttpConsts.SET_VIDEO_PRICE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("value", value)
                .execute(callback);
    }


    /**
     * 设置语音价格
     */
    public static void setVoicePrice(String value, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetVoiceValue", MainHttpConsts.SET_VOICE_PRICE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("value", value)
                .execute(callback);
    }


    /**
     * 礼物柜 获取礼物列表
     */
    public static void getGiftCabList(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetGiftCab", MainHttpConsts.GET_GIFT_CAB_LIST)
                .params("liveuid", liveUid)
                .execute(callback);
    }


    /**
     * 获取自己的相册
     */
    public static void getMyAlbum(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Photo.MyPhoto", MainHttpConsts.GET_MY_ALBUM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取自己的相册
     */
    public static void getAlbumFee(HttpCallback callback) {
        HttpClient.getInstance().get("Photo.GetFee", MainHttpConsts.GET_ALBUM_FEE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 把相册图片信息保存在服务器
     */
    public static void setPhoto(String thumb, int isprivate, String coin, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5(StringUtil.contact("isprivate=", String.valueOf(isprivate), "&thumb=", thumb, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Photo.SetPhoto", MainHttpConsts.SET_PHOTO)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("thumb", thumb)
                .params("isprivate", isprivate)
                .params("coin", coin)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 删除自己的相册中的照片
     */
    public static void deletePhoto(String photoId, HttpCallback callback) {
        HttpClient.getInstance().get("Photo.DelPhoto", MainHttpConsts.DELETE_PHOTO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("photoid", photoId)
                .execute(callback);
    }


    /**
     * 把自己的相册中的私密照片设为公开的
     */
    public static void publicPhoto(String photoId, HttpCallback callback) {
        HttpClient.getInstance().get("Photo.SetPublic", MainHttpConsts.PUBLIC_PHOTO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("photoid", photoId)
                .execute(callback);
    }


    /**
     * 设置背景墙的封面图
     */
    public static void setWallCover(String thumb, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5(StringUtil.contact("thumb=", thumb, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Wall.SetCover", MainHttpConsts.SET_WALL_COVER)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("thumb", thumb)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 获取个人主页相册
     */
    public static void getHomePhoto(String liveuid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Photo.GetHomePhoto", MainHttpConsts.GET_HOME_PHOTO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveuid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 购买照片
     */
    public static void buyPhoto(String photoId, HttpCallback callback) {
        HttpClient.getInstance().get("Photo.BuyPhoto", MainHttpConsts.BUY_PHOTO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("photoid", photoId)
                .execute(callback);
    }

    /**
     * 用于更新照片观看量
     */
    public static void photoAddView(String photoId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5(StringUtil.contact("photoid=", photoId, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Photo.AddView", MainHttpConsts.PHOTO_ADD_VIEW)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("photoid", photoId)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 获取自己的VIP状态
     */
    public static void getMyVip(HttpCallback callback) {
        HttpClient.getInstance().get("Vip.MyVip", MainHttpConsts.GET_MY_VIP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 余额购买vip
     */
    public static void buyVipWithCoin(String vipid, HttpCallback callback) {
        HttpClient.getInstance().get("Vip.BuyVip", MainHttpConsts.BUY_VIP_WITH_COIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("vipid", vipid)
                .execute(callback);
    }


    /**
     * 获取我的背景墙
     */
    public static void getMyWall(HttpCallback callback) {
        HttpClient.getInstance().get("Wall.MyWall", MainHttpConsts.GET_MY_WALL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 删除自己的背景墙中的照片
     */
    public static void deleteWall(String wallId, HttpCallback callback) {
        HttpClient.getInstance().get("Wall.DelWall", MainHttpConsts.DELETE_WALL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("wallid", wallId)
                .execute(callback);
    }


    /**
     * 设置背景墙中的照片
     *
     * @param action 行为,0新加,1替换
     * @param type   类型，0图片1视频
     * @param oldId  旧背景ID
     * @param newId  新背景ID
     */
    public static void setWall(int action, int type, String oldId, String newId, HttpCallback callback) {
        HttpClient.getInstance().get("Wall.SetWall", MainHttpConsts.SET_WALL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("action", action)
                .params("type", type)
                .params("oldid", oldId)
                .params("newid", newId)
                .execute(callback);
    }

    /**
     * 检查是否要弹邀请码的弹窗
     */
    public static void checkAgent(HttpCallback callback) {
        HttpClient.getInstance().get("Agent.Check", MainHttpConsts.CHECK_AGENT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 设置邀请码
     */
    public static void setAgent(String code, HttpCallback callback) {
        HttpClient.getInstance().get("Agent.SetAgent", MainHttpConsts.CHECK_AGENT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("code", code)
                .execute(callback);
    }
    /*-----------7.22*/

    /**
     * 搜索用户
     */
    public static void searchUser(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.search", MainHttpConsts.SEARCH_USER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }


    //排行榜  收益榜
    public static void profitList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.profitList", MainHttpConsts.PROFIT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    //排行榜  贡献榜
    public static void consumeList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.consumeList", MainHttpConsts.CONSUME_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);

    }

    /**
     * 首页 最新
     */
    public static void getNew(int p, int sex, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getNew", MainHttpConsts.GET_NEW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .params("sex", sex)
                .execute(callback);
    }


    /*****************10.23 start********************/


    /**
     * 1：新用户注册成功后修改头像 昵称 性别 城市（新加）
     * User.regUpdateInfo
     * http://www.taodada.wang/appapi/index.php?service=User.regUpdateInfo&uid=101312&token=c7edaa4f56a0b9d26482f6760b44c713
     * &avatar=/default.png
     * &name=张三&sex=1
     * &province=山东&city=泰安&district=岱岳区
     */
    public static void regUpdateInfo(String avatar, String name, int sex,
                                     String birthday, String city, HttpCallback callback) {
        HttpClient.getInstance().get("User.regUpdateInfo", "regUpdateInfo")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("avatar", avatar)
                .params("name", name)
                .params("city", city)
                .params("sex", sex)
                .params("birthday", birthday)
                .execute(callback);
    }

    /**
     * 判断用户是否注册成功后是否修改了昵称、头像、性别、城市信息（新加）
     * User.checkEditStatus
     * http://www.taodada.wang/appapi/index.php?service=User.checkEditStatus&uid=101312&token=c7edaa4f56a0b9d26482f6760b44c713
     * info[0]['status'] 1 已修改 0 未修改
     */
    public static void checkEditStatus(HttpCallback callback) {
        HttpClient.getInstance().get("User.checkEditStatus", "checkEditStatus")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 用户在线广场列表（新加）
     * Home.getOnlineList
     * http://www.taodada.wang/appapi/index.php?service=Home.getOnlineList&uid=101312&token=c7edaa4f56a0b9d26482f6760b44c713&sex=0&p=1
     * 按照用户状态倒序，用户登录时间倒序
     * 参数列表：uid  token   sex（0不限性别 1 男 2 女）p（页数）
     * 如果sex第一次传值时，记得p传值为1
     * 返回参数列表
     * info[0]['user_status']  用户在线状态   0离线，1勿扰，2在聊，3在线
     * info[0]['sex'] 用户性别 1 男 2 女
     */
    public static void getOnlineList(int sex, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getOnlineList", "getOnlineList")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("sex", sex)
                .params("p", p)
                .execute(callback);
    }


    /*****************10.23 end********************/


    /**
     *
     */
    public static void setAlipayUserAccount(String authCode, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.SetAlipayUserAccount", MainHttpConsts.SET_ALI_ACCOUNT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("auth_code", authCode)
                .execute(callback);
    }

    /**
     * 获取用户信息
     */
    public static void getUserBaseStatus(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetUserBaseStatus", MainHttpConsts.GET_USER_BASE_STATUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }
}