package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
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
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.AuthImageAdapter;
import com.aihuan.main.custom.UploadImageView;
import com.aihuan.main.utils.CityUtil;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.custom.ImpressGroup;
import com.aihuan.one.dialog.ChooseImpressDialogFragment;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by cxf on 2019/4/9.
 * 我要认证。
 */

public class AuthInfoActivity extends AbsActivity implements ChooseImpressDialogFragment.ActionListener {

    public static void forward(Context context, int authStauts) {
        Intent intent = new Intent(context, AuthInfoActivity.class);
        intent.putExtra(Constants.AUTH_STATUS, authStauts);
        context.startActivity(intent);
    }

    private static final String TAG = "AuthInfoActivity";
    private ProcessImageUtil mImageUtil;
    private UploadImageView mCover;
    private int mTargetPositon;
    private final int targetThumb = -1;//图片选择封面tag
    private final int targetAvatar = 99;//图片选择头像tag

    private RecyclerView mRecyclerView;
    private AuthImageAdapter mAdapter;
    private Dialog mChooseImageDialog;


    private EditText mHeight;//身高
    private EditText mWeight;//体重
    private TextView mSex;//性别
    private TextView mBirthday;//性别
    private TextView mStar;//星座
    private ImpressGroup mImpressGroup;//形象标签
    private View mImpressTip;//形象标签提示
    private TextView mCity;//城市
    private EditText mIntro;//介绍
    private EditText mSign;//个性签名
    private TextView mIntroNum;//介绍 字数
    private TextView mSignNum;//个性签名 字数

    private Integer[] mSexArray;
    private String[] mStarArray;

    private String mNameVal;
    private String mIdNumVal;
    private String mHeightVal;
    private String mWeightVal;
    private Integer mSexVal;
    private String mBirthdayVal;
    private String mStarVal;
    private String mImpressVal;
    private String mProvinceVal;
    private String mCityVal;
    private String mZoneVal;
    private String mIntroVal;
    private String mSignVal;

    private UploadBean mCoverUploadBean;
    private List<UploadBean> mUploadList;
    private UploadStrategy mUploadStrategy;
    private Dialog mLoading;
    private int mAuthStatus;

    //头像
    private UploadBean mAvatarUploadBean;
    private RoundedImageView ivAvatar;
    private View btnAvatar;
    private String mAvatarVal;
    //是否重新选择了头像
    private boolean changedAvatar = false;

    //昵称
    private EditText etNick;
    private String mNickVal;

    //搭讪招呼语
    private EditText etAccost;
    private String mAccostVal;

    //语音
    private TextView tvAudioStatus;
    private String mVoiceVal;
    private int mVoiceDuration;
    private final int RequestVoiceSetting = 1001;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_info));
        mAuthStatus = getIntent().getIntExtra(Constants.AUTH_STATUS, Constants.AUTH_NONE);
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
                if (mTargetPositon == targetThumb) {
                    if (mCover != null) {
                        mCoverUploadBean.setOriginFile(file);
                        mCover.showImageData(mCoverUploadBean);
                    }
                } else if (mTargetPositon == targetAvatar) {
                    if (ivAvatar != null) {
                        changedAvatar = true;
                        mAvatarUploadBean.setOriginFile(file);
                        ImgLoader.display(mContext, file, ivAvatar);
                    }
                } else {
                    if (mAdapter != null) {
                        mAdapter.updateItem(mTargetPositon, file);
                    }
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mCover = findViewById(R.id.cover);
        mCover.setActionListener(new UploadImageView.ActionListener() {
            @Override
            public void onAddClick(UploadImageView uploadImageView) {
                chooseImage(targetThumb);
            }

            @Override
            public void onDelClick(UploadImageView uploadImageView) {
                if (mCoverUploadBean != null && mCover != null) {
                    mCoverUploadBean.setEmpty();
                    mCover.showImageData(mCoverUploadBean);
                }
            }
        });
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AuthImageAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        //头像
        ivAvatar = findViewById(R.id.iv_avatar);
        btnAvatar = findViewById(R.id.group_avatar);
        mAvatarUploadBean = new UploadBean();
        btnAvatar.setOnClickListener(v -> chooseImage(targetAvatar));
        //昵称
        etNick = findViewById(R.id.et_nickname);
        //搭讪语
        etAccost = findViewById(R.id.et_accost);
        //音频
        tvAudioStatus = findViewById(R.id.tv_audio_status);
        tvAudioStatus.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, VoiceSettingActivity.class);
            intent.putExtra("voice_url", mVoiceVal);
            intent.putExtra("voice_duration", mVoiceDuration);
            startActivityForResult(intent, RequestVoiceSetting);
        });

