package com.aihuan.main.bean;

/**
 * Created by cxf on 2019/5/11.
 */

public class VipItemBean {

    private int mIcon;
    private int mTip1;
    private int mTip2;

    public VipItemBean(int icon, int tip1, int tip2) {
        mIcon = icon;
        mTip1 = tip1;
        mTip2 = tip2;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public int getTip1() {
        return mTip1;
    }

    public void setTip1(int tip1) {
        mTip1 = tip1;
    }

    public int getTip2() {
        return mTip2;
    }

    public void setTip2(int tip2) {
        mTip2 = tip2;
    }
}
