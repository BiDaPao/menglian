package com.aihuan.doc.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by cxf on 2019/3/9.
 */

public class Node {

    private int mLevel;//层级
    private int mIndex;//索引
    private String mName;
    private String mDes;
    private List<Node> mChildList;
    private boolean mExpand;

    @JSONField(name = "level")
    public int getLevel() {
        return mLevel;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        mLevel = level;
    }

    @JSONField(serialize = false)
    public int getIndex() {
        return mIndex;
    }

    @JSONField(serialize = false)
    public void setIndex(int index) {
        mIndex = index;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(name = "des")
    public String getDes() {
        return mDes;
    }

    @JSONField(name = "des")
    public void setDes(String des) {
        mDes = des;
    }

    @JSONField(name = "child")
    public List<Node> getChildList() {
        return mChildList;
    }

    @JSONField(name = "child")
    public void setChildList(List<Node> childList) {
        mChildList = childList;
    }

    @JSONField(serialize = false)
    public boolean isExpand() {
        return mExpand;
    }

    @JSONField(serialize = false)
    public void setExpand(boolean expand) {
        mExpand = expand;
    }

    public boolean hasChildren() {
        return mChildList != null && mChildList.size() > 0;
    }
}
