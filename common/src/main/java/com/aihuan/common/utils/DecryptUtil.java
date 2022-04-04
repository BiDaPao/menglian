package com.aihuan.common.utils;

import android.text.TextUtils;

/**
 * Created by cxf on 2019/7/27.
 */

public class DecryptUtil {

    private static final String KEY = "-x?8S6Ppl:BW9hKiO5m.IV7FdZzuXgRwyNQ=JsvY_C2eqDEf0T143kbGaAHjnocM/rLtU";
    private static StringBuilder sStringBuilder;

    /**
     * 解密url
     */
    public static String decrypt(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (sStringBuilder == null) {
            sStringBuilder = new StringBuilder();
        }
        sStringBuilder.delete(0, sStringBuilder.length());
        for (int i = 0, len1 = content.length(); i < len1; i++) {
            for (int j = 0, len2 = KEY.length(); j < len2; j++) {
                if (content.charAt(i) == KEY.charAt(j)) {
                    if (j - 1 < 0) {
                        sStringBuilder.append(KEY.charAt(len2 - 1));
                    } else {
                        sStringBuilder.append(KEY.charAt(j - 1));
                    }
                }
            }
        }
        return sStringBuilder.toString();
    }
}
