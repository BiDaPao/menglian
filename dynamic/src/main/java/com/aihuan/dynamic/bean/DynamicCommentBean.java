package com.aihuan.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.video.R;

import java.util.List;


/**
 * Created by cxf on 2017/7/14.
 */

public class DynamicCommentBean implements Parcelable {

    private static final String REPLY = WordUtil.getString(R.string.video_comment_reply) + " ";

    private String mId;
    private String mUid;
    private String mToUid;
    private String mDynamicId;
    private String mCommentId;
    private String mParentId;
    private String mContent;
    private String mLikeNum;
    private String mAddTime;
    private String mAtInfo;
    private int mIsLike;
    private int mReplyNum;
    private String mDatetime;
    private UserBean mUserBean;
    private UserBean mToUserBean;
    private List<DynamicCommentBean> mChildList;
    private boolean mParentNode;//是否是父元素
    private int mPosition;
    private DynamicCommentBean mParentNodeBean;
    private int mChildPage = 1;
    private int mType;
    private String mVoiceLink;
    private String mVoiceDuration;
    private boolean mVoicePlaying;

    public DynamicCommentBean() {

    }
    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "uid")
    public String getUid() {
        return mUid;
    }

    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
    }

    @JSONField(name = "touid")
    public String getToUid() {
        return mToUid;
    }

    @JSONField(name = "touid")
    public void setToUid(String toUid) {
        mToUid = toUid;
    }

    @JSONField(name = "dynamicid")
    public String getDynamicId() {
        return mDynamicId;
    }

    @JSONField(name = "dynamicid")
    public void setDynamicId(String dynamicId) {
        mDynamicId = dynamicId;
    }

    @JSONField(name = "commentid")
    public String getCommentId() {
        return mCommentId;
    }

    @JSONField(name = "commentid")
    public void setCommentId(String commentId) {
        mCommentId = commentId;
    }

    @JSONField(name = "parentid")
    public String getParentId() {
        return mParentId;
    }

    @JSONField(name = "parentid")
    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    @JSONField(name = "content")
    public String getContent() {
        if (!mParentNode && this.mToUserBean != null && !TextUtils.isEmpty(mToUserBean.getId())) {
            String userName = mToUserBean.getUserNiceName();
            if (!TextUtils.isEmpty(userName)) {
                return REPLY + userName + " : " + mContent;
            }
        }
        return mContent;
    }

    @JSONField(name = "content")
    public void setContent(String content) {
        mContent = content;
    }


    @JSONField(name = "likes")
    public String getLikeNum() {
        return mLikeNum;
    }

    @JSONField(name = "likes")
    public void setLikeNum(String likeNum) {
        mLikeNum = likeNum;
    }

    @JSONField(name = "addtime")
    public String getAddTime() {
        return mAddTime;
    }

    @JSONField(name = "addtime")
    public void setAddTime(String addTime) {
        mAddTime = addTime;
    }

    @JSONField(name = "userinfo")
    public UserBean getUserBean() {
        return mUserBean;
    }

    @JSONField(name = "userinfo")
    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }

    @JSONField(name = "datetime")
    public String getDatetime() {
        return mDatetime;
    }

    @JSONField(name = "datetime")
    public void setDatetime(String datetime) {
        mDatetime = datetime;
    }

    @JSONField(name = "islike")
    public int getIsLike() {
        return mIsLike;
    }

    @JSONField(name = "islike")
    public void setIsLike(int like) {
        mIsLike = like;
    }

    @JSONField(name = "at_info")
    public String getAtInfo() {
        return mAtInfo;
    }

    @JSONField(name = "at_info")
    public void setAtInfo(String atInfo) {
        mAtInfo = atInfo;
    }


    @JSONField(name = "touserinfo")
    public UserBean getToUserBean() {
        return mToUserBean;
    }

    @JSONField(name = "touserinfo")
    public void setToUserBean(UserBean toUserBean) {
        mToUserBean = toUserBean;
    }

    @JSONField(name = "replys")
    public int getReplyNum() {
        return mReplyNum;
    }

    @JSONField(name = "replys")
    public void setReplyNum(int replyNum) {
        mReplyNum = replyNum;
    }


    @JSONField(name = "replylist")
    public List<DynamicCommentBean> getChildList() {
        return mChildList;
    }

    @JSONField(name = "replylist")
    public void setChildList(List<DynamicCommentBean> childList) {
        mChildList = childList;
        for (DynamicCommentBean bean : childList) {
            if (bean != null) {
                bean.setParentNodeBean(this);
            }
        }
    }
    @JSONField(name = "type")
    public int getType() {
        return mType;
    }

    @JSONField(name = "type")
    public void setType(int type) {
        mType = type;
    }

    @JSONField(name = "voice")
    public String getVoiceLink() {
        return mVoiceLink;
    }

    @JSONField(name = "voice")
    public void setVoiceLink(String voiceLink) {
        mVoiceLink = voiceLink;
    }

    @JSONField(name = "length")
    public String getVoiceDuration() {
        return mVoiceDuration;
    }

    @JSONField(name = "length")
    public void setVoiceDuration(String voiceDuration) {
        mVoiceDuration = voiceDuration;
    }
    public boolean isVoice() {
        return mType == 1;
    }

    public boolean isVoicePlaying() {
        return mVoicePlaying;
    }


    public void setVoicePlaying(boolean voicePlaying) {
        mVoicePlaying = voicePlaying;
    }



    public DynamicCommentBean getParentNodeBean() {
        return mParentNodeBean;
    }

    public void setParentNodeBean(DynamicCommentBean parentNodeBean) {
        mParentNodeBean = parentNodeBean;
    }

    public boolean isParentNode() {
        return mParentNode;
    }

    public void setParentNode(boolean parentNode) {
        mParentNode = parentNode;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @JSONField(serialize = false)
    public int getChildPage() {
        return mChildPage;
    }
    @JSONField(serialize = false)
    public void setChildPage(int childPage) {
        mChildPage = childPage;
    }

    public boolean isFirstChild(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null && parentChildList.size() > 0) {
                return this == parentChildList.get(0);
            }
        }
        return false;
    }


    public boolean needShowExpand(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                if (parentNodeBean.getReplyNum() > 1 && parentNodeBean.getReplyNum() > size && this == parentChildList.get(size - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean needShowCollapsed(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                if (size > 1 && size >= parentNodeBean.getReplyNum() && this == parentChildList.get(size - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeChild() {
        if (mChildList != null && mChildList.size() > 1) {
            mChildList = mChildList.subList(0, 1);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mToUid);
        dest.writeString(mDynamicId);
        dest.writeString(mCommentId);
        dest.writeString(mParentId);
        dest.writeString(mContent);
        dest.writeString(mLikeNum);
        dest.writeString(mAddTime);
        dest.writeString(mAtInfo);
        dest.writeInt(mIsLike);
        dest.writeInt(mReplyNum);
        dest.writeString(mDatetime);
        dest.writeParcelable(mUserBean, flags);
        dest.writeParcelable(mToUserBean, flags);
        dest.writeTypedList(mChildList);
        dest.writeByte((byte) (mParentNode ? 1 : 0));
        dest.writeInt(mPosition);
        dest.writeInt(mType);
        dest.writeString(mVoiceLink);
        dest.writeString(mVoiceDuration);
    }

    public DynamicCommentBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mToUid = in.readString();
        mDynamicId = in.readString();
        mCommentId = in.readString();
        mParentId = in.readString();
        mContent = in.readString();
        mLikeNum = in.readString();
        mAddTime = in.readString();
        mAtInfo = in.readString();
        mIsLike = in.readInt();
        mReplyNum = in.readInt();
        mDatetime = in.readString();
        mUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mToUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mChildList = in.createTypedArrayList(DynamicCommentBean.CREATOR);
        mParentNode = in.readByte() != 0;
        mPosition = in.readInt();
        mType = in.readInt();
        mVoiceLink=in.readString();
        mVoiceDuration=in.readString();
    }

    public static final Creator<DynamicCommentBean> CREATOR = new Creator<DynamicCommentBean>() {
        @Override
        public DynamicCommentBean createFromParcel(Parcel in) {
            return new DynamicCommentBean(in);
        }

        @Override
        public DynamicCommentBean[] newArray(int size) {
            return new DynamicCommentBean[size];
        }
    };
}