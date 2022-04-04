package com.aihuan.video.upload;

import java.util.List;

/**
 * Created by cxf on 2018/5/21.
 */

public interface FileUploadCallback {
    void onSuccess(List<FileUploadBean> resultUrlList);

    void onFailure();
}
