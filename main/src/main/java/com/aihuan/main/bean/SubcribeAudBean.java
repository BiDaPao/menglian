package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/23.
 */

public class SubcribeAudBean {
    private String mUid;
    private String mSubscribeId;
    private String mUserNiceName;
    private String mAvatar;
    private String mAvatarThumb;
    private int mLevelAnchor;
    private int mIsvip;//是否是vip

    public SubcribeAudBean() {
    }

    @JSONField(name = "id")
    public String getUid() {
        return mUid;
    }

    @JSONField(name = "id")
    public void setUid(String uid) {
        mUid = uid;
    }

    @JSONField(name = "subscribeid")
    public String getSubscribeId() {
        return mSubscribeId;
    }

    @JSONField(name = "subscribeid")
    public void setSubscribeId(String subscribeId) {
        mSubscribeId = subscribeId;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return mUserNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        mUserNiceName = userNiceName;
    }

    @JSONField(name = "avatar")
    public String getAvatar() {
        return mAvatar;
    }

    @JSONField(name = "avatar")
    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return mAvatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        mAvatarThumb = avatarThumb;
    }

    @JSONField(name = "level_anchor")
    public int getLevelAnchor() {
        return mLevelAnchor;
    }

    @JSONField(name = "level_anchor")
    public void setLevelAnchor(int levelAnchor) {
        mLevelAnchor = levelAnchor;
    }

    @JSONField(name = "isvip")
    public int getIsvip() {
        return mIsvip;
    }

    @JSONField(name = "isvip")
    public void setIsvip(int isvip) {
        mIsvip = isvip;
    }


    public boolean isVip() {
        return mIsvip == 1;
    }
}
