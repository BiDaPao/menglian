package com.aihuan.im.event;

public class ImCensorMsgEvent {
   private String content;

    public ImCensorMsgEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
