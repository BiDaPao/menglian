package com.aihuan.common.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/22.
 * 收到送礼物消息的实体类
 */

public class ChatReceiveGiftBean {


    private String uid;
    private String avatar;
    private String userNiceName;
    private int level;
    private String giftId;
    private int giftCount;
    private String giftName;
    private String giftIcon;
    private int lianCount = 1;
    private int gif;//是否是gif礼物  1是 0不是
    private int gitType;//豪华礼物类型 0是gif  1是svga
    private String gifUrl;
    private String mKey;
    private String sessionId;
    //男用户送出金币字段
    private int coinNumber;
    //女用户收到甜蜜字段
    private int sweetNumber;

    @JSONField(name = "coin_number")
    public int getCoinNumber() {
        return coinNumber;
    }

    @JSONField(name = "coin_number")
    public void setCoinNumber(int coinNumber) {
        this.coinNumber = coinNumber;
    }

    @JSONField(name = "sweet_number")
    public int getSweetNumber() {
        return sweetNumber;
    }

    @JSONField(name = "sweet_number")
    public void setSweetNumber(int sweetNumber) {
        this.sweetNumber = sweetNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    @JSONField(name = "level")
    public int getLevel() {
        return level;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(name = "giftid")
    public String getGiftId() {
        return giftId;
    }

    @JSONField(name = "giftid")
    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    @JSONField(name = "giftcount")
    public int getGiftCount() {
        return giftCount;
    }

    @JSONField(name = "giftcount")
    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    @JSONField(name = "giftname")
    public String getGiftName() {
        return giftName;
    }

    @JSONField(name = "giftname")
    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    @JSONField(name = "gifticon")
    public String getGiftIcon() {
        return giftIcon;
    }

    @JSONField(name = "gifticon")
    public void setGiftIcon(String giftIcon) {
        this.giftIcon = giftIcon;
    }


    @JSONField(name = "type")
    public int getGif() {
        return gif;
    }

    @JSONField(name = "type")
    public void setGif(int gif) {
        this.gif = gif;
    }

    @JSONField(name = "swf")
    public String getGifUrl() {
        return gifUrl;
    }

    @JSONField(name = "swf")
    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    @JSONField(name = "swftype")
    public int getGitType() {
        return gitType;
    }

    @JSONField(name = "swftype")
    public void setGitType(int gitType) {
        this.gitType = gitType;
    }

    @JSONField(name = "showid")
    public String getSessionId() {
        return sessionId;
    }

    @JSONField(name = "showid")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getLianCount() {
        return lianCount;
    }

    public void setLianCount(int lianCount) {
        this.lianCount = lianCount;
    }

    public String getKey() {
        if (TextUtils.isEmpty(mKey)) {
            mKey = this.uid + this.giftId + this.giftCount;
        }
        return mKey;
    }
}
