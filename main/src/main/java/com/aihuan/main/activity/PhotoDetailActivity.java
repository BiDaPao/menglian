package com.aihuan.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadQnImpl;
import com.aihuan.common.upload.UploadStrategy;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DownloadUtil;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.PhotoBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.views.PhotoDetailViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/10.
 * 图片详情
 */

public class PhotoDetailActivity extends AbsActivity implements PhotoDetailViewHolder.ActionLister {

    private ProcessImageUtil mImageUtil;
    private PhotoBean mPhotoBean;
    private static final String TAG = "setCoverTag";
    private Dialog mLoading;

    public static void forward(Context context, PhotoBean bean) {
        Intent intent = new Intent(context, PhotoDetailActivity.class);
        intent.putExtra(Constants.PHOTO_BEAN, bean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mPhotoBean = intent.getParcelableExtra(Constants.PHOTO_BEAN);
        if (mPhotoBean == null) {
            return;
        }
        PhotoDetailViewHolder vh = new PhotoDetailViewHolder(mContext, (ViewGroup) findViewById(R.id.container), mPhotoBean.getThumb(), CommonAppConfig.getInstance().getUid().equals(mPhotoBean.getUid()));
        vh.setActionLister(this);
        vh.subscribeActivityLifeCycle();
        vh.addToParent();
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file == null) {
                    return;
                }
                uploadCropImageFile(file);
            }

            @Override
            public void onFailure() {
                onFailed();
            }
        });
    }

    /**
     * 点击更多
     */
    @Override
    public void onMoreClick() {
        if (mPhotoBean == null) {
            return;
        }
        List<Integer> list = new ArrayList<>();
        if (mPhotoBean.isPrivate()) {
            list.add(R.string.photo_set_public);
        } else {
            list.add(R.string.photo_set_cover);
        }
        list.add(R.string.delete);
        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), false, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.photo_set_cover) {
                    setCover();
                } else if (tag == R.string.photo_set_public) {
                    publicPhoto();
                } else if (tag == R.string.delete) {
                    delete();
                }
            }
        });
    }


    /**
     * 设为封面
     */
    private void setCover() {
        if (mPhotoBean == null) {
            return;
        }
        checkReadWritePermissions();
    }

    /**
     * 把相册中的图片设为背景墙封面  检查读写权限
     */
    private void checkReadWritePermissions() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean result) {
                        if (result) {
                            downloadImage();
                        }
                    }
                });
    }

    /**
     * 下载图片
     */
    private void downloadImage() {
        if (mPhotoBean == null) {
            return;
        }
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        mLoading.show();
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.download(TAG, CommonAppConfig.CAMERA_IMAGE_PATH, TAG, mPhotoBean.getThumb(), new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                if (mLoading != null) {
                    mLoading.hide();
                }
                if (mImageUtil != null) {
                    mImageUtil.cropImage(file);
                }
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {
                onFailed();
            }
        });
    }

    /**
     * 把裁剪后的图片上传
     *
     * @param file 裁剪后的图片
     */
    private void uploadCropImageFile(File file) {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        mLoading.show();
        UploadBean bean = new UploadBean(file);
        UploadStrategy uploadStrategy = new UploadQnImpl(mContext);
        uploadStrategy.upload(Arrays.asList(bean), true, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    if (list != null && list.size() > 0) {
                        doSetWallCover(list.get(0).getRemoteFileName());
                    }
                } else {
                    onFailed();
                }
            }
        });
    }


    private void doSetWallCover(String thumb) {
        MainHttpUtil.setWallCover(thumb, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
            }

            @Override
            public void onFinish() {
                if (mLoading != null) {
                    mLoading.dismiss();
                }
            }
        });
    }


    private void onFailed() {
        if (mLoading != null) {
            mLoading.dismiss();
        }
        ToastUtil.show(R.string.photo_set_cover_failed);
    }


    /**
     * 把自己的相册中的私密照片设为公开的
     */
    private void publicPhoto() {
        if (mPhotoBean == null) {
            return;
        }
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.photo_public_tip), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainHttpUtil.publicPhoto(mPhotoBean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mPhotoBean != null) {
                                mPhotoBean.setIsprivate(0);
                            }
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }

    /**
     * 删除自己的相册中的照片
     */
    private void delete() {
        if (mPhotoBean == null) {
            return;
        }
        MainHttpUtil.deletePhoto(mPhotoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(TAG);
        MainHttpUtil.cancel(MainHttpConsts.SET_WALL_COVER);
        MainHttpUtil.cancel(MainHttpConsts.DELETE_PHOTO);
        MainHttpUtil.cancel(MainHttpConsts.PUBLIC_PHOTO);
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mImageUtil = null;
        super.onDestroy();
    }


}
