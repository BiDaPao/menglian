package com.aihuan.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.text.TextUtils;


import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.R;
import com.aihuan.common.interfaces.ActivityResultCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.interfaces.VideoResultCallback;

import java.io.File;

/**
 * Created by cxf on 2018/9/29.
 * 选择图片 裁剪
 */

public class ProcessImageUtil extends ProcessResultUtil {

    private Context mContext;
    private String[] mCameraPermissions;
    private String[] mAlumbPermissions;
    private String[] mVideoPermissions;
    private CommonCallback<Boolean> mCameraPermissionCallback;
    private CommonCallback<Boolean> mAlumbPermissionCallback;
    private CommonCallback<Boolean> mVideoPermissionCallback;
    private ActivityResultCallback mCameraResultCallback;
    private ActivityResultCallback mAlumbResultCallback;
    private ActivityResultCallback mCropResultCallback;
    private ActivityResultCallback mVideoResultCallback;
    private File mCameraResult;//拍照后得到的图片
    private File mCorpResult;//裁剪后得到的图片
    private File mVideoResult;//裁剪后得到的图片
    private ImageResultCallback mResultCallback;
    private VideoResultCallback mResultCallbackVideo;
    private boolean mNeedCrop;//是否需要裁剪

    public ProcessImageUtil(FragmentActivity activity) {
        super(activity);
        mContext = activity;
        mCameraPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        mAlumbPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        mVideoPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        mCameraPermissionCallback = new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    takePhoto();
                }
            }
        };
        mAlumbPermissionCallback = new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    chooseFile();
                }
            }
        };
        mVideoPermissionCallback = new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    record();
                }
            }
        };
        mCameraResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mNeedCrop) {
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(mContext, WordUtil.getString(R.string.FILE_PROVIDER), mCameraResult);
                    } else {
                        uri = Uri.fromFile(mCameraResult);
                    }
                    if (uri != null) {
                        crop(uri);
                    }
                } else {
                    if (mResultCallback != null) {
                        mResultCallback.onSuccess(mCameraResult);
                    }
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_camera_cancel);
            }
        };
        mAlumbResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mNeedCrop) {
                    crop(intent.getData());
                } else {
                    String path = FileUtils.getPath(mContext, intent.getData());
                    if (!TextUtils.isEmpty(path) && mResultCallback != null) {
                        mResultCallback.onSuccess(new File(path));
                    }
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_alumb_cancel);
            }
        };
        mCropResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mResultCallback != null) {
                    mResultCallback.onSuccess(mCorpResult);
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.img_crop_cancel);
            }
        };

        mVideoResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(final Intent intent) {
                L.e("--onSuccess----" + intent + "----" + mVideoResult);
                if (mVideoResult != null && mResultCallbackVideo != null) {
                    mResultCallbackVideo.onSuccess(mVideoResult);
                }
            }
        };
    }

    /**
     * 拍照获取图片
     */
    public void getImageByCamera(boolean needCrop) {
        mNeedCrop = needCrop;
        requestPermissions(mCameraPermissions, mCameraPermissionCallback);
    }

    /**
     * 拍照获取图片
     */
    public void getImageByCamera() {
        getImageByCamera(true);
    }

    /**
     * 相册获取图片
     */
    public void getImageByAlumb(boolean needCrop) {
        mNeedCrop = needCrop;
        requestPermissions(mAlumbPermissions, mAlumbPermissionCallback);
    }

    /**
     * 相册获取图片
     */
    public void getImageByAlumb() {
        getImageByAlumb(true);
    }

    /**
     * 录制视频
     */
    public void getVideoRecord() {
        requestPermissions(mVideoPermissions, mVideoPermissionCallback);
    }


    public void record() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 表示跳转至相机的录视频界面
        mVideoResult = getNewVideoFile();
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, WordUtil.getString(R.string.FILE_PROVIDER), mVideoResult);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mCameraResult);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //好使
        //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 60 * 1048576L);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        startActivityForResult(intent, mVideoResultCallback);
    }

    /**
     * 开启摄像头，执行照相
     */
    private void takePhoto() {
        if (mResultCallback != null) {
            mResultCallback.beforeCamera();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraResult = getNewFile();
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, WordUtil.getString(R.string.FILE_PROVIDER), mCameraResult);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mCameraResult);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, mCameraResultCallback);
    }

    private File getNewFile() {
        // 裁剪头像的绝对路径
        File dir = new File(CommonAppConfig.CAMERA_IMAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".png");
    }

    private File getNewVideoFile() {
        // 裁剪头像的绝对路径
        File dir = new File(CommonAppConfig.VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DateFormatUtil.getCurTimeString() + ".mp4");
    }


    /**
     * 打开相册，选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intent, mAlumbResultCallback);
    }

    /**
     * 裁剪图片
     */
    public void cropImage(File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(mContext, WordUtil.getString(R.string.FILE_PROVIDER), file);
        } else {
            uri = Uri.fromFile(file);
        }
        if (uri != null) {
            crop(uri);
        }
    }


    /**
     * 裁剪
     */
    private void crop(Uri inputUri) {
        mCorpResult = getNewFile();
        try {
            Uri resultUri = Uri.fromFile(mCorpResult);
            if (resultUri == null || mFragment == null || mContext == null) {
                return;
            }
            UCrop uCrop = UCrop.of(inputUri, resultUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(400, 400);
            Intent intent = uCrop.getIntent(mContext);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, mCropResultCallback);
        } catch (Exception e) {
            try {
                Uri resultUri = FileProvider.getUriForFile(mContext, WordUtil.getString(R.string.FILE_PROVIDER), mCorpResult);
                if (resultUri == null || mFragment == null || mContext == null) {
                    return;
                }
                UCrop uCrop = UCrop.of(inputUri, resultUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(400, 400);
                Intent intent = uCrop.getIntent(mContext);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, mCropResultCallback);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    public void setImageResultCallback(ImageResultCallback resultCallback) {
        mResultCallback = resultCallback;
    }

    public void setVideosultCallback(VideoResultCallback resultCallback) {
        mResultCallbackVideo = resultCallback;
    }


    @Override
    public void release() {
        super.release();
        mResultCallback = null;
    }
}
