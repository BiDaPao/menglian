package com.aihuan.im.bean;

/**
 * Created by cxf on 2019/4/23.
 */

public class ChatInfoBean {
    private byte action;
    private byte type;
    private String content;

    public ChatInfoBean() {
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
