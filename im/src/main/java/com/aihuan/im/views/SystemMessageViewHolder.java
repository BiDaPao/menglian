package com.aihuan.im.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.im.R;
import com.aihuan.im.adapter.SystemMessageAdapter;
import com.aihuan.im.bean.SystemMessageBean;
import com.aihuan.im.http.ImHttpConsts;
import com.aihuan.im.http.ImHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/11/28.
 */

public class SystemMessageViewHolder extends AbsViewHolder implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private SystemMessageAdapter mAdapter;
    private ActionListener mActionListener;


    public SystemMessageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_sys_msg;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_back).setOnClickListener(this);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_sys_msg);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SystemMessageBean>() {
            @Override
            public RefreshAdapter<SystemMessageBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SystemMessageAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                ImHttpUtil.getSystemMessageList(p, callback);
            }

            @Override
            public List<SystemMessageBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SystemMessageBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SystemMessageBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SystemMessageBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    public void release() {
        mActionListener=null;
        ImHttpUtil.cancel(ImHttpConsts.GET_SYSTEM_MESSAGE_LIST);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            if (mActionListener != null) {
                mActionListener.onBackClick();
            }

        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener{
        void onBackClick();
    }
}
