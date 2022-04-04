package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/17.
 */

public class GiftCabBean {

    private String mId;
    private String mNum;
    private String mThumb;
    private String mName;

    public GiftCabBean() {
    }

    @JSONField(name = "actionid")
    public String getId() {
        return mId;
    }
    @JSONField(name = "actionid")
    public void setId(String id) {
        mId = id;
    }
    @JSONField(name = "total_nums")
    public String getNum() {
        return mNum;
    }
    @JSONField(name = "total_nums")
    public void setNum(String num) {
        mNum = num;
    }
    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }
    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }
    @JSONField(name = "name")
    public String getName() {
        return mName;
    }
    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }
}
