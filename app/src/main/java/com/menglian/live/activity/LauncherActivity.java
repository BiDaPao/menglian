package com.menglian.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.ToastUtil;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.main.activity.LoginActivity;
import com.aihuan.main.activity.MainActivity;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.menglian.live.AppContext;
import com.menglian.live.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by cxf on 2018/9/17.
 */
@Route(path = RouteUtil.PATH_LAUNCHER)
public class LauncherActivity extends AppCompatActivity {

    private Handler mHandler;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setStatusBar();
        setContentView(R.layout.activity_launcher);
        mContext = this;
        ImageView imageView = findViewById(R.id.img);
        ImgLoader.display(mContext, R.mipmap.screen, imageView);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getConfig();
            }
        }, 1000);
    }


    /**
     * 获取Config信息
     */
    private void getConfig() {
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (bean != null) {
                    String beautyKey = CommonAppConfig.getMetaDataString("SPROUT_KEY");
                    AppContext.sInstance.initBeautySdk(beautyKey);
                    checkUidAndToken();
                }
            }
        });
    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                new String[]{SpUtil.UID, SpUtil.TOKEN});
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            MainHttpUtil.getBaseInfo(uid, token, new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    if (bean != null) {
                        CommonAppConfig.getInstance().setLoginInfo(uid, token, false);
                        checkIsNewUser();
                    }
                }
            });
        } else {
            LoginActivity.forward();
        }
    }


    /**
     * 判断是否为新用户，是否需要跳转编辑信息页面
     */
    private void checkIsNewUser(){
        MainHttpUtil.checkEditStatus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0){
                    JSONObject object = JSON.parseObject(info[0]);
                    boolean isNew = "0".equals(object.getString("status"));
                    if (isNew){
                      //  NewUserInfoEditActivity.forward(mContext,false);
                        //如果用户在编辑资料时，杀进程，打开APP跳转登录界面
                        LoginActivity.forward();
                        finish();
                    }else {
                        forwardMainActivity();
                    }
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }



    /**
     * 跳转到首页
     */
    private void forwardMainActivity() {
        MainActivity.forward(mContext);
        finish();
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }
}
