package com.aihuan.main.bean;

/**
 * @author Saint  2022/2/21 15:10
 * @DESC:
 */
public class BaseBean {
    private String status;//0-待审核,1-同意,2-拒绝
    private String avatar;
    private String nickname;
    private String reason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
