package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.main.activity.UserHomeNewActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.GiftCabActivity;
import com.aihuan.main.adapter.GiftCabAdapter;
import com.aihuan.main.adapter.UserHomeDetailAdapter;
import com.aihuan.main.bean.GiftCabBean;
import com.aihuan.main.bean.UserEvaBean;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.custom.ImpressGroup;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.aihuan.one.views.AbsUserHomeViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/8.
 */

public class UserHomeDetailViewHolder extends AbsUserHomeViewHolder implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private UserHomeDetailAdapter mUserHomeDetailAdapter;
    private String mToUid;
    private TextView mIntro;
    private TextView mSign;
    private ImpressGroup mAnchorImpressGroup;
    private ImpressGroup mUserImpressGroup;
    private TextView mOnLine;
    private TextView mLintenRate;
    private TextView mHeight;
    private TextView mWeight;
    private TextView mCity;
    private TextView mStar;
    private TextView mGiftCount;
    private TextView mGoodNum;
    private TextView mBadNum;
    private RecyclerView mGiftRecyclerView;
    private View mNoImpress;
    private View mNoGift;
    private SpannableStringBuilder mBuilder;
    private ForegroundColorSpan mColorSpan;


    public UserHomeDetailViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_detail;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(0);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mUserHomeDetailAdapter = new UserHomeDetailAdapter(mContext);
        View headView = mUserHomeDetailAdapter.getHeadView();
        mRefreshView.setRecyclerViewAdapter(mUserHomeDetailAdapter);
        mIntro = headView.findViewById(R.id.intro);
        mSign = headView.findViewById(R.id.sign);
        mAnchorImpressGroup = headView.findViewById(R.id.anchor_impress_group);
        mUserImpressGroup = headView.findViewById(R.id.user_impress_group);
        mOnLine = headView.findViewById(R.id.on_line);
        mLintenRate = headView.findViewById(R.id.listen_rate);
        mHeight = headView.findViewById(R.id.height);
        mWeight = headView.findViewById(R.id.weight);
        mCity = headView.findViewById(R.id.city);
        mStar = headView.findViewById(R.id.star);
        mGiftCount = headView.findViewById(R.id.gift_count);
        mGoodNum = headView.findViewById(R.id.good_num);
        mBadNum = headView.findViewById(R.id.bad_num);
        mGiftRecyclerView = headView.findViewById(R.id.gift_recyclerView);
        mGiftRecyclerView.setHasFixedSize(true);
        mGiftRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        mNoImpress = headView.findViewById(R.id.no_user_impress);
        mNoGift = headView.findViewById(R.id.no_gift);
        headView.findViewById(R.id.btn_user_impress).setOnClickListener(this);
        headView.findViewById(R.id.btn_gift_cab).setOnClickListener(this);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserEvaBean>() {
            @Override
            public RefreshAdapter<UserEvaBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                OneHttpUtil.getUserEvaList(mToUid, p, callback);
            }

            @Override
            public List<UserEvaBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserEvaBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserEvaBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserEvaBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

    }

    @Override
    public void loadData() {
        JSONObject obj = ((UserHomeNewActivity) mContext).getUserObj();
        if (obj == null) {
            return;
        }
        if (!isFirstLoadData()) {
            return;
        }
        if (mIntro != null) {
            mIntro.setText(obj.getString("intr"));
        }
        if (mSign != null) {
            mSign.setText(obj.getString("signature"));
        }
        if (mAnchorImpressGroup != null) {
            List<ImpressBean> list = JSON.parseArray(obj.getString("label_list"), ImpressBean.class);
            mAnchorImpressGroup.showData(list);
        }
        List<ImpressBean> evaList = JSON.parseArray(obj.getString("evaluate_list"), ImpressBean.class);
        if (evaList != null && evaList.size() > 0) {
            if (mNoImpress != null) {
                mNoImpress.setVisibility(View.INVISIBLE);
            }
            if (mUserImpressGroup != null) {
                mUserImpressGroup.showData(evaList);
            }
        }
        if (mOnLine != null) {
            mOnLine.setText(createColorString(WordUtil.getString(R.string.user_home_online), obj.getString("last_online_time")));
        }
        if (mLintenRate != null) {
            mLintenRate.setText(createColorString(WordUtil.getString(R.string.user_home_listen_rate), obj.getString("answer_rate")));
        }
        if (mHeight != null) {
            mHeight.setText(createColorString(WordUtil.getString(R.string.user_home_height), obj.getString("height")));
        }
        if (mWeight != null) {
            mWeight.setText(createColorString(WordUtil.getString(R.string.user_home_weight), obj.getString("weight")));
        }
        if (mCity != null) {
            mCity.setText(createColorString(WordUtil.getString(R.string.user_home_city), obj.getString("city")));
        }
        if (mStar != null) {
            mStar.setText(createColorString(WordUtil.getString(R.string.user_home_star), obj.getString("constellation")));
        }
        if (mGiftCount != null) {
            mGiftCount.setText(obj.getString("gift_total"));
        }
        if (mGoodNum != null) {
            mGoodNum.setText(obj.getString("goodnums"));
        }
        if (mBadNum != null) {
            mBadNum.setText(obj.getString("badnums"));
        }
        List<GiftCabBean> giftList = JSON.parseArray(obj.getString("gift_list"), GiftCabBean.class);
        if (giftList != null && giftList.size() > 0) {
            if (mNoGift != null) {
                mNoGift.setVisibility(View.INVISIBLE);
            }
            if (mGiftRecyclerView != null) {
                mGiftRecyclerView.setVisibility(View.VISIBLE);
                GiftCabAdapter adapter = new GiftCabAdapter(mContext, giftList);
                mGiftRecyclerView.setAdapter(adapter);
            }
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    private SpannableStringBuilder createColorString(String text1, String text2) {
        if (mBuilder == null) {
            mBuilder = new SpannableStringBuilder();
        } else {
            mBuilder.delete(0, mBuilder.length());
        }
        if (mColorSpan == null) {
            mColorSpan = new ForegroundColorSpan(0xff323232);
        }
        mBuilder.append(text1);
        mBuilder.append(text2);
        mBuilder.setSpan(mColorSpan, text1.length(), mBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return mBuilder;
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_USER_EVA_LIST);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_user_impress) {
            RouteUtil.forwardImpress(mToUid);
        } else if (i == R.id.btn_gift_cab) {
            GiftCabActivity.forward(mContext, mToUid);
        }

    }
}
