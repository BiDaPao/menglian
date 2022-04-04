package com.aihuan.dynamic.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aihuan.common.utils.ToastUtil;
import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.DrawableTextView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.CommonIconUtil;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.custorm.DynamicVideoViewContainer;
import com.aihuan.dynamic.custorm.VideoPlayView;


import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by debug on 2019/7/24.
 * 动态列表
 */

public class DynamicListAdapter extends RefreshAdapter<DynamicBean> {
    private static final String TAG = "DynamicListAdapter";
    private static final int IMG = 1;
    private static final int VIDEO = 2;
    private static final int VOICE = 3;
    private Drawable mLikeDrawable;
    private Drawable mUnLikeDrawable;
    private VideoPlayView mPlayView;
    private int mHalfPlayViewHeight;
    private int mHalfVoicePlayViewHeight;
    private int mTopHeight;
    private int mBotHeight;
    private int mScreenHeight;
    private SparseArray<DynamicVideoViewContainer> mSparseArray;
    private LinearLayoutManager mLayoutManager;
    private boolean mFirstPlay = true;
    private boolean mBigPlay;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mLikeClickListener;
    private View.OnClickListener mSettingClickListener;
    private View.OnClickListener mHeadClickListener;
    private View.OnClickListener mVoiceClickListener;
    private DynamicBean mCurVoiceDynamicBean;
    private VoiceActionListener mActionListener;
    private View mCurVoiceView;


