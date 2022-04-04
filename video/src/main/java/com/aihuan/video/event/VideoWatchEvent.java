package com.aihuan.video.event;

/**
 * Created by cxf on 2019/5/9.
 */

public class VideoWatchEvent {

    private String mVideoId;
    private String mWatchNum;

    public VideoWatchEvent(String videoId, String watchNum) {
        mVideoId = videoId;
        mWatchNum = watchNum;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getWatchNum() {
        return mWatchNum;
    }

    public void setWatchNum(String watchNum) {
        mWatchNum = watchNum;
    }
}
