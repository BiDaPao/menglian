package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/10/20.
 */

public class CashAccountBean {

    private String id;
//    private String uid;
//    private int type;
//    private String bankName;
//    private String userName;
//    private String account;
//    private boolean checked;

    //支付宝绑定帐号相关信息
    private String alipay_uid;
    private String alipay_avatar;
    private String alipay_nickname;

    //提交删除审核相关信息
    private int del_status;//0-待审核,1-同意,2-拒绝  默认值 4
    private String del_remark;//拒绝原因


    public int getDel_status() {
        return del_status;
    }

    public void setDel_status(int del_status) {
        this.del_status = del_status;
    }

    public String getDel_remark() {
        return del_remark;
    }

    public void setDel_remark(String del_remark) {
        this.del_remark = del_remark;
    }

    public String getAlipay_uid() {
        return alipay_uid;
    }

    public void setAlipay_uid(String alipay_uid) {
        this.alipay_uid = alipay_uid;
    }

    public String getAlipay_avatar() {
        return alipay_avatar;
    }

    public void setAlipay_avatar(String alipay_avatar) {
        this.alipay_avatar = alipay_avatar;
    }

    public String getAlipay_nickname() {
        return alipay_nickname;
    }

    public void setAlipay_nickname(String alipay_nickname) {
        this.alipay_nickname = alipay_nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
//
//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    @JSONField(name = "account_bank")
//    public String getBankName() {
//        return bankName;
//    }
//
//    @JSONField(name = "account_bank")
//    public void setBankName(String bankName) {
//        this.bankName = bankName;
//    }
//
//    @JSONField(name = "name")
//    public String getUserName() {
//        return userName;
//    }
//
//    @JSONField(name = "name")
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public boolean isChecked() {
//        return checked;
//    }
//
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    }
}
