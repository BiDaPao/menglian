package com.aihuan.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/5/13.
 * 背景墙实体类
 */

public class WallBean implements Parcelable {

    private String mId;
    private String mUid;
    private String mThumb;
    private String mHref;
    private int mType;
    private boolean mAdd;

    public WallBean(){}

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

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "href")
    public String getHref() {
        return mHref;
    }

    @JSONField(name = "href")
    public void setHref(String href) {
        mHref = href;
    }

    @JSONField(name = "type")
    public int getType() {
        return mType;
    }

    @JSONField(name = "type")
    public void setType(int type) {
        mType = type;
    }

    @JSONField(serialize = false)
    public boolean isAdd() {
        return mAdd;
    }

    @JSONField(serialize = false)
    public void setAdd(boolean add) {
        mAdd = add;
    }

    public boolean isVideo() {
        return mType == 1;
    }

    public WallBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mThumb = in.readString();
        mHref = in.readString();
        mType = in.readInt();
        mAdd = in.readByte() != 0;
    }

    public static final Creator<WallBean> CREATOR = new Creator<WallBean>() {
        @Override
        public WallBean createFromParcel(Parcel in) {
            return new WallBean(in);
        }

        @Override
        public WallBean[] newArray(int size) {
            return new WallBean[size];
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
        dest.writeString(mThumb);
        dest.writeString(mHref);
        dest.writeInt(mType);
        dest.writeByte((byte) (mAdd ? 1 : 0));
    }
}
