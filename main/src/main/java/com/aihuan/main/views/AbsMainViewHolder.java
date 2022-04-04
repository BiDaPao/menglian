package com.aihuan.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;

/**
 * Created by cxf on 2018/10/26.
 */

public abstract class AbsMainViewHolder extends AbsViewHolder {

    protected boolean mFirstLoadData = true;
    private boolean mShowed;

    public AbsMainViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsMainViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    public void loadData() {
    }

    public void loadData(int pos) {
    }

    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }


    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    public boolean isShowed() {
        return mShowed;
    }
}
