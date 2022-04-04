package com.aihuan.main.bean;

import com.aihuan.common.bean.UserBean;

/**
 * Created by Sky.L on 2019/10/24
 */
public class OnlineUserBean{
    //private String id;
    private String avatar_thumb;
    private int sex;
    private String user_status;
    private String last_login_time;
    private String user_nicename;
    private UserBean user_info;

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }


    public UserBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserBean user_info) {
        this.user_info = user_info;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }


    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
