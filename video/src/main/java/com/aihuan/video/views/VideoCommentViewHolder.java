package com.aihuan.video.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.MyLinearLayout3;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.video.R;
import com.aihuan.video.activity.AbsVideoCommentActivity;
import com.aihuan.video.adapter.VideoCommentAdapter;
import com.aihuan.video.bean.VideoCommentBean;
import com.aihuan.video.event.VideoCommentEvent;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/3.
 * 视频评论相关
 */

public class VideoCommentViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemClickListener<VideoCommentBean>, VideoCommentAdapter.ActionListener {

    private View mRoot;
    private MyLinearLayout3 mBottom;
    private CommonRefreshView mRefreshView;
    private TextView mCommentNum;
    private VideoCommentAdapter mVideoCommentAdapter;
    private String mVideoId;
    private String mVideoUid;
    private String mCommentString;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;
    private boolean mAnimating;
    private boolean mVideoIdChanged;//视频id是否发生变化了
    private boolean mShowed;
    public VideoCommentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_comment;
    }

    @Override
    public void init() {
        mRoot = findViewById(R.id.root);
        mBottom = (MyLinearLayout3) findViewById(R.id.bottom);
        int height = mBottom.getHeight2();
        mBottom.setTranslationY(height);
        mShowAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0);
        mHideAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", height);
        mShowAnimator.setDuration(200);
        mHideAnimator.setDuration(200);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnimator.setInterpolator(interpolator);
        mHideAnimator.setInterpolator(interpolator);
        AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                if (animation == mHideAnimator) {
                    if (mRoot != null && mRoot.getVisibility() == View.VISIBLE) {
                        mRoot.setVisibility(View.INVISIBLE);
                    }
                } else if (animation == mShowAnimator) {
                    if (mVideoIdChanged) {
                        mVideoIdChanged = false;
                        if (mRefreshView != null) {
                            mRefreshView.initData();
                        }
                    }
                }
            }
        };
        mShowAnimator.addListener(animatorListener);
        mHideAnimator.addListener(animatorListener);

        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.input).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mCommentString = WordUtil.getString(R.string.video_comment);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_comment);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    L.e("onLayoutChildren------>" + e.getMessage());
                }
            }
        });

        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoCommentBean>() {
            @Override
            public RefreshAdapter<VideoCommentBean> getAdapter() {
                if (mVideoCommentAdapter == null) {
                    mVideoCommentAdapter = new VideoCommentAdapter(mContext);
                    mVideoCommentAdapter.setOnItemClickListener(VideoCommentViewHolder.this);
                    mVideoCommentAdapter.setActionListener(VideoCommentViewHolder.this);
                }
                return mVideoCommentAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mVideoId)) {
                    VideoHttpUtil.getVideoCommentList(mVideoId, p, callback);
                }
            }

            @Override
            public List<VideoCommentBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                String commentNum = obj.getString("comments");
                EventBus.getDefault().post(new VideoCommentEvent(mVideoId, commentNum));
                if (mCommentNum != null) {
                    mCommentNum.setText(commentNum + " " + mCommentString);
                }
                List<VideoCommentBean> list = JSON.parseArray(obj.getString("commentlist"), VideoCommentBean.class);
                for (VideoCommentBean bean : list) {
                    if (bean != null) {
                        bean.setParentNode(true);
                    }
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<VideoCommentBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoCommentBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    public void setVideoInfo(String videoId, String videoUid) {
        if (!TextUtils.isEmpty(videoId) && !TextUtils.isEmpty(videoUid)) {
            if (!TextUtils.isEmpty(mVideoId) && !mVideoId.equals(videoId)) {
                if (mVideoCommentAdapter != null) {
                    mVideoCommentAdapter.clearData();
                }
            }
            if (!videoId.equals(mVideoId)) {
                mVideoIdChanged = true;
            }
            mVideoId = videoId;
            mVideoUid = videoUid;
        }

    }

    public void showBottom() {
        if (mAnimating) {
            return;
        }
        if (mRoot != null && mRoot.getVisibility() != View.VISIBLE) {
            mRoot.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
        mShowed = true;
    }

    public void hideBottom() {
        if (mAnimating) {
            return;
        }
        if (mHideAnimator != null) {
            mHideAnimator.start();
        }
        mShowed = false;
    }


    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root || i == R.id.btn_close) {
            hideBottom();
        } else if (i == R.id.input) {
            if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
                ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, mVideoUid, null);
            }
        } else if (i == R.id.btn_face) {
            if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
                ((AbsVideoCommentActivity) mContext).openCommentInputWindow(true, mVideoId, mVideoUid, null);
            }
        }
    }


    public void release() {
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mShowAnimator = null;
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        mHideAnimator = null;
        VideoHttpUtil.cancel(VideoHttpConsts.GET_VIDEO_COMMENT_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.SET_COMMENT_LIKE);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_COMMENT_REPLY);
    }

    @Override
    public void onItemClick(VideoCommentBean bean, int position) {
        if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(mVideoUid)) {
            ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, mVideoUid, bean);
        }
    }

    @Override
    public void onExpandClicked(final VideoCommentBean commentBean) {
        final VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        VideoHttpUtil.getCommentReply(parentNodeBean.getId(), parentNodeBean.getChildPage(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<VideoCommentBean> list = JSON.parseArray(Arrays.toString(info), VideoCommentBean.class);
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    if (parentNodeBean.getChildPage() == 1) {
                        if (list.size() > 1) {
                            list = list.subList(1, list.size());
                        }
                    }
                    for (VideoCommentBean bean : list) {
                        bean.setParentNodeBean(parentNodeBean);
                    }
                    List<VideoCommentBean> childList = parentNodeBean.getChildList();
                    if (childList != null) {
                        childList.addAll(list);
                        if (childList.size() < parentNodeBean.getReplyNum()) {
                            parentNodeBean.setChildPage(parentNodeBean.getChildPage() + 1);
                        }
                        if (mVideoCommentAdapter != null) {
                            mVideoCommentAdapter.insertReplyList(commentBean, list.size());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCollapsedClicked(VideoCommentBean commentBean) {
        VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        List<VideoCommentBean> childList = parentNodeBean.getChildList();
        VideoCommentBean node0 = childList.get(0);
        int orignSize = childList.size();
        parentNodeBean.removeChild();
        parentNodeBean.setChildPage(1);
        if (mVideoCommentAdapter != null) {
            mVideoCommentAdapter.removeReplyList(node0, orignSize - childList.size());
        }
    }

    /**
     * 刷新评论
     */
    public void refreshComment() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onVoicePlay(VideoCommentBean commentBean) {
        if (mContext != null && mContext instanceof AbsVideoCommentActivity) {
            ((AbsVideoCommentActivity) mContext).playCommentVoice(commentBean);
        }
    }

    @Override
    public void onVoiceStop(VideoCommentBean commentBean) {
        if (mContext != null && mContext instanceof AbsVideoCommentActivity) {
            ((AbsVideoCommentActivity) mContext).stopCommentVoice(commentBean);
        }
    }

    /**
     * 停止语音播放的动画
     */
    public void stopVoiceAnim(){
        if(mVideoCommentAdapter!=null){
            mVideoCommentAdapter.stopVoiceAnim();
        }
    }

}
