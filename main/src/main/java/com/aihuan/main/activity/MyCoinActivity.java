package com.aihuan.main.activity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.bean.CoinBean;
import com.aihuan.common.bean.CoinPayBean;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.event.CoinChangeEvent;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.pay.AliCallback;
import com.aihuan.common.pay.PayPresenter;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.CoinAdapter;
import com.aihuan.main.adapter.CoinPayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/10/23.
 * 充值
 */
@Route(path = RouteUtil.PATH_COIN)
public class MyCoinActivity extends AbsActivity implements OnItemClickListener<CoinBean>, View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView mPayRecyclerView;
    private CoinAdapter mAdapter;
    private CoinPayAdapter mPayAdapter;
    private TextView mBalance;
    private long mBalanceValue;
    private boolean mFirstLoad = true;
    private PayPresenter mPayPresenter;
    private String mCoinName;

    private ImageView checkBox;
    private View tipGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.charge));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeResources(com.aihuan.video.R.color.global);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 20);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_tip).setOnClickListener(this);
        View headView = mAdapter.getHeadView();
        TextView coinNameTextView = headView.findViewById(R.id.coin_name);
        coinNameTextView.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), mCoinName));
        mBalance = headView.findViewById(R.id.coin);
        mPayRecyclerView = headView.findViewById(R.id.pay_recyclerView);
        ItemDecoration decoration2 = new ItemDecoration(mContext, 0x00000000, 14, 10);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        mPayRecyclerView.addItemDecoration(decoration2);
        mPayRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mPayAdapter = new CoinPayAdapter(mContext);
        mPayRecyclerView.setAdapter(mPayAdapter);
        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
        mPayPresenter.setPayCallback(new AliCallback() {
            @Override
            public void onSuccess() {
                if (mPayPresenter != null) {
                    mPayPresenter.checkPayResult();
                }
            }

            @Override
            public void onFailed() {

            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        }
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    mBalanceValue = Long.parseLong(coin);
                    mBalance.setText(coin);
                    List<CoinPayBean> payList = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    if (mPayAdapter != null) {
                        mPayAdapter.setList(payList);
                    }
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                    }
                    mPayPresenter.setBalanceValue(mBalanceValue);
                    mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                    mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                    mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key"));
                    mPayPresenter.setAppId(obj.getString("aliapp_id"));
                    mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {

        if (!checkBox.isSelected()) {
            ToastUtil.show(R.string.user_charge_check_tips);
            return;
        }

        if (mPayPresenter == null) {
            return;
        }
        if (mPayAdapter == null) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }
        String money = bean.getMoney();
        String goodsName = StringUtil.contact(bean.getCoin(), mCoinName);
        String orderParams = StringUtil.contact(
                "&uid=", CommonAppConfig.getInstance().getUid(),
                "&money=", money,
                "&changeid=", bean.getId(),
                "&coin=", bean.getCoin());
        mPayPresenter.pay(mPayAdapter.getPayType(), money, goodsName, orderParams);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        if (mBalance != null) {
            mBalance.setText(e.getCoin());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_tip) {
            WebViewActivity.forward(mContext, HtmlConfig.CHARGE_PRIVCAY);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }


}
