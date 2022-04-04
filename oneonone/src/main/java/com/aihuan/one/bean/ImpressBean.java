package com.aihuan.one.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/10/15.
 * 印象实体类
 */

public class ImpressBean {

    private int id;
    private String name;
    private String color;
    private boolean mChecked;
    private String mCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "colour")
    public String getColor() {
        return color;
    }

    @JSONField(name = "colour")
    public void setColor(String color) {
        this.color = color;
    }
    @JSONField(name = "nums")
    public String getCount() {
        return mCount;
    }
    @JSONField(name = "nums")
    public void setCount(String count) {
        mCount = count;
    }

    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }

    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
