package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.activity.DynamicPublishActivity;
import com.aihuan.main.views.UserDynamicViewHolder;

/**
 * Created by debug on 2019/7/29.
 * 我的动态
 */

public class MyDynamicActivity extends AbsDynamicActivity implements View.OnClickListener {
    private UserDynamicViewHolder mUserDynamicViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyDynamicActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_dynamic;
    }

    @Override
    protected void main() {
        super.main();
        findViewById(R.id.btn_to_publish).setOnClickListener(this);
        mUserDynamicViewHolder = new UserDynamicViewHolder(mContext, (ViewGroup) findViewById(R.id.container), CommonAppConfig.getInstance().getUid(), DpUtil.dp2px(65),0);
        mUserDynamicViewHolder.addToParent();
        mUserDynamicViewHolder.subscribeActivityLifeCycle();
        mUserDynamicViewHolder.loadData();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(mContext, DynamicPublishActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (isBackVideo()){
            videoBack();
        }else {
            super.onBackPressed();
        }
    }

}
