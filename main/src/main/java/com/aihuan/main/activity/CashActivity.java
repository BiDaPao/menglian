package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.pay.AliAuthCallback;
import com.aihuan.common.pay.ali.AliPayBuilder;
import com.aihuan.common.presenter.RobotPresenter;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.CashAccountAdapter;
import com.aihuan.main.bean.CashAccountBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.views.CashAccountViewHolder;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/10/20.
 * 提现账户
 */

public class CashActivity extends AbsActivity implements View.OnClickListener, CashAccountAdapter.ActionListener, AliAuthCallback {

    private CashAccountViewHolder mCashAccountViewHolder;
    private View mNoAccount;
    private RecyclerView mRecyclerView;
    private CashAccountAdapter mAdapter;
    private String mCashAccountId;
    private AliPayBuilder aliPayBuilder;
    private TextView btnBind;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cash;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mCashAccountId = intent.getStringExtra(Constants.CASH_ACCOUNT_ID);
        if (mCashAccountId == null) {
            mCashAccountId = "";
        }
        btnBind = findViewById(R.id.btn_add);
        btnBind.setOnClickListener(this);
        mNoAccount = findViewById(R.id.no_account);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CashAccountAdapter(mContext, mCashAccountId);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
        MainHttpUtil.getCashAccountList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<CashAccountBean> list = JSON.parseArray(Arrays.toString(info), CashAccountBean.class);
                    if (list.size() > 0) {
                        if (mNoAccount.getVisibility() == View.VISIBLE) {
                            mNoAccount.setVisibility(View.INVISIBLE);
                        }
                        mAdapter.setList(list);
                        btnBind.setEnabled(false);
                    } else {
                        if (mNoAccount.getVisibility() != View.VISIBLE) {
                            mNoAccount.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add && btnBind.isEnabled()) {
            getAliParam();
        }
    }

    private void getAliParam() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    aliPayBuilder = new AliPayBuilder(CashActivity.this, obj.getString("aliapp_partner"),
                            obj.getString("aliapp_seller_id"),
                            obj.getString("aliapp_id"),
                            obj.getString("aliapp_key"));
                    aliPayBuilder.setTargetId(CommonAppConfig.getInstance().getUid());
                    aliPayBuilder.setAuthCallback(CashActivity.this);
                    aliPayBuilder.authV2();
                }
            }
        });
    }

    private void addAccount() {
        if (mCashAccountViewHolder == null) {
            mCashAccountViewHolder = new CashAccountViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
        }
        mCashAccountViewHolder.addToParent();
    }

    @Override
    public void onBackPressed() {
        if (mCashAccountViewHolder != null && mCashAccountViewHolder.isShowed()) {
            mCashAccountViewHolder.removeFromParent();
            return;
        }
        super.onBackPressed();
    }

    public void insertAccount(CashAccountBean cashAccountBean) {
        if (mAdapter != null) {
            if (mNoAccount.getVisibility() == View.VISIBLE) {
                mNoAccount.setVisibility(View.INVISIBLE);
            }
            mAdapter.insertItem(cashAccountBean);
        }
    }

    @Override
    public void onItemClick(CashAccountBean bean, int position) {
//        if (!bean.getId().equals(mCashAccountId)) {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.CASH_ACCOUNT_ID, bean.getId());
//        map.put(Constants.CASH_ACCOUNT, bean.getAccount());
//        map.put(Constants.CASH_ACCOUNT_TYPE, String.valueOf(bean.getType()));
        map.put(Constants.CASH_ALI_ACCOUNT_AVATAR, bean.getAlipay_avatar());
        map.put(Constants.CASH_ALI_ACCOUNT_NICK, bean.getAlipay_nickname());
        SpUtil.getInstance().setMultiStringValue(map);
//        }
        setResult(RESULT_OK);
        onBackPressed();
    }

    @Override
    public void onItemDelete(final CashAccountBean bean, final int position) {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.cash_delete), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainHttpUtil.deleteCashAccount(bean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (bean.getId().equals(mCashAccountId)) {
                                SpUtil.getInstance().removeValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
                            }
                            loadData();
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_ACCOUNT_LIST);
        MainHttpUtil.cancel(MainHttpConsts.ADD_CASH_ACCOUNT);
        MainHttpUtil.cancel(MainHttpConsts.DEL_CASH_ACCOUNT);
        super.onDestroy();
    }


    @Override
    public void onSuccess(String authCode) {
        RobotPresenter.INSTANCE.postError("页面结果返回结果authCode: ", authCode);
        setAliAccount(authCode);
    }

    @Override
    public void onFailed() {
        ToastUtil.show(R.string.ali_auth_failed);
    }

    private void setAliAccount(String authCode) {
        MainHttpUtil.setAlipayUserAccount(authCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                RobotPresenter.INSTANCE.postError("绑定结果请求: ", Arrays.toString(info));
                if (code == 0) {
                    List<CashAccountBean> list = JSON.parseArray(Arrays.toString(info), CashAccountBean.class);
                    if (list.size() > 0) {
                        if (mNoAccount.getVisibility() == View.VISIBLE) {
                            mNoAccount.setVisibility(View.INVISIBLE);
                        }
                        mAdapter.setList(list);
                        btnBind.setEnabled(false);
                    } else {
                        if (mNoAccount.getVisibility() != View.VISIBLE) {
                            mNoAccount.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }

            }
        });
    }
}
