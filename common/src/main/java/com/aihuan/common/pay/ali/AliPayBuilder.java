package com.aihuan.common.pay.ali;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.aihuan.common.pay.AliAuthCallback;
import com.aihuan.common.presenter.RobotPresenter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.pay.AliCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cxf on 2017/9/21.
 */

public class AliPayBuilder {

    private Activity mActivity;
    private String mPartner;// 商户ID
    private String mSellerId; // 商户收款账号
    private String mPrivateKey; // 商户私钥，pkcs8格式
    private String mAppId;//
    private String mGoodsName;//商品名称
    private String mMoney;//要支付的金额
    private String mOrderParams;//订单获取订单需要的参数
    private String mCallbackUrl;//支付宝充值回调地址
    private AliHandler mAliHandler;
    private String mTargetId;//商户标识该次用户授权请求的 ID，该值在商户端应保持唯一

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public AliPayBuilder(Activity activity, String partner, String sellerId, String mAppId, String privateKey) {
        mActivity = new WeakReference<>(activity).get();
        mPartner = partner;
        mSellerId = sellerId;
        mPrivateKey = privateKey;
        this.mAppId = mAppId;
    }

    public AliPayBuilder setMoney(String money) {
        mMoney = money;
        return this;
    }

    public AliPayBuilder setGoodsName(String goodsName) {
        mGoodsName = goodsName;
        return this;
    }

    public AliPayBuilder setOrderParams(String orderParams) {
        mOrderParams = orderParams;
        return this;
    }


    public AliPayBuilder setPayCallback(AliCallback callback) {
        mAliHandler = new AliHandler(callback);
        return this;
    }

    public AliPayBuilder setAuthCallback(AliAuthCallback callback) {
        mAliHandler = new AliHandler(callback);
        return this;
    }

    public void setCallbackUrl(String callbackUrl) {
        mCallbackUrl = callbackUrl;
    }

    public void setTargetId(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    /**
     * 从服务器端获取订单号,即下单
     */
    public void pay() {
        if (TextUtils.isEmpty(mOrderParams) || TextUtils.isEmpty(mMoney) || TextUtils.isEmpty(mGoodsName) || TextUtils.isEmpty(mCallbackUrl)) {
            return;
        }
        CommonHttpUtil.getAliOrder(mOrderParams, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
//                    String orderId = obj.getString("orderid");//商品dingdanId
                    String signStr = obj.getString("sign_str");
                    payV2(signStr);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mActivity);
            }


        });
    }

    private static class AliHandler extends Handler {

        private AliCallback mAliCallback;
        private AliAuthCallback mCallBack;

        public AliHandler(AliCallback aliCallback) {
            mAliCallback = new WeakReference<>(aliCallback).get();
        }

        public AliHandler(AliAuthCallback aliCallback) {
            mCallBack = new WeakReference<>(aliCallback).get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    if (mAliCallback == null) break;
                    L.e("支付宝支付返回结果： " + msg.obj.toString());
                    Map<String, String> result = (Map<String, String>) msg.obj;
                    if ("9000".equals(result.get("resultStatus"))) {
                        mAliCallback.onSuccess();
                    } else {
                        mAliCallback.onFailed();
                    }
                    mAliCallback = null;
                    break;
                case SDK_AUTH_FLAG:
                    if (mCallBack == null) break;
                    L.e("支付宝授权返回结果： " + msg.obj.toString());

                    RobotPresenter.INSTANCE.postError("认证授权返回结果：", msg.obj.toString());
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        mCallBack.onSuccess(authResult.getAuthCode());
                    } else {
                        // 其他状态值则为授权失败
                        mCallBack.onFailed();
                    }
                    break;
                default:
                    break;
            }

        }

    }


    /**
     * 支付宝支付业务示例
     */
    public void payV2(String orderInfo) {
//        if (TextUtils.isEmpty(mAppId) || TextUtils.isEmpty(mPartner) || TextUtils.isEmpty(mPrivateKey)) {
////            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
//            return;
//        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        String timestamp = formatTime(System.currentTimeMillis());
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(mAppId
//                , mGoodsName
//                , mMoney
//                , orderSn
//                , mCallbackUrl
//                , timestamp
//                , true);
//        L.e("支付宝订单信息--Map------->" + params.toString());
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
////        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
////        String sign = OrderInfoUtil2_0.getSign(params, mPrivateKey, true);
//        final String orderInfo = orderParam + "&" + sign;
        L.e("支付宝订单信息----->" + orderInfo);
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {

                PayTask alipay = new PayTask(mActivity);
                //执行支付，这是一个耗时操作，最后返回支付的结果，用handler发送到主线程
                Map<String, String> result = alipay.payV2(orderInfo, true);
                L.e(alipay.getVersion() + "支付宝支付返回结果----->" + result);
                if (mAliHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mAliHandler.sendMessage(msg);
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    public String formatTime(long timeMillis) {
        if (timeMillis > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(timeMillis);
        }
        return "";
    }


    /**
     * 支付宝账户授权业务示例
     */
    public void authV2() {
        if (TextUtils.isEmpty(mPartner) || TextUtils.isEmpty(mAppId)
                || TextUtils.isEmpty(mTargetId) || TextUtils.isEmpty(mPrivateKey)) {
//            showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo 的获取必须来自服务端；
         */
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(mPartner, mAppId, mTargetId, true);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String sign = OrderInfoUtil2_0.getSign(authInfoMap, mPrivateKey, true);
        final String authInfo = info + "&" + sign;
        RobotPresenter.INSTANCE.postError("认证资料请求参数：", authInfo);
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(mActivity);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);
                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mAliHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


}
