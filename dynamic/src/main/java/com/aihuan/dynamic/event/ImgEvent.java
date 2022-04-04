package com.aihuan.dynamic.event;

import com.aihuan.dynamic.upload.UploadBean;

import java.util.List;

/**
 * Created by debug on 2019/8/2.
 */

public class ImgEvent {
    private List<UploadBean> mImgBeanList;

    public List<UploadBean> getImgBeanList() {
        return mImgBeanList;
    }

    public ImgEvent(List<UploadBean> imgBeanList) {
        mImgBeanList = imgBeanList;
    }
}
