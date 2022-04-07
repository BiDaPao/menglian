package com.aihuan.main.activity;

import static com.aihuan.common.Constants.AUTH_NONE;
import static com.aihuan.common.Constants.AUTH_SUCCESS;
import static com.aihuan.common.Constants.AUTH_WAITING;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.custom.XEditText;
import com.aihuan.one.http.OneHttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.mobile.android.verify.sdk.ServiceFactory;

public class AuthActivity extends AbsActivity {

    private Dialog mLoading;
    private String certifyId ;

    private int mAuthStatus;

    private String mNameVal ;
    private String mIdNumVal ;
    //支付宝人脸认证
    private Boolean hasVerify = false;
    //判断是否需要接受实人认证结果
    private boolean waitForResult = false;
    private int isAlipayAuth;//真人人脸认证结果  1 通过  0 未通过
    private XEditText realNameInput;
    private XEditText cardNumberInput;


    public static void forward(Context context, int authStauts) {
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(Constants.AUTH_STATUS, authStauts);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_basic;
    }

    @Override
    protected void main() {
        setTitle("认证中心");
        setOnTextListener();
        mAuthStatus = getIntent().getIntExtra(Constants.AUTH_STATUS, Constants.AUTH_NONE);
        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mNameVal)) {
                ToastUtil.show(R.string.auth_tip_22);
                return;
            }
            if (TextUtils.isEmpty(mIdNumVal)) {
                ToastUtil.show(R.string.auth_id_num_toast);
                return;
            }
            this.verifyTips();
        });
        getAuthInfo();

    }




    /**
     * 真实姓名、身份证认证提醒
     */
    private void verifyTips() {
        DialogUitl.showTipsAuth(mContext,
                "请确认以下信息：\n\n 1.真实姓名：" + mNameVal + "\n 2.身份证号：" + mIdNumVal + "\n",
                (dialog, content) -> {
                    dialog.dismiss();
                    verifyInfo();
                });
    }



    private void verifyInfo() {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        mLoading.show();

        final String bizCode = ServiceFactory.build().getBizCode(mContext);
        OneHttpUtil.setAliAuth(mNameVal, mIdNumVal, bizCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String url = obj.getString("url");
                    certifyId = obj.getString("certify_id");
                    startVerify(url, certifyId, bizCode);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    private void startVerify(String url, String certifyId, String bizCode) {
        // 封装认证数据
        JSONObject requestInfo = new JSONObject();
        requestInfo.put("url", url);
        requestInfo.put("certifyId", certifyId);
        requestInfo.put("bizCode", bizCode);
        // 发起认证
        ServiceFactory.build().startService(this, requestInfo, response -> {
            String responseCode = response.get("resultStatus");
            if (mLoading != null) {
                mLoading.dismiss();
            }
            //9001 等待支付宝返回结果  结果不会直接回调，需要返回APP后自主查询
            if ("9001".equals(responseCode)) {
                waitForResult = true;
//                    uploadFile();
            } else {
                ToastUtil.show("实人认证失败,请核对信息后重新认证！");
            }
        });
    }




    private void setOnTextListener(){
         realNameInput = findViewById(R.id.real_name_input);
          cardNumberInput = findViewById(R.id.card_number_input);
        cardNumberInput.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mIdNumVal =s.toString();
            }
        });
        realNameInput.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNameVal = s.toString();
            }
        });


    }

    private void goPlatformCertify(){
        OneHttpUtil.setUserAuth(mNameVal, mIdNumVal, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show("实名认证成功");
                AuthInfoActivity.forward(AuthActivity.this,AUTH_NONE);
                finish();
            }
        });
    }

    private void checkCertifyResult() {
        if (!TextUtils.isEmpty(certifyId)) {
            OneHttpUtil.checkAliAuthResult(certifyId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        isAlipayAuth = obj.getIntValue("passed");
                        if (isAlipayAuth == 1) {
                            goPlatformCertify();
                        } else {
                            ToastUtil.show("实人认证失败,请核对信息后重新认证！");
                            if (mLoading != null) {
                                mLoading.dismiss();
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                        if (mLoading != null) {
                            mLoading.dismiss();
                        }
                    }
                }
            });
        } else {
            if (mLoading != null) {
                mLoading.dismiss();
            }
        }
    }

    /**
     * 处理通过schema跳转过来的结果查询
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        queryCertifyResult(intent);
    }

    /**
     * 处理回前台触发结果查询
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (waitForResult) {
            L.e("实人认证结果——————————————————————");
            waitForResult = false;
//            uploadFile();
            // 查询认证结果
            checkCertifyResult();
        }
    }
    /**
     * 业务方查询结果
     *
     * @param intent
     */
    protected void queryCertifyResult(Intent intent) {
        L.e("查询结果：----------------" + intent.toString());
        if (intent == null) {
            return;
        }
        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        String queryResult = intent.getStringExtra("queryResult");
        // 如果有很多场景会通过schema调用到当前页面，建议在传给认证回跳的schema中，增加参数识别出需要查询认证结果的场景
        String param = data.getQueryParameter("queryResult");
        L.e("查询结果：queryResult" + queryResult + "   param: " + param);
        waitForResult = false;// 防止走到onresume中再次查询结果
        // 调用结构由后端查询结果
        checkCertifyResult();
    }

    private void getAuthInfo() {
        OneHttpUtil.getAuth(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mNameVal = obj.getString("name");
                    if (realNameInput != null) {
                        realNameInput.setText(mNameVal);
                    }
                    mIdNumVal = obj.getString("id_card");
                    if (cardNumberInput != null) {
                        cardNumberInput.setText(mIdNumVal);
                    }
                    int isGet = obj.getIntValue("is_alipay_auth");
                    //当姓名、身份证号不为空即代表已实人认证过
                    hasVerify = isGet == 1;
                    if (hasVerify) {
                        realNameInput.setEnabled(false);
                        cardNumberInput.setEnabled(false);
                        findViewById(R.id.btn_submit).setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(obj.getString("user_nickname"))){
                            AuthInfoActivity.forward(AuthActivity.this,AUTH_SUCCESS);
                        }else{
                            AuthInfoActivity.forward(AuthActivity.this,AUTH_NONE);
                        }
                        finish();
                    }
                }
            }
        });
    }

}
