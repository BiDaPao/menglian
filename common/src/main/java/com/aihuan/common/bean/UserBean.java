package com.aihuan.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/14.
 */

public class UserBean implements Parcelable {


    protected String id;
    protected String userNiceName;
    protected String avatar;
    protected String avatarThumb;
    protected int sex;
    protected String signature;
    protected String coin;
    protected String votes;
    protected String votesTotal;
    protected String birthday;
    protected int level;
    protected int levelAnchor;
    protected int follows;
    protected int fans;
    protected int auth;//认证状态，0未认证，1认证中，2认证成功，3认证失败
    protected int hasAuth;//是否认证过
    protected int onLineStatus;
    protected int isvoice;//是否开启语音
    protected int isvideo;//是否开启视频
    protected String videoPrice;//视频价格
    protected String voicePrice;//语音价格
    protected int isdisturb;//勿扰 是否开启 1开启 0关闭
    protected int isvip;//是否是vip

    protected int attent;//是否关注 1 关注 0未关注
    protected int attent2;//是否关注 1 关注 0未关注
    protected int isblack;//是否拉黑


    protected int isUserauth;


    public boolean isAuth() {
        return this.auth == 1;
    }



    /**
     * 解释一下，上面两行
     * 在服务端的某些接口里面，关注用 isattent 表示 有些用 u2t 表示，
     * 服务端就要这样弄，没办法，客户端只能这样写
     */

    //搭讪语
    protected String accost_text;

    public String getAccost_text() {
        return accost_text;
    }

    public void setAccost_text(String accost_text) {
        this.accost_text = accost_text;
    }

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    //用户备注，用于IM聊天给对方的备注
    private String toremark;

    public String getToremark() {
        return toremark;
    }

