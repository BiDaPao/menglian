package com.aihuan.common.event;

/**
 * Created by cxf on 2019/4/19.
 */

public class ImMsgEvent {

    public ImMsgEvent(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    private long mTimeStamp;

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }
}
