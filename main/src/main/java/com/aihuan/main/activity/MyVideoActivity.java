package com.aihuan.main.activity;


import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.interfaces.VideoResultCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.views.MyVideoViewHolder;
import com.aihuan.video.activity.LocalVideoChooseActivity;
import com.aihuan.video.activity.VideoPublishActivity;

import java.io.File;

/**
 * Created by cxf on 2018/12/14.
 */

public class MyVideoActivity extends AbsActivity implements View.OnClickListener {
    private ProcessResultUtil mProcessResultUtil;
    private MyVideoViewHolder mVideoHomeViewHolder;
    private Dialog mChooseVideoDialog;
    private ProcessImageUtil mImageUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_video;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_my_video));
        mProcessResultUtil = new ProcessResultUtil(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        mVideoHomeViewHolder = new MyVideoViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        mVideoHomeViewHolder.addToParent();
        mVideoHomeViewHolder.subscribeActivityLifeCycle();
        mVideoHomeViewHolder.loadData();
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setVideosultCallback(new VideoResultCallback() {
            @Override
            public void onSuccess(final File path) {
                VideoPublishActivity.forward(mContext, path.getAbsolutePath(), Constants.VIDEO_SAVE_PUB, 0);
            }
            @Override
            public void onFailure() {

            }
        });
        CommonAppConfig.getInstance().setVideoPublishType(Constants.VIDEO_PUBLISH);
    }

    private void release() {
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        mProcessResultUtil = null;
        if (mVideoHomeViewHolder != null) {
            mVideoHomeViewHolder.release();
        }
        mVideoHomeViewHolder = null;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (mChooseVideoDialog == null) {
            mChooseVideoDialog = DialogUitl.getStringArrayDialog(mContext, new Integer[]{
                    R.string.video_record, R.string.video_local}, true, new DialogUitl.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    if (tag == R.string.video_record) {
                        if (mImageUtil != null) {
                            mImageUtil.getVideoRecord();
                        }
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
        startActivityForResult(intent, 0);
    }
}