    public void setToremark(String toremark) {
        this.toremark = toremark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    @JSONField(name = "avatar")
    public String getAvatar() {
        return avatar;
    }

    @JSONField(name = "avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return avatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    @JSONField(name = "sex")
    public int getSex() {
        return sex;
    }

    @JSONField(name = "sex")
    public void setSex(int sex) {
        this.sex = sex;
    }

    @JSONField(name = "signature")
    public String getSignature() {
        return signature;
    }

    @JSONField(name = "signature")
    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return coin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        this.coin = coin;
    }

    @JSONField(name = "votes")
    public String getVotes() {
        return votes;
    }

    @JSONField(name = "votes")
    public void setVotes(String votes) {
        this.votes = votes;
    }

    @JSONField(name = "votestotal")
    public String getVotesTotal() {
        return votesTotal;
    }

    @JSONField(name = "votestotal")
    public void setVotesTotal(String votesTotal) {
        this.votesTotal = votesTotal;
    }

    @JSONField(name = "birthday")
    public String getBirthday() {
        return birthday;
    }

    @JSONField(name = "birthday")
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @JSONField(name = "level")
    public int getLevel() {
        if (level == 0) {
            level = 1;
        }
        return level;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(name = "level_anchor")
    public int getLevelAnchor() {
        if (levelAnchor == 0) {
            levelAnchor = 1;
        }
        return levelAnchor;
    }

    @JSONField(name = "level_anchor")
    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    @JSONField(name = "follows")
    public int getFollows() {
        return follows;
    }

    @JSONField(name = "follows")
    public void setFollows(int follows) {
        this.follows = follows;
    }

    @JSONField(name = "fans")
    public int getFans() {
        return fans;
    }

    @JSONField(name = "fans")
    public void setFans(int fans) {
        this.fans = fans;
    }

    @JSONField(name = "isauth")
    public int getAuth() {
        return this.auth;
    }

    @JSONField(name = "isauth")
    public void setAuth(int isAuth) {
        this.auth = isAuth;
    }

    @JSONField(name = "oldauth")
    public int getHasAuth() {
        return hasAuth;
    }

    @JSONField(name = "oldauth")
    public void setHasAuth(int hasAuth) {
        this.hasAuth = hasAuth;
    }

    @JSONField(name = "u2t")
    public int getAttent() {
        return attent;
    }

    @JSONField(name = "u2t")
    public void setAttent(int attent) {
        this.attent = attent;
    }

    @JSONField(name = "isattent")
    public int getAttent2() {
        return attent2;
    }

    @JSONField(name = "isattent")
    public void setAttent2(int attent2) {
        this.attent2 = attent2;
    }

    @JSONField(name = "online")
    public int getOnLineStatus() {
        return onLineStatus;
    }

    @JSONField(name = "online")
    public void setOnLineStatus(int onLineStatus) {
        this.onLineStatus = onLineStatus;
    }

    @JSONField(name = "video_value")
    public String getVideoPrice() {
        return videoPrice;
    }

    @JSONField(name = "video_value")
    public void setVideoPrice(String videoPrice) {
        this.videoPrice = videoPrice;
    }

    @JSONField(name = "voice_value")
    public String getVoicePrice() {
        return voicePrice;
    }

    @JSONField(name = "voice_value")
    public void setVoicePrice(String voicePrice) {
        this.voicePrice = voicePrice;
    }

    @JSONField(name = "isdisturb")
    public int getIsdisturb() {
        return isdisturb;
    }

    @JSONField(name = "isdisturb")
    public void setIsdisturb(int isdisturb) {
        this.isdisturb = isdisturb;
    }

    @JSONField(name = "isvip")
    public int getIsvip() {
        return isvip;
    }

    @JSONField(name = "isvip")
    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }


    public int getIsUserauth() {
        return isUserauth;
    }

    public void setIsUserauth(int isUserauth) {
        this.isUserauth = isUserauth;
    }

    public int getIsblack() {
        return isblack;
    }

    public void setIsblack(int isblack) {
        this.isblack = isblack;
    }

    public int getIsvoice() {
        return isvoice;
    }

    public void setIsvoice(int isvoice) {
        this.isvoice = isvoice;
    }

    public int getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(int isvideo) {
        this.isvideo = isvideo;
    }

    public boolean hasAuth() {
        return this.hasAuth == 1;
    }

    public boolean isFollowing() {
        return this.attent == 1 || this.attent2 == 1;
    }

    public boolean isBlacking() {
        return this.isblack == 1;
    }


    public boolean openVideo() {
        return isvideo == 1;
    }

    public boolean openVoice() {
        return isvoice == 1;
    }

    public boolean openDisturb() {
        return isdisturb == 1;
    }

    public boolean isVip() {
        return this.isvip == 1;
    }

    public boolean isAlAuth() {
        return this.isUserauth == 1;
    }


    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.id = in.readString();
        this.userNiceName = in.readString();
        this.avatar = in.readString();
        this.avatarThumb = in.readString();
        this.sex = in.readInt();
        this.signature = in.readString();
        this.coin = in.readString();
        this.votes = in.readString();
        this.votesTotal = in.readString();
        this.birthday = in.readString();
        this.level = in.readInt();
        this.follows = in.readInt();
        this.fans = in.readInt();
        this.auth = in.readInt();
        this.hasAuth = in.readInt();
        this.attent = in.readInt();
        this.attent2 = in.readInt();
        this.onLineStatus = in.readInt();
        this.isvideo = in.readInt();
        this.isvoice = in.readInt();
        this.isdisturb = in.readInt();
        this.videoPrice = in.readString();
        this.voicePrice = in.readString();
        this.city = in.readString();
        this.isvip = in.readInt();
        this.isblack = in.readInt();
        this.isUserauth = in.readInt();
        this.accost_text = in.readString();
        this.toremark = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userNiceName);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarThumb);
        dest.writeInt(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.coin);
        dest.writeString(this.votes);
        dest.writeString(this.votesTotal);
        dest.writeString(this.birthday);
        dest.writeInt(this.level);
        dest.writeInt(this.follows);
        dest.writeInt(this.fans);
        dest.writeInt(this.auth);
        dest.writeInt(this.hasAuth);
        dest.writeInt(this.attent);
        dest.writeInt(this.attent2);
        dest.writeInt(this.onLineStatus);
        dest.writeInt(this.isvideo);
        dest.writeInt(this.isvoice);
        dest.writeInt(this.isdisturb);
        dest.writeString(this.videoPrice);
        dest.writeString(this.voicePrice);
        dest.writeString(this.city);
        dest.writeInt(this.isvip);
        dest.writeInt(this.isblack);
        dest.writeInt(this.isUserauth);
        dest.writeString(this.accost_text);
        dest.writeString(this.toremark);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }
    };


}
