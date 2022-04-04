package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.SubcribeAudAdapter;
import com.aihuan.main.bean.SubcribeAudBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/23.
 * 我预约的
 */

public class SubcribeAudViewHolder extends AbsMainViewHolder implements OnItemClickListener<SubcribeAudBean> {

    private CommonRefreshView mRefreshView;
    private SubcribeAudAdapter mAdapter;

    public SubcribeAudViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_subcribe_aud;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_subcribe_1);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SubcribeAudBean>() {
            @Override
            public RefreshAdapter<SubcribeAudBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SubcribeAudAdapter(mContext);
                    mAdapter.setOnItemClickListener(SubcribeAudViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                OneHttpUtil.getMySubscribeList(p, callback);
            }

            @Override
            public List<SubcribeAudBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SubcribeAudBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SubcribeAudBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SubcribeAudBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_MY_SUBCRIBE_LIST);
        super.onDestroy();
    }

    @Override
    public void onItemClick(SubcribeAudBean bean, int position) {
        RouteUtil.forwardUserHome(bean.getUid());
    }
}
