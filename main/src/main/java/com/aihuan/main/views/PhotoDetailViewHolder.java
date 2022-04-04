package com.aihuan.main.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.main.R;

/**
 * Created by cxf on 2019/5/10.
 */

public class PhotoDetailViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mImg;
    private String mPhotoUrl;
    private boolean mShowMore;
    private ActionLister mActionLister;
    private boolean mDestory;

    public PhotoDetailViewHolder(Context context, ViewGroup parentView, String photoUrl, boolean showMore) {
        super(context, parentView, photoUrl, showMore);
    }

    @Override
    protected void processArguments(Object... args) {
        mPhotoUrl = (String) args[0];
        mShowMore = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_photo_detail;
    }

    @Override
    public void init() {
        if (TextUtils.isEmpty(mPhotoUrl)) {
            return;
        }
        mImg = (ImageView) findViewById(R.id.img);
        View btnMore = findViewById(R.id.btn_more);
        if (mShowMore) {
            btnMore.setOnClickListener(this);
        } else {
            btnMore.setVisibility(View.INVISIBLE);
        }
        ImgLoader.displayDrawable(mContext, mPhotoUrl, new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (!mDestory && mImg != null && drawable != null) {
                    float w = drawable.getIntrinsicWidth();
                    float h = drawable.getIntrinsicHeight();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImg.getLayoutParams();
                    int targetH = 0;
                    if (w / h > 0.5625f) {//横屏  9:16=0.5625
                        targetH = (int) (ScreenDimenUtil.getInstance().getScreenWdith() / w * h);
                    } else {
                        targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (targetH != params.height) {
                        params.height = targetH;
                        params.gravity = Gravity.CENTER;
                        mImg.requestLayout();
                    }
                    mImg.setImageDrawable(drawable);
                }
            }

            @Override
            public void onLoadFailed() {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_more) {
            if (mActionLister != null) {
                mActionLister.onMoreClick();
            }
        }
    }


    public void setActionLister(ActionLister actionLister) {
        mActionLister = actionLister;
    }

    public interface ActionLister {
        void onMoreClick();
    }


    @Override
    public void onDestroy() {
        mDestory = true;
        mActionLister = null;
        super.onDestroy();
    }

}
