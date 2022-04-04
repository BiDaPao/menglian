package com.aihuan.doc;

import android.app.Application;

/**
 * Created by cxf on 2019/3/13.
 */

public class AppContext extends Application {

    public static AppContext sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
