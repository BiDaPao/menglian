package com.aihuan.main.views;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.ViewGroup;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.UserBean;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.main.interfaces.OnAccostClick;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.one.bean.ChatLiveBean;
import com.aihuan.main.R;
import com.aihuan.main.adapter.MainHomeNearAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 附近
 */

public class MainHomeNearViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<ChatLiveBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeNearAdapter mAdapter;

    private int mSex = 0;


    public MainHomeNearViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        UserBean user = CommonAppConfig.getInstance().getUserBean();
        if (user != null) {
            if (user.getSex() == 2) {
                mSex = Constants.MAIN_SEX_MALE;
            } else if (user.getSex() == 1) {
                mSex = Constants.MAIN_SEX_FAMALE;
            } else {
                mSex = Constants.MAIN_SEX_NONE;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_near;
    }

    @Override
    public void init() {

        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 5);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<ChatLiveBean>() {
            @Override
            public RefreshAdapter<ChatLiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeNearAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeNearViewHolder.this);
                }
                mAdapter.setOnAccostClick(new OnAccostClick() {
                    @Override
                    public void onAccostClick(ChatLiveBean bean) {
                        MainHomeNearViewHolder.this.onAccostClick(bean);
                    }
                });
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getNear(p, mSex, callback);
            }

            @Override
            public List<ChatLiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ChatLiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<ChatLiveBean> list, int count) {
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<ChatLiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onItemClick(ChatLiveBean bean, int position) {
        if (bean.isAuth()) {
            forwardUserHome(bean.getUid());
        } else {
            onAccostClick(bean);
        }
    }


    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_NEAR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
