package com.aihuan.main.activity;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadQnImpl;
import com.aihuan.common.upload.UploadStrategy;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.BaseBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class EditProfileActivity extends AbsActivity implements View.OnClickListener {

    private ImageView mAvatar;
    private EditText mName;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private String mAvatarRemoteFileName;
    private String mNameVal;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;

    private View btnAvatar;
    private TextView tvStatus;
    private BaseBean baseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        tvStatus = findViewById(R.id.tv_auth_status);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        findViewById(R.id.btn_save).setOnClickListener(this);
        btnAvatar = findViewById(R.id.btn_avatar);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });
        getAuthStatus();
    }

    private void getAuthStatus() {
        MainHttpUtil.getUserBaseStatus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    baseBean = JSON.parseObject(info[0], BaseBean.class);
                    showData(baseBean);
                }
            }
        });
    }

//    private void getUser() {
//        UserBean u = CommonAppConfig.getInstance().getUserBean();
//        if (u != null) {
//            showData(u);
//        } else {
//            MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
//                @Override
//                public void callback(UserBean u) {
//                    showData(u);
//                }
//            });
//        }
//    }

    private void showData(BaseBean u) {
        //0-待审核,1-同意,2-拒绝
        if (TextUtils.equals("0", u.getStatus())) {
            btnAvatar.setOnClickListener(null);
            mName.setEnabled(false);
            tvStatus.setText(R.string.file_in_auth);
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            if (TextUtils.equals("2", u.getStatus())) {
                tvStatus.setText(u.getReason());
                tvStatus.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setVisibility(View.INVISIBLE);
            }
            mName.setEnabled(true);
            btnAvatar.setOnClickListener(this);
        }
//        if (TextUtils.isEmpty(u.getAvatar())) {
//            ImgLoader.displayAvatar(mContext, mUserBean.getAvatar(), mAvatar);
//        } else {
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
//        }

        String name = u.getNickname();
//        if (TextUtils.isEmpty(name)) {
//            name = mUserBean.getUserNiceName();
//        }
        if (!TextUtils.isEmpty(name)) {
            if (name.length() > 7) {
                name = name.substring(0, 7);
            }
            mName.setText(name);
            mName.setSelection(name.length());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_save) {
            save();
        } else if (i == R.id.btn_avatar) {
            editAvatar();
        }
    }

    private void editAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }


    /**
     * 保存
     */
    public void save() {
        mNameVal = mName.getText().toString().trim();
        if (TextUtils.isEmpty(mNameVal)) {
            ToastUtil.show(R.string.edit_profile_name_empty);
            return;
        }
        if (baseBean != null && mNameVal.equals(baseBean.getNickname()) && mAvatarFile == null) {
            ToastUtil.show(R.string.edit_profile_not_update);
            return;
        }
        if (mAvatarFile != null) {
            uploadAvatarImage();
        } else {
            mAvatarRemoteFileName = baseBean.getAvatar();
            submit();
        }
    }

    /**
     * 上传头像
     */
    private void uploadAvatarImage() {
        mLoading = DialogUitl.loadingDialog(mContext);
        mLoading.show();
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        List<UploadBean> list = new ArrayList<>();
        list.add(new UploadBean(mAvatarFile));
        mUploadStrategy.upload(list, true, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    if (list != null && list.size() > 0) {
                        mAvatarRemoteFileName = list.get(0).getRemoteFileName();
                    }
                }
                submit();
            }
        });
    }

    private void submit() {
        MainHttpUtil.updateUserInfo(mAvatarRemoteFileName, mNameVal, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                getAuthStatus();
//                if (code == 0 && info.length > 0) {
//                    JSONObject obj = JSON.parseObject(info[0]);
//                    UserBean u = CommonAppConfig.getInstance().getUserBean();
//                    if (u != null) {
//                        u.setUserNiceName(obj.getString("user_nickname"));
//                        u.setAvatar(obj.getString("avatar"));
//                        u.setAvatarThumb(obj.getString("avatar_thumb"));
//                    }
//                    finish();
//                }
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
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_USER_INFO);
        super.onDestroy();
    }
}
