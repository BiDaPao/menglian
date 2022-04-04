package com.aihuan.video.views;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.CommonIconUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.video.R;
import com.aihuan.video.activity.AbsVideoPlayActivity;
import com.aihuan.video.activity.VideoReportActivity;
import com.aihuan.video.bean.VideoBean;
import com.aihuan.video.dialog.VideoShareDialogFragment;
import com.aihuan.video.event.VideoLikeEvent;
import com.aihuan.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/11/26.
 * 视频播放外框
 */

public class VideoPlayWrapViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ViewGroup mVideoContainer;
    private ImageView mCover;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mTitle;
    private ImageView mBtnLike;//点赞按钮
    private TextView mLikeNum;//点赞数
    //private TextView mCommentNum;//评论数
    private TextView mWatchNum;//观看数
    private TextView mShareNum;//分享数
    private ImageView mBtnFollow;//关注按钮
    private ImageView mOnLine;//在线状态
    private ImageView mBtnChat;//通话按钮
    private View mOtherGroup;
    private View mGroup;
    private VideoBean mVideoBean;
    private Drawable mFollowDrawable;//已关注
    private Drawable mUnFollowDrawable;//未关注
    private Animation mFollowAnimation;
    private boolean mCurPageShowed;//当前页面是否可见
    private ValueAnimator mLikeAnimtor;
    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    private int mLikeAnimIndex;
    private String mTag;


    public VideoPlayWrapViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_play_wrap;
    }

    @Override
    public void init() {
        mTag = this.toString();
        mVideoContainer = (ViewGroup) findViewById(R.id.video_container);
        mCover = (ImageView) findViewById(R.id.cover);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mTitle = (TextView) findViewById(R.id.title);
        mBtnLike = (ImageView) findViewById(R.id.btn_like);
        mLikeNum = (TextView) findViewById(R.id.like_num);
        //mCommentNum = (TextView) findViewById(R.id.comment_num);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        mShareNum = (TextView) findViewById(R.id.share_num);
        mBtnFollow = (ImageView) findViewById(R.id.btn_follow);
        mOnLine = (ImageView) findViewById(R.id.on_line);
        mBtnChat = (ImageView) findViewById(R.id.btn_chat);
        mOtherGroup = findViewById(R.id.other_group);
        mGroup = findViewById(R.id.group);
        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_2_1);
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_2_0);
        mAvatar.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnLike.setOnClickListener(this);
        //findViewById(R.id.btn_comment).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
        findViewById(R.id.btn_chat).setOnClickListener(this);

    }

    /**
     * 初始化点赞动画
     */
    private void initLikeAnimtor() {
        if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
            mLikeAnimtor = ValueAnimator.ofFloat(0, mLikeAnimDrawables.length);
            mLikeAnimtor.setDuration(800);
            mLikeAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    int index = (int) v;
                    if (mLikeAnimIndex != index) {
                        mLikeAnimIndex = index;
                        if (mBtnLike != null && mLikeAnimDrawables != null && index < mLikeAnimDrawables.length) {
                            mBtnLike.setImageDrawable(mLikeAnimDrawables[index]);
                        }
                    }
                }
            });
        }
    }

    /**
     * 初始化关注动画
     */
    public void initFollowAnimation() {
        mFollowAnimation = new ScaleAnimation(1, 0.3f, 1, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mFollowAnimation.setRepeatMode(Animation.REVERSE);
        mFollowAnimation.setRepeatCount(1);
        mFollowAnimation.setDuration(200);
        mFollowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (mBtnFollow != null && mVideoBean != null) {
                    if (mVideoBean.getAttent() == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }
                }
            }
        });
    }

    public void setLikeAnimDrawables(Drawable[] drawables) {
        mLikeAnimDrawables = drawables;
    }

    public void setData(VideoBean bean, Object payload) {
        if (bean == null) {
            return;
        }
        mVideoBean = bean;
        UserBean u = mVideoBean.getUserBean();
        if (payload == null) {
            if (mCover != null) {
                setCoverImage();
            }
            if (mTitle != null) {
                mTitle.setText(bean.getTitle());
            }
            if (u != null) {
                if (mAvatar != null) {
                    ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
                }
                if (mName != null) {
                    mName.setText(u.getUserNiceName());
                }
                if (mOnLine != null) {
                    mOnLine.setImageResource(CommonIconUtil.getOnLineIcon2(u.getOnLineStatus()));
                }
                if (mBtnChat != null) {
                    if (u.openVideo() && u.openVoice()) {
                        mBtnChat.setImageResource(R.mipmap.icon_video_chat_0);
                    } else {
                        if (u.openVideo()) {
                            mBtnChat.setImageResource(R.mipmap.icon_video_chat_1);
                        } else if (u.openVoice()) {
                            mBtnChat.setImageResource(R.mipmap.icon_video_chat_2);
                        }
                    }
                }
                if (mOtherGroup != null) {
                    if (CommonAppConfig.getInstance().getUid().equals(u.getId())) {
                        if (mVideoBean.getStatus() == 1) {
                            if (mOtherGroup.getVisibility() == View.VISIBLE) {
                                mOtherGroup.setVisibility(View.GONE);
                            }
                        } else {
                            if (mGroup.getVisibility() == View.VISIBLE) {
                                mGroup.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        if (mGroup.getVisibility() != View.VISIBLE) {
                            mGroup.setVisibility(View.VISIBLE);
                        }
                        if (mOtherGroup.getVisibility() != View.VISIBLE) {
                            mOtherGroup.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
        if (mBtnLike != null) {
            if (bean.getLike() == 1) {
                if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
                    mBtnLike.setImageDrawable(mLikeAnimDrawables[mLikeAnimDrawables.length - 1]);
                }
            } else {
                mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
            }
        }
        if (mLikeNum != null) {
            mLikeNum.setText(bean.getLikeNum());
        }
//        if (mCommentNum != null) {
//            mCommentNum.setText(bean.getCommentNum());
//        }
        if (mWatchNum != null) {
            mWatchNum.setText(bean.getViewNum());
        }
        if (mShareNum != null) {
            mShareNum.setText(bean.getShareNum());
        }
        if (u != null && mBtnFollow != null) {
            String toUid = u.getId();
            if (!TextUtils.isEmpty(toUid) && !toUid.equals(CommonAppConfig.getInstance().getUid())) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
                if (u.isFollowing()) {
                    mBtnFollow.setImageDrawable(mFollowDrawable);
                } else {
                    mBtnFollow.setImageDrawable(mUnFollowDrawable);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setCoverImage() {
        ImgLoader.displayDrawable(mContext, mVideoBean.getThumb(), new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (mCover != null && drawable != null) {
                    float w = drawable.getIntrinsicWidth();
                    float h = drawable.getIntrinsicHeight();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCover.getLayoutParams();
                    int targetH = 0;
                    if (w / h > 0.5625f) {//横屏  9:16=0.5625
                        targetH = (int) (ScreenDimenUtil.getInstance().getScreenWdith() / w * h);
                    } else {
                        targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (targetH != params.height) {
                        params.height = targetH;
                        mCover.requestLayout();
                    }
                    mCover.setImageDrawable(drawable);
                }
            }

            @Override
            public void onLoadFailed() {

            }
        });
    }

    public void addVideoView(View view) {
        if (mVideoContainer != null && view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                ViewGroup viewGroup = (ViewGroup) parent;
                if (viewGroup != mVideoContainer) {
                    viewGroup.removeView(view);
                    mVideoContainer.addView(view);
                }
            } else {
                mVideoContainer.addView(view);
            }
        }
    }

    public VideoBean getVideoBean() {
        return mVideoBean;
    }


    /**
     * 获取到视频首帧回调
     */
    public void onFirstFrame() {
        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
            mCover.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 滑出屏幕
     */
    public void onPageOutWindow() {
        mCurPageShowed = false;
        if (mCover != null && mCover.getVisibility() != View.VISIBLE) {
            mCover.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滑入屏幕
     */
    public void onPageInWindow() {
        if (mCover != null) {
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
            mCover.setImageDrawable(null);
            setCoverImage();
        }
    }

    /**
     * 滑动到这一页 准备开始播放
     */
    public void onPageSelected() {
        mCurPageShowed = true;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_follow) {
            clickFollow();

        }
//        else if (i == R.id.btn_comment) {
//            clickComment();
//
//        }
        else if (i == R.id.btn_share) {
            clickShare();

        } else if (i == R.id.btn_like) {
            clickLike();

        } else if (i == R.id.avatar) {
            //clickAvatar();

        } else if (i == R.id.btn_more) {
            clickMore();
        } else if (i == R.id.btn_gift) {
            ((AbsVideoPlayActivity) mContext).openGiftWindow(mVideoBean.getUid());
        } else if (i == R.id.btn_chat) {
            ((AbsVideoPlayActivity) mContext).chatClick(mVideoBean.getUserBean());
        }
    }

    /**
     * 点击更多
     */
    private void clickMore() {
        if (mVideoBean == null) {
            return;
        }
        UserBean u = mVideoBean.getUserBean();
        if (u == null) {
            return;
        }
        String uid = u.getId();
        if (TextUtils.isEmpty(uid)) {
            return;
        }

        if (CommonAppConfig.getInstance().getUid().equals(uid)) {//自己的视频
            List<Integer> list = new ArrayList<>();
            if (mVideoBean.isPrivate()) {
                list.add(R.string.video_set_public);
            }
            list.add(R.string.delete);
            DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), false, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.delete) {
                        ((AbsVideoPlayActivity) mContext).deleteVideo(mVideoBean);
                    } else if (tag == R.string.video_set_public) {
                        setVideoPublic();
                    }
                }
            });
        } else {
            DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.report}, false, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.report) {
                        VideoReportActivity.forward(mContext, mVideoBean.getId());
                    }
                }
            });
        }
    }

    /**
     * 设置公开视频
     */
    private void setVideoPublic() {
        if (mVideoBean == null) {
            return;
        }
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.video_public_tip), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                VideoHttpUtil.videoPublic(mVideoBean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mVideoBean != null) {
                                mVideoBean.setIsprivate(0);
                            }
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }


    /**
     * 点击头像
     */
    public void clickAvatar() {
//        if (mVideoBean != null) {
//            String uid = mVideoBean.getUid();
//            if (!TextUtils.isEmpty(uid) && !uid.equals(CommonAppConfig.getInstance().getUid())) {
//                RouteUtil.forwardUserHome(mVideoBean.getUid());
//            }
//        }
    }

    /**
     * 点赞,取消点赞
     */
    private void clickLike() {
        if (mVideoBean == null) {
            return;
        }
        VideoHttpUtil.setVideoLike(mTag, mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String likeNum = obj.getString("nums");
                    int like = obj.getIntValue("islike");
                    if (mVideoBean != null) {
                        mVideoBean.setLikeNum(likeNum);
                        mVideoBean.setLike(like);
                        EventBus.getDefault().post(new VideoLikeEvent(mVideoBean.getId(), like, likeNum));
                    }
                    if (mLikeNum != null) {
                        mLikeNum.setText(likeNum);
                    }
                    if (mBtnLike != null) {
                        if (like == 1) {
                            if (mLikeAnimtor == null) {
                                initLikeAnimtor();
                            }
                            mLikeAnimIndex = -1;
                            if (mLikeAnimtor != null) {
                                mLikeAnimtor.start();
                            }
                        } else {
                            mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 点击关注按钮
     */
    private void clickFollow() {
        if (mVideoBean == null) {
            return;
        }
        final UserBean u = mVideoBean.getUserBean();
        if (u == null) {
            return;
        }
        CommonHttpUtil.setAttention(mTag, u.getId(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer attent) {
                mVideoBean.setAttent(attent);
                if (mCurPageShowed) {
                    if (mFollowAnimation == null) {
                        initFollowAnimation();
                    }
                    mBtnFollow.startAnimation(mFollowAnimation);
                } else {
                    if (attent == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }
                }
            }
        });
    }

    /**
     * 点击评论按钮
     */
    private void clickComment() {
        ((AbsVideoPlayActivity) mContext).openCommentWindow(mVideoBean.getId(), mVideoBean.getUid());
    }

    /**
     * 点击分享按钮
     */
    private void clickShare() {
        if (mVideoBean == null) {
            return;
        }
        VideoShareDialogFragment fragment = new VideoShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.VIDEO_BEAN, mVideoBean);
        fragment.setArguments(bundle);
        fragment.show(((AbsVideoPlayActivity) mContext).getSupportFragmentManager(), "VideoShareDialogFragment");
    }

    public void release() {
        VideoHttpUtil.cancel(mTag);
        if (mLikeAnimtor != null) {
            mLikeAnimtor.cancel();
        }
        if (mBtnFollow != null && mFollowAnimation != null) {
            mBtnFollow.clearAnimation();
        }
    }


}
