package com.aihuan.dynamic.inter;

/**
 * Created by debug on 2019/7/25.
 */

public interface VoicePlayCallBack {
    void onPlayStart();
    void onPlayResume();
    void onPlayPause();
    void onPlayEnd();
    void onPlayAutoEnd();

}
