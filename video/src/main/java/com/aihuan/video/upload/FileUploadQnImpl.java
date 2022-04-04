package com.aihuan.video.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by cxf on 2018/5/21.
 * 文件上传 七牛云实现类
 */

public class FileUploadQnImpl implements FileUploadStrategy {

    private static final String TAG = "FileUploadQnImpl";
    private List<FileUploadBean> mUpLoadList;
    private int mIndex;
    private FileUploadCallback mFileUploadCallback;
    private HttpCallback mGetUploadTokenCallback;
    private String mToken;
    private UploadManager mUploadManager;
    private UpCompletionHandler mCompletionHandler;//上传回调
    //private String mQiNiuHost;


    public FileUploadQnImpl(ConfigBean configBean) {
        //mQiNiuHost = configBean.getVideoQiNiuHost();
        mCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (mIndex>mUpLoadList.size()-1){
                    return;
                }
                FileUploadBean uploadBean = mUpLoadList.get(mIndex);
                uploadBean.setSuccess(true);
                String resultUrl = uploadBean.getOriginFile().getName();
                L.e(TAG, "文件上传结果-------->" + resultUrl);
                uploadBean.setRemoteAccessUrl(resultUrl);
                mIndex++;
                L.e("---mIndex----"+mIndex);
                if (mIndex < mUpLoadList.size()) {
                    uploadFile(mUpLoadList.get(mIndex));
                } else {
                    if (mFileUploadCallback != null) {
                        mFileUploadCallback.onSuccess(mUpLoadList);
                    }
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
        if (mGetUploadTokenCallback == null) {
            mGetUploadTokenCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        mToken = JSON.parseObject(info[0]).getString("token");
                        L.e(TAG, "-------上传的token------>" + mToken);
                        uploadFile(mUpLoadList.get(mIndex));
                    }
                }
            };
        }
        VideoHttpUtil.getQiNiuToken(mGetUploadTokenCallback);
    }


    /**
     * 上传文件
     */
    private void uploadFile(FileUploadBean bean) {
        if (bean == null || TextUtils.isEmpty(mToken) || mCompletionHandler == null) {
            return;
        }
        bean.setRemoteFileName(StringUtil.getRandomName());
        if (mUploadManager == null) {
            mUploadManager = new UploadManager();
        }
        mUploadManager.put(bean.getOriginFile(), bean.getOriginFile().getName(), mToken, mCompletionHandler, null);
    }

    @Override
    public void cancel() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_QI_NIU_TOKEN);
        if (mUpLoadList != null) {
            mUpLoadList.clear();
        }
        mFileUploadCallback = null;
    }

}
