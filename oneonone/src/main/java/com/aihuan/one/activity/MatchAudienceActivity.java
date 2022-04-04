package com.aihuan.one.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.event.MatchSuccessEvent;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.MediaManager;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.one.R;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2019/5/7.
 * 观众匹配
 */

public class MatchAudienceActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String price, String priceVip, int type) {
        Intent intent = new Intent(context, MatchAudienceActivity.class);
        intent.putExtra(Constants.MATCH_PRICE, price);
        intent.putExtra(Constants.MATCH_PRICE_VIP, priceVip);
        intent.putExtra(Constants.CHAT_TYPE, type);
        context.startActivity(intent);
    }

    private View mMatchTip;
    private TextView mPrice;
    private TextView mPriceVip;
    private int mType;
    private boolean mStartMatch;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_audience;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.match));
        Intent intent = getIntent();
        String price = intent.getStringExtra(Constants.MATCH_PRICE);
        String priceVip = intent.getStringExtra(Constants.MATCH_PRICE_VIP);
        mType = intent.getIntExtra(Constants.CHAT_TYPE, Constants.CHAT_TYPE_VIDEO);
        String coinName = CommonAppConfig.getInstance().getCoinName();
        mMatchTip = findViewById(R.id.match_tip);
        mPrice = findViewById(R.id.price);
        mPriceVip = findViewById(R.id.price_vip);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mPrice.setText(price);
        mPriceVip.setText(priceVip);
        EventBus.getDefault().register(this);
        startMatch();
        MediaManager.getInstance().playSoundLoop(mContext, com.aihuan.common.R.raw.match_music, new MediaManager.Callback() {
            @Override
            public void onCompletion(Boolean success) {

            }
        });
    }


    public void startMatch() {
        OneHttpUtil.matchAudience(mType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                mStartMatch = true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        MediaManager.getInstance().stopPlayRecord();
        if (mStartMatch) {
            OneHttpUtil.matchAudienceCancel();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (!mStartMatch) {
            OneHttpUtil.cancel(OneHttpConsts.MATCH_AUDIENCE);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMatchSuccessEvent(MatchSuccessEvent e){
        finish();
    }
}
