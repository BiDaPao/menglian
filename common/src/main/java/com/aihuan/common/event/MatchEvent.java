package com.aihuan.common.event;

/**
 * 新增主页面点击按钮跳转匹配页面，进行视屏、语音速配
 */
public class MatchEvent {
    private int type; // 1 视频速配 2 音频速配

    public MatchEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
