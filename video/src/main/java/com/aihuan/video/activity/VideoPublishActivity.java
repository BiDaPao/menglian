package com.aihuan.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.dialog.MainPriceDialogFragment;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.mob.MobShareUtil;
import com.aihuan.common.mob.ShareData;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.video.R;
import com.aihuan.video.adapter.VideoPubShareAdapter;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;
import com.aihuan.video.upload.VideoUploadBean;
import com.aihuan.video.upload.VideoUploadCallback;
import com.aihuan.video.upload.VideoUploadQnImpl;
import com.aihuan.video.upload.VideoUploadStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;


/**
 * Created by cxf on 2018/12/10.
 * 视频发布
 */
@Route(path = RouteUtil.PATH_VIDEO_PUBLISH)
public class VideoPublishActivity extends AbsActivity implements ITXVodPlayListener, View.OnClickListener, MainPriceDialogFragment.ActionListener {

    public static void forward(Context context, String videoPath, int saveType, int musicId) {
        Intent intent = new Intent(context, VideoPublishActivity.class);
        intent.putExtra(Constants.VIDEO_PATH, videoPath);
        intent.putExtra(Constants.VIDEO_SAVE_TYPE, saveType);
        intent.putExtra(Constants.VIDEO_MUSIC_ID, musicId);
        context.startActivity(intent);
    }

    private static final String TAG = "VideoPublishActivity";
    private TextView mNum;
    // private TextView mLocation;
    private TXCloudVideoView mTXCloudVideoView;
    private TXVodPlayer mPlayer;
    private String mVideoPath;
    private boolean mPlayStarted;//播放是否开始了
    private boolean mPaused;//生命周期暂停
    //    private RecyclerView mRecyclerView;
    private ConfigBean mConfigBean;
    private VideoPubShareAdapter mAdapter;
    private VideoUploadStrategy mUploadStrategy;
    private EditText mInput;
    private String mVideoTitle;//视频标题
    private Dialog mLoading;
    private MobShareUtil mMobShareUtil;
    private int mSaveType;
    private int mMusicId;
    private View mBtnPub;
    private CheckBox mCheckBox;
    private TextView mCoin;
    private TextView mTip;
    private List<ChatPriceBean> mPriceList;
    private String mCoinName;
    private String mVideoPrice;
    private View mBtnPrice;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_publish;
    }

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(WordUtil.getString(R.string.video_pub));
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        mSaveType = intent.getIntExtra(Constants.VIDEO_SAVE_TYPE, Constants.VIDEO_SAVE_SAVE_AND_PUB);
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        mMusicId = intent.getIntExtra(Constants.VIDEO_MUSIC_ID, 0);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mBtnPub = findViewById(R.id.btn_pub);
        mBtnPub.setOnClickListener(this);
        mCheckBox = findViewById(R.id.checkbox);
        mCoin = findViewById(R.id.coin);
        mTip = findViewById(R.id.tip);
        mBtnPrice = findViewById(R.id.btn_price);
