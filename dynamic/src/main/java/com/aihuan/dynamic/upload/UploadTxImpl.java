package com.aihuan.dynamic.upload;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
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
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.dynamic.upload.UploadBean;
import com.aihuan.dynamic.upload.UploadCallback;
import com.aihuan.dynamic.upload.UploadStrategy;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.StringUtil;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by cxf on 2019/4/16.
 * 腾讯上传文件
 */

public class UploadTxImpl implements UploadStrategy {

    private static final String TAG = "UploadQnImpl";
    private Context mContext;
    private List<UploadBean> mList;
    private int mIndex;
    private boolean mNeedCompress;
    private UploadCallback mUploadCallback;
    private Luban.Builder mLubanBuilder;
    private CosXmlService mCosXmlService;
    private CosXmlResultListener mCosXmlResultListener;
    private String mAppId;//appId
    private String mRegion;//区域
    private String mBucketName;//桶的名字
    private ConfigBean mConfigBean;
    private String mCosVideoPath;//腾讯云存储上面的 视频文件夹
    private String mCosImagePath;//腾讯云存储上面的 图片文件夹


    public UploadTxImpl(Context context) {
        mContext = context;
        mConfigBean= CommonAppConfig.getInstance().getConfig();
        mAppId = mConfigBean.getTxCosAppId();
        mRegion = mConfigBean.getTxCosRegion();
        mBucketName = mConfigBean.getTxCosBucketName();
        mCosVideoPath = mConfigBean.getTxCosVideoPath();
        mCosImagePath = mConfigBean.getTxCosImagePath();
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
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                UploadBean uploadBean = mList.get(mIndex);
                uploadBean.setSuccess(true);
                if (cosXmlResult != null) {
                    String resultUrl = "http://" + cosXmlResult.accessUrl;
                    L.e(TAG, "---上传结果---->  " + resultUrl);
                    uploadBean.setRemoteFileName(resultUrl);
                }
                if (mNeedCompress) {
                    //上传完成后把 压缩后的图片 删掉
                    File compressedFile = uploadBean.getOriginFile();
                    if (compressedFile != null && compressedFile.exists()) {
                        compressedFile.delete();
                    }
                }
                mIndex++;
                if (mIndex < mList.size()) {
                    uploadNext();
                } else {
                    if (mUploadCallback != null) {
                        mUploadCallback.onFinish(mList, true);
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException e, CosXmlServiceException e1) {
                if (mUploadCallback != null) {
                    mUploadCallback.onFinish(mList, false);
                }
            }
        };

    }

    @Override
    public void upload(List<UploadBean> list, boolean needCompress, UploadCallback callback) {
        if (callback == null) {
            return;
        }
        if (list == null || list.size() == 0) {
            callback.onFinish(list, false);
            return;
        }
        boolean hasFile = false;
        for (UploadBean bean : list) {
            if (bean.getOriginFile() != null) {
                hasFile = true;
                break;
            }
        }
        if (!hasFile) {
            callback.onFinish(list, true);
            return;
        }
        mList = list;
        mNeedCompress = needCompress;
        mUploadCallback = callback;
        mIndex = 0;

        CommonHttpUtil.getTxUploadCredential(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String result = response.body();
                if (!TextUtils.isEmpty(result)) {
                    com.alibaba.fastjson.JSONObject obj = JSON.parseObject(result);
                    if (obj.getIntValue("code") == 0) {
                        com.alibaba.fastjson.JSONObject data = obj.getJSONObject("data");
                        com.alibaba.fastjson.JSONObject credentials = data.getJSONObject("credentials");


                        startUpload(credentials.getString("tmpSecretId"),
                                credentials.getString("tmpSecretKey"),
                                credentials.getString("sessionToken"),
                                data.getLongValue("expiredTime"));
                    } else {
                        if (mUploadCallback != null) {
                            mUploadCallback.onFinish(mList, false);
                        }
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                if (mUploadCallback != null) {
                    mUploadCallback.onFinish(mList, false);
                }
            }
        });


    }

    @Override
    public void cancelUpload() {
        mCosXmlResultListener = null;
        if (mCosXmlService != null) {
            mCosXmlService.release();
        }
        mCosXmlService = null;
    }

    private void uploadNext() {
        UploadBean bean = null;
        while (mIndex < mList.size() && (bean = mList.get(mIndex)).getOriginFile() == null) {
            mIndex++;
        }
        if (mIndex >= mList.size() || bean == null) {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, true);
            }
            return;
        }
        if (bean.getType() == UploadBean.VIDEO) {
            bean.setRemoteFileName(StringUtil.generateVideoOutputPath());
        } else {
            bean.setRemoteFileName(StringUtil.generateFileName());
        }
        if (mNeedCompress) {
            if (mLubanBuilder == null) {
                mLubanBuilder = Luban.with(mContext)
                        .ignoreBy(8)//8k以下不压缩
                        .setTargetDir(CommonAppConfig.CAMERA_IMAGE_PATH)
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                return mList.get(mIndex).getRemoteFileName();
                            }
                        }).setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                UploadBean uploadBean = mList.get(mIndex);
                                uploadBean.setOriginFile(file);
                                upload(uploadBean);
                            }

                            @Override
                            public void onError(Throwable e) {
                                upload(mList.get(mIndex));
                            }
                        });
            }
            mLubanBuilder.load(bean.getOriginFile()).launch();
        } else {
            upload(bean);
        }
    }


    private void upload(UploadBean bean) {
        if (mCosXmlService == null || mCosXmlResultListener == null) {
            return;
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(mBucketName,bean.getType()== UploadBean.VIDEO?mCosVideoPath+ bean.getRemoteFileName():mCosImagePath+ bean.getRemoteFileName(), bean.getOriginFile().getAbsolutePath());
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                L.e(TAG, "---上传进度--->" + progress * 100 / max);
            }
        });
        // 使用异步回调上传
        mCosXmlService.putObjectAsync(putObjectRequest,mCosXmlResultListener);

    }

    private void startUpload(String secretId, String secretKey, String token, long expiredTime) {
        try {
            SessionQCloudCredentials credentials = new SessionQCloudCredentials(secretId, secretKey, token, expiredTime);
            QCloudCredentialProvider qCloudCredentialProvider = new StaticCredentialProvider(credentials);
            CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                    .setAppidAndRegion(mAppId, mRegion)
                    .builder();
            mCosXmlService = new CosXmlService(CommonAppContext.sInstance, serviceConfig, qCloudCredentialProvider);
        } catch (Exception e) {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, false);
            }
        }
        uploadNext();
    }


}
