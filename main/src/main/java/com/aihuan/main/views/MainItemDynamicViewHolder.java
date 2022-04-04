package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.ViewGroup;
import android.widget.TextView;

import com.aihuan.common.utils.ToastUtil;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.activity.DynamicDetailsActivity;
import com.aihuan.dynamic.adapter.DynamicListAdapter;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.event.DynamicCommentEvent;
import com.aihuan.dynamic.event.DynamicDelEvent;
import com.aihuan.dynamic.event.DynamicLikeEvent;
import com.aihuan.dynamic.event.DynamicTabChangeEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.dynamic.inter.VoicePlayCallBack;
import com.aihuan.main.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by debug on 2019/7/24.
 * 动态 最新 关注
 */

public class MainItemDynamicViewHolder extends AbsMainViewHolder implements OnItemClickListener<DynamicBean>, VoicePlayCallBack {
    public static final int NEW = 0;
    public static final int ATTEN = 1;
    private CommonRefreshView mCommonRefreshView;
    private DynamicListAdapter mAdapter;
    private int mType;
    private boolean mIsShowing;


    public MainItemDynamicViewHolder(Context context, ViewGroup parentView, int type) {
        super(context, parentView, type);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mType = (int) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_item_dynamic;
    }

    @Override
    public void init() {
        mCommonRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mCommonRefreshView.setEmptyLayoutId(R.layout.view_no_data_dynamic);
        mCommonRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCommonRefreshView.setDataHelper(new CommonRefreshView.DataHelper<DynamicBean>() {
            @Override
            public RefreshAdapter<DynamicBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new DynamicListAdapter(mContext, "", DpUtil.dp2px(70), DpUtil.dp2px(50));
                    mAdapter.setOnItemClickListener(MainItemDynamicViewHolder.this);
                    mAdapter.setActionListener(new DynamicListAdapter.VoiceActionListener() {
                        @Override
                        public void onVoicePlay(DynamicBean dynamicBean, TextView time) {
                            ((AbsDynamicActivity) mContext).setVoiceInfo(dynamicBean.getLength(), time);
                            ((AbsDynamicActivity) mContext).playVoice(dynamicBean.getVoice());
                        }

                        @Override
                        public void onVoicePause(DynamicBean dynamicBean) {
                            ((AbsDynamicActivity) mContext).pauseVoice();
                        }

                        @Override
                        public void onVoiceResume(DynamicBean dynamicBean) {
                            ((AbsDynamicActivity) mContext).resumeVoice(dynamicBean.getVoice());
                        }

                        @Override
                        public void onVoiceStop(DynamicBean dynamicBean) {
                            ((AbsDynamicActivity) mContext).stopVoice();
                        }
                    });
                    // ((AbsDynamicActivity) mContext).setVoicePlayCallBack(MainItemDynamicViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mType == NEW) {
                    DynamicHttpUtil.getDynamicList(p, callback);
                } else {
                    DynamicHttpUtil.getAttentionDynamic(p, callback);
                }
            }

            @Override
            public List<DynamicBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), DynamicBean.class);
            }

            @Override
            public void onRefreshSuccess(List<DynamicBean> list, int count) {
                mCommonRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null &&mIsShowing) {
                            mAdapter.scrollToTop();
                            mAdapter.play();
                        }
                    }
                }, 500);
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<DynamicBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
    }


    @Override
    public void loadData() {
        super.loadData();
        if (isFirstLoadData()) {
            if (mCommonRefreshView != null) {
                mCommonRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCommonRefreshView.initData();
                    }
                }, 200);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicTabChange(DynamicTabChangeEvent e) {
        mIsShowing=e.getType() == mType;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicLike(DynamicLikeEvent e) {
        if (mAdapter != null && e != null) {
            mAdapter.notifyLike(e.getDynamicId(), e.getIsLike(), e.getLikes());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicComment(DynamicCommentEvent e) {
        if (mAdapter != null && e != null) {
            mAdapter.notifyComment(e.getDynamicId(), e.getComments());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicDel(DynamicDelEvent e) {
        if (mAdapter != null && e != null) {
            mAdapter.deleteDynamic(e.getDynamicId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.back(false);
            mAdapter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.back(true);
            mAdapter.onPause();
        }
        if (mAdapter != null) {
            mAdapter.stopVoiceAnim();
        }
    }


    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.release();
        }
        EventBus.getDefault().unregister(this);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_GETNEW);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_GETATTEN);
        super.onDestroy();
    }

    @Override
    public void setShowed(boolean showed) {
        super.setShowed(showed);
        if (showed) {
            ((AbsDynamicActivity) mContext).setVoicePlayCallBack(MainItemDynamicViewHolder.this);
            if (mAdapter != null) {
                mAdapter.resumePlay();
            }
        } else {
            if (mAdapter != null) {
                mAdapter.pausePlay();
            }
            if (mAdapter != null) {
                mAdapter.stopVoiceAnim();
            }
        }
    }

    @Override
    public void onItemClick(DynamicBean bean, int position) {
//        if (!bean.getUserinfo().isAlAuth()){
//            ToastUtil.show(com.aihuan.im.R.string.user_is_not_auth);
//            return;
//        }
        DynamicDetailsActivity.forward(mContext, bean);
    }

    @Override
    public void onPlayStart() {
        if (mAdapter != null) {
            mAdapter.setVideoViewMute(true);
        }
    }

    @Override
    public void onPlayResume() {
        if (mAdapter != null) {
            mAdapter.setVideoViewMute(true);
        }
    }

    @Override
    public void onPlayPause() {
        if (mAdapter != null) {
            mAdapter.setVideoViewMute(false);
        }
    }

    @Override
    public void onPlayEnd() {
        if (mAdapter != null) {
            mAdapter.setVideoViewMute(false);
        }
    }

    @Override
    public void onPlayAutoEnd() {
        if (mAdapter != null) {
            mAdapter.setVideoViewMute(false);
        }
        if (mAdapter != null) {
            mAdapter.stopVoiceAnim();
        }
    }
}
