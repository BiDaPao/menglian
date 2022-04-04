package com.aihuan.dynamic.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dynamic.R;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.DynamicPublishActivity;


/**
 * Created by debug on 2019/7/22.
 * 视频播放
 */

public class VideoDialogFragment extends AbsDialogFragment implements View.OnClickListener, ITXVodPlayListener {

    private TXVodPlayer mTXVodPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_look_video;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog3;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        String videoPath = ((DynamicPublishActivity) mContext).getVideoPath();
        TXCloudVideoView txCloudVideoView = (TXCloudVideoView) findViewById(com.aihuan.video.R.id.video_view);
        txCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXVodPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig txVodPlayConfig = new TXVodPlayConfig();
        txVodPlayConfig.setMaxCacheItems(15);
        txVodPlayConfig.setProgressInterval(200);
        mTXVodPlayer.setConfig(txVodPlayConfig);
        mTXVodPlayer.setAutoPlay(true);
        mTXVodPlayer.setVodListener(this);
        mTXVodPlayer.setPlayerView(txCloudVideoView);
        if (mTXVodPlayer != null) {
            mTXVodPlayer.startPlay(videoPath);
        }
    }

    private void del() {
        DialogUitl.showTitleStringArrayDialog(mContext, new Integer[]{
                R.string.delete
        }, false, WordUtil.getString(R.string.dynamic_video_del_tip), new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                ((DynamicPublishActivity) mContext).delVideo();
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_del) {
            del();
        } else if (i == R.id.btn_close) {
            dismiss();
        }

    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.seek(0);
            mTXVodPlayer.resume();
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mTXVodPlayer != null) {
            mTXVodPlayer.stopPlay(true);
            mTXVodPlayer.setVodListener(null);
        }
        mTXVodPlayer = null;
    }


}
