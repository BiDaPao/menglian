package com.aihuan.common.interfaces;

import java.io.File;

/**
 * Created by cxf on 2018/9/29.
 */

public interface VideoResultCallback {

    void onSuccess(File path);

    void onFailure();
}