//        mRecyclerView = findViewById(R.id.recyclerView);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
//                if (mRecyclerView != null) {
//                    mAdapter = new VideoPubShareAdapter(mContext, bean);
//                    mRecyclerView.setAdapter(mAdapter);
//                }
            }
        });
        getVideoFee();
        mNum = (TextView) findViewById(R.id.num);
        mInput = (EditText) findViewById(R.id.input);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNum != null) {
                    mNum.setText(s.length() + "/20");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        mLocation = findViewById(R.id.location);
//        mLocation.setText(CommonAppConfig.getInstance().getCity());
        mTXCloudVideoView = findViewById(R.id.video_view);
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig txVodPlayConfig = new TXVodPlayConfig();
        txVodPlayConfig.setMaxCacheItems(15);
        txVodPlayConfig.setProgressInterval(200);
        mPlayer.setConfig(txVodPlayConfig);
        mPlayer.setAutoPlay(true);
        mPlayer.setVodListener(this);
        mPlayer.setPlayerView(mTXCloudVideoView);
        if (mPlayer != null) {
            mPlayer.startPlay(mVideoPath);
        }
    }


    /**
     * 循环播放
     */
    private void onReplay() {
        if (mPlayStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayStarted && mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mPlayStarted && mPlayer != null) {
            mPlayer.resume();
        }
        mPaused = false;
    }

    public void release() {
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        VideoHttpUtil.cancel(VideoHttpConsts.SAVE_UPLOAD_VIDEO_INFO);
        mPlayStarted = false;
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        if (mUploadStrategy != null) {
            mUploadStrategy.cancel();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        mPlayer = null;
        mUploadStrategy = null;
        mMobShareUtil = null;
    }

    @Override
    public void onBackPressed() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.video_give_up_pub), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        File file = new File(mVideoPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                release();
                VideoPublishActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        L.e(TAG, "-------->onDestroy");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_pub) {
            publishVideo();

        } else if (i == R.id.btn_price) {
            videoPriceClick();
        }
    }

    /**
     * 发布视频
     */
    private void publishVideo() {
        if (mConfigBean == null) {
            return;
        }
        mBtnPub.setEnabled(false);
        String title = mInput.getText().toString().trim();
        //产品要求把视频描述判断去掉
//        if (TextUtils.isEmpty(title)) {
//            ToastUtil.show(R.string.video_title_empty);
//            return;
//        }
        mVideoTitle = title;
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing));
        mLoading.show();
        Bitmap bitmap = null;
        //生成视频封面图
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mVideoPath);
            bitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        if (bitmap == null) {
            ToastUtil.show(R.string.video_cover_img_failed);
            onFailed();
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
            ToastUtil.show(R.string.video_cover_img_failed);
            onFailed();
            return;
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
                        uploadVideoFile(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadVideoFile(finalImageFile);
                    }
                }).launch();
    }

    /**
     * 上传封面图片
     */
    private void uploadVideoFile(final File imageFile) {
//        if (mConfigBean.getVideoCloudType() == 1) {
//            mUploadStrategy = new VideoUploadQnImpl(mConfigBean);
//        } else {
//            mUploadStrategy = new VideoUploadTxImpl(mConfigBean);
//        }
        mUploadStrategy = new VideoUploadQnImpl(mConfigBean);
        mUploadStrategy.upload(new VideoUploadBean(new File(mVideoPath), imageFile), new VideoUploadCallback() {
            @Override
            public void onSuccess(VideoUploadBean bean) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    bean.deleteFile();
                }
                saveUploadVideoInfo(bean);
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.video_pub_failed);
                onFailed();
            }
        });
    }


    private void onFailed() {
        if (mLoading != null) {
            mLoading.dismiss();
        }
        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }


    /**
     * 把视频上传后的信息保存在服务器
     */
    private void saveUploadVideoInfo(VideoUploadBean bean) {
        if (mCheckBox == null) {
            return;
        }
        boolean isPrivate = mCheckBox.isChecked();
        String coin = isPrivate ? mVideoPrice : "0";
        VideoHttpUtil.saveUploadVideoInfo(mVideoTitle, bean.getResultImageUrl(), bean.getResultVideoUrl(), mMusicId, isPrivate ? 1 : 0, coin,
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
//                            if (mConfigBean != null && mConfigBean.getVideoAuditSwitch() == 1) {
//                                ToastUtil.show(R.string.video_pub_success_2);
//                            } else {
//                                ToastUtil.show(R.string.video_pub_success);
//                            }
//                    if (mAdapter != null) {
//                        String shareType = mAdapter.getShareType();
//                        if (shareType != null) {
//                            JSONObject obj = JSON.parseObject(info[0]);
//                            shareVideoPage(shareType, obj.getString("id"), obj.getString("thumb_s"));
//                        }
//                    }
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
     * 分享页面链接
     */
    public void shareVideoPage(String shareType, String videoId, String videoImageUrl) {
        ShareData data = new ShareData();
        data.setTitle(mConfigBean.getVideoShareTitle());
        data.setDes(mConfigBean.getVideoShareDes());
        data.setImgUrl(CommonAppConfig.getInstance().getUserBean().getAvatarThumb());
        String webUrl = HtmlConfig.SHARE_VIDEO + videoId;
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(shareType, data, null);
    }

    /**
     * 获取视频价格
     */
    public void getVideoFee() {
        VideoHttpUtil.videoGetFee(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mTip != null) {
                        mTip.setText(obj.getString("tips"));
                    }
                    mPriceList = JSON.parseArray(obj.getString("list"), ChatPriceBean.class);
                    if (mPriceList != null && mPriceList.size() > 0) {
                        mVideoPrice = mPriceList.get(0).getCoin();
                        if (mCoin != null) {
                            mCoin.setText(StringUtil.contact(mVideoPrice, mCoinName));
                        }
                        if (mBtnPrice != null) {
                            mBtnPrice.setOnClickListener(VideoPublishActivity.this);
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置视频价格
     */
    private void videoPriceClick() {
        if (mPriceList == null || mPriceList.size() == 0) {
            return;
        }
        MainPriceDialogFragment fragment = new MainPriceDialogFragment();
        fragment.setPriceList(mPriceList);
        fragment.setNowPrice(mVideoPrice);
        fragment.setFrom(Constants.MAIN_ME_VIDEO);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceDialogFragment");
    }

    @Override
    public void onPriceSelected(int from, ChatPriceBean bean) {
        mVideoPrice = bean.getCoin();
        if (mCoin != null) {
            mCoin.setText(StringUtil.contact(mVideoPrice, mCoinName));
        }
    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }


    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int i, Bundle bundle) {
        switch (i) {
            case TXLiveConstants.PLAY_EVT_PLAY_END://获取到视频播放完毕的回调
                replay();
                break;
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }
}
