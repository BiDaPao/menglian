package com.aihuan.common.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.R;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by cxf on 2019/4/17.
 */

public class MainPriceDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private WheelView mWheelView;
    private int mFrom;
    private List<ChatPriceBean> mPriceList;
    private String mNowPrice;
    private TextView mCoinName;
    private int mMaxCanUseIndex;
    private ActionListener mActionListener;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_main_price;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(320);
        params.height = DpUtil.dp2px(200);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCoinName = (TextView) findViewById(R.id.coin_name);
        mCoinName.setText(CommonAppConfig.getInstance().getCoinName());
        if (mPriceList == null || TextUtils.isEmpty(mNowPrice)) {
            return;
        }
        findViewById(R.id.btn_price_tip).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        List<String> stringList = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0, size = mPriceList.size(); i < size; i++) {
            ChatPriceBean bean = mPriceList.get(i);
            if (bean.isCanUse()) {
                mMaxCanUseIndex = i;
            }
            String coin = bean.getCoin();
            if (mNowPrice.equals(coin)) {
                selectedIndex = i;
            }
            stringList.add(coin);
        }
        mWheelView = (WheelView) findViewById(R.id.wheelView);
        mWheelView.setTextSize(20);
        mWheelView.setTextColor(0xff969696, 0xff323232);
        mWheelView.setCycleDisable(true);//禁用循环
        mWheelView.setGravity(Gravity.CENTER);
        mWheelView.setVisibleItemCount(5);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(0xffdcdcdc);//线颜色
        config.setRatio(0.8f);//线比率
        mWheelView.setDividerConfig(config);
        mWheelView.setItems(stringList);
        mWheelView.setSelectedIndex(selectedIndex);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(DpUtil.dp2px(150), FrameLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;
        param.topMargin = -DpUtil.dp2px(10);
        mWheelView.setLayoutParams(param);
        mWheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int i) {
                ChatPriceBean bean = mPriceList.get(i);
                if (!bean.isCanUse()) {
                    mWheelView.setSelectedIndex(mMaxCanUseIndex);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_price_tip) {
            tipClick();
        } else if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            confirmClick();
        }
    }


    private void tipClick() {
        MainPriceTipDialogFragment fragment = new MainPriceTipDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FROM, mFrom);
        fragment.setArguments(bundle);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceTipDialogFragment");
    }

    private void confirmClick() {
        if (mWheelView == null || TextUtils.isEmpty(mNowPrice)) {
            return;
        }
        int index = mWheelView.getSelectedIndex();
        ChatPriceBean bean = mPriceList.get(index);
        if (!bean.isCanUse()) {
            ToastUtil.show(R.string.main_price_tip_3);
            return;
        }
        if (!mNowPrice.equals(bean.getCoin()) && mActionListener != null) {
            mActionListener.onPriceSelected(mFrom, bean);
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        mActionListener = null;
        super.onDestroy();
    }

    public interface ActionListener {
        void onPriceSelected(int from, ChatPriceBean bean);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setPriceList(List<ChatPriceBean> priceList) {
        mPriceList = priceList;
    }


    public void setNowPrice(String nowPrice) {
        mNowPrice = nowPrice;
    }

    public void setFrom(int from) {
        mFrom = from;
    }
}