    public DynamicListAdapter(Context context, final String dynamicUid, int topHeight, int botHeight) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicBean tag = (DynamicBean) v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((tag), 0);
                }
            }
        };
        mLikeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicBean tag = (DynamicBean) v.getTag();
                ((AbsDynamicActivity) mContext).addLike(tag);
            }
        };
        mSettingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicBean tag = (DynamicBean) v.getTag();
                ((AbsDynamicActivity) mContext).setting(tag);
            }
        };
        mHeadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicBean bean = (DynamicBean) v.getTag();
                L.e("用户认证状态：" + bean.getUserinfo().isAlAuth());
                if (!bean.getUserinfo().isAlAuth()){
                    ToastUtil.show(com.aihuan.im.R.string.user_is_not_auth);
                    return;
                }
                if (!bean.getUid().equals(CommonAppConfig.getInstance().getUid()) && !bean.getUid().equals(dynamicUid)) {
                    RouteUtil.forwardUserHome(bean.getUid());
                }
            }
        };
        mVoiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicBean dynamicBean = (DynamicBean) v.getTag();
                if (mCurVoiceDynamicBean == null) {
                    TextView time = (TextView) ((ViewGroup) v).getChildAt(1);
                    dynamicBean.setVoicePlaying(true);
                    mCurVoiceDynamicBean = dynamicBean;
                    mCurVoiceView = v;
                    if (mActionListener != null) {
                        mActionListener.onVoicePlay(dynamicBean, time);
                    }
                } else {
                    if (mCurVoiceDynamicBean.getId().equals(dynamicBean.getId())) {
                        if (dynamicBean.isVoicePlaying()) {
                            dynamicBean.setVoicePlaying(false);
                            if (mActionListener != null) {
                                mActionListener.onVoicePause(mCurVoiceDynamicBean);
                            }
                        } else {
                            dynamicBean.setVoicePlaying(true);
                            mActionListener.onVoiceResume(mCurVoiceDynamicBean);
                        }
                    } else {
                        TextView time = (TextView) ((ViewGroup) v).getChildAt(1);
                        if (mCurVoiceDynamicBean.isVoicePlaying()) {
                            mCurVoiceDynamicBean.setVoicePlaying(false);
                            notifyItemChanged(mCurVoiceDynamicBean.getPosition(), Constants.PAYLOAD);
                        }
                        if (mActionListener != null) {
                            mActionListener.onVoiceStop(mCurVoiceDynamicBean);
                        }
                        if (mActionListener != null) {
                            mActionListener.onVoicePlay(dynamicBean, time);
                        }
                        dynamicBean.setVoicePlaying(true);
                        mCurVoiceDynamicBean = dynamicBean;
                        mCurVoiceView = v;
                    }

                }
                notifyItemChanged(dynamicBean.getPosition(), Constants.PAYLOAD);
            }
        };
        mUnLikeDrawable = context.getDrawable(R.mipmap.dynamic_unlike);
        mLikeDrawable = context.getDrawable(R.mipmap.dynamic_like);
        mHalfPlayViewHeight = DpUtil.dp2px(100);
        mHalfVoicePlayViewHeight = DpUtil.dp2px(22);
        mTopHeight = topHeight;
        mBotHeight = botHeight;
        mScreenHeight = ScreenDimenUtil.getInstance().getScreenHeight();
        mPlayView = (VideoPlayView) LayoutInflater.from(context).inflate(R.layout.dynamic_videoplay_view, null);
        mPlayView.setLargePlayCallback(new VideoPlayView.LargePlayCallback() {
            @Override
            public void largePlay(boolean large) {
                mBigPlay = large;
            }
        });
        mSparseArray = new SparseArray<>();
    }


    /**
     * 刷新点赞
     */
    public void notifyLike(String dynamicId, int like, String likes) {
        if (mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                if (dynamicId.equals(mList.get(i).getId())) {
                    mList.get(i).setLikes(likes);
                    mList.get(i).setIslike(like);
                    notifyItemChanged(i, Constants.PAYLOAD);
                }
            }
        }

    }

    /**
     * 刷新评论数
     */
    public void notifyComment(String dynamicId, String comments) {
        if (mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                if (dynamicId.equals(mList.get(i).getId())) {
                    mList.get(i).setComments(comments);
                    notifyItemChanged(i, Constants.PAYLOAD);
                }
            }
        }

    }

    /**
     * 删除动态
     */
    public void deleteDynamic(String dynamic) {
        if (TextUtils.isEmpty(dynamic)) {
            return;
        }
        int position = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (dynamic.equals(mList.get(i).getId())) {
                position = i;
                DynamicBean bean = mList.get(position);
                if (bean.getType() == Constants.DYNAMIC_TYPE_VIDEO) {
                    DynamicVideoViewContainer videoViewContainer = mSparseArray.get(position);
                    if (videoViewContainer.hasPlayView()) {
                        mPlayView.stop();
                        videoViewContainer.removeView(mPlayView);
                    }
                    mSparseArray.remove(position);
                }
                mList.remove(position);
                break;
            }
        }
        if (position > 0) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        } else {
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemViewType(int position) {
        int type = mList.get(position).getType();
        if (type == IMG) {
            return IMG;
        } else if (type == VIDEO) {
            return VIDEO;
        } else if (type == VOICE) {
            return VOICE;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMG) {
            return new ImgVh(mInflater.inflate(R.layout.item_dynamic_img, parent, false));
        } else if (viewType == VIDEO) {
            return new VideoVh(mInflater.inflate(R.layout.item_dynamic_video, parent, false));
        } else if (viewType == VOICE) {
            return new VoiceVh(mInflater.inflate(R.layout.item_dynamic_voice, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_dynamic_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof ImgVh) {
            ((ImgVh) vh).setData(mList.get(position), position, payload);
        } else if (vh instanceof VideoVh) {
            ((VideoVh) vh).setData(mList.get(position), position, payload);
        } else if (vh instanceof VoiceVh) {
            ((VoiceVh) vh).setData(mList.get(position), position, payload);
        } else {
            ((Vh) vh).setData(mList.get(position), position, payload);
        }
    }

    private class Vh extends RecyclerView.ViewHolder {
        private View mHeadView;
        private ImageView mIvAvatar;
        private TextView mTvName;
        private TextView mTvCity;
        private TextView mTvTime;
        private TextView mTvContent;
        private View mBtnSetting;
        private DrawableTextView mTvLike;
        private TextView mTvComment;
        private ImageView mIvSex;

        private Vh(View itemView) {
            super(itemView);
            mHeadView = itemView.findViewById(R.id.head_content);
            mIvAvatar = itemView.findViewById(R.id.avatar);
            mTvName = itemView.findViewById(R.id.name);
            mTvCity = itemView.findViewById(R.id.city);
            mTvTime = itemView.findViewById(R.id.time);
            mTvContent = itemView.findViewById(R.id.content);
            mBtnSetting = itemView.findViewById(R.id.btn_setting);
            mTvLike = itemView.findViewById(R.id.like);
            mTvComment = itemView.findViewById(R.id.comment);
            mIvSex = itemView.findViewById(R.id.sex);
            itemView.setOnClickListener(mOnClickListener);
            mTvLike.setOnClickListener(mLikeClickListener);
            mBtnSetting.setOnClickListener(mSettingClickListener);
            mHeadView.setOnClickListener(mHeadClickListener);

        }

        void setData(DynamicBean dynamicBean, int pos, Object payload) {
            itemView.setTag(dynamicBean);
            mTvLike.setTag(dynamicBean);
            mBtnSetting.setTag(dynamicBean);
            mHeadView.setTag(dynamicBean);
            if (payload == null) {
                UserBean bean = dynamicBean.getUserinfo();
                ImgLoader.display(mContext, bean.getAvatarThumb(), mIvAvatar);
                mTvName.setText(bean.getUserNiceName());
                mTvCity.setText(dynamicBean.getCity());
                mTvTime.setText(dynamicBean.getDatetime());
                String title = dynamicBean.getTitle();
                if (TextUtils.isEmpty(title)) {
                    mTvContent.setVisibility(View.GONE);
                } else {
                    mTvContent.setVisibility(View.VISIBLE);
                    mTvContent.setText(dynamicBean.getTitle());
                }
                mIvSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            }
            if (dynamicBean.getIslike() == 0) {
                mTvLike.setLeftDrawable(mUnLikeDrawable);
            } else {
                mTvLike.setLeftDrawable(mLikeDrawable);
            }
            mTvLike.setText(dynamicBean.getLikes());
            mTvComment.setText(dynamicBean.getComments());
        }
    }

    private class ImgVh extends Vh {
        RecyclerView mRecyclerView;
        DynamicImgAdapter mDynamicImgAdapter;
        private DynamicBean mDynamicBean;

        private ImgVh(final View itemView) {
            super(itemView);
            mRecyclerView = itemView.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
            ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 3, 3);
            decoration.setOnlySetItemOffsetsButNoDraw(true);
            mRecyclerView.addItemDecoration(decoration);
            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        itemView.performClick();  //模拟父控件的点击
                    }
                    return false;
                }
            });
            mDynamicImgAdapter = new DynamicImgAdapter(mContext);
            mDynamicImgAdapter.setOnItemClickListener(new OnItemClickListener<String>() {
                @Override
                public void onItemClick(String link, int position) {
                    pausePlay();
                    pauseVoiceAnim();
                    ((AbsDynamicActivity) mContext).lookImgs(mDynamicBean.getThumbs(), position, new CommonCallback<Boolean>() {
                        @Override
                        public void callback(Boolean bean) {
                            resumePlay();
                        }
                    });
                }
            });
            mRecyclerView.setAdapter(mDynamicImgAdapter);
        }

        void setData(final DynamicBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mDynamicBean = bean;
            if (payload == null) {
                if (mDynamicImgAdapter != null) {
                    mDynamicImgAdapter.setList(bean.getThumbs());
                }
            }

        }
    }

    private class VideoVh extends Vh {
        private FrameLayout mContainer;
        private View mVideoGroup;
        private DynamicVideoViewContainer mDynamicVideoViewContainer;
        private ImageView mIvCover;
        private FrameLayout.LayoutParams mCoverLayoutParams;
        private LinearLayout.LayoutParams mVideoLayoutParams;
        private int mPos;

        private VideoVh(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            mVideoGroup = itemView.findViewById(R.id.video_group);
            mDynamicVideoViewContainer = itemView.findViewById(R.id.playViewContainer);
            mIvCover = itemView.findViewById(R.id.cover);
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseVoiceAnim();
                    if (mDynamicVideoViewContainer.hasPlayView()) {
                        if (mPlayView.getVideoWidth() > 0) {
                            mPlayView.pausePlay();
                        }
                        mBigPlay = true;
                        ((AbsDynamicActivity) mContext).addPlayerGroup(mContainer, mVideoGroup, mPlayView);
                    } else {
                        mBigPlay = true;
                        mDynamicVideoViewContainer.addPlayView(mPlayView);
                        DynamicBean bean = mDynamicVideoViewContainer.getDynamicBean();
                        mPlayView.setDynamicBean(bean);
                        mPlayView.play();
                        ((AbsDynamicActivity) mContext).addPlayerGroup(mContainer, mVideoGroup, mPlayView);

                    }
                    mRecyclerView.smoothScrollToPosition(mPos);
                }
            });
            mDynamicVideoViewContainer.setPlayEventListener(new DynamicVideoViewContainer.PlayEventListener() {
                @Override
                public void onStartPlay() {
                    L.e(TAG, "onStartPlay");
                    if (mIvCover != null && mIvCover.getVisibility() == View.VISIBLE) {
                        mIvCover.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFirstFrame() {
                    L.e(TAG, "onFirstFrame");
                    if (mIvCover != null && mIvCover.getVisibility() == View.VISIBLE) {
                        mIvCover.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onResumePlay() {
                    L.e(TAG, "onResumePlay");
                    if (mIvCover != null && mIvCover.getVisibility() == View.VISIBLE) {
                        mIvCover.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPausePlay() {
                    L.e(TAG, "onPausePlay");
                }

                @Override
                public void onPausePlay2() {
                    L.e(TAG, "onPausePlay2");
                    if (mIvCover != null && mIvCover.getVisibility() != View.VISIBLE) {
                        mIvCover.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onRemoveView() {
                    L.e(TAG, "onRemoveView");
                    if (mIvCover != null && mIvCover.getVisibility() != View.VISIBLE) {
                        mIvCover.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onVideoSize(int width, int height, boolean onlyImgChange) {
                    L.e(TAG, "onVideoSize");
                    if (mIvCover != null) {
                        mCoverLayoutParams = (FrameLayout.LayoutParams) mIvCover.getLayoutParams();
                        mCoverLayoutParams.width = width;
                        mCoverLayoutParams.height = height;
                        mIvCover.setLayoutParams(mCoverLayoutParams);
                    }
                    if (mContainer != null && !onlyImgChange) {
                        mVideoLayoutParams = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
                        mVideoLayoutParams.width = width;
                        mContainer.setLayoutParams(mVideoLayoutParams);
                    }
                }
            });
        }

        void setData(DynamicBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mPos = position;
            if (payload == null) {
                mDynamicVideoViewContainer.setDynamicBean(bean);
                if (mDynamicVideoViewContainer.getChildCount() > 0) {
                    mDynamicVideoViewContainer.removeAllViews();
                }
                mSparseArray.put(position, mDynamicVideoViewContainer);
                if (mIvCover != null && mIvCover.getVisibility() == View.INVISIBLE) {
                    mIvCover.setVisibility(View.VISIBLE);
                }
                ImgLoader.display(mContext, bean.getVideo_thumb(), mIvCover);
            }
        }
    }

    private class VoiceVh extends Vh {
        private View mVoiceView;
        private ImageView mIvPlay;
        private TextView mTvTime;
        private AnimationDrawable mAnimationDrawable;

        private VoiceVh(View itemView) {
            super(itemView);
            mVoiceView = itemView.findViewById(R.id.voice_view);
            mIvPlay = itemView.findViewById(R.id.voice_play);
            mTvTime = itemView.findViewById(R.id.voice_time);
            mAnimationDrawable = (AnimationDrawable) mIvPlay.getDrawable();
            mVoiceView.setOnClickListener(mVoiceClickListener);
        }

        void setData(DynamicBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mVoiceView.setTag(bean);
            bean.setPosition(position);
            if (payload == null) {
                mTvTime.setText(bean.getLength() + "s");
            }
            if (bean.isVoicePlaying()) {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.start();
                }
            } else {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.selectDrawable(0);
                    mAnimationDrawable.stop();
                }
            }
        }

    }

//    @Override
//    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
//        L.e(TAG,"onViewDetachedFromWindow");
//        if (holder instanceof VideoVh) {
//        VideoVh vh = (VideoVh) holder;
//        if (vh.mIvCover != null && vh.mIvCover.getVisibility() != View.VISIBLE) {
//            vh.mIvCover.setVisibility(View.VISIBLE);
//        }
//    }
//}

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    play();
                    voiceCheck();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mFirstPlay) {
                    mFirstPlay = false;
                    play();
                }
            }
        });
    }

    public void play() {
        if (mBigPlay) {
            return;
        }
        if (mSparseArray.size() <= 0) {
            return;
        }
        int topPosition = mLayoutManager.findFirstVisibleItemPosition();
        int bottomPosition = topPosition == mList.size() - 1 ? topPosition : topPosition + 1;
        int bottomPosition2 = mLayoutManager.findLastCompletelyVisibleItemPosition();
        DynamicBean topDynamicBean = mList.get(topPosition);
        DynamicBean bottomDynamicBean = mList.get(bottomPosition);
        L.e(TAG, "--size-" + mSparseArray.size() + "--topDynamicBean-type=" + topDynamicBean.getType()
                + "---bottomDynamicBean-type=" + bottomDynamicBean.getType() + "---topPosition=" + topPosition + "--bottomPosition=" + bottomPosition + "---bottomPosition2=" + bottomPosition2);
        if (topDynamicBean.getType() == VIDEO && bottomDynamicBean.getType() == VIDEO) {
            DynamicVideoViewContainer topContainer = mSparseArray.get(topPosition);
            DynamicVideoViewContainer bottomContainer = mSparseArray.get(bottomPosition);
            if (topContainer != null) {
                int topHeight = topContainer.getLocation()[1] - mTopHeight;
                L.e(TAG, "--topContainer-hasPlayView---" + topContainer.hasPlayView() + "----half=" + topHeight);
                if (topHeight > 0) {
                    if (!topContainer.hasPlayView()) {
                        topContainer.addPlayView(mPlayView);
                        DynamicBean bean = topContainer.getDynamicBean();
                        mPlayView.setDynamicBean(bean);
                        mPlayView.play();
                    } else {
                        scrollResumePlay();
                    }
                } else {
                    int botHeight = bottomContainer.getLocation()[1] + mHalfPlayViewHeight * 2 + mBotHeight - mScreenHeight;
                    L.e(TAG, "--topContainer-hasPlayView---" + topContainer.hasPlayView() + "----half=" + topHeight + "---botHeight=" + botHeight);
                    if (botHeight < 0) {
                        if (!bottomContainer.hasPlayView()) {
                            bottomContainer.addPlayView(mPlayView);
                            DynamicBean bean = bottomContainer.getDynamicBean();
                            mPlayView.setDynamicBean(bean);
                            mPlayView.play();
                        } else {
                            scrollResumePlay();
                        }
                    } else {
                        scrollPausePlay();
                    }
                }
            }
        } else if (topDynamicBean.getType() == VIDEO) {
            DynamicVideoViewContainer topContainer = mSparseArray.get(topPosition);
            int topHeight = topContainer.getLocation()[1] - mTopHeight;
            L.e(TAG, "--topContainer-hasPlayView---" + topContainer.hasPlayView() + "--topHeight=" + topHeight);
            if (topHeight > 0) {
                if (!topContainer.hasPlayView()) {
                    topContainer.addPlayView(mPlayView);
                    DynamicBean bean = topContainer.getDynamicBean();
                    mPlayView.setDynamicBean(bean);
                    mPlayView.play();
                } else {
                    scrollResumePlay();
                }
            } else {
                scrollPausePlay();
            }
        } else if (bottomDynamicBean.getType() == VIDEO) {
            DynamicVideoViewContainer bottomContainer = mSparseArray.get(bottomPosition);
            int topHeight = bottomContainer.getLocation()[1] - mTopHeight;
            int botHeight = bottomContainer.getLocation()[1] + mHalfPlayViewHeight * 2 + mBotHeight - mScreenHeight;
            L.e(TAG, "--bottomContainer-hasPlayView---" + bottomContainer.hasPlayView() + "--topHeight=" + topHeight + "--botHeight=" + botHeight);
            if (topHeight > 0 && botHeight < 0) {
                if (!bottomContainer.hasPlayView()) {
                    bottomContainer.addPlayView(mPlayView);
                    DynamicBean bean = bottomContainer.getDynamicBean();
                    mPlayView.setDynamicBean(bean);
                    mPlayView.play();
                } else {
                    scrollResumePlay();
                }
            } else {
                scrollPausePlay();
            }
        } else {
            if (bottomPosition2 != -1 && bottomPosition2 != bottomPosition) {
                DynamicBean bottomDynamicBean2 = mList.get(bottomPosition2);
                if (bottomDynamicBean2.getType() == VIDEO) {
                    DynamicVideoViewContainer bottomContainer2 = mSparseArray.get(bottomPosition2);
                    L.e(TAG, "--bottomContainer2-hasPlayView---" + bottomContainer2.hasPlayView());
                    int topHeight = bottomContainer2.getLocation()[1] - mTopHeight;
                    int botHeight = bottomContainer2.getLocation()[1] + mHalfPlayViewHeight * 2 + mBotHeight - mScreenHeight;
                    L.e(TAG, "--bottomContainer-hasPlayView---" + bottomContainer2.hasPlayView() + "--topHeight=" + topHeight + "--botHeight=" + botHeight);
                    if (topHeight > 0 && botHeight < 0) {
                        if (!bottomContainer2.hasPlayView()) {
                            bottomContainer2.addPlayView(mPlayView);
                            DynamicBean bean = bottomContainer2.getDynamicBean();
                            mPlayView.setDynamicBean(bean);
                            mPlayView.play();
                        } else {
                            scrollResumePlay();
                        }
                    } else {
                        scrollPausePlay();
                    }
                } else {
                    scrollPausePlay();
                }
            } else {
                scrollPausePlay();
            }
        }
    }

    public void release() {
        if (mPlayView != null) {
            mPlayView.destroy();
        }
    }

    public void onPause() {
        if (mPlayView != null) {
            mPlayView.onPause();
        }
    }

    public void onResume() {
        if (mPlayView != null) {
            mPlayView.onResume();
        }
    }

    public void pausePlay() {
        if (mPlayView != null) {
            mPlayView.pausePlay();
        }
    }

    public void resumePlay() {
        if (mPlayView != null) {
            mPlayView.resumePlay();
        }
    }

    private void scrollPausePlay() {
        if (mPlayView != null) {
            mPlayView.scrollPausePlay();
        }
    }

    private void scrollResumePlay() {
        if (mPlayView != null) {
            mPlayView.scrollResumePlay();
        }
    }

    public void back(boolean back) {
        if (mPlayView != null) {
            mPlayView.setBack(back);
        }
    }

    @Override
    public void refreshData(List<DynamicBean> list) {
        if (mPlayView != null) {
            mPlayView.stopPlay();
            ViewParent parent = mPlayView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mPlayView);
            }
            mPlayView.onDetchWindow();
        }
        stopVoiceAnim();
        if (mCurVoiceDynamicBean != null) {
            mCurVoiceDynamicBean = null;
        }
        super.refreshData(list);
    }

    @Override
    public void clearData() {
        if (mPlayView != null) {
            mPlayView.pausePlay();
            mPlayView.onDetchWindow();
        }
        if (mSparseArray != null) {
            mSparseArray.clear();
        }
        super.clearData();
    }


    public void scrollToTop() {
        if (mRecyclerView != null && mList.size() > 0) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    private void voiceCheck() {
        if (mCurVoiceView == null || mCurVoiceDynamicBean == null) {
            return;
        }
        int location[] = new int[2];
        mCurVoiceView.getLocationOnScreen(location);
        int half = location[1] - mTopHeight + mHalfVoicePlayViewHeight;
        if (half < 0) {
            //&& mCurVoiceDynamicBean.isVoicePlaying()
            mCurVoiceDynamicBean.setVoicePlaying(false);
            if (mActionListener != null) {
                mActionListener.onVoiceStop(mCurVoiceDynamicBean);
            }
            notifyItemChanged(mCurVoiceDynamicBean.getPosition(), Constants.PAYLOAD);
        }
    }

    public void stopVoiceAnim() {
        if (mCurVoiceDynamicBean == null) {
            return;
        }
        if (mCurVoiceDynamicBean.isVoicePlaying()) {
            mCurVoiceDynamicBean.setVoicePlaying(false);
            if (mActionListener != null) {
                mActionListener.onVoiceStop(mCurVoiceDynamicBean);
            }
            notifyItemChanged(mCurVoiceDynamicBean.getPosition(), Constants.PAYLOAD);
        }
    }

    private void pauseVoiceAnim() {
        if (mCurVoiceDynamicBean == null) {
            return;
        }
        if (mCurVoiceDynamicBean.isVoicePlaying()) {
            mCurVoiceDynamicBean.setVoicePlaying(false);
            if (mActionListener != null) {
                mActionListener.onVoicePause(mCurVoiceDynamicBean);
            }
            notifyItemChanged(mCurVoiceDynamicBean.getPosition(), Constants.PAYLOAD);
        }
    }

    public void resumeVoiceAnim() {
        if (mCurVoiceDynamicBean == null) {
            return;
        }
        if (!mCurVoiceDynamicBean.isVoicePlaying()) {
            mCurVoiceDynamicBean.setVoicePlaying(true);
            if (mActionListener != null) {
                mActionListener.onVoiceResume(mCurVoiceDynamicBean);
            }
            notifyItemChanged(mCurVoiceDynamicBean.getPosition(), Constants.PAYLOAD);
        }
    }


    //播语音的时候视频静音
    public void setVideoViewMute(boolean mute) {
//        if (mPlayView != null) {
//            mPlayView.setMute(mute);
//        }
        if (mute) {
            if (mPlayView != null) {
                mPlayView.voicePlayPause();
            }
        } else {
            if (mPlayView != null) {
                mPlayView.voicePauseResume();
            }
        }
    }


    public interface VoiceActionListener {
        void onVoicePlay(DynamicBean dynamicBean, TextView time);

        void onVoicePause(DynamicBean dynamicBean);

        void onVoiceResume(DynamicBean dynamicBean);

        void onVoiceStop(DynamicBean dynamicBean);
    }

    public void setActionListener(VoiceActionListener actionListener) {
        mActionListener = actionListener;
    }

}
