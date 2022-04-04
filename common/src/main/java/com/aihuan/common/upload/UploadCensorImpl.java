package com.aihuan.common.upload;

import android.content.Context;
import android.text.TextUtils;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.FileUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by cxf on 2019/4/16.
 * 七牛上传文件
 */

public class UploadCensorImpl {

    private static final String TAG = "UploadQnImpl";
    private Context mContext;
    private UploadBean uploadBean;
    private UploadCallbackSignle mUploadCallback;
    private HttpCallback mGetUploadTokenCallback;
    private String mToken;
    private UploadManager mUploadManager;
    private Luban.Builder mLubanBuilder;
    private String BaseHost = "http://cc.lcmdhr.top/";
    //七牛云存储目录
    private String qiniuDir = "ImageCensor/";
    private boolean mNeedCompress;

    public UploadCensorImpl(Context context) {
        mContext = context;
    }

    public void upload(UploadBean bean, boolean needCompress, UploadCallbackSignle callback) {
        if (callback == null) {
            return;
        }
        if (bean == null || bean.getOriginFile() == null) {
            callback.onFinish(bean, false);
            return;
        }
        this.mNeedCompress = needCompress;
        mUploadCallback = callback;
        uploadBean = bean;
        L.e("文件名： " + uploadBean.getRemoteFileName());
        if (mToken == null || mToken.length() <= 0) {
            if (mGetUploadTokenCallback == null) {
                mGetUploadTokenCallback = new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            mToken = JSON.parseObject(info[0]).getString("token");
                            L.e(TAG, "-------上传的token------>" + mToken);
                            if (needCompress) {
                                compress(uploadBean);
                            } else {
                                upload(uploadBean);
                            }
                        }
                    }
                };
            }
            CommonHttpUtil.getUploadQiNiuToken(mGetUploadTokenCallback);
        } else {
            if (needCompress) {
                compress(uploadBean);
            } else {
                upload(uploadBean);
            }
        }
    }

    public void cancelUpload() {
    }

    private void compress(UploadBean bean) {
        if (mLubanBuilder == null) {
//            //判断文件路径是否存在并创建
//            File newFile = new File(CommonAppConfig.CAMERA_IMAGE_PATH);
//            if (!newFile.exists()){
//                newFile.mkdir();
//            }
            mLubanBuilder = Luban.with(mContext)
                    .ignoreBy(100)//100k以下不压缩
                    .setTargetDir(CommonAppConfig.CAMERA_IMAGE_PATH)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            L.e("压缩完成： " + file.getAbsolutePath());
                            bean.setOriginFile(file);
                            upload(bean);
                        }

                        @Override
                        public void onError(Throwable e) {
                            L.e("压缩失败： " + e.getMessage());
                            upload(bean);
                        }
                    });
        }
        mLubanBuilder.load(bean.getOriginFile())
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        L.e("重命名： " + filePath);
                        return bean.getRemoteFileName();
                    }
                }).launch();
    }

    private void upload(UploadBean bean) {
        if (bean != null && !TextUtils.isEmpty(mToken)) {
            if (mUploadManager == null) {
                mUploadManager = new UploadManager();
            }
            mUploadManager.put(bean.getOriginFile(), qiniuDir + bean.getRemoteFileName(), mToken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    uploadBean.setSuccess(true);
                    uploadBean.setRemoteAccessUrl(BaseHost + key);
//                    if (mNeedCompress) {
//                        //上传完成后把 压缩后的图片 删掉
//                        File compressedFile = uploadBean.getOriginFile();
//                        if (compressedFile != null && compressedFile.exists()) {
//                            compressedFile.delete();
//                        }
//                    }

                    if (mUploadCallback != null) {
                        mUploadCallback.onFinish(uploadBean, true);
                    }
                    uploadBean = null;
                }
            }, null);
        } else {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(bean, false);
            }
        }
    }

}
