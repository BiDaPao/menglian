package com.aihuan.main.views;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.ViewGroup;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.UserBean;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.main.interfaces.OnAccostClick;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.main.R;
import com.aihuan.main.adapter.MainHomeFollowAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.one.bean.ChatLiveBean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 关注
 */

public class MainHomeFollowViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<ChatLiveBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;

    private int mSex = 0;


    public MainHomeFollowViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        UserBean user = CommonAppConfig.getInstance().getUserBean();
        if (user != null) {
            mSex = user.getSex();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_follow;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_follow);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<ChatLiveBean>() {
            @Override
            public RefreshAdapter<ChatLiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeFollowViewHolder.this);
                }
                mAdapter.setOnAccostClick(new OnAccostClick() {
                    @Override
                    public void onAccostClick(ChatLiveBean bean) {
                        MainHomeFollowViewHolder.this.onAccostClick(bean);
                    }
                });
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollow(p, mSex, callback);
            }

            @Override
            public List<ChatLiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ChatLiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<ChatLiveBean> adapterItemList, int allItemCount) {
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
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
