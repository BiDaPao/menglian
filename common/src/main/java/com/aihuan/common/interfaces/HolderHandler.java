package com.aihuan.common.interfaces;

import android.os.Handler;
import android.os.Message;

import com.aihuan.common.views.AbsViewHolder;

import java.lang.ref.WeakReference;

/**
 * @author Saint  2022/3/17 12:14
 * @DESC:
 */
public class HolderHandler<T extends AbsViewHolder> extends Handler {
    private WeakReference<T> activityWeakReference;

    public HolderHandler(T holder) {
        activityWeakReference = new WeakReference<>(holder);
    }

    @Override
    public void handleMessage(Message msg) {
        final T t = activityWeakReference.get();
        if (null != t) {
            t.handleMessage(msg);
        }
    }
}
