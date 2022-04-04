package com.aihuan.dynamic.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.DrawableTextView;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.CommonIconUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.activity.AbsDynamicCommentActivity;
import com.aihuan.dynamic.activity.DynamicDetailsActivity;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.bean.DynamicCommentBean;
import com.aihuan.dynamic.event.DynamicLikeEvent;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.video.utils.VideoTextRender;

import org.greenrobot.eventbus.EventBus;

import java.util.List;



/**
 * Created by debug on 2019/7/24.
 * 动态详情
 */

public class DynamicDetailsCommentAdapter extends RefreshAdapter<DynamicCommentBean> {

    private static final int HEAD = -1;
    private static final int TEXT_PARENT = 1;
    private static final int TEXT_CHILD = 2;
    private static final int VOICE_PARENT = 3;
    private static final int VOICE_CHILD = 4;
    private Drawable mLikeDrawable;
    private Drawable mUnLikeDrawable;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mLikeClickListener;
    private View.OnClickListener mExpandClickListener;
    private View.OnClickListener mCollapsedClickListener;
    private View.OnClickListener mVoiceClickListener;
    private ActionListener mActionListener;
    private int mCurLikeCommentPosition;
    private DynamicCommentBean mCurLikeCommentBean;
    private HttpCallback mLikeCommentCallback;
    private Drawable mVoicePlay;
    private Drawable mVoicePause;
    private DynamicCommentBean mCurVoiceCommentBean;
    private DynamicBean mDynamicBean;
    private boolean mIsFromUserCenter;

