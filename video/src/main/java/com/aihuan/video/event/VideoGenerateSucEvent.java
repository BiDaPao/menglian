package com.aihuan.video.event;

/**
 * Created by debug on 2019/7/23.
 */

public class VideoGenerateSucEvent {
    private String mGenerateVideoPath;

    public VideoGenerateSucEvent(String generateVideoPath) {
        mGenerateVideoPath = generateVideoPath;
    }

    public String getGenerateVideoPath() {
        return mGenerateVideoPath;
    }


}
