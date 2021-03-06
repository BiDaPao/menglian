//package com.yunbao.video.activity;
//
//import android.animation.ValueAnimator;
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.tencent.rtmp.TXLiveConstants;
//import com.tencent.rtmp.ui.TXCloudVideoView;
//import com.tencent.ugc.TXRecordCommon;
//import com.tencent.ugc.TXUGCPartsManager;
//import com.tencent.ugc.TXUGCRecord;
//import com.yunbao.beauty.bean.FilterBean;
//import com.yunbao.beauty.interfaces.BeautyViewHolder;
//import com.yunbao.beauty.interfaces.DefaultBeautyEffectListener;
//import com.yunbao.beauty.interfaces.TiBeautyEffectListener;
//import com.yunbao.beauty.views.DefaultBeautyViewHolder;
//import com.yunbao.beauty.views.TiBeautyViewHolder;
//import com.yunbao.common.CommonAppConfig;
//import com.yunbao.common.CommonAppContext;
//import com.yunbao.common.Constants;
//import com.yunbao.common.activity.AbsActivity;
//import com.yunbao.common.bean.ConfigBean;
//import com.yunbao.common.custom.DrawableRadioButton2;
//import com.yunbao.common.utils.BitmapUtil;
//import com.yunbao.common.utils.DialogUitl;
//import com.yunbao.common.utils.L;
//import com.yunbao.common.utils.StringUtil;
//import com.yunbao.common.utils.ToastUtil;
//import com.yunbao.common.utils.WordUtil;
//import com.yunbao.video.R;
//import com.yunbao.video.bean.MusicBean;
//import com.yunbao.video.custom.RecordProgressView;
//import com.yunbao.video.custom.VideoRecordBtnView;
//import com.yunbao.video.views.VideoMusicViewHolder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.tillusory.sdk.TiSDKManager;
//import cn.tillusory.sdk.TiSDKManagerBuilder;
//import cn.tillusory.sdk.bean.TiDistortionEnum;
//import cn.tillusory.sdk.bean.TiFilterEnum;
//import cn.tillusory.sdk.bean.TiRockEnum;
//import cn.tillusory.sdk.bean.TiRotation;
//
///**
// * Created by cxf on 2018/12/5.
// * ????????????
// */
//
//public class VideoRecordActivity extends AbsActivity implements
//        TXRecordCommon.ITXVideoRecordListener, //????????????????????????
//        TiBeautyEffectListener, //????????????????????????
//        TXUGCRecord.VideoCustomProcessListener //?????????????????????????????????????????????????????????
//{
//
//    private static final String TAG = "VideoRecordActivity";
//    private static final int MIN_DURATION = 5000;//??????????????????5s
//    private static final int MAX_DURATION = 15000;//??????????????????15s
//    //??????????????????
//    private VideoRecordBtnView mVideoRecordBtnView;
//    private View mRecordView;
//    private ValueAnimator mRecordBtnAnimator;//????????????????????????????????????
//    private Drawable mRecordDrawable;
//    private Drawable mUnRecordDrawable;
//
//    /****************************/
//    private boolean mRecordStarted;//????????????????????????true ????????? false ????????????
//    private boolean mRecordStoped;//?????????????????????
//    private boolean mRecording;//????????????????????????true ????????? false ????????????
//    private ViewGroup mRoot;
//    private TXCloudVideoView mVideoView;//????????????
//    private RecordProgressView mRecordProgressView;//???????????????
//    private TextView mTime;//????????????
//    private DrawableRadioButton2 mBtnFlash;//???????????????
//    private TXUGCRecord mRecorder;//?????????
//    private TXRecordCommon.TXUGCCustomConfig mCustomConfig;//??????????????????
//    private boolean mFrontCamera = true;//????????????????????????
//    private String mVideoPath;//?????????????????????
//    private int mRecordSpeed;//????????????
//    private View mGroup1;
//    private View mGroup2;
//    private View mGroup3;
//    private View mGroup4;
//    private View mBtnNext;//????????????????????????
//    private Dialog mStopRecordDialog;//????????????????????????dialog
//    private boolean mIsReachMaxRecordDuration;//??????????????????????????????
//    private long mDuration;//?????????????????????
//    private BeautyViewHolder mBeautyViewHolder;
//    private TiSDKManager mTiSDKManager;//?????????????????????
//    private VideoMusicViewHolder mVideoMusicViewHolder;
//    private MusicBean mMusicBean;//????????????
//    private boolean mHasBgm;
//    private boolean mBgmPlayStarted;//???????????????????????????????????????
//    private Bitmap mFilterBitmap;//?????????????????????
//    //??????????????????
//    private int mMeibai = 0;//??????
//    private int mMoPi = 0;//??????
//    private int mBaoHe = 0;//??????
//    private int mFengNen = 0;//??????
//    private int mBigEye = 0;//??????
//    private int mFace = 0;//??????
//    private String mTieZhi = "";//??????
//    private TiFilterEnum mTiFilterEnum = TiFilterEnum.NO_FILTER;//??????
//    private TiDistortionEnum mTiDistortionEnum = TiDistortionEnum.NO_DISTORTION;//?????????
//    private TiRockEnum mTiRockEnum = TiRockEnum.NO_ROCK;//??????
//    private boolean mFristInitBeauty = true;
//    private long mRecordTime;
//    private boolean mIsDynamicVideo;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_video_record;
//    }
//
//    @Override
//    protected boolean isStatusBarWhite() {
//        return true;
//    }
//
//    @Override
//    protected void main() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        //?????????????????????
//        mIsDynamicVideo=getIntent().getBooleanExtra(Constants.DYNAMIC_VIDEO,false);
//        //??????????????????
//        mVideoRecordBtnView = (VideoRecordBtnView) findViewById(R.id.record_btn_view);
//        mRecordView = findViewById(R.id.record_view);
//        mUnRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_1);
//        mRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_2);
//        mRecordBtnAnimator = ValueAnimator.ofFloat(100, 0);
//        mRecordBtnAnimator.setDuration(500);
//        mRecordBtnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float v = (float) animation.getAnimatedValue();
//                if (mVideoRecordBtnView != null) {
//                    mVideoRecordBtnView.setRate((int) v);
//                }
//            }
//        });
//        mRecordBtnAnimator.setRepeatCount(-1);
//        mRecordBtnAnimator.setRepeatMode(ValueAnimator.REVERSE);
//
//        /****************************/
//        mRoot = (ViewGroup) findViewById(R.id.root);
//        mGroup1 = findViewById(R.id.group_1);
//        mGroup2 = findViewById(R.id.group_2);
//        mGroup3 = findViewById(R.id.group_3);
//        mGroup4 = findViewById(R.id.group_4);
//        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
//        //mVideoView.enableHardwareDecode(true);
//        mTime = findViewById(R.id.time);
//        mRecordProgressView = (RecordProgressView) findViewById(R.id.record_progress_view);
//        mRecordProgressView.setMaxDuration(MAX_DURATION);
//        mRecordProgressView.setMinDuration(MIN_DURATION);
//        mBtnFlash = (DrawableRadioButton2) findViewById(R.id.btn_flash);
//        mBtnNext = findViewById(R.id.btn_next);
//        initCameraRecord();
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void initCameraRecord() {
//        mRecorder = TXUGCRecord.getInstance(CommonAppContext.sInstance);
//        mRecorder.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
//        mRecorder.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
//        mRecordSpeed = TXRecordCommon.RECORD_SPEED_NORMAL;
//        mRecorder.setRecordSpeed(mRecordSpeed);
//        mRecorder.setAspectRatio(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);
//        mCustomConfig = new TXRecordCommon.TXUGCCustomConfig();
//        mCustomConfig.videoResolution = TXRecordCommon.VIDEO_RESOLUTION_1080_1920;
//        mCustomConfig.minDuration = MIN_DURATION;
//        mCustomConfig.maxDuration = MAX_DURATION;
//        mCustomConfig.videoBitrate = 6500;
//        mCustomConfig.videoGop = 3;
//        mCustomConfig.videoFps = 20;
//        mCustomConfig.isFront = mFrontCamera;
//        mRecorder.setVideoRecordListener(this);
//    }
//
//
//    /**
//     * ???????????????
//     */
//    private void initBeauty() {
//        try {
//            mTiSDKManager = new TiSDKManagerBuilder().build();
//            mTiSDKManager.setBeautyEnable(true);
//            mTiSDKManager.setFaceTrimEnable(true);
//            if (mFristInitBeauty) {
//                mFristInitBeauty = false;
//                ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
//                if (configBean != null) {
//                    mMeibai = configBean.getBeautyMeiBai();//??????
//                    mMoPi = configBean.getBeautyMoPi();//??????
//                    mBaoHe = configBean.getBeautyBaoHe();//??????
//                    mFengNen = configBean.getBeautyFenNen();//??????
//                    mBigEye = configBean.getBeautyBigEye();//??????
//                    mFace = configBean.getBeautyFace();//??????
//                }
//            }
//            mTiSDKManager.setSkinWhitening(mMeibai);//??????
//            mTiSDKManager.setSkinBlemishRemoval(mMoPi);//??????
//            mTiSDKManager.setSkinSaturation(mBaoHe);//??????
//            mTiSDKManager.setSkinTenderness(mFengNen);//??????
//            mTiSDKManager.setEyeMagnifying(mBigEye);//??????
//            mTiSDKManager.setChinSlimming(mFace);//??????
//            mTiSDKManager.setSticker(mTieZhi);//??????
//            mTiSDKManager.setFilterEnum(mTiFilterEnum);//??????
//            mTiSDKManager.setDistortionEnum(mTiDistortionEnum);//?????????
//        } catch (Exception e) {
//            ToastUtil.show(R.string.beauty_init_error);
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    @Override
//    public void onRecordEvent(int event, Bundle bundle) {
//        if (event == TXRecordCommon.EVT_ID_PAUSE) {
//            if (mRecordProgressView != null) {
//                mRecordProgressView.clipComplete();
//            }
//        } else if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
//            ToastUtil.show(R.string.video_record_camera_failed);
//        } else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
//            ToastUtil.show(R.string.video_record_audio_failed);
//        }
//    }
//
//    /**
//     * ???????????? ????????????
//     */
//    @Override
//    public void onRecordProgress(long milliSecond) {
//        if (mRecordProgressView != null) {
//            mRecordProgressView.setProgress((int) milliSecond);
//        }
//        if (mTime != null) {
//            mTime.setText(String.format("%.2f", milliSecond / 1000f) + "s");
//        }
//        mRecordTime = milliSecond;
//        if (milliSecond >= MIN_DURATION) {
//            if (mBtnNext != null && mBtnNext.getVisibility() != View.VISIBLE) {
//                mBtnNext.setVisibility(View.VISIBLE);
//            }
//        }
//        if (milliSecond >= MAX_DURATION) {
//            if (!mIsReachMaxRecordDuration) {
//                mIsReachMaxRecordDuration = true;
//                if (mRecordBtnAnimator != null) {
//                    mRecordBtnAnimator.cancel();
//                }
//                showProccessDialog();
//            }
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    @Override
//    public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
//        hideProccessDialog();
//        mRecordStarted = false;
//        mRecordStoped = true;
//        if (mRecorder != null) {
//            mRecorder.toggleTorch(false);
//            mRecorder.stopBGM();
//            mDuration = mRecorder.getPartsManager().getDuration();
//        }
//        if (result.retCode < 0) {
//            release();
//            ToastUtil.show(R.string.video_record_failed);
//        } else {
//            VideoEditActivity.forward(mContext, mDuration, mVideoPath, true, mHasBgm,mIsDynamicVideo);
//        }
//        finish();
//    }
//
//
//    /**
//     * ???????????????????????????
//     */
//    @Override
//    public int onTextureCustomProcess(int i, int i1, int i2) {
//        if (mTiSDKManager != null) {
//            return mTiSDKManager.renderTexture2D(i, i1, i2, TiRotation.CLOCKWISE_ROTATION_0, false);
//        }
//        return 0;
//    }
//
//    /**
//     * ???????????????????????????
//     */
//    @Override
//    public void onDetectFacePoints(float[] floats) {
//
//    }
//
//    /**
//     * ???????????????????????????
//     */
//    @Override
//    public void onTextureDestroyed() {
//
//    }
//
//    public void recordClick(View v) {
//        if (mRecordStoped || !canClick()) {
//            return;
//        }
//        int i = v.getId();
//        if (i == R.id.btn_start_record) {
//            clickRecord();
//
//        } else if (i == R.id.btn_camera) {
//            clickCamera();
//
//        } else if (i == R.id.btn_flash) {
//            clickFlash();
//
//        } else if (i == R.id.btn_beauty) {
//            clickBeauty();
//
//        } else if (i == R.id.btn_music) {
//            clickMusic();
//
//        } else if (i == R.id.btn_speed_1) {
//            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOWEST);
//
//        } else if (i == R.id.btn_speed_2) {
//            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOW);
//
//        } else if (i == R.id.btn_speed_3) {
//            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_NORMAL);
//
//        } else if (i == R.id.btn_speed_4) {
//            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FAST);
//
//        } else if (i == R.id.btn_speed_5) {
//            changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FASTEST);
//
//        } else if (i == R.id.btn_upload) {
//            clickUpload();
//
//        } else if (i == R.id.btn_delete) {
//            clickDelete();
//
//        } else if (i == R.id.btn_next) {
//            clickNext();
//
//        }
//    }
//
//    /**
//     * ???????????????
//     */
//    private void clickCamera() {
//        if (mRecorder != null) {
//            if (mBtnFlash != null && mBtnFlash.isChecked()) {
//                mBtnFlash.doToggle();
//                mRecorder.toggleTorch(mBtnFlash.isChecked());
//            }
//            mFrontCamera = !mFrontCamera;
//            mRecorder.switchCamera(mFrontCamera);
//        }
//    }
//
//    /**
//     * ???????????????
//     */
//    private void clickFlash() {
//        if (mFrontCamera) {
//            ToastUtil.show(R.string.live_open_flash);
//            return;
//        }
//        if (mBtnFlash != null) {
//            mBtnFlash.doToggle();
//            if (mRecorder != null) {
//                mRecorder.toggleTorch(mBtnFlash.isChecked());
//            }
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void clickBeauty() {
//        if (mBeautyViewHolder == null) {
//            if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//                mBeautyViewHolder = new TiBeautyViewHolder(mContext, mRoot);
//            } else {
//                mBeautyViewHolder = new DefaultBeautyViewHolder(mContext, mRoot);
//            }
//            mBeautyViewHolder.setVisibleListener(new DefaultBeautyViewHolder.VisibleListener() {
//                @Override
//                public void onVisibleChanged(boolean visible) {
//                    if (mGroup1 != null) {
//                        if (visible) {
//                            if (mGroup1.getVisibility() == View.VISIBLE) {
//                                mGroup1.setVisibility(View.INVISIBLE);
//                            }
//                        } else {
//                            if (mGroup1.getVisibility() != View.VISIBLE) {
//                                mGroup1.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                }
//            });
//            if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//                mBeautyViewHolder.setEffectListener(this);//??????
//            } else {
//                mBeautyViewHolder.setEffectListener(mBaseBeautyEffectListener);//????????????
//            }
//        }
//        mBeautyViewHolder.show();
//    }
//
//    //????????????????????? ???????????????
//    private DefaultBeautyEffectListener mBaseBeautyEffectListener = new DefaultBeautyEffectListener() {
//        @Override
//        public void onFilterChanged(FilterBean filterBean) {
//            if (mFilterBitmap != null) {
//                mFilterBitmap.recycle();
//            }
//            if (mRecorder != null) {
//                int filterSrc = filterBean.getFilterSrc();
//                if (filterSrc != 0) {
//                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
//                    if (bitmap != null) {
//                        mFilterBitmap = bitmap;
//                        mRecorder.setFilter(bitmap);
//                    } else {
//                        mRecorder.setFilter(null);
//                    }
//                } else {
//                    mRecorder.setFilter(null);
//                }
//            }
//        }
//
//        @Override
//        public void onMeiBaiChanged(int progress) {
//            if (mRecorder != null) {
//                int v = progress / 10;
//                if (mMeibai != v) {
//                    mMeibai = v;
//                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
//                }
//            }
//
//        }
//
//        @Override
//        public void onMoPiChanged(int progress) {
//            if (mRecorder != null) {
//                int v = progress / 10;
//                if (mMoPi != v) {
//                    mMoPi = v;
//                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
//                }
//            }
//        }
//
//        @Override
//        public void onHongRunChanged(int progress) {
//            if (mRecorder != null) {
//                int v = progress / 10;
//                if (mFengNen != v) {
//                    mFengNen = v;
//                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
//                }
//            }
//        }
//    };
//
//    /**
//     * ????????????
//     */
//    private void clickMusic() {
//        if (mVideoMusicViewHolder == null) {
//            mVideoMusicViewHolder = new VideoMusicViewHolder(mContext, mRoot);
//            mVideoMusicViewHolder.setActionListener(new VideoMusicViewHolder.ActionListener() {
//                @Override
//                public void onChooseMusic(MusicBean musicBean) {
//                    mMusicBean = musicBean;
//                    mBgmPlayStarted = false;
//                }
//            });
//            mVideoMusicViewHolder.addToParent();
//            mVideoMusicViewHolder.subscribeActivityLifeCycle();
//        }
//        mVideoMusicViewHolder.show();
//    }
//
//    /**
//     * ?????????????????????????????????
//     */
//    private void clickUpload() {
//        Intent intent = new Intent(mContext, VideoChooseActivity.class);
//        intent.putExtra(Constants.VIDEO_DURATION, MAX_DURATION);
//        startActivityForResult(intent, 0);
//    }
//
//    /**
//     * ???????????????????????????
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0 && resultCode == RESULT_OK) {
//            mVideoPath = data.getStringExtra(Constants.VIDEO_PATH);
//            mDuration = data.getLongExtra(Constants.VIDEO_DURATION, 0);
//            VideoEditActivity.forward(mContext, mDuration, mVideoPath, false, false,mIsDynamicVideo);
//            finish();
//        }
//    }
//
//
//    /**
//     * ????????????
//     */
//    private void startCameraPreview() {
//        if (mRecorder != null && mCustomConfig != null && mVideoView != null) {
//            mRecorder.startCameraCustomPreview(mCustomConfig, mVideoView);
//            if (!mFrontCamera) {
//                mRecorder.switchCamera(false);
//            }
//            if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//                initBeauty();
//                mRecorder.setVideoProcessListener(this);
//            }
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void stopCameraPreview() {
//        if (mRecorder != null) {
//            if (mRecording) {
//                pauseRecord();
//            }
//            mRecorder.stopCameraPreview();
//            mRecorder.setVideoProcessListener(null);
//            if (mTiSDKManager != null) {
//                mTiSDKManager.destroy();
//            }
//            mTiSDKManager = null;
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void clickRecord() {
//        if (mRecordStarted) {
//            if (mRecording) {
//                pauseRecord();
//            } else {
//                resumeRecord();
//            }
//        } else {
//            startRecord();
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void startRecord() {
//        if (mRecorder != null) {
//            mVideoPath = StringUtil.generateVideoOutputPath();//?????????????????????
//            int result = mRecorder.startRecord(mVideoPath, CommonAppConfig.VIDEO_RECORD_TEMP_PATH, null);//???????????????????????????????????????
//            if (result != TXRecordCommon.START_RECORD_OK) {
//                if (result == TXRecordCommon.START_RECORD_ERR_NOT_INIT) {
//                    ToastUtil.show(R.string.video_record_tip_1);
//                } else if (result == TXRecordCommon.START_RECORD_ERR_IS_IN_RECORDING) {
//                    ToastUtil.show(R.string.video_record_tip_2);
//                } else if (result == TXRecordCommon.START_RECORD_ERR_VIDEO_PATH_IS_EMPTY) {
//                    ToastUtil.show(R.string.video_record_tip_3);
//                } else if (result == TXRecordCommon.START_RECORD_ERR_API_IS_LOWER_THAN_18) {
//                    ToastUtil.show(R.string.video_record_tip_4);
//                } else if (result == TXRecordCommon.START_RECORD_ERR_LICENCE_VERIFICATION_FAILED) {
//                    ToastUtil.show(R.string.video_record_tip_5);
//                }
//                return;
//            }
//        }
//        mRecordStarted = true;
//        mRecording = true;
//        resumeBgm();
//        startRecordBtnAnim();
//        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
//            mGroup2.setVisibility(View.INVISIBLE);
//        }
//    }
//
//
//    /**
//     * ????????????
//     */
//    private void pauseRecord() {
//        if (mRecorder == null) {
//            return;
//        }
//        pauseBgm();
//        mRecorder.pauseRecord();
//        mRecording = false;
//        stopRecordBtnAnim();
//        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
//            mGroup2.setVisibility(View.VISIBLE);
//        }
//        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
//        if (partsManager != null) {
//            List<String> partList = partsManager.getPartsPathList();
//            if (partList != null && partList.size() > 0) {
//                if (mGroup3 != null && mGroup3.getVisibility() == View.VISIBLE) {
//                    mGroup3.setVisibility(View.INVISIBLE);
//                }
//                if (mGroup4 != null && mGroup4.getVisibility() != View.VISIBLE) {
//                    mGroup4.setVisibility(View.VISIBLE);
//                }
//            } else {
//                if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
//                    mGroup3.setVisibility(View.VISIBLE);
//                }
//                if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
//                    mGroup4.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//    }
//
//    /**
//     * ????????????
//     */
//    private void resumeRecord() {
//        if (mRecorder != null) {
//            int startResult = mRecorder.resumeRecord();
//            if (startResult != TXRecordCommon.START_RECORD_OK) {
//                ToastUtil.show(WordUtil.getString(R.string.video_record_failed));
//                return;
//            }
//        }
//        mRecording = true;
//        resumeBgm();
//        startRecordBtnAnim();
//        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
//            mGroup2.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void pauseBgm() {
//        if (mRecorder != null) {
//            mRecorder.pauseBGM();
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void resumeBgm() {
//        if (mRecorder == null) {
//            return;
//        }
//        if (!mBgmPlayStarted) {
//            if (mMusicBean == null) {
//                return;
//            }
//            int bgmDuration = mRecorder.setBGM(mMusicBean.getLocalPath());
//            mRecorder.playBGMFromTime(0, bgmDuration);
//            mRecorder.setBGMVolume(1);//????????????1??????
//            mRecorder.setMicVolume(0);//????????????0
//            mBgmPlayStarted = true;
//            mHasBgm = true;
//        } else {
//            mRecorder.resumeBGM();
//        }
//    }
//
//    /**
//     * ????????????????????????
//     */
//    private void startRecordBtnAnim() {
//        if (mRecordView != null) {
//            mRecordView.setBackground(mRecordDrawable);
//        }
//        if (mRecordBtnAnimator != null) {
//            mRecordBtnAnimator.start();
//        }
//    }
//
//    /**
//     * ????????????????????????
//     */
//    private void stopRecordBtnAnim() {
//        if (mRecordView != null) {
//            mRecordView.setBackground(mUnRecordDrawable);
//        }
//        if (mRecordBtnAnimator != null) {
//            mRecordBtnAnimator.cancel();
//        }
//        if (mVideoRecordBtnView != null) {
//            mVideoRecordBtnView.reset();
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void changeRecordSpeed(int speed) {
//        if (mRecordSpeed == speed) {
//            return;
//        }
//        mRecordSpeed = speed;
//        if (mRecorder != null) {
//            mRecorder.setRecordSpeed(speed);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void clickDelete() {
//        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.video_record_delete_last), new DialogUitl.SimpleCallback() {
//            @Override
//            public void onConfirmClick(Dialog dialog, String content) {
//                doClickDelete();
//            }
//        });
//    }
//
//    /**
//     * ??????????????????
//     */
//    private void doClickDelete() {
//        if (!mRecordStarted || mRecording || mRecorder == null) {
//            return;
//        }
//        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
//        if (partsManager == null) {
//            return;
//        }
//        List<String> partList = partsManager.getPartsPathList();
//        if (partList == null || partList.size() == 0) {
//            return;
//        }
//        partsManager.deleteLastPart();
//        int time = partsManager.getDuration();
//        if (mTime != null) {
//            mTime.setText(String.format("%.2f", time / 1000f) + "s");
//        }
//        mRecordTime = time;
//        if (time < MIN_DURATION && mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
//            mBtnNext.setVisibility(View.INVISIBLE);
//        }
//        if (mRecordProgressView != null) {
//            mRecordProgressView.deleteLast();
//        }
//        partList = partsManager.getPartsPathList();
//        if (partList != null && partList.size() == 0) {
//            if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
//                mGroup2.setVisibility(View.VISIBLE);
//            }
//            if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
//                mGroup3.setVisibility(View.VISIBLE);
//            }
//            if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
//                mGroup4.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//    /**
//     * ???????????????????????? onRecordComplete
//     */
//    public void clickNext() {
//        stopRecordBtnAnim();
//        if (mRecorder != null) {
//            mRecorder.stopBGM();
//            mRecorder.stopRecord();
//            showProccessDialog();
//        }
//    }
//
//
//    /**
//     * ??????????????????????????????????????????
//     */
//    private void showProccessDialog() {
//        if (mStopRecordDialog == null) {
//            mStopRecordDialog = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_processing));
//            mStopRecordDialog.show();
//        }
//    }
//
//    /**
//     * ????????????????????????
//     */
//    private void hideProccessDialog() {
//        if (mStopRecordDialog != null) {
//            mStopRecordDialog.dismiss();
//        }
//        mStopRecordDialog = null;
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        if (!canClick()) {
//            return;
//        }
//        if (mBeautyViewHolder != null && mBeautyViewHolder.isShowed()) {
//            mBeautyViewHolder.hide();
//            return;
//        }
//        if (mVideoMusicViewHolder != null && mVideoMusicViewHolder.isShowed()) {
//            mVideoMusicViewHolder.hide();
//            return;
//        }
//        List<Integer> list = new ArrayList<>();
//        if (mRecordTime > 0) {
//            list.add(R.string.video_re_record);
//        }
//        list.add(R.string.video_exit);
//        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), new DialogUitl.StringArrayDialogCallback() {
//            @Override
//            public void onItemClick(String text, int tag) {
//                if (tag == R.string.video_re_record) {
//                    reRecord();
//                } else if (tag == R.string.video_exit) {
//                    exit();
//                }
//            }
//        });
//    }
//
//    /**
//     * ????????????
//     */
//    private void reRecord() {
//        if (mRecorder == null) {
//            return;
//        }
//        mRecorder.pauseBGM();
//        mMusicBean = null;
//        mHasBgm = false;
//        mBgmPlayStarted = false;
//        mRecorder.pauseRecord();
//        mRecording = false;
//        stopRecordBtnAnim();
//        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
//            mGroup2.setVisibility(View.VISIBLE);
//        }
//        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
//        if (partsManager != null) {
//            partsManager.deleteAllParts();
//        }
//        mRecorder.onDeleteAllParts();
//        if (mTime != null) {
//            mTime.setText("0.00s");
//        }
//        mRecordTime = 0;
//        if (mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
//            mBtnNext.setVisibility(View.INVISIBLE);
//        }
//        if (mRecordProgressView != null) {
//            mRecordProgressView.deleteAll();
//        }
//        if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
//            mGroup3.setVisibility(View.VISIBLE);
//        }
//        if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
//            mGroup4.setVisibility(View.INVISIBLE);
//        }
//
//    }
//
//    /**
//     * ??????
//     */
//    private void exit() {
//        release();
//        super.onBackPressed();
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        startCameraPreview();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopCameraPreview();
//        if (mRecorder != null && mBtnFlash != null && mBtnFlash.isChecked()) {
//            mBtnFlash.doToggle();
//            mRecorder.toggleTorch(mBtnFlash.isChecked());
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        release();
//        super.onDestroy();
//        L.e(TAG, "-------->onDestroy");
//    }
//
//
//    private void release() {
//        if (mStopRecordDialog != null && mStopRecordDialog.isShowing()) {
//            mStopRecordDialog.dismiss();
//        }
//        if (mBeautyViewHolder != null) {
//            mBeautyViewHolder.release();
//        }
//        if (mVideoMusicViewHolder != null) {
//            mVideoMusicViewHolder.release();
//        }
//        if (mRecordProgressView != null) {
//            mRecordProgressView.release();
//        }
//        if (mRecordBtnAnimator != null) {
//            mRecordBtnAnimator.cancel();
//        }
//        if (mRecorder != null) {
//            mRecorder.toggleTorch(false);
//            mRecorder.stopBGM();
//            if (mRecordStarted) {
//                mRecorder.stopRecord();
//            }
//            mRecorder.stopCameraPreview();
//            mRecorder.setVideoProcessListener(null);
//            mRecorder.setVideoRecordListener(null);
//            TXUGCPartsManager getPartsManager = mRecorder.getPartsManager();
//            if (getPartsManager != null) {
//                getPartsManager.deleteAllParts();
//            }
//            mRecorder.release();
//        }
//        if (mTiSDKManager != null) {
//            mTiSDKManager.destroy();
//        }
//        mStopRecordDialog = null;
//        mBeautyViewHolder = null;
//        mVideoMusicViewHolder = null;
//        mRecordProgressView = null;
//        mRecordBtnAnimator = null;
//        mRecorder = null;
//        mTiSDKManager = null;
//
//    }
//
//
//    /****************???????????? start********************/
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onFilterChanged(TiFilterEnum tiFilterEnum) {
//        if (mTiSDKManager != null) {
//            mTiFilterEnum = tiFilterEnum;
//            mTiSDKManager.setFilterEnum(tiFilterEnum);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onMeiBaiChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mMeibai = progress;
//            mTiSDKManager.setSkinWhitening(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onMoPiChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mMoPi = progress;
//            mTiSDKManager.setSkinBlemishRemoval(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onBaoHeChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mBaoHe = progress;
//            mTiSDKManager.setSkinSaturation(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onFengNenChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mFengNen = progress;
//            mTiSDKManager.setSkinTenderness(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onBigEyeChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mBigEye = progress;
//            mTiSDKManager.setEyeMagnifying(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onFaceChanged(int progress) {
//        if (mTiSDKManager != null) {
//            mFace = progress;
//            mTiSDKManager.setChinSlimming(progress);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onTieZhiChanged(String tieZhiName) {
//        if (mTiSDKManager != null) {
//            mTieZhi = tieZhiName;
//            mTiSDKManager.setSticker(tieZhiName);
//        }
//    }
//
//    /**
//     * ?????????????????????
//     */
//    @Override
//    public void onHaHaChanged(TiDistortionEnum tiDistortionEnum) {
//        if (mTiSDKManager != null) {
//            mTiDistortionEnum = tiDistortionEnum;
//            mTiSDKManager.setDistortionEnum(tiDistortionEnum);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    @Override
//    public void onRockChanged(TiRockEnum tiRockEnum) {
//        if (mTiSDKManager != null) {
//            mTiRockEnum = tiRockEnum;
//            mTiSDKManager.setRockEnum(tiRockEnum);
//        }
//    }
//
//
//    /****************???????????? end********************/
//
//
//}
