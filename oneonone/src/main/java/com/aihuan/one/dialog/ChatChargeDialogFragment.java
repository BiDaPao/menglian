package com.aihuan.one.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.CoinBean;
import com.aihuan.common.bean.CoinPayBean;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.pay.PayPresenter;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.one.R;
import com.aihuan.one.adapter.ChatChargeCoinAdapter;

import java.util.List;

/**
 * Created by cxf on 2019/4/22.
 */

public class ChatChargeDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<CoinBean>, ChatChargePayDialogFragment.ActionListener {

    private RecyclerView mRecyclerView;
    private TextView mBtnCharge;
    private List<CoinPayBean> mPayList;
    private ChatChargeCoinAdapter mAdapter;
    private CoinBean mCheckedCoinBean;
    private PayPresenter mPayPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_charge;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(310);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 20);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mBtnCharge = (TextView) findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mRecyclerView == null) {
                        return;
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<CoinPayBean> paylist = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    mPayList = paylist;
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (list != null && list.size() > 0) {
                        CoinBean bean = list.get(0);
                        bean.setChecked(true);
                        mAdapter = new ChatChargeCoinAdapter(mContext, list);
                        mAdapter.setOnItemClickListener(ChatChargeDialogFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        showMoney(bean);
                    }
                    if (mPayPresenter != null) {
                        String coin = obj.getString("coin");
                        mPayPresenter.setBalanceValue(Long.parseLong(coin));
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_charge) {
            charge();
        }
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {
        showMoney(bean);
    }

    private void showMoney(CoinBean bean) {
        mCheckedCoinBean = bean;
        if (mCheckedCoinBean != null && mBtnCharge != null) {
            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), mCheckedCoinBean.getMoney()));
        }
    }

    private void charge() {
        if (mCheckedCoinBean == null || mPayList == null || mPayList.size() == 0) {
            return;
        }
        ChatChargePayDialogFragment fragment = new ChatChargePayDialogFragment();
        fragment.setCoinString(StringUtil.contact(mCheckedCoinBean.getCoin(), CommonAppConfig.getInstance().getCoinName()));
        fragment.setMoneyString(mCheckedCoinBean.getMoney());
        fragment.setPayList(mPayList);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatChargePayDialogFragment");
    }

    @Override
    public void onChargeClick(String payType) {
        if (mPayPresenter != null && mCheckedCoinBean != null) {
            String money = mCheckedCoinBean.getMoney();
            String goodsName = StringUtil.contact(mCheckedCoinBean.getCoin(), CommonAppConfig.getInstance().getCoinName());
            String orderParams = StringUtil.contact(
                    "&uid=", CommonAppConfig.getInstance().getUid(),
                    "&money=", money,
                    "&changeid=", mCheckedCoinBean.getId(),
                    "&coin=", mCheckedCoinBean.getCoin());
            mPayPresenter.pay(payType, money, goodsName, orderParams);
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        mPayPresenter = null;
        super.onDestroy();
    }

    public void setPayPresenter(PayPresenter payPresenter) {
        mPayPresenter = payPresenter;
    }
}
