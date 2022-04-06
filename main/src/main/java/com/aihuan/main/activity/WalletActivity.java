package com.aihuan.main.activity;

import android.view.View;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.CoinChangeEvent;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2019/4/10.
 */

public class WalletActivity extends AbsActivity {

    private TextView mCoinName;
    private TextView mCoin;
    private boolean mPaused;
    private boolean mCoinChanged;
    private TextView sweet ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.wallet));
        mCoinName = findViewById(R.id.coin_name);
        mCoin = findViewById(R.id.coin);
        sweet =findViewById(R.id.sweet);
        mCoinName.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), CommonAppConfig.getInstance().getCoinName()));
        EventBus.getDefault().register(this);
        getLastCoin();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mCoinChanged) {
            mCoinChanged = false;
            getLastCoin();
        }
        mPaused = false;
    }

    /**
     * 获取余额
     */
    private void getLastCoin() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean u) {
                if (mCoin != null) {
                    mCoin.setText(u.getCoin());
                    sweet.setText(u.getVotes());
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        mCoinChanged = true;
    }

    public void walletClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_charge) {
            RouteUtil.forwardMyCoin();
        } else if (i == R.id.btn_detail) {
            WalletDetailActivity.forward(mContext);
        } else if (i == R.id.btn_profit) {
            MyProfitActivity.forward(mContext);
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        super.onDestroy();
    }
}
