package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.event.BlackEvent;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.BlackAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/14.
 * 黑名单
 */

public class BlackListActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, BlackListActivity.class));
    }

    private CommonRefreshView mRefreshView;
    private BlackAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_black_list;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.black_list));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_black_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new BlackAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                CommonHttpUtil.getBlackList(callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
        mRefreshView.initData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlackEvent(BlackEvent e) {
        if (mAdapter != null && e.getIsBlack() == 0) {
            mAdapter.removeItem(e.getToUid());
            if (mAdapter.getListSize() == 0) {
                mRefreshView.showEmpty();
            }
        }
    }


    @Override
    protected void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BLACK_LIST);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
