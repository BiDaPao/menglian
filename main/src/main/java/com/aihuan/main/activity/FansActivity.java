package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.main.R;
import com.aihuan.main.adapter.FansAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 我的粉丝 TA的粉丝
 */

public class FansActivity extends AbsActivity implements FansAdapter.ActionListener {

    public static void forward(Context context, String toUid) {
        Intent intent = new Intent(context, FansActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private FansAdapter mAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fans;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.fans));
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans_2);
        }
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new FansAdapter(mContext);
                    mAdapter.setActionListener(FansActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFansList(mToUid, p, callback);
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
        mRefreshView.initData();
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_FANS_LIST);
        super.onDestroy();
    }

    @Override
    public void onItemClick(UserBean u) {
        RouteUtil.forwardUserHome(u.getId());
    }

    @Override
    public void onMsgClick(UserBean u) {
        ChatRoomActivity.forward(mContext, u, u.isFollowing(), u.isBlacking(), u.getAuth() == 1, false);
    }
}
