package com.aihuan.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.dialog.MainPriceDialogFragment;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.ActivityResultCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadQnImpl;
import com.aihuan.common.upload.UploadStrategy;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.activity.ChatChooseImageActivity;
import com.aihuan.main.R;
import com.aihuan.main.custom.UploadImageView2;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/10.
 * 上传相册图片
 */

public class MyPhotoPubActivity extends AbsActivity implements View.OnClickListener, MainPriceDialogFragment.ActionListener {

    private static final String TAG = "MyAlbumPubActivity";

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyPhotoPubActivity.class));
    }

    private Dialog mChooseImageDialog;
    private ActivityResultCallback mChooseImageCallback;
    private ProcessImageUtil mImageUtil;
    private UploadImageView2 mImgUpload;
    private UploadBean mUploadBean;
    private View mBtnPub;
    private CheckBox mCheckBox;
    private TextView mCoin;
    private TextView mTip;
    private List<ChatPriceBean> mPriceList;
    private String mCoinName;
    private String mImagePrice;
    private View mBtnPrice;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_album_pub;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.album_pub));
        mCoinName = CommonAppConfig.getInstance().getCoinName();
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
                if (mImgUpload != null && mUploadBean != null) {
                    mUploadBean.setOriginFile(file);
                    mImgUpload.showImageData(mUploadBean);
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mUploadBean = new UploadBean();
        mImgUpload = findViewById(R.id.img_upload);
        mImgUpload.setActionListener(new UploadImageView2.ActionListener() {
            @Override
            public void onAddClick(UploadImageView2 uploadImageView) {
                chooseImage();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        mBtnPub = findViewById(R.id.btn_upload);
        mBtnPub.setOnClickListener(this);
        mCheckBox = findViewById(R.id.checkbox);
        mCoin = findViewById(R.id.coin);
        mTip = findViewById(R.id.tip);
        mBtnPrice = findViewById(R.id.btn_price);
        getAlbumFee();
    }


    /**
     * 获取照片价格
     */
    public void getAlbumFee() {
        MainHttpUtil.getAlbumFee(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mTip != null) {
                        mTip.setText(obj.getString("tips"));
                    }
                    mPriceList = JSON.parseArray(obj.getString("list"), ChatPriceBean.class);
                    if (mPriceList != null && mPriceList.size() > 0) {
                        mImagePrice = mPriceList.get(0).getCoin();
                        if (mCoin != null) {
                            mCoin.setText(StringUtil.contact(mImagePrice, mCoinName));
                        }
                        if (mBtnPrice != null) {
                            mBtnPrice.setOnClickListener(MyPhotoPubActivity.this);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            cancel();
        } else if (i == R.id.btn_upload) {
            uploadImage();
        } else if (i == R.id.btn_price) {
            priceClick();
        }
    }

    /**
     * 设置图片价格
     */
    private void priceClick() {
        if (mPriceList == null || mPriceList.size() == 0) {
            return;
        }
        MainPriceDialogFragment fragment = new MainPriceDialogFragment();
        fragment.setPriceList(mPriceList);
        fragment.setNowPrice(mImagePrice);
        fragment.setFrom(Constants.MAIN_ME_VIDEO);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceDialogFragment");
    }

    @Override
    public void onPriceSelected(int from, ChatPriceBean bean) {
        mImagePrice = bean.getCoin();
        if (mCoin != null) {
            mCoin.setText(StringUtil.contact(mImagePrice, mCoinName));
        }
    }


    private void cancel() {
        if (mUploadBean == null || mUploadBean.isEmpty()) {
            MyPhotoPubActivity.super.onBackPressed();
        } else {
            DialogUitl.showStringArrayDialog(
                    mContext, new Integer[]{R.string.upload_give_up}, new DialogUitl.StringArrayDialogCallback() {
                        @Override
                        public void onItemClick(String text, int tag) {
                            MyPhotoPubActivity.super.onBackPressed();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    /**
     * 选择图片
     */
    public void chooseImage() {
        if (mChooseImageDialog == null) {
            mChooseImageDialog = DialogUitl.getStringArrayDialog(mContext, new Integer[]{
                    R.string.camera, R.string.alumb}, true, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.camera) {
                        mImageUtil.getImageByCamera(false);
                    } else {
                        checkReadWritePermissions();
                    }
                }
            });
        }
        mChooseImageDialog.show();
    }

    /**
     * 选择图片，检查读写权限
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
                            forwardChooseImage();
                        }
                    }
                });
    }

    /**
     * 跳转到选择图片页面
     */
    private void forwardChooseImage() {
        if (mChooseImageCallback == null) {
            mChooseImageCallback = new ActivityResultCallback() {
                @Override
                public void onSuccess(Intent intent) {
                    if (intent != null) {
                        String imagePath = intent.getStringExtra(Constants.SELECT_IMAGE_PATH);
                        if (!TextUtils.isEmpty(imagePath)) {
                            File file = new File(imagePath);
                            if (file.exists()) {
                                if (mImgUpload != null && mUploadBean != null) {
                                    mUploadBean.setOriginFile(file);
                                    mImgUpload.showImageData(mUploadBean);
                                }
                            }
                        }
                    }
                }
            };
        }
        mImageUtil.startActivityForResult(new Intent(mContext, ChatChooseImageActivity.class), mChooseImageCallback);
    }


    /**
     * 上传图片
     */
    private void uploadImage() {
        if (mUploadBean == null) {
            ToastUtil.show(R.string.album_upload_empty);
            return;
        }
        File file = mUploadBean.getOriginFile();
        if (file == null || !file.exists() || file.length() == 0) {
            ToastUtil.show(R.string.album_upload_empty);
            return;
        }
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        mLoading.show();
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        mUploadStrategy.upload(Arrays.asList(mUploadBean), true, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    L.e(TAG, "上传图片完成---------> " + success);
                    if (list != null && list.size() > 0) {
                        upload(list.get(0).getRemoteFileName());
                    }
                }
            }
        });
    }

    /**
     * 把相册图片信息保存在服务器
     */
    private void upload(String thumb) {
        boolean isPrivate = mCheckBox != null && mCheckBox.isChecked();
        String coin = isPrivate ? mImagePrice : "0";
        MainHttpUtil.setPhoto(thumb, isPrivate ? 1 : 0, coin, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mImgUpload != null && mUploadBean != null) {
                        mUploadBean.setEmpty();
                        mImgUpload.showImageData(mUploadBean);
                    }
                }
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

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_ALBUM_FEE);
        MainHttpUtil.cancel(MainHttpConsts.SET_PHOTO);
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mImageUtil = null;
        super.onDestroy();
    }
}
