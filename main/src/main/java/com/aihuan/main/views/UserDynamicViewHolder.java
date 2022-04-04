package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.activity.DynamicDetailsActivity;
import com.aihuan.dynamic.adapter.DynamicListAdapter;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.event.DynamicCommentEvent;
import com.aihuan.dynamic.event.DynamicDelEvent;
import com.aihuan.dynamic.event.DynamicLikeEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.dynamic.inter.VoicePlayCallBack;
import com.aihuan.main.R;
import com.aihuan.one.views.AbsUserHomeViewHolder;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by debug on 2019/7/24.
 */

public class UserDynamicViewHolder extends AbsUserHomeViewHolder implements OnItemClickListener<DynamicBean>,VoicePlayCallBack {

    private CommonRefreshView mCommonRefreshView;
    private DynamicListAdapter mAdapter;
    private String mToUid;
    private boolean mIsFirstLoad;
    private int mTopH;
    private int mBotH;

    public UserDynamicViewHolder(Context context, ViewGroup parentView, String toUid,int topH,int botH) {
        super(context, parentView, toUid,topH,botH);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mToUid = (String) args[0];
        mTopH = (int) args[1];
        mBotH = (int) args[2];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_dynamic;
    }

    @Override
    public void init() {
        mCommonRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())){
            mCommonRefreshView.setEmptyLayoutId(R.layout.view_no_data_dynamic3);
        }else {
            mCommonRefreshView.setEmptyLayoutId(R.layout.view_no_data_dynamic2);
        }
        mCommonRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCommonRefreshView.setDataHelper(new CommonRefreshView.DataHelper<DynamicBean>() {
            @Override
            public RefreshAdapter<DynamicBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new DynamicListAdapter(mContext,mToUid,mTopH,mBotH);
                    mAdapter.setOnItemClickListener(UserDynamicViewHolder.this);
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
                    ((AbsDynamicActivity)mContext).setVoicePlayCallBack(UserDynamicViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                    DynamicHttpUtil.getHomeDynamicList(mToUid,p, callback);

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
                        if (mAdapter != null) {
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
        if (isFirstLoadData()){
            if (mCommonRefreshView != null) {
                mCommonRefreshView.initData();
            }
        }
    }

    @Override
    public void setShowed(boolean showed) {
        super.setShowed(showed);
        if (showed) {
            if (mAdapter != null) {
                mAdapter.resumePlay();
            }
        } else {
            if (mAdapter != null) {
                mAdapter.pausePlay();
            }
            if (mAdapter!=null){
                mAdapter.stopVoiceAnim();
            }
        }
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
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_GET_USERHOME);
        super.onDestroy();
    }



    @Override
    public void onItemClick(DynamicBean bean, int position) {
        DynamicDetailsActivity.forward(mContext, bean,true);
    }

    @Override
    public void onPlayStart() {
        if (mAdapter!=null){
            mAdapter.setVideoViewMute(true);
        }
    }

    @Override
    public void onPlayResume() {
        if (mAdapter!=null){
            mAdapter.setVideoViewMute(true);
        }
    }

    @Override
    public void onPlayPause() {
        if (mAdapter!=null){
            mAdapter.setVideoViewMute(false);
        }
    }

    @Override
    public void onPlayEnd() {
        if (mAdapter!=null){
            mAdapter.setVideoViewMute(false);
        }
    }

    @Override
    public void onPlayAutoEnd() {
        if (mAdapter!=null){
            mAdapter.setVideoViewMute(false);
        }
        if (mAdapter!=null){
            mAdapter.stopVoiceAnim();
        }
    }


}
