package com.aihuan.one.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;

/**
 * Created by cxf on 2019/3/23.
 */

public abstract class AbsUserHomeViewHolder extends AbsViewHolder {

    protected boolean mFirstLoadData = true;
    private boolean mShowed;
    public AbsUserHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsUserHomeViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }


    public abstract void loadData();

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
