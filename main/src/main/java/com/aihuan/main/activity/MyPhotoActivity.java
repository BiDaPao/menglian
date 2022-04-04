package com.aihuan.main.activity;

import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.views.MyAlbumViewHolder;

/**
 * Created by cxf on 2019/5/10.
 */

public class MyPhotoActivity extends AbsActivity implements View.OnClickListener {

    private MyAlbumViewHolder mAlbumViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_album;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_my_album));
        findViewById(R.id.btn_add).setOnClickListener(this);
        mAlbumViewHolder = new MyAlbumViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        mAlbumViewHolder.addToParent();
        mAlbumViewHolder.subscribeActivityLifeCycle();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAlbumViewHolder != null) {
            mAlbumViewHolder.loadData();
        }
    }

    @Override
    public void onClick(View v) {
        MyPhotoPubActivity.forward(mContext);
    }
}
