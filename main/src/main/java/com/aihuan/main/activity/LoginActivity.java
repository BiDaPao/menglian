package com.aihuan.main.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.LoginSuccessEvent;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.mob.LoginData;
import com.aihuan.common.mob.MobBean;
import com.aihuan.common.mob.MobCallback;
import com.aihuan.common.mob.MobLoginUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.LoginTypeAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by cxf on 2018/9/17.
 */

public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private View mRoot;
    private ImageView mBg;
    private RecyclerView mRecyclerView;
    private TextView mTip;
    private ObjectAnimator mAnimator;
    private MobLoginUtil mLoginUtil;
    private boolean mFirstLogin;//是否是第一次登录
    private String mLoginType = Constants.MOB_PHONE;//登录方式

    private String mUserAvatar;//三方登陆获取的信息
    private String mUserName;

    private ImageView checkBox;
    private View tipGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        mRoot = findViewById(R.id.root);
        ImageView appIcon = findViewById(R.id.app_icon);
        ImgLoader.display(mContext, R.mipmap.logo, appIcon);
        mTip = findViewById(R.id.btn_tip);
        mTip.setText(
                String.format(WordUtil.getString(R.string.login_tip_2),
                        CommonAppConfig.APP_IS_YUNBAO_SELF ? "云豹" : CommonAppConfig.getInstance().getAppName()
                ));
        mBg = findViewById(R.id.bg);
        mBg.post(new Runnable() {
            @Override
            public void run() {
                if (mBg != null && mRoot != null) {
                    int bgHeight = mBg.getHeight();
                    int rootHeight = mRoot.getHeight();
                    int dy = bgHeight - rootHeight;
                    if (dy > 0) {
                        mAnimator = ObjectAnimator.ofFloat(mBg, "translationY", 0, -dy);
                        mAnimator.setInterpolator(new LinearInterpolator());
                        mAnimator.setDuration(4000);
                        mAnimator.setRepeatCount(-1);
                        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
                        mAnimator.start();
                    }
                }
            }
        });
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<MobBean> list = MobBean.getLoginTypeList(configBean.getLoginType());
            if (list != null && list.size() > 0) {
                mRecyclerView = findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                adapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(adapter);
                mLoginUtil = new MobLoginUtil();
            }
        }
        checkBox = findViewById(R.id.checkBox);
        tipGroup = findViewById(R.id.tip_group);
        tipGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setSelected(!checkBox.isSelected());
            }
        });
        EventBus.getDefault().register(this);
    }


    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }


    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_tip) {
            forwardTip();
        }
    }


    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        mUserAvatar = data.getAvatar();
        mUserName = data.getNickName();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getNickName(), data.getAvatar(), data.getFlag(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    /**
     * 登录成功！
     */
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            SpUtil.getInstance().setStringValue(SpUtil.TX_IM_USER_SIGN, obj.getString("usersig"));
            //友盟统计登录
            MobclickAgent.onProfileSignIn(mLoginType, uid);
            getBaseUserInfo();
        } else {
            ToastUtil.show(msg);
        }
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
                        NewUserInfoEditActivity.forward(mContext, true, mUserName, mUserAvatar);
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
    public void onItemClick(MobBean bean, int position) {
        if (!checkBox.isSelected()) {
            ToastUtil.show(R.string.user_agreement_check_tips);
            return;
        }
        if (Constants.MOB_PHONE.equals(bean.getType())) {
            LoginPhoneActivity.forward(mContext);
            return;
        }
        if (mLoginUtil == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = null;
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
    }
}
