package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.main.bean.LoginBean;
import com.aihuan.main.presenter.OnCheckBack;
import com.aihuan.main.presenter.UserStatusP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.LoginSuccessEvent;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.ValidatePhoneUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by cxf on 2018/9/17.
 * 手机登录
 */

public class LoginPhoneActivity extends AbsActivity {

    private static final int TOTAL = 60;
    private EditText mEditPhone;
    private EditText mEditCode;
    private TextView mBtnCode;
    private TextView mTip;
    private Handler mHandler;
    private int mCount = TOTAL;
    private HttpCallback mGetCodeCallback;
    private String mGetCodeAgain;
    private boolean mHasGetCode;
    private boolean mFirstLogin;//是否是第一次登录
    private ImageView checkBox;
    private View tipGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_phone;
    }

    @Override
    protected void main() {
        mGetCodeAgain = WordUtil.getString(R.string.login_get_code_again);
        mEditPhone = findViewById(R.id.edit_phone);
        mEditCode = findViewById(R.id.edit_code);
        mBtnCode = findViewById(R.id.btn_get_code);
        mTip = findViewById(R.id.btn_tip);
        mTip.setText(
                String.format(WordUtil.getString(R.string.login_tip_2),
                        CommonAppConfig.APP_IS_YUNBAO_SELF ? "云豹私聊" : CommonAppConfig.getInstance().getAppName()
                ));

        checkBox = findViewById(R.id.checkBox);
        tipGroup = findViewById(R.id.tip_group);
        tipGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setSelected(!checkBox.isSelected());
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnCode.setText(mCount + "s");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnCode.setText(mGetCodeAgain);
                    mCount = TOTAL;
                    if (mBtnCode != null) {
                        mBtnCode.setEnabled(true);
                    }
                }
            }
        };
    }


    public static void forward(Context context) {
        context.startActivity(new Intent(context, LoginPhoneActivity.class));
    }


    public void phoneLoginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            login();
        } else if (i == R.id.btn_get_code) {
            getLoginCode();
        } else if (i == R.id.btn_tip) {
            forwardTip();
        }
    }

    /**
     * 手机号验证码登录
     */
    private void login() {
        if (!checkBox.isSelected()) {
            ToastUtil.show(R.string.user_agreement_check_tips);
            return;
        }
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        if (!mHasGetCode) {
            ToastUtil.show(R.string.login_get_code_please);
            return;
        }
        String code = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            mEditCode.setError(WordUtil.getString(R.string.login_input_code));
            mEditCode.requestFocus();
            return;
        }
        MainHttpUtil.login(phoneNum, code, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //登录结果返回后优先检测当前账号是否被封禁
                getUserStatus(code, msg, info);
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getLoginCode() {
        if (!checkBox.isSelected()) {
            ToastUtil.show(R.string.user_agreement_check_tips);
            return;
        }
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mEditPhone.requestFocus();
            return;
        }
        mEditCode.requestFocus();
        if (mGetCodeCallback == null) {
            mGetCodeCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (mBtnCode != null) {
                            mBtnCode.setEnabled(false);
                        }
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(0);
                        }
                        if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                            ToastUtil.show(msg);
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            };
        }
        mHasGetCode = true;
        MainHttpUtil.getLoginCode(phoneNum, mGetCodeCallback);
    }


    /**
     * 登录即代表同意服务和隐私条款
     */
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    /**
     * 登录成功！
     */
    private void onLoginSuccess(int code, String msg, LoginBean info) {
        if (code == 0 && info != null) {

            mFirstLogin = info.getIsreg() == 1;
            CommonAppConfig.getInstance().setLoginInfo(info.getId(), info.getToken(), true);
            SpUtil.getInstance().setStringValue(SpUtil.TX_IM_USER_SIGN, info.getUsersig());
            //友盟统计登录
            MobclickAgent.onProfileSignIn(Constants.MOB_PHONE, info.getId());
            EventBus.getDefault().post(new LoginSuccessEvent());
            getBaseUserInfo();
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 检测用户是否被封禁，并提示
     */
    private void getUserStatus(int loginCode, String loginMsg, String[] info) {
        if (loginCode != 0) {
            ToastUtil.show(loginMsg);
            return;
        }
        if (info.length == 0) {
            ToastUtil.show(loginMsg);
            return;
        }
        LoginBean loginInfo = JSONObject.parseObject(info[0], LoginBean.class);
        new UserStatusP().checkStatus(this, loginInfo.getId(), loginInfo.getToken(), new OnCheckBack() {
            @Override
            public void onCheckNormal() {
                onLoginSuccess(loginCode, loginMsg, loginInfo);
            }
        });
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                checkIsNewUser();
            }
        });
    }


    /**
     * 判断是否为新用户，是否需要跳转编辑信息页面
     */
    private void checkIsNewUser() {
        MainHttpUtil.checkEditStatus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject object = JSON.parseObject(info[0]);
                    boolean isNew = "0".equals(object.getString("status"));
                    if (isNew) {
                        NewUserInfoEditActivity.forward(mContext, false, "", "");
                        finish();
                    } else {
                        MainActivity.forward(mContext, mFirstLogin);
                        finish();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        MainHttpUtil.cancel(MainHttpConsts.GET_LOGIN_CODE);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        super.onDestroy();
    }
}
