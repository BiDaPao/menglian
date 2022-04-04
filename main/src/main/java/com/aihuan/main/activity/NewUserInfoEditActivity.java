package com.aihuan.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.LocationEvent;
import com.aihuan.common.event.LocationGetCityEvent;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadQnImpl;
import com.aihuan.common.upload.UploadStrategy;
import com.aihuan.common.utils.DateFormatUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.LocationUtil;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.utils.CityUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by Sky.L on 2019/10/23
 */
public class NewUserInfoEditActivity extends AbsActivity {
    private static final String TAG = "NewUserInfoEditActivity";
    private String mUserAvatar = "";
    private String mUserName = "";
    private boolean mIsByThird;
    private byte mSex = 0;
    private RadioButton mBtnSexMale;
    private RadioButton mBtnSexFamale;
    private EditText edt_nickname;
    private TextView tv_location;
    private ImageView iv_avatar;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private String mAvatarRemoteFileName;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private ProcessResultUtil mProcessResultUtil;
    private boolean mFirstLoad;

    private View birthday;
    private TextView mBirthday;
    private String mBirthdayVal;


    private String mProvinceVal;
    private String mCityVal;
//    private String mZoneVal;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_user_info_edit;
    }

    public static void forward(Context context, boolean isByThird, String userName, String userAvatar) {
        Intent intent = new Intent(context, NewUserInfoEditActivity.class);
        intent.putExtra(Constants.USER_LOGIN_BY_THIRD, isByThird);
        intent.putExtra(Constants.USER_AVATAT, userAvatar);
        intent.putExtra(Constants.USER_NICK_NAME, userName);
        context.startActivity(intent);
    }

    @Override
    protected void main() {
        super.main();
        mProcessResultUtil = new ProcessResultUtil(this);
        mFirstLoad = true;
        Intent intent = getIntent();
        mIsByThird = intent.getBooleanExtra(Constants.USER_LOGIN_BY_THIRD, false);
        if (mIsByThird) {
            mUserAvatar = intent.getStringExtra(Constants.USER_AVATAT);
            mUserName = intent.getStringExtra(Constants.USER_NICK_NAME);
        }
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, iv_avatar);
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            getLocation();
        }
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        mBtnSexMale = (RadioButton) findViewById(R.id.btn_sex_male);
        mBtnSexFamale = (RadioButton) findViewById(R.id.btn_sex_famale);
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_location = findViewById(R.id.tv_location);
        String city = CommonAppConfig.getInstance().getCity();
        if (!TextUtils.isEmpty(city)) {
            tv_location.setText(city);
            mCity = city;
        }
        edt_nickname = findViewById(R.id.edt_nickname);
        if (mIsByThird) {
            showData();
        }
        birthday = findViewById(R.id.btn_birthday);
        mBirthday = findViewById(R.id.birthday);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCity();
            }
        });
    }

    private void showData() {
        ImgLoader.displayAvatar(mContext, mUserAvatar, iv_avatar);
        String name = mUserName;
        mAvatarRemoteFileName = mUserAvatar;
        if (!mAvatarRemoteFileName.startsWith("android_")) {
            try {
                String encode = URLEncoder.encode(mAvatarRemoteFileName, "utf-8");
                mAvatarRemoteFileName = encode;
            } catch (UnsupportedEncodingException e) {
                L.e(TAG, "_____URL编码失败______");
                e.printStackTrace();
            }

        }
        if (!TextUtils.isEmpty(name)) {
            if (name.length() > 7) {
                name = name.substring(0, 7);
            }
            edt_nickname.setText(name);
            edt_nickname.setSelection(name.length());
        }
    }

    public void onNewUserInfoEditActivityClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_avatar) {
            editAvatar();
        } else if (id == R.id.btn_confirm) {
            onConfirm();
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


    private void onConfirm() {
        mUserName = edt_nickname.getText().toString();
        if (TextUtils.isEmpty(mUserName)) {
            ToastUtil.show(R.string.edit_profile_name_empty);
            return;
        }
        if (mBtnSexMale.isChecked()) {
            mSex = Constants.MAIN_SEX_MALE;
        } else if (mBtnSexFamale.isChecked()) {
            mSex = Constants.MAIN_SEX_FAMALE;
        }
        if (mSex == 0) {
            ToastUtil.show(R.string.new_user_info_edit_9);
            return;
        }
        if (mBirthdayVal == null || mBirthdayVal.isEmpty()) {
            ToastUtil.show(R.string.auth_tip_birthday);
            return;
        }
        if (TextUtils.isEmpty(mCity)) {
            ToastUtil.show("请选择城市");
            return;
        }
        if (mAvatarFile == null) {
            //若是三方账号登录，并且没有手动更换头像，直接使用三方账号的头像
            if (mIsByThird) {
                submit();
                return;
            }
            ToastUtil.show(R.string.new_user_info_edit_8);
            return;
        } else {
            uploadAvatarImage();
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

    /**
     * 选择出生日期
     */
    public void chooseDate() {
        int year = 0, month = 0, day = 0;

        if (mBirthdayVal != null && mBirthdayVal.length() >= 10) {
            ArrayList<Integer> list = DateFormatUtil.parseTimeList(mBirthdayVal);
            year = list.get(0);
            month = list.get(1);
            day = list.get(2);
        }
        DialogUitl.showDatePickerDialog(mContext, year, month, day, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(String date) {
                mBirthdayVal = date;
                mBirthday.setText(date);
            }
        });
    }

    private void submit() {
        MainHttpUtil.regUpdateInfo(mAvatarRemoteFileName, mUserName, mSex, mBirthdayVal, mCity, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    UserBean u = CommonAppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setUserNiceName(obj.getString("user_nickname"));
                        u.setAvatar(obj.getString("avatar"));
                        u.setAvatarThumb(obj.getString("avatar_thumb"));
                        u.setSex(obj.getIntValue("sex"));
                    }
                    MainActivity.forward(mContext, true);
                    finish();
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

    /**
     * 获取所在位置, 启动定位
     */
    private void getLocation() {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result != null && result) {
                    LocationUtil.getInstance().startLocation();
                }
            }
        });
    }

    /**
     * 清除用户登录信息
     */
    private void clearUserInfo() {
        CommonAppConfig.getInstance().clearLoginInfo();
        MobclickAgent.onProfileSignOff();
        LoginActivity.forward();
        finish();
    }

    @Override
    public void onBackPressed() {
        clearUserInfo();
    }


    String mProvince;
    String mCity;
    String mDistrict;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationGetCityEvent e) {
        CommonAppConfig commonAppConfig = CommonAppConfig.getInstance();
        mProvince = commonAppConfig.getProvince();
        mCity = commonAppConfig.getCity();
        mDistrict = commonAppConfig.getDistrict();
        if (tv_location != null && TextUtils.isEmpty(tv_location.getText().toString())) {
            tv_location.setText(mCity);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 选择城市
     */
    private void chooseCity() {
        ArrayList<Province> list = CityUtil.getInstance().getCityList();
        if (list == null || list.size() == 0) {
            final Dialog loading = DialogUitl.loadingDialog(mContext);
            loading.show();
            CityUtil.getInstance().getCityListFromAssets(new CommonCallback<ArrayList<Province>>() {
                @Override
                public void callback(ArrayList<Province> newList) {
                    loading.dismiss();
                    if (newList != null) {
                        showChooseCityDialog(newList);
                    }
                }
            });
        } else {
            showChooseCityDialog(list);
        }
    }

    /**
     * 选择城市
     */
    private void showChooseCityDialog(ArrayList<Province> list) {
        String province = mProvinceVal;
        String city = mCityVal;
//        String district = mZoneVal;
        if (TextUtils.isEmpty(province)) {
            province = CommonAppConfig.getInstance().getProvince();
        }
        if (TextUtils.isEmpty(city)) {
            city = CommonAppConfig.getInstance().getCity();
        }
//        if (TextUtils.isEmpty(district)) {
//            district = CommonAppConfig.getInstance().getDistrict();
//        }
        DialogUitl.showCityChooseDialog(this, list, true, province, city, "", new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, final City city, County county) {
                String provinceName = province.getAreaName();
                String cityName = city.getAreaName();
//                String zoneName = county.getAreaName();
                mProvinceVal = provinceName;
                mCityVal = cityName;
//                mZoneVal = zoneName;
                if (tv_location != null) {
                    mCity = cityName;
                    tv_location.setText(cityName);
                }
            }
        });
    }
}
