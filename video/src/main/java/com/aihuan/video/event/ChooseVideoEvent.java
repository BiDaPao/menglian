package com.aihuan.video.event;

/**
 * Created by debug on 2019/7/15.
 */

public class ChooseVideoEvent {
    private String mVideoPath;

    public ChooseVideoEvent(String videoPath) {
        mVideoPath = videoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }
}
