package com.aihuan.dynamic.custorm;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.aihuan.dynamic.bean.DynamicBean;


/**
 * Created by cxf on 2018/9/21.
 */

public class DynamicVideoViewContainer extends FrameLayout implements VideoPlayView.PlayEventListener {
    private int[] mLocation;
    private DynamicBean mDynamicBean;

    public DynamicVideoViewContainer(@NonNull Context context) {
        this(context, null);
    }

    public DynamicVideoViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicVideoViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLocation = new int[2];
    }

    public int[] getLocation() {
        this.getLocationOnScreen(mLocation);
        return mLocation;
    }

    public void addPlayView(VideoPlayView playView) {
        if (playView == null) {
            return;
        }
        ViewParent parent = playView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(playView);
        }
        addView(playView);
        playView.setPlayEventListener(this);
    }

    public DynamicBean getDynamicBean() {
        return mDynamicBean;
    }

    public void setDynamicBean(DynamicBean dynamicBean) {
        mDynamicBean = dynamicBean;
    }

    @Override
    public void onStartPlay() {
        if (mPlayEventListener != null) {
            mPlayEventListener.onStartPlay();
        }
    }

    @Override
    public void onFirstFrame() {
        if (mPlayEventListener != null) {
            mPlayEventListener.onFirstFrame();
        }
    }

    @Override
    public void onResumePlay() {
        if (mPlayEventListener != null) {
            mPlayEventListener.onResumePlay();
        }
    }

    @Override
    public void onPausePlay() {
        if (mPlayEventListener != null) {
            mPlayEventListener.onPausePlay();
        }
    }

    @Override
    public void onPausePlay2() {
        if (mPlayEventListener != null) {
            mPlayEventListener.onPausePlay2();
        }
    }

    @Override
    public void onVideoSize(int width, int height, boolean onlyImgChange) {
        if (mPlayEventListener != null) {
            mPlayEventListener.onVideoSize(width, height, false);
        }
    }

    public interface PlayEventListener {

        void onStartPlay();

        void onFirstFrame();

        void onResumePlay();

        void onPausePlay();

        void onPausePlay2();

        void onRemoveView();

        void onVideoSize(int width, int height, boolean onlyImgChange);
    }

    private PlayEventListener mPlayEventListener;

    public void setPlayEventListener(PlayEventListener listener) {
        mPlayEventListener = listener;
    }

    public boolean hasPlayView() {
        return getChildCount() > 0;
    }

    @Override
    public void removeView(View view) {
        if (mPlayEventListener != null) {
            mPlayEventListener.onRemoveView();
        }
        super.removeView(view);
    }
}
