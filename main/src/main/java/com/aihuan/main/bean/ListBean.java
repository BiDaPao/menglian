package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/2/2.
 * 排行榜实体类
 */

public class ListBean {

    private long totalCoin;
    private String uid;
    private String userNiceName;
    private String avatarThumb;
    private int sex;
    private int levelAnchor;
    private int level;
    private int attention;
    private int isAuth;

    @JSONField(name = "totalcoin")
    public long getTotalCoin() {
        return totalCoin;
    }

    @JSONField(name = "totalcoin")
    public void setTotalCoin(long totalCoin) {
        this.totalCoin = totalCoin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return avatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public int getLevelAnchor() {
        return levelAnchor;
    }

    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(name = "isAttention")
    public int getAttention() {
        return attention;
    }

    @JSONField(name = "isAttention")
    public void setAttention(int attention) {
        this.attention = attention;
    }

    public int getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(int isAuth) {
        this.isAuth = isAuth;
    }

    public String getTotalCoinFormat() {
        //return StringUtil.toWan(this.totalCoin);
        return String.valueOf(this.totalCoin);
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
