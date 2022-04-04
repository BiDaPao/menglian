package com.aihuan.main.utils;

import android.util.SparseIntArray;

import com.aihuan.common.Constants;
import com.aihuan.main.R;

/**
 * Created by cxf on 2018/10/11.
 */

public class MainIconUtil {
    private static SparseIntArray sOnLineMap1;//在线类型图标
    private static SparseIntArray sCashTypeMap;//提现图片

    static {
        sOnLineMap1 = new SparseIntArray();
        sOnLineMap1.put(Constants.LINE_TYPE_OFF, R.mipmap.o_home_line_off);
        sOnLineMap1.put(Constants.LINE_TYPE_DISTURB, R.mipmap.o_home_line_disturb);
        sOnLineMap1.put(Constants.LINE_TYPE_CHAT, R.mipmap.o_home_line_chat);
        sOnLineMap1.put(Constants.LINE_TYPE_ON, R.mipmap.o_home_line_on);


        sCashTypeMap = new SparseIntArray();
        sCashTypeMap.put(Constants.CASH_ACCOUNT_ALI, R.mipmap.icon_cash_ali);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_WX, R.mipmap.icon_cash_wx);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_BANK, R.mipmap.icon_cash_bank);

    }

    public static int getOnLineIcon1(int key) {
        return sOnLineMap1.get(key);
    }


    public static int getCashTypeIcon(int key) {
        return sCashTypeMap.get(key);
    }

}
