package com.aihuan.one.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/9.
 */

public class ChatLiveBean implements Parcelable {

    private String uid;
    private String avatar;
    private String avatarThumb;
    private String userNiceName;
    private int sex;
    private String thumb;
    private int levelAnchor;
    private String distance;
    private String signature;
    private int onLineStatus;
    private int openVoice;
    private int openVideo;
    private int openDisturb;
    private String priceVideo;
    private String priceVoice;
    private int age;
    private int height;
    private String city;
    private int isAuth;

    public ChatLiveBean() {

    }

    @JSONField(name = "is_auth")
    public int getIsAuth() {
        return isAuth;
    }

    @JSONField(name = "is_auth")
    public void setIsAuth(int isAuth) {
        this.isAuth = isAuth;
    }

    @JSONField(name = "age")
    public int getAge() {
        return age;
    }

    @JSONField(name = "age")
    public void setAge(int age) {
        this.age = age;
    }

    @JSONField(name = "height")
    public int getHeight() {
        return height;
    }

    @JSONField(name = "height")
    public void setHeight(int height) {
        this.height = height;
    }

    @JSONField(name = "city")
    public String getCity() {
        return city;
    }

    @JSONField(name = "city")
    public void setCity(String city) {
        this.city = city;
    }

    @JSONField(name = "id")
    public String getUid() {
        return uid;
    }

    @JSONField(name = "id")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JSONField(name = "avatar")
    public String getAvatar() {
        return avatar;
    }

    @JSONField(name = "avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    @JSONField(name = "sex")
    public int getSex() {
        return sex;
    }

    @JSONField(name = "sex")
    public void setSex(int sex) {
        this.sex = sex;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return thumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @JSONField(name = "level_anchor")
    public int getLevelAnchor() {
        return levelAnchor;
    }

    @JSONField(name = "level_anchor")
    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    @JSONField(name = "distance")
    public String getDistance() {
        return distance;
    }

    @JSONField(name = "distance")
    public void setDistance(String distance) {
        this.distance = distance;
    }

    @JSONField(name = "signature")
    public String getSignature() {
        return signature;
    }

    @JSONField(name = "signature")
    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JSONField(name = "online")
    public int getOnLineStatus() {
        return onLineStatus;
    }

    @JSONField(name = "online")
    public void setOnLineStatus(int onLineStatus) {
        this.onLineStatus = onLineStatus;
    }

    @JSONField(name = "isvoice")
    public int getOpenVoice() {
        return openVoice;
    }

    @JSONField(name = "isvoice")
    public void setOpenVoice(int openVoice) {
        this.openVoice = openVoice;
    }

    @JSONField(name = "isvideo")
    public int getOpenVideo() {
        return openVideo;
    }

    @JSONField(name = "isvideo")
    public void setOpenVideo(int openVideo) {
        this.openVideo = openVideo;
    }

    @JSONField(name = "isdisturb")
    public int getOpenDisturb() {
        return openDisturb;
    }

    @JSONField(name = "isdisturb")
    public void setOpenDisturb(int openDisturb) {
        this.openDisturb = openDisturb;
    }

    @JSONField(name = "video_value")
    public String getPriceVideo() {
        return priceVideo;
    }

    @JSONField(name = "video_value")
    public void setPriceVideo(String priceVideo) {
        this.priceVideo = priceVideo;
    }

    @JSONField(name = "voice_value")
    public String getPriceVoice() {
        return priceVoice;
    }

    @JSONField(name = "voice_value")
    public void setPriceVoice(String priceVoice) {
        this.priceVoice = priceVoice;
    }

    public boolean isOpenVideo() {
        return this.openVideo == 1;
    }

    public boolean isOpenVoice() {
        return this.openVoice == 1;
    }

    public boolean isAuth() {
        return this.isAuth == 1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(avatar);
        dest.writeString(avatarThumb);
        dest.writeString(userNiceName);
        dest.writeInt(sex);
        dest.writeString(thumb);
        dest.writeInt(levelAnchor);
        dest.writeString(distance);
        dest.writeString(signature);
        dest.writeInt(onLineStatus);
        dest.writeInt(openVoice);
        dest.writeInt(openVideo);
        dest.writeInt(openDisturb);
        dest.writeString(priceVideo);
        dest.writeString(priceVoice);
        dest.writeInt(age);
        dest.writeInt(height);
        dest.writeString(city);
        dest.writeInt(isAuth);
    }

    public ChatLiveBean(Parcel in) {
        uid = in.readString();
        avatar = in.readString();
        avatarThumb = in.readString();
        userNiceName = in.readString();
        sex = in.readInt();
        thumb = in.readString();
        levelAnchor = in.readInt();
        distance = in.readString();
        signature = in.readString();
        onLineStatus = in.readInt();
        openVoice = in.readInt();
        openVideo = in.readInt();
        openDisturb = in.readInt();
        priceVideo = in.readString();
        priceVoice = in.readString();
        age = in.readInt();
        height = in.readInt();
        city = in.readString();
        isAuth = in.readInt();
    }

    public static final Creator<ChatLiveBean> CREATOR = new Creator<ChatLiveBean>() {
        @Override
        public ChatLiveBean createFromParcel(Parcel in) {
            return new ChatLiveBean(in);
        }

        @Override
        public ChatLiveBean[] newArray(int size) {
            return new ChatLiveBean[size];
        }
    };
}
