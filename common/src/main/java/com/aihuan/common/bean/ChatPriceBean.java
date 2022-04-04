package com.aihuan.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/17.
 */

public class ChatPriceBean {

    private String mCoin;
    private int mLevel;
    private int mCanUse;


    public ChatPriceBean() {
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
    }

    @JSONField(name = "level")
    public int getLevel() {
        return mLevel;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        mLevel = level;
    }

    @JSONField(name = "canselect")
    public int getCanUse() {
        return mCanUse;
    }

    @JSONField(name = "canselect")
    public void setCanUse(int canUse) {
        mCanUse = canUse;
    }

    public boolean isCanUse() {
        return this.mCanUse == 1;
    }
}
