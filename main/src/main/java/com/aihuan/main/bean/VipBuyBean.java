package com.aihuan.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/5/11.
 */

public class VipBuyBean {

    private String mId;
    private String mName;
    private String mCoin;
    private String mMoney;
    private boolean mChecked;

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
    }

    @JSONField(name = "coin_money")
    public String getMoney() {
        return mMoney;
    }

    @JSONField(name = "coin_money")
    public void setMoney(String money) {
        mMoney = money;
    }

    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }
    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
