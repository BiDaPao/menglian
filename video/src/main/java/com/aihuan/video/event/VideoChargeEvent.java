package com.aihuan.video.event;

/**
 * Created by cxf on 2019/5/9.
 */

public class VideoChargeEvent {

    private String mVideoId;
    private String mHref;

    public VideoChargeEvent(String videoId,String href) {
        mVideoId = videoId;
        mHref=href;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getHref() {
        return mHref;
    }

    public void setHref(String href) {
        mHref = href;
    }
}