    public DynamicDetailsCommentAdapter(Context context, DynamicBean dynamicBean, boolean isFromUserCenter) {
        super(context);
        mDynamicBean = dynamicBean;
        mIsFromUserCenter = isFromUserCenter;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicCommentBean bean = (DynamicCommentBean) v.getTag();
                if (bean == null) {
                    return;
                }
                String uid = bean.getUid();
                if (!TextUtils.isEmpty(uid) && uid.equals(CommonAppConfig.getInstance().getUid())) {
                    return;
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
        mLikeDrawable = ContextCompat.getDrawable(context, R.mipmap.dynamic_like);
        mUnLikeDrawable = ContextCompat.getDrawable(context, R.mipmap.dynamic_unlike);
        mLikeCommentCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0 && mCurLikeCommentBean != null) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int like = obj.getIntValue("islike");
                    String likeNum = obj.getString("nums");
                    if (mCurLikeCommentBean != null) {
                        mCurLikeCommentBean.setIsLike(like);
                        mCurLikeCommentBean.setLikeNum(likeNum);
                        notifyItemChanged(mCurLikeCommentPosition, Constants.PAYLOAD);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        };
        mLikeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                DynamicCommentBean bean = (DynamicCommentBean) tag;
                String uid = bean.getUid();
                if (!TextUtils.isEmpty(uid) && uid.equals(CommonAppConfig.getInstance().getUid())) {
                    ToastUtil.show(R.string.video_comment_cannot_self);
                    return;
                }
                mCurLikeCommentPosition = bean.getPosition();
                mCurLikeCommentBean = bean;
                DynamicHttpUtil.setCommentLike(bean.getId(), mLikeCommentCallback);
            }
        };
        mExpandClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onExpandClicked((DynamicCommentBean) tag);
                }
            }
        };
        mCollapsedClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                DynamicCommentBean commentBean = (DynamicCommentBean) tag;
                if (mCurVoiceCommentBean != null) {
                    DynamicCommentBean parentNodeBean = commentBean.getParentNodeBean();
                    if (parentNodeBean != null) {
                        List<DynamicCommentBean> childList = parentNodeBean.getChildList();
                        if (childList != null && childList.size() > 0) {
                            for (DynamicCommentBean child : childList) {
                                if (mCurVoiceCommentBean.getId().equals(child.getId())) {
                                    if (mActionListener != null) {
                                        mActionListener.onVoiceStop(mCurVoiceCommentBean);
                                    }
                                    mCurVoiceCommentBean = null;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (mActionListener != null) {
                    mActionListener.onCollapsedClicked(commentBean);
                }
            }
        };
        mVoiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                DynamicCommentBean commentBean = (DynamicCommentBean) tag;
                if (mCurVoiceCommentBean != null) {
                    if (mCurVoiceCommentBean.getId().equals(commentBean.getId())) {
                        if (commentBean.isVoicePlaying()) {
                            commentBean.setVoicePlaying(false);
                            if (mActionListener != null) {
                                mActionListener.onVoicePause(mCurVoiceCommentBean);
                            }
                        } else {
                            commentBean.setVoicePlaying(true);
                            if (mActionListener != null) {
                                mActionListener.onVoiceResume(mCurVoiceCommentBean);
                            }
                        }
                        //mCurVoiceCommentBean = null;
                    } else {
                        TextView time = (TextView) ((ViewGroup) v).getChildAt(0);
                        mCurVoiceCommentBean.setVoicePlaying(false);
                        notifyItemChanged(mCurVoiceCommentBean.getPosition(), Constants.PAYLOAD);
                        if (mActionListener != null) {
                            mActionListener.onVoiceStop(mCurVoiceCommentBean);
                            mActionListener.onVoicePlay(commentBean, time);
                        }
                        commentBean.setVoicePlaying(true);
                        mCurVoiceCommentBean = commentBean;
                    }
                } else {
                    TextView time = (TextView) ((ViewGroup) v).getChildAt(0);
                    commentBean.setVoicePlaying(true);
                    mCurVoiceCommentBean = commentBean;
                    if (mActionListener != null) {
                        mActionListener.onVoicePlay(commentBean, time);
                    }

                }
                notifyItemChanged(commentBean.getPosition(), Constants.PAYLOAD);

            }
        };

        mVoicePlay = ContextCompat.getDrawable(context, R.mipmap.icon_comment_voice_1);
        mVoicePause = ContextCompat.getDrawable(context, R.mipmap.icon_comment_voice_0);
    }


    @Override
    public void refreshData(List<DynamicCommentBean> list) {
        ((AbsDynamicActivity) mContext).stopVoice();
        super.refreshData(list);
    }

    /**
     * 停止语音播放动画
     */
    public void stopVoiceAnim() {
        if (mCurVoiceCommentBean != null) {
            mCurVoiceCommentBean.setVoicePlaying(false);
            notifyItemChanged(mCurVoiceCommentBean.getPosition(), Constants.PAYLOAD);
        }
        mCurVoiceCommentBean = null;
    }

    public void notifyComments(String comments) {
        mDynamicBean.setComments(comments);
        notifyItemChanged(0, Constants.PAYLOAD);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            count++;
            List<DynamicCommentBean> childList = mList.get(i).getChildList();
            if (childList != null) {
                count += childList.size();
            }
        }
        return count + 1;
    }

    private DynamicCommentBean getItem(int position) {
        int index = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            DynamicCommentBean parentNode = mList.get(i);
            if (index == position) {
                return parentNode;
            }
            index++;
            List<DynamicCommentBean> childList = mList.get(i).getChildList();
            if (childList != null) {
                for (int j = 0, childSize = childList.size(); j < childSize; j++) {
                    if (position == index) {
                        return childList.get(j);
                    }
                    index++;
                }
            }
        }
        return null;
    }


    /**
     * 展开子评论
     *
     * @param childNode   在这个子评论下面插入子评论
     * @param insertCount 插入的子评论的个数
     */
    public void insertReplyList(DynamicCommentBean childNode, int insertCount) {
        //这种方式也能达到  notifyItemRangeInserted 的效果，而且不容易出问题
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(childNode.getPosition());
        }
        notifyItemRangeChanged(childNode.getPosition(), getItemCount(), Constants.PAYLOAD);
    }

    /**
     * 收起子评论
     *
     * @param childNode   在这个子评论下面收起子评论
     * @param removeCount 被收起的子评论的个数
     */
    public void removeReplyList(DynamicCommentBean childNode, int removeCount) {
        //这种方式也能达到  notifyItemRangeRemoved 的效果，而且不容易出问题
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(childNode.getPosition());
        }
        notifyItemRangeChanged(childNode.getPosition(), getItemCount(), Constants.PAYLOAD);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        DynamicCommentBean bean = getItem(position - 1);
        if (bean != null) {
            if (bean.isParentNode()) {
                if (bean.isVoice()) {
                    return VOICE_PARENT;
                }
                return TEXT_PARENT;
            } else {
                if (bean.isVoice()) {
                    return VOICE_CHILD;
                }
            }
        }
        return TEXT_CHILD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadVh(mInflater.inflate(R.layout.view_dynamic_top, parent, false));
        } else if (viewType == TEXT_PARENT) {
            return new ParentVh(mInflater.inflate(R.layout.item_dynamic_comment_parent, parent, false));
        } else if (viewType == TEXT_CHILD) {
            return new ChildVh(mInflater.inflate(R.layout.item_dynamic_comment_child, parent, false));
        } else if (viewType == VOICE_PARENT) {
            return new VoiceParentVh(mInflater.inflate(R.layout.item_dynamic_comment_parent_voice, parent, false));
        }
        return new VoiceChildVh(mInflater.inflate(R.layout.item_dynamic_comment_child_voice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof HeadVh) {
            ((HeadVh) vh).setData(payload);
        } else {
            DynamicCommentBean bean = getItem(position - 1);
            if (bean != null) {
                bean.setPosition(position);
                if (vh instanceof ParentVh) {
                    ((ParentVh) vh).setData(bean, payload);
                } else if (vh instanceof ChildVh) {
                    ((ChildVh) vh).setData(bean, payload);
                }
            }
        }

    }

    class HeadVh extends RecyclerView.ViewHolder {
        private View mHeadView;
        private ImageView mAvatar;
        private TextView mName;
        private TextView mTvCity;
        private TextView mTitle;
        private TextView mTime;
        private FrameLayout mContainer;
        private FrameLayout mContainerVideo;
        private DrawableTextView mLike;
        private DrawableTextView mComment;
        private View mBtnSetting;
        private View noDataView;
        private ImageView mIvSex;

        private HeadVh(View itemView) {
            super(itemView);
            mHeadView = itemView.findViewById(R.id.head_content);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mTvCity = itemView.findViewById(R.id.city);
            mTitle = itemView.findViewById(R.id.content);
            mLike = itemView.findViewById(R.id.like);
            mTime = itemView.findViewById(R.id.time);
            mComment = itemView.findViewById(R.id.comment);
            mContainer = itemView.findViewById(R.id.container);
            mContainerVideo = itemView.findViewById(R.id.container_video);
            mBtnSetting = itemView.findViewById(R.id.btn_setting);
            noDataView = itemView.findViewById(R.id.no_data);
            mIvSex = itemView.findViewById(R.id.sex);
            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = mDynamicBean.getUid();
                    if (!TextUtils.isEmpty(uid) && uid.equals(CommonAppConfig.getInstance().getUid())) {
                        ToastUtil.show(R.string.video_comment_cannot_self);
                        return;
                    }
                    DynamicHttpUtil.addLike(mDynamicBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                int islike = obj.getIntValue("islike");
                                String likes = obj.getString("nums");
                                mDynamicBean.setIslike(islike);
                                mDynamicBean.setLikes(likes);
                                notifyItemChanged(0, Constants.PAYLOAD);
                                EventBus.getDefault().post(new DynamicLikeEvent(mDynamicBean.getId(), islike, likes));
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                }
            });
            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AbsDynamicCommentActivity) mContext).openCommentInputWindow(false, mDynamicBean.getId(), mDynamicBean.getUid(), null);
                }
            });
            mHeadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mDynamicBean.getUserinfo().isAlAuth()) {
                        ToastUtil.show(com.aihuan.im.R.string.user_is_not_auth);
                        return;
                    }
                    if (!mIsFromUserCenter) {
                        RouteUtil.forwardUserHome(mDynamicBean.getUid());
                    }

                }
            });
            mBtnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AbsDynamicActivity) mContext).setting(mDynamicBean);
                }
            });
        }

        void setData(Object payload) {
            if (payload == null) {
                UserBean userbean = mDynamicBean.getUserinfo();
                ImgLoader.display(mContext, userbean.getAvatar(), mAvatar);
                mName.setText(userbean.getUserNiceName());
                if (TextUtils.isEmpty(mDynamicBean.getTitle())) {
                    mTitle.setVisibility(View.GONE);
                } else {
                    mTitle.setVisibility(View.VISIBLE);
                    mTitle.setText(mDynamicBean.getTitle());
                }
                mTime.setText(mDynamicBean.getDatetime());
                mTvCity.setText(mDynamicBean.getCity());
                if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_IMG) {
                    ((DynamicDetailsActivity) mContext).loadImg(mContainer);
                } else if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_VIDEO) {
                    ((DynamicDetailsActivity) mContext).loadVideo(mContainerVideo);
                } else if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_VOICE) {
                    ((DynamicDetailsActivity) mContext).loadVoice(mContainer);
                }
                mIvSex.setImageResource(CommonIconUtil.getSexIcon(userbean.getSex()));
            }
            if (mList.size() == 0) {
                noDataView.setVisibility(View.VISIBLE);
            } else {
                noDataView.setVisibility(View.GONE);
            }
            mLike.setText(mDynamicBean.getLikes());
            mComment.setText(mDynamicBean.getComments());
            if (mDynamicBean.getIslike() == 1) {
                mLike.setLeftDrawable(mLikeDrawable);
            } else {
                mLike.setLeftDrawable(mUnLikeDrawable);
            }
        }

    }


    class Vh extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mContent;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mContent = itemView.findViewById(R.id.content);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            itemView.setTag(bean);
            if (payload == null) {
                UserBean u = bean.getUserBean();
                if (u != null) {
                    mName.setText(u.getUserNiceName());
                }
                if (mContent != null && bean.getContent() != null) {
                    mContent.setText(VideoTextRender.renderVideoComment(bean.getContent(), "  " + bean.getDatetime()));
                }
            }
        }
    }

    class ParentVh extends Vh {

        //        ImageView mBtnLike;
        DrawableTextView mBtnLike;
        //TextView mLikeNum;
        ImageView mAvatar;


        private ParentVh(View itemView) {
            super(itemView);
            //mBtnLike = (ImageView) itemView.findViewById(R.id.btn_like);
            mBtnLike = itemView.findViewById(R.id.btn_like);
            //mLikeNum = (TextView) itemView.findViewById(R.id.like_num);
            mBtnLike.setOnClickListener(mLikeClickListener);
            mAvatar = itemView.findViewById(R.id.avatar);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            super.setData(bean, payload);
            if (payload == null) {
                UserBean u = bean.getUserBean();
                if (u != null) {
                    ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
                }
            }
            mBtnLike.setTag(bean);
            boolean like = bean.getIsLike() == 1;
            mBtnLike.setTopDrawable(like ? mLikeDrawable : mUnLikeDrawable);
            mBtnLike.setText(bean.getLikeNum());
            //mBtnLike.setTextColor(like ? mLikeColor : mUnLikeColor);
        }
    }

    class ChildVh extends Vh {
        View mBtnGroup;
        View mBtnExpand;//展开按钮
        View mBtnbCollapsed;//收起按钮
        TextView mTvChildAllCommentNum;

        private ChildVh(View itemView) {
            super(itemView);
            mBtnGroup = itemView.findViewById(R.id.btn_group);
            mBtnExpand = itemView.findViewById(R.id.btn_expand);
            mBtnbCollapsed = itemView.findViewById(R.id.btn_collapsed);
            mTvChildAllCommentNum = itemView.findViewById(R.id.comment_num);
            mBtnExpand.setOnClickListener(mExpandClickListener);
            mBtnbCollapsed.setOnClickListener(mCollapsedClickListener);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            super.setData(bean, payload);
            mBtnExpand.setTag(bean);
            mBtnbCollapsed.setTag(bean);
            DynamicCommentBean parentNodeBean = bean.getParentNodeBean();
            if (bean.needShowExpand(parentNodeBean)) {
                if (mBtnGroup.getVisibility() != View.VISIBLE) {
                    mBtnGroup.setVisibility(View.VISIBLE);
                }
                if (mBtnbCollapsed.getVisibility() == View.VISIBLE) {
                    mBtnbCollapsed.setVisibility(View.INVISIBLE);
                }
                if (mBtnExpand.getVisibility() != View.VISIBLE) {
                    mBtnExpand.setVisibility(View.VISIBLE);
                }
                mTvChildAllCommentNum.setText(String.format(WordUtil.getString(R.string.all_comments_replays), parentNodeBean.getReplyNum()));
            } else if (bean.needShowCollapsed(parentNodeBean)) {
                if (mBtnGroup.getVisibility() != View.VISIBLE) {
                    mBtnGroup.setVisibility(View.VISIBLE);
                }
                if (mBtnExpand.getVisibility() == View.VISIBLE) {
                    mBtnExpand.setVisibility(View.INVISIBLE);
                }
                if (mBtnbCollapsed.getVisibility() != View.VISIBLE) {
                    mBtnbCollapsed.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnGroup.getVisibility() == View.VISIBLE) {
                    mBtnGroup.setVisibility(View.GONE);
                }
            }
        }
    }

    private class VoiceParentVh extends ParentVh {
        TextView mVoiceDuration;
        TextView mTime;
        ImageView mPlayGif;
        ImageView mPlayImg;
        View mBubble;
        AnimationDrawable mAnimationDrawable;

        private VoiceParentVh(View itemView) {
            super(itemView);
            mVoiceDuration = itemView.findViewById(R.id.voice_duration);
            mTime = itemView.findViewById(R.id.time);
            mPlayGif = itemView.findViewById(R.id.play_gif);
            mPlayImg = itemView.findViewById(R.id.play_img);
            mBubble = itemView.findViewById(R.id.bubble);
            mBubble.setOnClickListener(mVoiceClickListener);
            mAnimationDrawable = (AnimationDrawable) mPlayGif.getDrawable();
        }

        void setData(DynamicCommentBean bean, Object payload) {
            super.setData(bean, payload);
            mBubble.setTag(bean);
            if (payload == null) {
                mVoiceDuration.setText(bean.getVoiceDuration() + "s");
                mTime.setText(bean.getDatetime());
            }
            if (bean.isVoicePlaying()) {
                mPlayImg.setImageDrawable(mVoicePlay);
//                if (mPlayGif.getVisibility() != View.VISIBLE) {
//                    mPlayGif.setVisibility(View.VISIBLE);
//                    mAnimationDrawable.start();
//                }
            } else {
                mPlayImg.setImageDrawable(mVoicePause);
//                if (mPlayGif.getVisibility() == View.VISIBLE) {
//                    mPlayGif.setVisibility(View.INVISIBLE);
//                    mAnimationDrawable.stop();
//                }
            }
        }
    }


    private class VoiceChildVh extends ChildVh {

        TextView mVoiceDuration;
        TextView mTime;
        //ImageView mPlayGif;
        ImageView mPlayImg;
        View mBubble;
        //AnimationDrawable mAnimationDrawable;

        private VoiceChildVh(View itemView) {
            super(itemView);
            mVoiceDuration = itemView.findViewById(R.id.voice_duration);
            mTime = itemView.findViewById(R.id.time);
            // mPlayGif = itemView.findViewById(R.id.play_gif);
            mPlayImg = itemView.findViewById(R.id.play_img);
            mBubble = itemView.findViewById(R.id.bubble);
            mBubble.setOnClickListener(mVoiceClickListener);
            // mAnimationDrawable = (AnimationDrawable) mPlayGif.getDrawable();
        }

        void setData(DynamicCommentBean bean, Object payload) {
            super.setData(bean, payload);
            mBubble.setTag(bean);
            if (payload == null) {
                mVoiceDuration.setText(bean.getVoiceDuration() + "s");
                mTime.setText(bean.getDatetime());
            }
            if (bean.isVoicePlaying()) {
                mPlayImg.setImageDrawable(mVoicePlay);
//                if (mPlayGif.getVisibility() != View.VISIBLE) {
//                    mPlayGif.setVisibility(View.VISIBLE);
//                    mAnimationDrawable.start();
//                }
            } else {
                mPlayImg.setImageDrawable(mVoicePause);
//                if (mPlayGif.getVisibility() == View.VISIBLE) {
//                    mPlayGif.setVisibility(View.INVISIBLE);
//                    mAnimationDrawable.stop();
//                }
            }
        }
    }


    public interface ActionListener {
        void onExpandClicked(DynamicCommentBean commentBean);

        void onCollapsedClicked(DynamicCommentBean commentBean);

        void onVoicePlay(DynamicCommentBean commentBean, TextView time);

        void onVoicePause(DynamicCommentBean commentBean);

        void onVoiceResume(DynamicCommentBean commentBean);

        void onVoiceStop(DynamicCommentBean commentBean);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

}
