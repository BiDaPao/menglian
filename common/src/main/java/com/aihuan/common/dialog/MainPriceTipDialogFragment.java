package com.aihuan.common.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.R;
import com.aihuan.common.adapter.PriceTipAdapter;
import com.aihuan.common.bean.ChatPriceTipBean;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class MainPriceTipDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private int mFrom;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_main_price_tip;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(320);
        params.height = DpUtil.dp2px(330);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mFrom = bundle.getInt(Constants.FROM, -1);
        if (mFrom == -1) {
            return;
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        getData();
    }


    private void getData() {
        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ChatPriceTipBean> list = JSON.parseArray(Arrays.toString(info), ChatPriceTipBean.class);
                    if (mRecyclerView != null && list != null && list.size() > 0) {
                        PriceTipAdapter adapter = new PriceTipAdapter(mContext, list);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        };
        if (mFrom == Constants.MAIN_ME_VIDEO) {
            CommonHttpUtil.getVideoPriceTip(callback);
        } else if (mFrom == Constants.MAIN_ME_VOICE) {
            CommonHttpUtil.getVoicePriceTip(callback);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.GET_VIDEO_PRICE_TIP);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_VOICE_PRICE_TIP);
        super.onDestroy();
    }
}
