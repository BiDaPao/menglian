package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.aihuan.one.bean.ImpressBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/21.
 */

public class UserEvaBean {
    private String mUid;
    private String mUserNiceName;
    private String mAvatar;
    private List<ImpressBean> mEvaList;

    public UserEvaBean() {
    }

    @JSONField(name = "uid")
    public String getUid() {
        return mUid;
    }

    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
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

    @JSONField(name = "label")
    public List<ImpressBean> getEvaList() {
        return mEvaList;
    }

    @JSONField(name = "label")
    public void setEvaList(List<ImpressBean> evaList) {
        mEvaList = evaList;
    }
}
