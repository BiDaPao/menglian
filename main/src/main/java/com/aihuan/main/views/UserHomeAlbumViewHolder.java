package com.aihuan.main.views;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.PhotoDetailActivity;
import com.aihuan.main.adapter.UserHomeAlbumAdapter;
import com.aihuan.main.bean.PhotoBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.one.views.AbsUserHomeViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/8.
 */

public class UserHomeAlbumViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<PhotoBean> {

    private String mToUid;
    private CommonRefreshView mRefreshView;
    private UserHomeAlbumAdapter mAdapter;
    private String mCoinName;

    public UserHomeAlbumViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_alumb;
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
                    mAdapter = new UserHomeAlbumAdapter(mContext);
                    mAdapter.setOnItemClickListener(UserHomeAlbumViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getHomePhoto(mToUid, p, callback);
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
        if (isFirstLoadData()) {
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

    @Override
    public void onItemClick(final PhotoBean bean, int position) {
        if (bean.isCanSee()) {
            watchPhoto(bean);
        } else {
            if (TextUtils.isEmpty(mCoinName)) {
                mCoinName = CommonAppConfig.getInstance().getCoinName();
            }
            new DialogUitl.Builder(mContext)
                    .setContent(String.format(WordUtil.getString(R.string.photo_privte_tip), bean.getCoin(), mCoinName))
                    .setCancelable(true)
                    .setBackgroundDimEnabled(true)
                    .setCancelString(WordUtil.getString(R.string.open_vip))
                    .setConfrimString(WordUtil.getString(R.string.video_watch_charge))
                    .setClickCallback(new DialogUitl.SimpleCallback2() {
                        @Override
                        public void onCancelClick() {
                            RouteUtil.forwardVip();
                        }

                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            buyPhoto(bean);
                        }
                    })
                    .build()
                    .show();
        }
    }


    private void buyPhoto(final PhotoBean bean) {
        MainHttpUtil.buyPhoto(bean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mAdapter != null) {
                        mAdapter.updateCanSee(bean.getId(), 1);
                    }
                    watchPhoto(bean);
                } else if (code == 1005) {
                    ToastUtil.show(R.string.chat_coin_not_enough);
                    RouteUtil.forwardMyCoin();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    private void watchPhoto(final PhotoBean bean) {
        PhotoDetailActivity.forward(mContext, bean);
        MainHttpUtil.photoAddView(bean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mAdapter != null) {
                        mAdapter.updateViewNum(bean.getId(), JSON.parseObject(info[0]).getString("nums"));
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_HOME_PHOTO);
        MainHttpUtil.cancel(MainHttpConsts.BUY_PHOTO);
        MainHttpUtil.cancel(MainHttpConsts.PHOTO_ADD_VIEW);
        super.onDestroy();
    }
}
