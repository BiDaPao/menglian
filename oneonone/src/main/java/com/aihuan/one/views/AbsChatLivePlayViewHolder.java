package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.R;

/**
 * Created by cxf on 2018/10/25.
 */

public abstract class AbsChatLivePlayViewHolder extends AbsViewHolder {

    private View mCameraCover;//摄像头覆盖物，用来关闭摄像头

    public AbsChatLivePlayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        mCameraCover = findViewById(R.id.camera_cover);
    }


    public void showCameraCover() {
        if (mCameraCover != null && mCameraCover.getVisibility() != View.VISIBLE) {
            mCameraCover.setVisibility(View.VISIBLE);
        }
    }

    public void hideCameraCover() {
        if (mCameraCover != null && mCameraCover.getVisibility() == View.VISIBLE) {
            mCameraCover.setVisibility(View.INVISIBLE);
        }
    }

    public abstract void startPlay(String url);

    public abstract void stopPlay();

    public abstract void pausePlay();

    public abstract void resumePlay();

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
