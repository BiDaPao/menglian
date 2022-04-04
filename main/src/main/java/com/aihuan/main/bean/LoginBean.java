package com.aihuan.main.bean;

/**
 * @author Saint  2022/3/15 10:35
 * @DESC:
 */
public class LoginBean {


    private String id;
    private String user_nickname;
    private String avatar;
    private String consumption;
    private int isreg;
    private String token;
    private String usersig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public int getIsreg() {
        return isreg;
    }

    public void setIsreg(int isreg) {
        this.isreg = isreg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsersig() {
        return usersig;
    }

    public void setUsersig(String usersig) {
        this.usersig = usersig;
    }
}
