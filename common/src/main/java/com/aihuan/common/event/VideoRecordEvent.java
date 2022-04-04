package com.aihuan.common.event;

/**
 * Created by debug on 2019/7/15.
 */

public class VideoRecordEvent {
    private String mVideoPath;

    public VideoRecordEvent(String videoPath) {
        mVideoPath = videoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }
}
