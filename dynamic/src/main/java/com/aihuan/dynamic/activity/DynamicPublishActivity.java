package com.aihuan.dynamic.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamic.R;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.event.VideoRecordEvent;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.interfaces.VideoResultCallback;
import com.aihuan.dynamic.custorm.VoicePlayView;
import com.aihuan.dynamic.dialog.DynamicLookImgDialogFragment;
import com.aihuan.dynamic.event.ImgEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.dynamic.upload.UploadCallback;
import com.aihuan.dynamic.upload.UploadQnImpl;
import com.aihuan.dynamic.upload.UploadStrategy;
import com.aihuan.common.utils.FilePathUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.dynamic.dialog.VideoDialogFragment;
import com.aihuan.dynamic.dialog.VoiceRecordDialogFragment;
import com.aihuan.dynamic.upload.UploadBean;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;
import com.aihuan.video.activity.LocalVideoChooseActivity;
import com.aihuan.video.event.ChooseVideoEvent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by debug on 2019/7/22.
 * 发布动态
 */

public class DynamicPublishActivity extends AbsActivity implements View.OnClickListener {
    private TextView mBtnPublish;
    private EditText mEtContent;
    private TextView mNum;

    private TextView mTvLocation;
    private CheckBox mCbLocation;

    private View mLlUploadType;

    private View mLlShowImg;
    private ImageView mIvLastImg;
    private TextView mTvImgNum;
    private View mBtnAddImg;

    private View mRlShowVideo;
    private ImageView mIvVideoImg;

    private View mRlShowVoice;
    private VoicePlayView mVoicePlayView;

    private String mLocation;
    private ProcessImageUtil mImageUtil;
    private Dialog mChooseImageDialog;

    private String mVideoPath;

    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private File mVoiceFile;
    private int mVoiceSumTime;

    private UploadStrategy mUploadStrategy;
    private int mPublishType;
    private List<UploadBean> mUploadList;
    private String mContent;//文字内容

