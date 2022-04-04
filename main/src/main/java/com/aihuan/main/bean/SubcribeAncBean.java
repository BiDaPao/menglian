package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/23.
 */

public class SubcribeAncBean {

    private String mUid;
    private String mSubscribeId;
    private String mUserNiceName;
    private String mAvatar;
    private String mAvatarThumb;
    private int mLevel;
    private String mCoin;
    private int mIsvip;//是否是vip

    public SubcribeAncBean() {
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

    @JSONField(name = "level")
    public int getLevel() {
        return mLevel;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        mLevel = level;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
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
