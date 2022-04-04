package com.aihuan.video.upload;

import java.util.List;

/**
 * Created by cxf on 2019/7/10.
 */

public interface FileUploadStrategy {
    void upload(List<FileUploadBean> upLoadList, FileUploadCallback callback);

    void cancel();
}
