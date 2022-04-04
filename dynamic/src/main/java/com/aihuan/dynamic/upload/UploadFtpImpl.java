package com.aihuan.dynamic.upload;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.http.Data;
import com.aihuan.common.http.JsonBean;
import com.aihuan.dynamic.upload.UploadBean;
import com.aihuan.dynamic.upload.UploadCallback;
import com.aihuan.dynamic.upload.UploadStrategy;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;

import java.io.File;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by cxf on 2018/12/21.
 * 上传文件，不使用云存储，使用自建存储，比如ftp服务器等
 */

public class UploadFtpImpl implements UploadStrategy {
    private static final String TAG = "UploadFtpImpl";
    private UploadTask<String> mTask;
    private List<UploadBean> mList;
    private int mIndex;
    private boolean mNeedCompress;
    private UploadCallback mUploadCallback;
    private Luban.Builder mLubanBuilder;
    private Context mContext;
    private UploadListener mUploadListener;

    public UploadFtpImpl(Context context) {
        mContext = context;
        mUploadListener=new  UploadListener<String>(TAG) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {

            }

            @Override
            public void onError(Progress progress) {

            }

            @Override
            public void onFinish(String s, Progress progress) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JsonBean bean = JSON.parseObject(s, JsonBean.class);
                        if (bean != null) {
                            if (200 == bean.getRet()) {
                                Data data = bean.getData();
                                if (data != null) {
                                    if (700 == data.getCode()) {
                                        //token过期，重新登录
                                        RouteUtil.forwardLoginInvalid(data.getMsg());
                                        if (mUploadCallback != null) {
                                            mUploadCallback.onFinish(mList,false);
                                        }
                                    } else {
                                        String[] info = data.getInfo();
                                        if (data.getCode() == 0 && info.length > 0) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            String url=obj.getString("video");
                                            String urlImg=obj.getString("img");
                                            UploadBean uploadBean = mList.get(mIndex);
                                            uploadBean.setSuccess(true);
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
                                    }
                                } else {
                                    L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                                    if (mUploadCallback != null) {
                                        mUploadCallback.onFinish(mList,false);
                                    }
                                }
                            } else {
                                L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                                if (mUploadCallback != null) {
                                    mUploadCallback.onFinish(mList,false);
                                }
                            }

                        } else {
                            L.e("服务器返回值异常--->bean = null");
                            if (mUploadCallback != null) {
                                mUploadCallback.onFinish(mList,false);
                            }
                        }
                    } catch (Exception e) {
                        if (mUploadCallback != null) {
                            mUploadCallback.onFinish(mList,false);
                        }
                    }
                }
            }



            @Override
            public void onRemove(Progress progress) {

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

        uploadNext();
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
        if (bean.getType()== UploadBean.VIDEO){
            bean.setRemoteFileName(StringUtil.generateVideoOutputPath());
        }else if (bean.getType()==UploadBean.IMG){
            bean.setRemoteFileName(StringUtil.generateFileName());
        }else {

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
        if (bean != null &&  mUploadListener != null) {
            PostRequest<String> postRequest = OkGo.<String>post("http://www.mytoday.net/api/public/?service=Video.uploadvideo")
                    .params("uid", CommonAppConfig.getInstance().getUid())
                    .params("token", CommonAppConfig.getInstance().getToken())
                    .params("file", bean.getOriginFile())
//                    .params("file1", bean.getOriginFile())
                    .converter(new StringConvert());
            mTask = OkUpload.request(TAG, postRequest)
                    .save()
                    .register(mUploadListener);
            mTask.start();
        } else {
            if (mUploadCallback != null) {
                mUploadCallback.onFinish(mList, false);
            }
        }
    }

    @Override
    public void cancelUpload() {
        if (mTask != null) {
            mTask.remove();
        }
    }
}
