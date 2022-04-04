package com.aihuan.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/5/10.
 */

public class PhotoBean implements Parcelable {

    private String mId;
    private String mUid;
    private String mTitle;
    private String mThumb;
    private String mViews;
    private int mIsprivate;
    private String mCoin;
    private int mStatus;
    private int mCansee;
    private boolean mChecked;

    public PhotoBean() {

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

    @JSONField(name = "title")
    public String getTitle() {
        return mTitle;
    }

    @JSONField(name = "title")
    public void setTitle(String title) {
        mTitle = title;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "views")
    public String getViews() {
        return mViews;
    }

    @JSONField(name = "views")
    public void setViews(String views) {
        mViews = views;
    }

    @JSONField(name = "isprivate")
    public int getIsprivate() {
        return mIsprivate;
    }

    @JSONField(name = "isprivate")
    public void setIsprivate(int isprivate) {
        mIsprivate = isprivate;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
    }

    @JSONField(name = "status")
    public int getStatus() {
        return mStatus;
    }

    @JSONField(name = "status")
    public void setStatus(int status) {
        mStatus = status;
    }

    @JSONField(name = "cansee")
    public int getCansee() {
        return mCansee;
    }

    @JSONField(name = "cansee")
    public void setCansee(int cansee) {
        mCansee = cansee;
    }

    public boolean isCanSee() {
        return mCansee == 1;
    }

    public boolean isPrivate() {
        return mIsprivate == 1;
    }

    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }
    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public PhotoBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mTitle = in.readString();
        mThumb = in.readString();
        mViews = in.readString();
        mIsprivate = in.readInt();
        mCoin = in.readString();
        mStatus = in.readInt();
        mCansee = in.readInt();
    }

    public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel in) {
            return new PhotoBean(in);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mTitle);
        dest.writeString(mThumb);
        dest.writeString(mViews);
        dest.writeInt(mIsprivate);
        dest.writeString(mCoin);
        dest.writeInt(mStatus);
        dest.writeInt(mCansee);
    }
}
