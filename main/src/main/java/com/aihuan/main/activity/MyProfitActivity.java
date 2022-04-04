package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.presenter.ServiceP;
import com.aihuan.common.utils.DialogUitl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.utils.MainIconUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.SimpleFormatter;

import cn.tillusory.sdk.net.S;

/**
 * Created by cxf on 2018/10/20.
 */

public class MyProfitActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyProfitActivity.class));
    }

    private TextView mAllName;//总映票数TextView
    private TextView mAll;//总映票数
    private TextView mCanName;//可提取映票数TextView
    private TextView mCan;//可提取映票数
    private TextView mGetName;//输入要提取的映票数
    private TextView mMoney;
    private TextView mTip;//温馨提示
    private EditText mEdit;
    private double mRate;
    private long mMaxCanMoney;//可提取映票数
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private String mAccountID;
    private String mVotesName;
    private View mBtnCash;

    private ImageView checkBox;

    private final int REQUEST_ACCOUNT = 101;


    //提现手续费比例
//    private int cashRateFee;
    //扣除手续费提现比例
    private double cashRate = 1;
//    private NumberFormat numberFormat = NumberFormat.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
    }

    @Override
    protected void main() {
        mAllName = (TextView) findViewById(R.id.all_name);
        mAll = (TextView) findViewById(R.id.all);
        mCanName = (TextView) findViewById(R.id.can_name);
        mCan = (TextView) findViewById(R.id.can);
        mGetName = (TextView) findViewById(R.id.get_name);
        mTip = (TextView) findViewById(R.id.tip);
        mMoney = (TextView) findViewById(R.id.money);
        mEdit = findViewById(R.id.edit);
        checkBox = findViewById(R.id.checkBox);
//        numberFormat.setMaximumFractionDigits(2);
        findViewById(R.id.tip_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setSelected(!checkBox.isSelected());
            }
        });
        findViewById(R.id.btn_tip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, HtmlConfig.WITHDDRAW_MONEY);
            }
        });
//        DecimalFormat df = new DecimalFormat("0.00");
        //截取两位小数
        DecimalFormat df = new DecimalFormat("######0.00");
        df.setRoundingMode(RoundingMode.FLOOR);

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    double i = Double.parseDouble(s.toString());
                    if (i > mMaxCanMoney) {
                        i = mMaxCanMoney;
                        s = String.valueOf(mMaxCanMoney);
                        mEdit.setText(s);
                        mEdit.setSelection(s.length());
                    }
                    if (mRate != 0) {
//                        BigDecimal decimal = new BigDecimal(s.toString());
//                        BigDecimal rateDecimal = new BigDecimal(mRate);
//                        BigDecimal cashDecimal = new BigDecimal(cashRate);
//                        double f = i/mRate;
//                        BigDecimal bg = new BigDecimal(f);
//                        double f1 = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();//截取小数点后两位
//                        L.e("F: " + f+"  截取String ： " + f1);
//                        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//四舍五入
                        double cashMoney = i / mRate * cashRate;
//                        BigDecimal cashMoney = decimal.divide(rateDecimal).multiply(cashDecimal);
                        L.e("比率：" + mRate + "  可提现比例： " + cashRate + " 提现金额： " + cashMoney);
                        mMoney.setText("￥" + df.format(cashMoney));
                    }
                    mBtnCash.setEnabled(true);
                } else {
                    mMoney.setText("￥");
                    mBtnCash.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVotesName = CommonAppConfig.getInstance().getVotesName();
        mAllName.setText("总" + mVotesName + "数");
        mCanName.setText("可提现" + mVotesName + "数");
        mGetName.setText("输入要提取的" + mVotesName + "数");
        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCash.setOnClickListener(this);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccount = findViewById(R.id.account);
        loadData();
    }


    private void loadData() {
        MainHttpUtil.getProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mAll.setText(obj.getString("votestotal"));
                        mTip.setText(obj.getString("tips"));
                        String votes = obj.getString("votes");
                        mCan.setText(votes);
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mMaxCanMoney = Long.parseLong(votes);
                        mRate = obj.getDoubleValue("cash_rate");
                        int cashRateFee = obj.getIntValue("cash_rate_fee");
                        L.e("提现比例： " + cashRateFee);
                        if (cashRateFee > 0) {
                            //计算可提现比例
                            cashRate = (100.00d - cashRateFee) / 100.00d;
                        }
                        //甜蜜冻结状态  0-不冻结，1-冻结
                        int status = obj.getIntValue("disable_votes");
                        if (status == 1) {
                            showCongelation();
                        }
                    } catch (Exception e) {
                        L.e("提现接口错误------>" + e.getClass() + "------>" + e.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cash) {
            cash();

        } else if (i == R.id.btn_choose_account) {
            chooseAccount();

        } else if (i == R.id.btn_cash_record) {
            cashRecord();

        }
    }

    /**
     * 提现记录
     */
    private void cashRecord() {
        WebViewActivity.forward(mContext, HtmlConfig.CASH_RECORD);
    }


    /**
     * 提现
     */
    private void cash() {
        if (!checkBox.isSelected()) {
            ToastUtil.show(R.string.withdraw_agreement_tips);
            return;
        }
        String votes = mEdit.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show(String.format(WordUtil.getString(R.string.profit_input_votes), mVotesName));
            return;
        }
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        MainHttpUtil.doCash(votes, mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                mEdit.setText("");
                loadData();
            }
        });
    }

    /**
     * 选择账户
     */
    private void chooseAccount() {
        Intent intent = new Intent(mContext, CashActivity.class);
        intent.putExtra(Constants.CASH_ACCOUNT_ID, mAccountID);
        startActivityForResult(intent, REQUEST_ACCOUNT);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ALI_ACCOUNT_AVATAR, Constants.CASH_ALI_ACCOUNT_NICK);
//        String[] aliValues = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ALI_ACCOUNT_AVATAR, Constants.CASH_ALI_ACCOUNT_NICK);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String accountAvatar = values[1];
            String accountNick = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(accountAvatar) && !TextUtils.isEmpty(accountNick)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                ImgLoader.displayAvatar(mContext, accountAvatar, mAccountIcon);
                mAccount.setText(accountNick);
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.DO_CASH);
        MainHttpUtil.cancel(MainHttpConsts.GET_PROFIT);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT && resultCode == RESULT_OK) {
            getAccount();
        }
    }

    private void showCongelation() {
        DialogUitl.showCongelation(this, "当前账户因违规收益被冻结，如有问题请联系客服处理。", (dialog, content) -> {
//            ServiceActivity.forward(this);
            new ServiceP(mContext).openWXService();
        }, (dialog, content) -> {
            dialog.dismiss();
            finish();
        });
    }

    // double 类型保留2位小数
    private String changeStrtwo(Double str) {
        //截取两位小数
        DecimalFormat df = new DecimalFormat("######0.00");
        df.setRoundingMode(RoundingMode.FLOOR);
        String format = df.format(str);


        return format;
    }
}