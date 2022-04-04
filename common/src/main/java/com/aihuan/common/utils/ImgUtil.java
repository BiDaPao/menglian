package com.aihuan.common.utils;

import android.content.Context;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author Saint  2022/3/18 9:53
 * @DESC:
 */
public class ImgUtil {
    public static void compressImg(Context context, String path, OnCompressListener listener) {
        Luban.with(context)
                .load(path)
                .ignoreBy(50)
//                .setTargetDir(path)
                .setCompressListener(listener)
                .launch();

    }
}
