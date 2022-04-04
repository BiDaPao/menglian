package com.aihuan.dynamic.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.aihuan.common.CommonAppContext;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.dynamic.upload.UploadBean;
import com.aihuan.im.bean.ChatChooseImageBean;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class ImageUtil {

    private ContentResolver mContentResolver;
    private ImageHandler mHandler;
    private CommonCallback<List<UploadBean>> mCallback;
    private boolean mStop;

    public ImageUtil() {
        mContentResolver = CommonAppContext.sInstance.getContentResolver();
        mHandler = new ImageHandler(this);
    }

    public void getLocalImageList(CommonCallback<List<UploadBean>> callback) {
        if (callback == null) {
            return;
        }
        mCallback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    List<UploadBean> imageList = getAllImage();
                    Message msg = Message.obtain();
                    msg.obj = imageList;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void imageCallback(List<UploadBean> imageList) {
        if (!mStop && mCallback != null) {
            mCallback.callback(imageList);
        }
    }

    private List<UploadBean> getAllImage() {
        List<UploadBean> imageList = new ArrayList<>();
        Cursor cursor = null;
        try {
            //只查询jpeg和png的图片
            cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            if (cursor != null) {
                while (!mStop && cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (!imagePath.contains("/DCIM/")) {
                        continue;
                    }
                    File file = new File(imagePath);
                    if (!file.exists()) {
                        continue;
                    }
                    boolean canRead = file.canRead();
                    long length = file.length();
                    if (!canRead || length == 0) {
                        continue;
                    }
                    imageList.add(new UploadBean(file));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageList;
    }


    public void release() {
        mStop = true;
        if (mHandler != null) {
            mHandler.release();
        }
        mCallback = null;
    }

    private static class ImageHandler extends Handler {

        private ImageUtil mImageUtil;

        public ImageHandler(ImageUtil util) {
            mImageUtil = new WeakReference<>(util).get();
        }

        @Override
        public void handleMessage(Message msg) {
            List<UploadBean> imageList = (List<UploadBean>) msg.obj;
            if (mImageUtil != null && imageList != null) {
                mImageUtil.imageCallback(imageList);
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mImageUtil = null;
        }
    }

}
