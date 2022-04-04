package com.aihuan.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.HtmlConfig;

/**
 * Created by cxf on 2019/4/11.
 * 收入
 */

public class WalletIncomeViewHolder extends AbsWalletDetailViewHolder {

    public WalletIncomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getHtmlUrl() {
        return HtmlConfig.WALLET_INCOME;
    }


}
