package com.aihuan.main.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.umeng.analytics.MobclickAgent;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.event.LoginInvalidEvent;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.main.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */
@Route(path = RouteUtil.PATH_LOGIN_INVALID)
public class LoginInvalidActivity extends AbsActivity implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_invalid;
    }

    @Override
    protected void main() {
        TextView textView = (TextView) findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        EventBus.getDefault().post(new LoginInvalidEvent());
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出IM
        ImMessageUtil.getInstance().logoutImClient();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
    }

    @Override
    public void onClick(View v) {
        LoginActivity.forward();
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
