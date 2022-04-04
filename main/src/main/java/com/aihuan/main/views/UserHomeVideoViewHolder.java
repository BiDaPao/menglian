package com.aihuan.main.views;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.aihuan.main.activity.UserHomeNewActivity;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.VideoHomeAdapter;
import com.aihuan.one.views.AbsUserHomeViewHolder;
import com.aihuan.video.activity.VideoPlayActivity;
import com.aihuan.video.bean.VideoBean;
import com.aihuan.video.event.VideoChargeEvent;
import com.aihuan.video.event.VideoDeleteEvent;
import com.aihuan.video.event.VideoScrollPageEvent;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;
import com.aihuan.video.interfaces.VideoScrollDataHelper;
import com.aihuan.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/14.
 * 用户个人中心发布的视频列表
 */

public class UserHomeVideoViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private VideoHomeAdapter mAdapter;
    private String mToUid;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private ActionListener mActionListener;
    private String mKey;
    private String mCoinName;

    public UserHomeVideoViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_video;
    }

    @Override
    public void init() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mKey = Constants.VIDEO_USER + this.hashCode();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_home);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_home_2);
        }
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new VideoHomeAdapter(mContext);
                    mAdapter.setOnItemClickListener(UserHomeVideoViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getHomeVideo(mToUid, p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefreshSuccess(List<VideoBean> list, int listCount) {
                VideoStorge.getInstance().put(mKey, list);
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

        mVideoScrollDataHelper = new VideoScrollDataHelper() {

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getHomeVideo(mToUid, p, callback);
            }
        };
        EventBus.getDefault().register(UserHomeVideoViewHolder.this);
    }


    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

    public void release() {
        mVideoScrollDataHelper = null;
        mActionListener = null;
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_MY_VIDEO);
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (!TextUtils.isEmpty(mKey) && mKey.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPageCount(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showEmpty();
            }
        }
        if (mActionListener != null) {
            mActionListener.onVideoDelete(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoChargeEvent(VideoChargeEvent e) {
        if (mAdapter != null) {
            mAdapter.onVideoChargeSuccess(e.getVideoId(), e.getHref());
        }
    }


    private void forwardVideo(VideoBean bean) {
        UserBean u = ((UserHomeNewActivity) mContext).getUserBean();
        if (u == null) {
            return;
        }
        bean.setUserBean(u);
        VideoPlayActivity.forwardSingle(mContext, bean);
    }

    @Override
    public void onItemClick(final VideoBean bean, int position) {
//        int page = 1;
//        if (mRefreshView != null) {
//            page = mRefreshView.getPageCount();
//        }
//        VideoStorge.getInstance().putDataHelper(mKey, mVideoScrollDataHelper);
        //VideoPlayActivity.forward(mContext, position, mKey, page);


        if (bean.isCanSee()) {
            forwardVideo(bean);
        } else {
            new DialogUitl.Builder(mContext)
                    .setContent(String.format(WordUtil.getString(R.string.video_privte_tip), bean.getCoin(), mCoinName))
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
                            videoCharge(bean);
                        }
                    })
                    .build()
                    .show();
        }
    }


    public interface ActionListener {
        void onVideoDelete(int deleteCount);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    /**
     * 购买视频
     */
    private void videoCharge(final VideoBean bean) {
        VideoHttpUtil.videoCharge(bean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        String href = JSON.parseObject(info[0]).getString("href");
                        //EventBus.getDefault().post(new VideoChargeEvent(bean.getId(), href));
                        bean.setHref(href);
                        bean.setCansee(1);
                    }
                    forwardVideo(bean);
                } else if (code == 1005) {
                    ToastUtil.show(R.string.chat_coin_not_enough);
                    RouteUtil.forwardMyCoin();
                } else {
                    ToastUtil.show(msg);
                }

            }
        });
    }


    @Override
    public void onDestroy() {
        VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_CHARGE);
        release();
        super.onDestroy();
    }
}
