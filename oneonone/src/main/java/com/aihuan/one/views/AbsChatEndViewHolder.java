package com.aihuan.one.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatBaseActivity;

/**
 * Created by cxf on 2019/4/21.
 */

public abstract class AbsChatEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    public AbsChatEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsChatEndViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    public void init() {
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    protected abstract void confirmClick();

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_confirm) {
            confirmClick();
        }
    }
}
