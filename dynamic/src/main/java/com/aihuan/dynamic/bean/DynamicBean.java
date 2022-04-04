package com.aihuan.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.aihuan.common.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debug on 2019/7/24.
 */

public class DynamicBean implements Parcelable{

    /**
     * id : 6
     * uid : 100190
     * title : 文字跟图片
     * thumb : http://qiniuplay.yunbaozb.com/android_23016_20190718_171350_374722.jpg;http://qiniuplay.yunbaozb.com/android_23016_20190718_171350_6554933.jpg
     * video_thumb :
     * href :
     * voice :
     * length : 0
     * likes : 0
     * comments : 0
     * type : 1
     * isdel : 0
     * status : 1
     * uptime : 1563861840
     * xiajia_reason :
     * lat :
     * lng :
     * city :
     * addtime : 1563861840
     * thumbs : ["http://qiniuplay.yunbaozb.com/android_23016_20190718_171350_374722.jpg","http://qiniuplay.yunbaozb.com/android_23016_20190718_171350_6554933.jpg"]
     * islike : 0
     * userinfo : {"id":"100190","user_nickname":"郑星星","avatar":"http://live1v1qiniu.yunbaozb.com/android_100190_20190514_173213_2831794?imageView2/2/w/600/h/600","avatar_thumb":"http://live1v1qiniu.yunbaozb.com/android_100190_20190514_173213_2831794?imageView2/2/w/200/h/200"}
     */

    private String id;
    private String uid;
    private String title;
    private String video_thumb;
    private String href;
    private String voice;
    private int length;
    private String likes;
    private String comments;
    private int type;
    private String isdel;
    private String status;
    private String uptime;
    private String xiajia_reason;
    private String lat;
    private String lng;
    private String city;
    private String addtime;
    private int islike;
    private UserBean userinfo;
    private ArrayList<String> thumbs;
    private String datetime;

    private boolean mVoicePlaying;
    private int mPosition;

    public DynamicBean() {
    }

    protected DynamicBean(Parcel in) {
        id = in.readString();
        uid = in.readString();
        title = in.readString();
        video_thumb = in.readString();
        href = in.readString();
        voice = in.readString();
        length = in.readInt();
        likes = in.readString();
        comments = in.readString();
        type = in.readInt();
        isdel = in.readString();
        status = in.readString();
        uptime = in.readString();
        xiajia_reason = in.readString();
        lat = in.readString();
        lng = in.readString();
        city = in.readString();
        addtime = in.readString();
        islike = in.readInt();
        userinfo = in.readParcelable(UserBean.class.getClassLoader());
        thumbs = in.createStringArrayList();
        datetime=in.readString();
    }

    public static final Creator<DynamicBean> CREATOR = new Creator<DynamicBean>() {
        @Override
        public DynamicBean createFromParcel(Parcel in) {
            return new DynamicBean(in);
        }

        @Override
        public DynamicBean[] newArray(int size) {
            return new DynamicBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getXiajia_reason() {
        return xiajia_reason;
    }

    public void setXiajia_reason(String xiajia_reason) {
        this.xiajia_reason = xiajia_reason;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }

    public ArrayList<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(ArrayList<String> thumbs) {
        this.thumbs = thumbs;
    }

    public boolean isVoicePlaying() {
        return mVoicePlaying;
    }

    public void setVoicePlaying(boolean voicePlaying) {
        mVoicePlaying = voicePlaying;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(video_thumb);
        dest.writeString(href);
        dest.writeString(voice);
        dest.writeInt(length);
        dest.writeString(likes);
        dest.writeString(comments);
        dest.writeInt(type);
        dest.writeString(isdel);
        dest.writeString(status);
        dest.writeString(uptime);
        dest.writeString(xiajia_reason);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(city);
        dest.writeString(addtime);
        dest.writeInt(islike);
        dest.writeParcelable(userinfo, flags);
        dest.writeStringList(thumbs);
        dest.writeString(datetime);
    }
}
