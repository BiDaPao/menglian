package com.aihuan.im.event;

/**
 * Created by cxf on 2018/10/24.
 */

public class ImUnReadIntimacyCountEvent {

    public int mUnReadCount;

    public ImUnReadIntimacyCountEvent(int unReadCount) {
        mUnReadCount = unReadCount;
    }

    public int getUnReadCount() {
        return mUnReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        mUnReadCount = unReadCount;
    }
}
