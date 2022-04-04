package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.main.R;
import com.aihuan.main.adapter.VideoMyAdapter;
import com.aihuan.one.views.AbsUserHomeViewHolder;
import com.aihuan.video.activity.VideoPlayActivity;
import com.aihuan.video.bean.VideoBean;
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
 * 我的视频列表
 */

public class MyVideoViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private VideoMyAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private ActionListener mActionListener;
    private String mKey;
    private boolean mPaused;

    public MyVideoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_my_video;
    }

    @Override
    public void init() {
        mKey = Constants.VIDEO_USER + this.hashCode();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_video_home);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new VideoMyAdapter(mContext);
                    mAdapter.setOnItemClickListener(MyVideoViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getMyVideo(p, callback);
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
                VideoHttpUtil.getMyVideo(p, callback);
            }
        };
        EventBus.getDefault().register(MyVideoViewHolder.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPaused) {
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
        mPaused = false;
    }

    @Override
    public void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            mRefreshView.initData();
        }
    }

    public void release() {
        mVideoScrollDataHelper = null;
        mActionListener = null;
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

    @Override
    public void onItemClick(VideoBean bean, int position) {
//        int page = 1;
//        if (mRefreshView != null) {
//            page = mRefreshView.getPageCount();
//        }
//        VideoStorge.getInstance().putDataHelper(mKey, mVideoScrollDataHelper);
        //VideoPlayActivity.forward(mContext, position, mKey, page);


        if (bean == null) {
            return;
        }
        UserBean u = bean.getUserBean();
        if (u == null) {
            return;
        }
        u.setOnLineStatus(Constants.LINE_TYPE_ON);
        VideoPlayActivity.forwardSingle(mContext, bean);

    }


    public interface ActionListener {
        void onVideoDelete(int deleteCount);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
