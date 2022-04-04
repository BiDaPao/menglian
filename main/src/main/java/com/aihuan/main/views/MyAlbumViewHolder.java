package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.main.R;
import com.aihuan.main.activity.PhotoDetailActivity;
import com.aihuan.main.adapter.MyPhotoAdapter;
import com.aihuan.main.bean.PhotoBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.one.views.AbsUserHomeViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/10.
 */

public class MyAlbumViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<PhotoBean> {

    private CommonRefreshView mRefreshView;
    private MyPhotoAdapter mAdapter;

    public MyAlbumViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_my_album;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_album_home);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<PhotoBean>() {
            @Override
            public RefreshAdapter<PhotoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MyPhotoAdapter(mContext);
                    mAdapter.setOnItemClickListener(MyAlbumViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getMyAlbum(p, callback);
            }

            @Override
            public List<PhotoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), PhotoBean.class);
            }

            @Override
            public void onRefreshSuccess(List<PhotoBean> list, int listCount) {
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<PhotoBean> loadItemList, int loadItemCount) {

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
    public void onItemClick(PhotoBean bean, int position) {
        PhotoDetailActivity.forward(mContext, bean);
    }


    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_ALBUM);
    }

    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }
}