    private Dialog mLoading;
    private Dialog mChooseVideoDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_publish;
    }

    @Override
    protected void main() {
        super.main();
        mEtContent = findViewById(R.id.et_content);
        mNum = (TextView) findViewById(com.aihuan.video.R.id.num);
        mTvLocation = findViewById(R.id.location);
        mCbLocation = findViewById(R.id.cb_location);
        mLlUploadType = findViewById(R.id.btn_upload_type);
        mLlShowImg = findViewById(R.id.ll_show_img);
        mIvLastImg = findViewById(R.id.last_img);
        mBtnAddImg = findViewById(R.id.btn_addimg);
        mBtnPublish = findViewById(R.id.btn_publish);
        mRlShowVideo = findViewById(R.id.rl_video_show);
        mIvVideoImg = findViewById(R.id.video_thumb);
        mRlShowVoice = findViewById(R.id.ll_show_voice);
        mVoicePlayView = findViewById(R.id.voice_view);
        mTvImgNum = findViewById(R.id.tv_img_num);
        findViewById(R.id.btn_img).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.btn_del_img).setOnClickListener(this);
        findViewById(R.id.btn_voice_del).setOnClickListener(this);
        findViewById(R.id.btn_del_video).setOnClickListener(this);
        mBtnAddImg.setOnClickListener(this);
        mIvLastImg.setOnClickListener(this);
        mBtnPublish.setOnClickListener(this);
        mRlShowVideo.setOnClickListener(this);
        mBtnPublish.setEnabled(false);
        mCbLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTvLocation.setText(mLocation);
                } else {
                    mTvLocation.setText(WordUtil.getString(R.string.at_mars));
                }
            }
        });
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    setPublishEnable(true);
                } else {
                    setPublishEnable(false);
                }
                if (mNum != null) {
                    mNum.setText(s.length() + "/200");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                if (mLlUploadType != null) {
                    mLlUploadType.setVisibility(View.GONE);
                }
                if (mLlShowImg != null) {
                    mLlShowImg.setVisibility(View.VISIBLE);
                }
                mBtnPublish.setEnabled(true);
                mPublishType = Constants.DYNAMIC_TYPE_IMG;
                ImgLoader.display(mContext, file, mIvLastImg);
                UploadBean uploadBean = new UploadBean();
                uploadBean.setOriginFile(file);
                uploadBean.setType(UploadBean.IMG);
                mUploadList.add(uploadBean);
                if (mUploadList.size() == Constants.DYNAMIC_IMG_MAX_NUM) {
                    if (mBtnAddImg != null) {
                        mBtnAddImg.setVisibility(View.GONE);
                    }
                }
                if (mTvImgNum != null) {
                    mTvImgNum.setText(String.format(WordUtil.getString(R.string.img_num_tip), mUploadList.size()));
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mImageUtil.setVideosultCallback(new VideoResultCallback() {
            @Override
            public void onSuccess(final File path) {
                mPublishType = Constants.DYNAMIC_TYPE_VIDEO;
                mVideoPath = path.getAbsolutePath();
                UploadBean video = new UploadBean();
                video.setOriginFile(path);
                video.setType(UploadBean.VIDEO);
                if (mUploadList != null) {
                    mUploadList.clear();
                }
                mUploadList.add(video);
                createVideoThumb();
            }

            @Override
            public void onFailure() {
            }
        });
        mUploadList = new ArrayList<>();
        mLocation = CommonAppConfig.getInstance().getCity();
        mTvLocation.setText(mLocation);
        EventBus.getDefault().register(this);
        CommonAppConfig.getInstance().setVideoPublishType(Constants.DYNAMIC_VIDEO_PUBLISH);
    }

    private void setPublishEnable(boolean isEnable) {
        if (TextUtils.isEmpty(mContent) || mUploadList.size() > 0) {
            mBtnPublish.setEnabled(isEnable);
        } else {
            mBtnPublish.setEnabled(true);
        }
    }

    /**
     * 选择图片
     */
    private void chooseImage() {
        if (mChooseImageDialog == null) {
            mChooseImageDialog = DialogUitl.getStringArrayDialog(mContext, new Integer[]{
                    R.string.take_photo, R.string.from_album}, true, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.take_photo) {
                        mImageUtil.getImageByCamera(false);
                    } else {
                        ChooseImageActivity.forward(mContext, mUploadList.size());
                    }
                }
            });
        }
        mChooseImageDialog.show();
    }

    /**
     * 选择上传视频
     */
    public void chooseVideo() {
        if (mChooseVideoDialog == null) {
            mChooseVideoDialog = DialogUitl.getStringArrayDialog(mContext, new Integer[]{
                    R.string.video_record, R.string.video_local}, true, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.video_record) {
                        if (mImageUtil != null) {
                            mImageUtil.getVideoRecord();
                        }
                        ;
                    } else {
                        selectLocalVideo();
                    }
                }
            });
        }
        mChooseVideoDialog.show();

    }


    /**
     * 点击上传，选择本地视频
     */
    private void selectLocalVideo() {
        Intent intent = new Intent(mContext, LocalVideoChooseActivity.class);
        startActivity(intent);
    }


    private void recordVoice() {
        VoiceRecordDialogFragment voiceRecordDialogFragment = new VoiceRecordDialogFragment();
        voiceRecordDialogFragment.show(getSupportFragmentManager(), "VoiceRecordDialogFragment");
    }


    /**
     * 查看选择的所有图片
     */
    private void lookAllImgs() {
        DynamicLookImgDialogFragment lookImgDialogFragment = new DynamicLookImgDialogFragment();
        Bundle bundle = new Bundle();
        ArrayList<String> list = new ArrayList<>();
        for (UploadBean bean : mUploadList) {
            list.add(bean.getOriginFile().getAbsolutePath());
        }
        bundle.putString(Constants.DYNAMIC_UID, CommonAppConfig.getInstance().getUid());
        bundle.putStringArrayList(Constants.DYNAMIC_IMG_LIST, list);
        lookImgDialogFragment.setArguments(bundle);
        lookImgDialogFragment.show(getSupportFragmentManager(), "DynamicLookImgDialogFragment");
    }

    /**
     * 观看视频
     */
    private void lookVideo() {
        VideoDialogFragment videoDialogFragment = new VideoDialogFragment();
        videoDialogFragment.show(getSupportFragmentManager(), "VideoDialogFragment");
    }


    public void delUploadImgs(int pos) {
        boolean isDelLast = false;
        if (pos == mUploadList.size() - 1) {
            isDelLast = true;
        }
        mUploadList.remove(pos);
        if (mUploadList.size() == 0) {
            mPublishType = Constants.DYNAMIC_TYPE_TEXT;
            if (mLlShowImg != null) {
                mLlShowImg.setVisibility(View.GONE);
            }
            if (mLlUploadType != null) {
                mLlUploadType.setVisibility(View.VISIBLE);
            }
            setPublishEnable(false);
        } else {
            if (isDelLast) {
                int lastSize = mUploadList.size() - 1;
                ImgLoader.display(mContext, mUploadList.get(lastSize).getOriginFile(), mIvLastImg);
            }
        }
    }

    /**
     * 删除所有图片
     */
    private void delAllImgs() {
        DialogUitl.showSimpleDialog3(mContext, WordUtil.getString(R.string.dynamic_del_all_tip), WordUtil.getString(R.string.delete), "", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                mPublishType = Constants.DYNAMIC_TYPE_TEXT;
                if (mUploadList != null) {
                    mUploadList.clear();
                }
                if (mLlShowImg != null) {
                    mLlShowImg.setVisibility(View.GONE);
                }
                if (mLlUploadType != null) {
                    mLlUploadType.setVisibility(View.VISIBLE);
                }
                setPublishEnable(false);
            }
        });
    }

    /**
     * 生成视频封面
     */
    private void createVideoThumb() {
        Bitmap bitmap = null;
        //生成视频封面图
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mVideoPath);
            bitmap = mmr.getFrameAtTime(-1);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        if (bitmap == null) {
            ToastUtil.show(com.aihuan.video.R.string.video_cover_img_failed);
            return;
        }
        final String coverImagePath = mVideoPath.replace(".mp4", ".jpg");
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            imageFile = null;
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (imageFile == null) {
            ToastUtil.show(com.aihuan.video.R.string.video_cover_img_failed);
            return;
        }
        File dir = new File(CommonAppConfig.VIDEO_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File finalImageFile = imageFile;
        //用鲁班压缩图片
        Luban.with(this)
                .load(finalImageFile)
                .setFocusAlpha(false)
                .ignoreBy(8)//8k以下不压缩
                .setTargetDir(CommonAppConfig.VIDEO_PATH)
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                        return filePath.replace(".jpg", "_c.jpg");
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        if (finalImageFile.exists()) {
                            finalImageFile.delete();
                        }
                        UploadBean img = new UploadBean();
                        img.setOriginFile(file);
                        img.setType(UploadBean.IMG);
                        if (mUploadList != null) {
                            mUploadList.add(img);
                        }
                        ////
                        ImgLoader.display(mContext, file, mIvVideoImg);
                        if (mLlUploadType != null) {
                            mLlUploadType.setVisibility(View.GONE);
                        }
                        if (mRlShowVideo != null) {
                            mRlShowVideo.setVisibility(View.VISIBLE);
                        }
                        setPublishEnable(true);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    /**
     * 删除视频
     */
    public void delVideo() {
        mPublishType = Constants.DYNAMIC_TYPE_TEXT;
        mVideoPath = "";
        if (mUploadList != null) {
            mUploadList.clear();
        }
        if (mLlUploadType != null) {
            mLlUploadType.setVisibility(View.VISIBLE);
        }
        if (mRlShowVideo != null) {
            mRlShowVideo.setVisibility(View.GONE);
        }
        setPublishEnable(false);
    }

    /**
     * 添加音频
     *
     * @param file
     * @param sumTime 总时长
     */
    public void addVoiceInfo(File file, int sumTime) {
        mPublishType = Constants.DYNAMIC_TYPE_VOICE;
        mVoiceFile = file;
        mVoiceSumTime = sumTime;
        UploadBean voice = new UploadBean();
        voice.setType(UploadBean.VOICE);
        voice.setOriginFile(file);
        mUploadList.clear();
        mUploadList.add(voice);
        if (mLlUploadType != null) {
            mLlUploadType.setVisibility(View.GONE);
        }
        if (mRlShowVoice != null) {
            mRlShowVoice.setVisibility(View.VISIBLE);
        }
        if (mVoicePlayView != null) {
            if (mVoiceMediaPlayerUtil == null) {
                mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            }
            mVoicePlayView.setVoiceMediaPlayerUtil(mVoiceMediaPlayerUtil);
            mVoicePlayView.setVoiceInfo(mVoiceSumTime, file.getAbsolutePath());
        }
        setPublishEnable(true);
    }


    //删除已录制音频
    private void delVoice() {
        DialogUitl.showSimpleDialog3(mContext, WordUtil.getString(R.string.is_del_voice), WordUtil.getString(R.string.delete), "", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                mPublishType = Constants.DYNAMIC_TYPE_TEXT;
                FilePathUtil.clearFilePath(mContext, mVoiceFile);
                mVoiceSumTime = 0;
                mVoiceFile = null;
                if (mUploadList != null) {
                    mUploadList.clear();
                }
                if (mLlUploadType != null) {
                    mLlUploadType.setVisibility(View.VISIBLE);
                }
                if (mRlShowVoice != null) {
                    mRlShowVoice.setVisibility(View.GONE);
                }
                if (mVoicePlayView != null) {
                    mVoicePlayView.resetView();
                }
                setPublishEnable(false);
            }
        });
    }

    private void publish() {
        mContent = mEtContent.getText().toString().trim();
        if (mPublishType == Constants.DYNAMIC_TYPE_TEXT && TextUtils.isEmpty(mContent)) {
            ToastUtil.show(WordUtil.getString(R.string.dynamic_null));
            return;
        }
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing));
        }
        mLoading.show();
        if (mPublishType == Constants.DYNAMIC_TYPE_TEXT) {
            doSubmit("", "", "", "");
        }else {
            uploadFile();
        }
    }

    private void uploadFile() {
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        mUploadStrategy.upload(mUploadList, mPublishType == Constants.DYNAMIC_TYPE_IMG, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (success) {
                    if (list != null && list.size() > 0) {
                        if (mPublishType == Constants.DYNAMIC_TYPE_IMG) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0, size = list.size(); i < size; i++) {
                                String fileName = list.get(i).getRemoteFileName();
                                if (!TextUtils.isEmpty(fileName)) {
                                    sb.append(fileName);
                                    sb.append(";");
                                }
                            }
                            String photos = sb.toString();
                            if (photos.length() > 1) {
                                photos = photos.substring(0, photos.length() - 1);
                            }
                            doSubmit(photos, "", "", "");
                            L.e("上传图片完成---------> " + photos);
                        } else if (mPublishType == Constants.DYNAMIC_TYPE_VIDEO) {
                            String videoPath = list.get(0).getRemoteFileName();
                            String imgPath = list.get(1).getRemoteFileName();
                            L.e("上传视频完成---------> " + videoPath);
                            L.e("上传视频完成------img---> " + imgPath);
                            doSubmit("", imgPath, videoPath, "");
                        } else if (mPublishType == Constants.DYNAMIC_TYPE_VOICE) {
                            String voicePath = list.get(0).getRemoteFileName();
                            L.e("上传语音完成---------> " + voicePath);
                            doSubmit("", "", "", voicePath);
                        }
                    }
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.upload_fail));
                    if (mLoading != null) {
                        mLoading.dismiss();
                    }
                }
            }
        });
    }

    private void doSubmit(String photos, String videoThumb, String videoPath, String voicePath) {
        DynamicHttpUtil.uploadDynamic(mCbLocation.isChecked(), mContent, photos, videoThumb, videoPath, voicePath, mVoiceSumTime, mPublishType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                } else {
                    setPublishEnable(true);
                }
                ToastUtil.show(msg);
            }

            @Override
            public void onError() {
                super.onError();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mLoading != null) {
                    mLoading.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_publish) {
            publish();
        } else if (i == R.id.btn_img || i == R.id.btn_addimg) {
            chooseImage();
        } else if (i == R.id.btn_video) {
            chooseVideo();
        } else if (i == R.id.btn_voice) {
            recordVoice();
        } else if (i == R.id.last_img) {
            lookAllImgs();
        } else if (i == R.id.btn_del_img) {
            delAllImgs();
        } else if (i == R.id.rl_video_show) {
            lookVideo();
        } else if (i == R.id.btn_voice_del) {
            delVoice();
        } else if (i == R.id.btn_del_video) {
            DialogUitl.showSimpleDialog3(mContext, WordUtil.getString(R.string.is_del_video), WordUtil.getString(R.string.delete), "", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    delVideo();
                }
            });
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onVideoGenerateSucEvent(VideoGenerateSucEvent e) {
//        mPublishType = Constants.DYNAMIC_TYPE_VIDEO;
//        mVideoPath = e.getGenerateVideoPath();
//        UploadBean video = new UploadBean();
//        video.setOriginFile(new File(mVideoPath));
//        video.setType(UploadBean.VIDEO);
//        if (mUploadList != null) {
//            mUploadList.clear();
//        }
//        mUploadList.add(video);
//        createVideoThumb();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseLocalVideo(ChooseVideoEvent e) {
        mVideoPath = e.getVideoPath();
        if (!TextUtils.isEmpty(mVideoPath)) {
            mPublishType = Constants.DYNAMIC_TYPE_VIDEO;
            UploadBean video = new UploadBean();
            video.setOriginFile(new File(mVideoPath));
            video.setType(UploadBean.VIDEO);
            if (mUploadList != null) {
                mUploadList.clear();
            }
            mUploadList.add(video);
            createVideoThumb();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordVideo(VideoRecordEvent e) {
        mPublishType = Constants.DYNAMIC_TYPE_VIDEO;
        mVideoPath = e.getVideoPath();
        UploadBean video = new UploadBean();
        video.setOriginFile(new File(mVideoPath));
        video.setType(UploadBean.VIDEO);
        if (mUploadList != null) {
            mUploadList.clear();
        }
        mUploadList.add(video);
        createVideoThumb();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectImg(ImgEvent imgEvent) {
        if (imgEvent.getImgBeanList() == null) {
            return;
        }
        if (imgEvent.getImgBeanList().size() == 0) {
            return;
        }
        if (mLlUploadType != null) {
            mLlUploadType.setVisibility(View.GONE);
        }
        if (mLlShowImg != null) {
            mLlShowImg.setVisibility(View.VISIBLE);
        }
        mBtnPublish.setEnabled(true);
        mPublishType = Constants.DYNAMIC_TYPE_IMG;
        mUploadList.addAll(mUploadList.size(), imgEvent.getImgBeanList());
        ImgLoader.display(mContext, mUploadList.get(mUploadList.size() - 1).getOriginFile(), mIvLastImg);
        if (mUploadList.size() == Constants.DYNAMIC_IMG_MAX_NUM) {
            if (mBtnAddImg != null) {
                mBtnAddImg.setVisibility(View.GONE);
            }
        }
        if (mTvImgNum != null) {
            mTvImgNum.setText(String.format(WordUtil.getString(R.string.img_num_tip), mUploadList.size()));
        }
    }

    @Override
    public void onBackPressed() {
        mContent = mEtContent.getText().toString().trim();
        if (mUploadList != null && mUploadList.size() > 0 || mContent != null && mContent.length() > 0) {
            DialogUitl.showSimpleDialog2(mContext, WordUtil.getString(R.string.is_giveup_edit), new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVoicePlayView != null) {
            mVoicePlayView.release();
        }
        EventBus.getDefault().unregister(this);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_PUBLISH);
    }


}
