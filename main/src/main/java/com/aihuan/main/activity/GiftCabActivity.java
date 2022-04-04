package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.GiftCabAdapter;
import com.aihuan.main.bean.GiftCabBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class GiftCabActivity extends AbsActivity {

    public static void forward(Context context, String toUid) {
        Intent intent = new Intent(context, GiftCabActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        context.startActivity(intent);
    }

    private TextView mAllCount;//总数量
    private TextView mAllValue;//总价值
    private CommonRefreshView mRefreshView;
    private GiftCabAdapter mAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gift_cab;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.gift_cab));
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        mAllCount = findViewById(R.id.all_count);
        mAllValue = findViewById(R.id.all_value);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_gift_cab);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GiftCabBean>() {
            @Override
            public RefreshAdapter<GiftCabBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new GiftCabAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getGiftCabList(mToUid, callback);
            }

            @Override
            public List<GiftCabBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (mAllCount != null) {
                    mAllCount.setText(String.format(WordUtil.getString(R.string.gift_all_count), obj.getString("nums")));
                }
                if (mAllValue != null) {
                    mAllValue.setText(obj.getString("total"));
                }
                return JSON.parseArray(obj.getString("list"), GiftCabBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GiftCabBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GiftCabBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_GIFT_CAB_LIST);
        super.onDestroy();
    }
}
