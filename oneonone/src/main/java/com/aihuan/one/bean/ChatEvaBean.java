package com.aihuan.one.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/21.
 */

public class ChatEvaBean extends ImpressBean {

    private boolean mGood;//是否是好评
    private String mTitleText;//标题
    private boolean mTitle;

    @JSONField(serialize = false)
    public boolean isGood() {
        return mGood;
    }

    @JSONField(serialize = false)
    public void setGood(boolean good) {
        mGood = good;
    }

    @JSONField(serialize = false)
    public String getTitleText() {
        return mTitleText;
    }

    @JSONField(serialize = false)
    public void setTitleText(String titleText) {
        mTitleText = titleText;
    }

    @JSONField(serialize = false)
    public boolean isTitle() {
        return mTitle;
    }

    @JSONField(serialize = false)
    public void setTitle(boolean title) {
        mTitle = title;
    }
}
