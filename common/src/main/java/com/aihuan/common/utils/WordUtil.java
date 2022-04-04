package com.aihuan.common.utils;

import android.content.res.Resources;

import com.aihuan.common.CommonAppContext;

/**
 * Created by cxf on 2017/10/10.
 * 获取string.xml中的字
 */

public class WordUtil {

    private static Resources sResources;

    static {
        sResources = CommonAppContext.sInstance.getResources();
    }

    public static String getString(int res) {
        return sResources.getString(res);
    }

    public static String getString(int res, int arg) {
        return sResources.getString(res, arg);
    }
}
