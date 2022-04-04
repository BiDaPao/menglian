package com.aihuan.dynamic.event;

/**
 * Created by debug on 2019/8/13.
 */

public class DynamicTabChangeEvent {
    private int mType;

    public DynamicTabChangeEvent(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