//        mName = findViewById(R.id.name);//姓名
//        mIdNum = findViewById(R.id.id_card_num);//身份证号
        mHeight = findViewById(R.id.height);//身高
        mWeight = findViewById(R.id.weight);//体重
        mSex = findViewById(R.id.sex);//性别
        if (CommonAppConfig.getInstance().getUserBean().getSex() == 1) {
            mSex.setText(WordUtil.getString(R.string.sex_male));
        } else {
            mSex.setText(WordUtil.getString(R.string.sex_female));
        }

        mBirthday = findViewById(R.id.birthday);//生日
        mBirthdayVal = CommonAppConfig.getInstance().getUserBean().getBirthday();
        if (mBirthdayVal != null && !mBirthdayVal.isEmpty()) {
            mBirthday.setText(mBirthdayVal);
        }

        mStar = findViewById(R.id.star);//星座
        mImpressGroup = findViewById(R.id.impress_group);//形象标签
        mImpressTip = findViewById(R.id.impress_tip);
        mCity = findViewById(R.id.city);//城市
        mIntro = findViewById(R.id.intro);//介绍
        mSign = findViewById(R.id.sign);//个性签名
        mIntroNum = findViewById(R.id.intro_num);//介绍 字数
        mSignNum = findViewById(R.id.sign_num);//个性签名 字数
        if (mAuthStatus == Constants.AUTH_SUCCESS) {
            findViewById(R.id.photo_group).setVisibility(View.GONE);
        }

        mIntro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIntroNum != null) {
                    mIntroNum.setText(s.length() + "/40");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSignNum != null) {
                    mSignNum.setText(s.length() + "/40");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCoverUploadBean = new UploadBean();
        getAuthInfo();
    }

    public void authClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_city) {
            chooseCity();
        } else if (i == R.id.btn_star) {
            chooseXinZuo();
        } else if (i == R.id.btn_sex) {
            //chooseSex();
        } else if (i == R.id.btn_impression) {
            chooseImpress();
        } else if (i == R.id.btn_submit) {
            submit();
        } else if (i == R.id.btn_birthday) {
            chooseDate();
        }
    }

    public void chooseDate() {
        int year = 0, month = 0, day = 0;

        if (mBirthdayVal != null && mBirthdayVal.length() >= 10) {
//            year = mBirthdayVal.substring(0, 3);
//            month = mBirthdayVal.substring(4, 6);
//            day = mBirthdayVal.substring(7);
            ArrayList<Integer> list = DateFormatUtil.parseTimeList(mBirthdayVal);
            year = list.get(0);
            month = list.get(1);
            day = list.get(2);
        }
        L.e("mBirthdayVal:" + mBirthdayVal + "  年： " + year + "   月：" + month + "  day: " + day);
        DialogUitl.showDatePickerDialog(mContext, year, month, day, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(String date) {
                mBirthdayVal = date;
                mBirthday.setText(date);
            }
        });
    }


    /**
     * 选择图片 -- 封面 1  、 背景 2、 头像 3
     */
    public void chooseImage(int targetPositon) {
        mTargetPositon = targetPositon;
        if (mChooseImageDialog == null) {
            mChooseImageDialog = DialogUitl.getStringArrayDialog(mContext, new Integer[]{
                    R.string.camera, R.string.alumb}, true, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    boolean needCrop = mTargetPositon == targetThumb || mTargetPositon == targetAvatar;
                    if (tag == R.string.camera) {
                        mImageUtil.getImageByCamera(needCrop);
                    } else {
                        mImageUtil.getImageByAlumb(needCrop);
                    }
                }
            });
        }
        mChooseImageDialog.show();
    }


    /**
     * 选择性别
     */
    private void chooseSex() {
        if (mSexArray == null) {
            mSexArray = new Integer[]{R.string.sex_male, R.string.sex_female};
        }
        DialogUitl.showStringArrayDialog(mContext, mSexArray, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                mSexVal = tag == mSexArray[0] ? 1 : 2;
                if (mSex != null) {
                    mSex.setText(text);
                }
            }
        });
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
        String district = mZoneVal;
        if (TextUtils.isEmpty(province)) {
            province = CommonAppConfig.getInstance().getProvince();
        }
        if (TextUtils.isEmpty(city)) {
            city = CommonAppConfig.getInstance().getCity();
        }
        if (TextUtils.isEmpty(district)) {
            district = CommonAppConfig.getInstance().getDistrict();
        }
        DialogUitl.showCityChooseDialog(this, list, false, province, city, district, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, final City city, County county) {
                String provinceName = province.getAreaName();
                String cityName = city.getAreaName();
                String zoneName = county.getAreaName();
                mProvinceVal = provinceName;
                mCityVal = cityName;
                mZoneVal = zoneName;
                if (mCity != null) {
                    mCity.setText(provinceName + cityName + zoneName);
                }
            }
        });
    }

    /**
     * 选择星座
     */
    private void chooseXinZuo() {
        if (mStarArray == null) {
            mStarArray = new String[]{
                    WordUtil.getString(R.string.xingzuo_00),
                    WordUtil.getString(R.string.xingzuo_01),
                    WordUtil.getString(R.string.xingzuo_02),
                    WordUtil.getString(R.string.xingzuo_03),
                    WordUtil.getString(R.string.xingzuo_04),
                    WordUtil.getString(R.string.xingzuo_05),
                    WordUtil.getString(R.string.xingzuo_06),
                    WordUtil.getString(R.string.xingzuo_07),
                    WordUtil.getString(R.string.xingzuo_08),
                    WordUtil.getString(R.string.xingzuo_09),
                    WordUtil.getString(R.string.xingzuo_10),
                    WordUtil.getString(R.string.xingzuo_11)
            };
        }
        DialogUitl.showXinZuoDialog(this, mStarArray, new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (mStar != null) {
                    mStar.setText(item.substring(0, item.indexOf("(")));
                }
            }
        });
    }


    /**
     * 选择形象标签
     */
    private void chooseImpress() {
        if (mImpressGroup == null) {
            return;
        }
        ChooseImpressDialogFragment fragment = new ChooseImpressDialogFragment();
        fragment.setCheckImpressList(mImpressGroup.getImpressBeanList());
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "ChooseImpressDialogFragment");
    }


    @Override
    public void onChooseImpress(List<ImpressBean> list) {
        showImpress(list);
    }

    private void showImpress(List<ImpressBean> list) {
        if (mImpressGroup != null) {
            mImpressGroup.showData(list);
        }
        if (list != null && list.size() > 0) {
            if (mImpressTip != null && mImpressTip.getVisibility() == View.VISIBLE) {
                mImpressTip.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mImpressTip != null && mImpressTip.getVisibility() != View.VISIBLE) {
                mImpressTip.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 提交认证信息
     */
    private void submit() {
        mUploadList = new ArrayList<>();
        if (mCoverUploadBean.isEmpty()) {
            ToastUtil.show(R.string.auth_tip_32);
            return;
        }
        mUploadList.add(mCoverUploadBean);
        if (mAdapter.isEmpty()) {
            ToastUtil.show(R.string.auth_tip_33);
            return;
        }
        List<UploadBean> bgUploadList = mAdapter.getList();
        L.e("背景图选择长度： " + bgUploadList.size());
        mUploadList.addAll(bgUploadList);

        if (mAvatarUploadBean.isEmpty()) {
            ToastUtil.show(R.string.avatar_tips);
            return;
        }
        mUploadList.add(mAvatarUploadBean);

        mNickVal = etNick.getText().toString().trim();
        if (TextUtils.isEmpty(mNickVal)) {
            ToastUtil.show(R.string.nickname_tips_input);
            return;
        }
        mAccostVal = etAccost.getText().toString().trim();
        if (TextUtils.isEmpty(mAccostVal)) {
            ToastUtil.show(R.string.accost_word_tips_inpu);
            return;
        }

//     //    mNameVal = mName.getText().toString().trim();
//        if (TextUtils.isEmpty(mNameVal)) {
//            ToastUtil.show(R.string.auth_tip_22);
//            return;
//        }
//        mIdNumVal = mIdNum.getText().toString().trim();
//        if (TextUtils.isEmpty(mIdNumVal)) {
//            ToastUtil.show(R.string.auth_id_num_toast);
//            return;
//        }
        mBirthdayVal = mBirthday.getText().toString().trim();
        if (TextUtils.isEmpty(mBirthdayVal)) {
            ToastUtil.show(R.string.auth_tip_birthday);
            return;
        }
        if (mSexVal == null) {
            if (CommonAppConfig.getInstance().getUserBean().getSex() == 1) {
                mSexVal = 1;
            } else {
                mSexVal = 2;
            }
//            ToastUtil.show(R.string.auth_tip_24);
//            return;
        }
        mHeightVal = mHeight.getText().toString().trim();
        if (TextUtils.isEmpty(mHeightVal)) {
            ToastUtil.show(R.string.auth_tip_25);
            return;
        }
        mWeightVal = mWeight.getText().toString().trim();
        if (TextUtils.isEmpty(mWeightVal)) {
            ToastUtil.show(R.string.auth_tip_26);
            return;
        }
        mStarVal = mStar.getText().toString().trim();
        if (TextUtils.isEmpty(mStarVal)) {
            ToastUtil.show(R.string.auth_tip_27);
            return;
        }
        List<ImpressBean> impressBeanList = mImpressGroup.getImpressBeanList();
        if (impressBeanList == null || impressBeanList.size() == 0) {
            ToastUtil.show(R.string.auth_tip_28);
            return;
        }
        mImpressVal = "";
        for (int i = 0, size = impressBeanList.size(); i < size; i++) {
            mImpressVal += impressBeanList.get(i).getId();
            if (i < size - 1) {
                mImpressVal += ",";
            }
        }
        String address = mCity.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtil.show(R.string.auth_tip_29);
            return;
        }
        mIntroVal = mIntro.getText().toString().trim();
        if (TextUtils.isEmpty(mIntroVal)) {
            ToastUtil.show(R.string.auth_tip_30);
            return;
        }
        mSignVal = mSign.getText().toString().trim();
        if (TextUtils.isEmpty(mSignVal)) {
            ToastUtil.show(R.string.auth_tip_31);
            return;
        }
         uploadFile();

    }

    /**
     * 上传图片
     */
    private void uploadFile() {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext);
        }
        mLoading.show();
        L.e(TAG, "上传图片开始--------->");
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        mUploadStrategy.upload(mUploadList, true, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    L.e(TAG, "上传图片完成---------> " + list.size() + "  结果：" + list.toString());
                    if (list != null && list.size() > 1) {
                        String thumb = list.get(0).getRemoteFileName();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1, size = list.size() - 1; i < size; i++) {
                            String fileName = list.get(i).getRemoteFileName();
                            if (!TextUtils.isEmpty(fileName)) {
                                sb.append(fileName);
                                sb.append(",");
                            }
                        }
                        String photos = sb.toString();
                        if (photos.length() > 1) {
                            photos = photos.substring(0, photos.length() - 1);
                        }
                        if (changedAvatar) {
                            String avatar = list.get(list.size() - 1).getRemoteFileName();
                            doSubmit(thumb, photos, avatar);
                        } else {
                            doSubmit(thumb, photos, mAvatarUploadBean.getRemoteFileName());
                        }
                    }
                }
            }
        });
    }

    private void doSubmit(String thumb, String photos, String avatar) {
        OneHttpUtil.setAuth(thumb, photos, mNameVal, mIdNumVal, mSexVal, mBirthdayVal, mHeightVal, mWeightVal, mStarVal,
                mImpressVal, mProvinceVal, mCityVal, mZoneVal, mIntroVal, mSignVal,
                avatar, mNickVal, mAccostVal, mVoiceVal, mVoiceDuration, 1, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
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
                }
        );
    }


    private void getAuthInfo() {
        if (mAuthStatus == Constants.AUTH_NONE) {
            OneHttpUtil.getAuth(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mNameVal = obj.getString("name");
                        mIdNumVal =obj.getString("id_card");
                        EditText nameInput = findViewById(R.id.name);
                        nameInput.setText(mNameVal);
                        EditText cardInput =findViewById(R.id.id_card_num);
                        cardInput.setText(mIdNumVal);
                    }
                }
            });
            return;
        }
        OneHttpUtil.getAuth(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mCover != null) {
                        String thumb = obj.getString("thumb");
                        if (!TextUtils.isEmpty(thumb)){
                            mCoverUploadBean.setRemoteAccessUrl(thumb);
                            mCoverUploadBean.setRemoteFileName(thumb.substring(thumb.lastIndexOf("/") + 1));
                            mCover.showImageData(mCoverUploadBean);
                        }
                    }
                    if (mAdapter != null) {
                        List<String> imageUrlList = JSON.parseArray(obj.getString("photos_list"), String.class);
                        List<UploadBean> bgList = new ArrayList<>();
                        for (String imageUrl : imageUrlList) {
                            if (!TextUtils.isEmpty(imageUrl)){
                                UploadBean bean = new UploadBean();
                                bean.setRemoteAccessUrl(imageUrl);
                                bean.setRemoteFileName(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
                                bgList.add(bean);
                            }
                        }
                        mAdapter.setList(bgList);
                    }

                    mAvatarVal = obj.getString("avatar");
                    if (ivAvatar != null && mAvatarVal != null && !mAvatarVal.isEmpty()) {
                        mAvatarUploadBean.setRemoteAccessUrl(mAvatarVal);
                        mAvatarUploadBean.setRemoteFileName(mAvatarVal.substring(mAvatarVal.lastIndexOf("/") + 1));
                        ImgLoader.displayAvatar(mContext, mAvatarVal, ivAvatar);
                    }
                    mNickVal = obj.getString("user_nickname");
                    if (etNick != null) {
                        etNick.setText(mNickVal);
                    }
                    mAccostVal = obj.getString("accost_text");
                    if (etAccost != null) {
                        etAccost.setText(mAccostVal);
                    }
                    mVoiceVal = obj.getString("f_voice");
                    mVoiceDuration = obj.getIntValue("f_voice_duration");
                    if (mVoiceVal != null && !mVoiceVal.isEmpty()) {
                        if (tvAudioStatus != null) {
                            tvAudioStatus.setText(R.string.has_setting);
                        }

                    } else {
                        if (tvAudioStatus != null) {
                            tvAudioStatus.setText(R.string.not_setting);
                        }
                    }
                    mNameVal = obj.getString("name");
//                    if (mName != null) {
//                        mName.setText(mNameVal);
//                    }
                    mIdNumVal = obj.getString("id_card");
//                    if (mIdNum != null) {
//                        mIdNum.setText(mIdNumVal);
//                    }
                //    int isGet = obj.getIntValue("is_alipay_auth");
                    //当姓名、身份证号不为空即代表已实人认证过
//                    hasVerify = isGet == 1;
//                    if (hasVerify) {
//                        mName.setEnabled(false);
//                        mIdNum.setEnabled(false);
//                    }

                    mSexVal = obj.getInteger("sex");
                    if (mSex != null) {
                        mSex.setText(mSexVal == 1 ? R.string.sex_male : R.string.sex_female);
                    }
                    mBirthdayVal = obj.getString("birthday");
                    if (mBirthday != null) {
                        mBirthday.setText(mBirthdayVal);
                    }
                    mHeightVal = obj.getString("height");
                    if (mHeight != null) {
                        mHeight.setText(mHeightVal);
                    }
                    mWeightVal = obj.getString("weight");
                    if (mWeight != null) {
                        mWeight.setText(mWeightVal);
                    }
                    mStarVal = obj.getString("constellation");
                    if (mStar != null) {
                        mStar.setText(mStarVal);
                    }
                    mProvinceVal = obj.getString("province");
                    mCityVal = obj.getString("city");
                    mZoneVal = obj.getString("district");
                    if (mCity != null) {
                        mCity.setText(StringUtil.contact(mProvinceVal, mCityVal, mZoneVal));
                    }
                    mIntroVal = obj.getString("intr");
                    if (mIntro != null) {
                        mIntro.setText(mIntroVal);
                    }
                    mSignVal = obj.getString("signature");
                    if (mSign != null) {
                        mSign.setText(mSignVal);
                    }
                    List<ImpressBean> impressBeanList = JSON.parseArray(obj.getString("label_list"), ImpressBean.class);
                    showImpress(impressBeanList);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_AUTH);
        OneHttpUtil.cancel(OneHttpConsts.SET_AUTH);
        if (mAdapter != null) {
            mAdapter.release();
        }
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mImageUtil = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestVoiceSetting && resultCode == RESULT_OK) {
            mVoiceVal = data.getStringExtra("voice_url");
            mVoiceDuration = data.getIntExtra("voice_duration", 0);
            if (mVoiceVal != null && !mVoiceVal.isEmpty()) {
                if (tvAudioStatus != null) {
                    tvAudioStatus.setText(R.string.has_setting);
                }

            } else {
                if (tvAudioStatus != null) {
                    tvAudioStatus.setText(R.string.not_setting);
                }
            }
        }
        L.e("设置的语音文件地址：" + mVoiceVal + "  时长： " + mVoiceDuration);
    }










}
