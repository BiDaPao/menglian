package com.aihuan.main.views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.event.MatchEvent;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.dialog.MainFilterDialogFragment;
import com.aihuan.main.presenter.OnCheckBack;
import com.aihuan.main.presenter.UserStatusP;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainHomeParentViewHolder implements View.OnClickListener {

    private MainHomeRecommendViewHolder mRecommendViewHolder;
    private MainHomeNearViewHolder mNearViewHolder;
    private MainHomeNewViewHolder mHomeNewestViewHolder;
    private MainHomeFollowViewHolder mFollowViewHolder;
    private MainHomeOnlineViewHolder mOnlineViewHolder;
//    private View mBtnFilter;
    private int mCurrentPagePosition = 0;

    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home;
    }

    @Override
    public void init() {
        super.init();
//        mBtnFilter = findViewById(R.id.btn_filter);
//        mBtnFilter.setOnClickListener(this);
        View statusView = findViewById(R.id.space_status_bar);
        ScreenDimenUtil.getInstance().setStatusBar(statusView);
        //视频速配
        findViewById(R.id.video_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MatchEvent(1));
            }
        });
        //语音速配
        findViewById(R.id.voice_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MatchEvent(2));
            }
        });
    }

    //首页tab选项卡
    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mRecommendViewHolder = new MainHomeRecommendViewHolder(mContext, parent);
                    vh = mRecommendViewHolder;
                } else if (position == 1) {
                    mNearViewHolder = new MainHomeNearViewHolder(mContext, parent);
                    vh = mNearViewHolder;
                } else if (position == 2) {
                    mHomeNewestViewHolder = new MainHomeNewViewHolder(mContext, parent);
                    vh = mHomeNewestViewHolder;
                } else if (position == 3) {
                    mFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    vh = mFollowViewHolder;
                } else if (position == 4) {
                    mOnlineViewHolder = new MainHomeOnlineViewHolder(mContext, parent);
                    vh = mOnlineViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
//        if (mBtnFilter != null) {
//            if (position == 0) {
//                if (mBtnFilter.getVisibility() != View.VISIBLE) {
//                    mBtnFilter.setVisibility(View.VISIBLE);
//                }
//                mCurrentPagePosition = 0;
//            } else if (position == 4) {
//                if (mBtnFilter.getVisibility() != View.VISIBLE) {
//                    mBtnFilter.setVisibility(View.VISIBLE);
//                }
//                mCurrentPagePosition = 4;
//            } else {
//                if (mBtnFilter.getVisibility() == View.VISIBLE) {
//                    mBtnFilter.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
        new UserStatusP().checkStatus(mContext, new OnCheckBack() {
            @Override
            public void onCheckNormal() {

            }
        });
    }

    @Override
    protected int getPageCount() {
        return 5;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.recommend),
                WordUtil.getString(R.string.near),
                WordUtil.getString(R.string.newest),
                WordUtil.getString(R.string.follow),
                WordUtil.getString(R.string.online)
        };
    }

    @Override
    public void onResume() {
        if (isShowed() && mPaused ) {
            if (mCurrentPagePosition!=0) {
                loadData();
            }
        }
        mPaused = false;
    }

    @Override
    public void onClick(View v) {
        if (mCurrentPagePosition == 0) {
            if (mRecommendViewHolder != null) {
                mRecommendViewHolder.onFilterClick(true);
            }
        } else if (mCurrentPagePosition == 4) {
            if (mOnlineViewHolder != null) {
                mOnlineViewHolder.onFilterClick(false);
            }
        }

    }


}
