package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.MyWallAdapter;
import com.aihuan.main.bean.WallBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/13.
 * 背景墙
 */

public class MyWallActivity extends AbsActivity implements MyWallAdapter.ActionListener {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyWallActivity.class));
    }

    private CommonRefreshView mRefreshView;
    private MyWallAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_wall;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.wall));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<WallBean>() {
            @Override
            public RefreshAdapter<WallBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MyWallAdapter(mContext);
                    mAdapter.setActionListener(MyWallActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getMyWall(callback);
            }

            @Override
            public List<WallBean> processData(String[] info) {
                List<WallBean> list = JSON.parseArray(Arrays.toString(info), WallBean.class);
                if (list.size() < Constants.AUTH_IMAGE_MAX_SIZE) {
                    WallBean bean = new WallBean();
                    bean.setAdd(true);
                    list.add(bean);
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<WallBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<WallBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_WALL);
        super.onDestroy();
    }


    @Override
    public void onItemClick(WallBean bean) {
        if (mAdapter == null) {
            return;
        }
        MyWallDetailActivity.forward(mContext, bean, mAdapter.getImageCount(), bean.isVideo());
    }

    @Override
    public void onAddClick() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.wall_add_img, R.string.wall_add_video}, true, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.wall_add_img) {
                    MyWallChooseImageActivity.forward(mContext, 0, null);
                } else if (tag == R.string.wall_add_video) {
                    if (mAdapter != null && mAdapter.hasVideo()) {
                        ToastUtil.show(R.string.wall_has_video);
                    } else {
                        MyWallChooseVideoActivity.forward(mContext, 0, null);
                    }
                }
            }
        });
    }


}
