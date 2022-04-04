package com.aihuan.dynamic.event;

/**
 * Created by debug on 2019/7/23.
 */

public class VideoDelEvent {
    private String videoId;

    public VideoDelEvent(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }


}
