package com.aihuan.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.HtmlConfig;

/**
 * Created by cxf on 2019/4/11.
 * 支出
 */

public class WalletExpandViewHolder extends AbsWalletDetailViewHolder {

    public WalletExpandViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getHtmlUrl() {
        return HtmlConfig.WALLET_EXPAND;
    }


}
