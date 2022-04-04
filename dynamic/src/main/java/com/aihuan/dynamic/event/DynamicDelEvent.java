package com.aihuan.dynamic.event;

/**
 * Created by debug on 2019/7/25.
 */

public class DynamicDelEvent {
    private String mDynamicId;

    public DynamicDelEvent(String dynamicId) {
        mDynamicId = dynamicId;
    }

    public String getDynamicId() {
        return mDynamicId;
    }
}
