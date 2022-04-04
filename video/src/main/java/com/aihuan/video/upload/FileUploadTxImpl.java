package com.aihuan.video.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.SessionQCloudCredentials;
import com.tencent.qcloud.core.auth.StaticCredentialProvider;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.video.http.VideoHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/5/21.
 */

public class FileUploadTxImpl implements FileUploadStrategy {

    private static final String TAG = "FileUploadTxImpl";

    private List<FileUploadBean> mUpLoadList;
    private int mIndex;
    private FileUploadCallback mFileUploadCallback;
    private CosXmlProgressListener mCosXmlProgressListener;//上传进度回调
    private CosXmlResultListener mCosXmlResultListener;//上传成功回调
    private StringCallback mCredentialCallback;
    private CosXmlService mCosXmlService;
    private String mAppId;//appId
    private String mRegion;//区域
    private String mBucketName;//桶的名字
    private String mCosVideoPath;//腾讯云存储上面的 视频文件夹
    private String mCosImagePath;//腾讯云存储上面的 图片文件夹

    public FileUploadTxImpl(ConfigBean configBean) {
        mAppId = configBean.getTxCosAppId();
        mRegion = configBean.getTxCosRegion();
        mBucketName = configBean.getTxCosBucketName();
        mCosVideoPath = configBean.getTxCosVideoPath();
        mCosImagePath = configBean.getTxCosImagePath();
        if (mCosVideoPath == null) {
            mCosVideoPath = "";
        }
        if (!mCosVideoPath.endsWith("/")) {
            mCosVideoPath += "/";
        }
        if (mCosImagePath == null) {
            mCosImagePath = "";
        }
        if (!mCosImagePath.endsWith("/")) {
            mCosImagePath += "/";
        }
        mCosXmlResultListener = new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                if (result != null) {
                    String resultUrl = "http://" + result.accessUrl;
                    FileUploadBean uploadBean = mUpLoadList.get(mIndex);
                    uploadBean.setSuccess(true);
                    L.e(TAG, "文件上传结果-------->" + resultUrl);
                    uploadBean.setRemoteAccessUrl(resultUrl);
                    mIndex++;
                    if (mIndex < mUpLoadList.size()) {
                        uploadFile(mUpLoadList.get(mIndex));
                    } else {
                        if (mFileUploadCallback != null) {
                            mFileUploadCallback.onSuccess(mUpLoadList);
                        }
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                if (mFileUploadCallback != null) {
                    mFileUploadCallback.onFailure();
                }
            }
        };

    }

    @Override
    public void upload(List<FileUploadBean> upLoadList, FileUploadCallback callback) {
        if (upLoadList == null || upLoadList.size() == 0) {
            return;
        }
        mUpLoadList = upLoadList;
        mFileUploadCallback = callback;
        mIndex = 0;
        if (mCredentialCallback == null) {
            mCredentialCallback = new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String result = response.body();
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject obj = JSON.parseObject(result);
                        if (obj.getIntValue("code") == 0) {
                            JSONObject data = obj.getJSONObject("data");
                            JSONObject cls = data.getJSONObject("credentials");
                            try {
                                SessionQCloudCredentials credentials
                                        = new SessionQCloudCredentials(cls.getString("tmpSecretId"),
                                        cls.getString("tmpSecretKey"),
                                        cls.getString("sessionToken"),
                                        data.getLongValue("expiredTime"));
                                QCloudCredentialProvider qCloudCredentialProvider
                                        = new StaticCredentialProvider(credentials);
                                CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                                        .setAppidAndRegion(mAppId, mRegion)
                                        .builder();
                                mCosXmlService = new CosXmlService(CommonAppContext.sInstance, serviceConfig, qCloudCredentialProvider);
                            } catch (Exception e) {
                                if (mFileUploadCallback != null) {
                                    mFileUploadCallback.onFailure();
                                }
                            }
                            if (mCosXmlService == null) {
                                return;
                            }
                            uploadFile(mUpLoadList.get(mIndex));
                        } else {
                            if (mFileUploadCallback != null) {
                                mFileUploadCallback.onFailure();
                            }
                        }
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    if (mFileUploadCallback != null) {
                        mFileUploadCallback.onFailure();
                    }
                }
            };
        }
        VideoHttpUtil.getTxUploadCredential(mCredentialCallback);
    }


    /**
     * 上传文件
     */
    private void uploadFile(FileUploadBean bean) {
        if (bean == null || mCosXmlService == null) {
            return;
        }
        //bean.setRemoteFileName(StringUtil.getRandomName());
        PutObjectRequest putObjectRequest = new PutObjectRequest(mBucketName, mCosVideoPath + bean.getRemoteFileName(),
                bean.getOriginFile().getAbsolutePath());
        if (mCosXmlProgressListener == null) {
            mCosXmlProgressListener = new CosXmlProgressListener() {
                @Override
                public void onProgress(long progress, long max) {
                    L.e(TAG, "---上传进度--->" + progress * 100 / max);
                }
            };
        }
        putObjectRequest.setProgressListener(mCosXmlProgressListener);
        mCosXmlService.putObjectAsync(putObjectRequest, mCosXmlResultListener);
    }


    @Override
    public void cancel() {
        if (mUpLoadList != null) {
            mUpLoadList.clear();
        }
        mFileUploadCallback = null;
        if (mCosXmlService != null) {
            mCosXmlService.release();
        }
        mCosXmlService = null;
    }
}
