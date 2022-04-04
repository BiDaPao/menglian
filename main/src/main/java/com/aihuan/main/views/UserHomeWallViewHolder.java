package com.aihuan.main.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.main.R;
import com.aihuan.main.bean.WallBean;

/**
 * Created by cxf on 2019/5/10.
 */

public class UserHomeWallViewHolder extends AbsViewHolder implements UserHomeWallVideoViewHolder.ActionListener {


    private ImageView mCover;
    private boolean mFristLoad;
    private UserHomeWallVideoViewHolder mVideoVh;

    public UserHomeWallViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_wall;
    }

    @Override
    public void init() {
        mFristLoad = true;
        mCover = (ImageView) findViewById(R.id.img);
    }


    public void loadData(WallBean wallBean) {
        if (!mFristLoad || wallBean == null) {
            return;
        }
        mFristLoad = false;
        if (wallBean.isVideo()) {
            findViewById(R.id.root).setBackgroundColor(0xff000000);
            ImgLoader.displayDrawable(mContext, wallBean.getThumb(), new ImgLoader.DrawableCallback() {
                @Override
                public void onLoadSuccess(Drawable drawable) {
                    if (mCover != null && mCover.getVisibility() == View.VISIBLE && drawable != null) {
                        float w = drawable.getIntrinsicWidth();
                        float h = drawable.getIntrinsicHeight();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCover.getLayoutParams();
                        int targetH = 0;
                        if (w / h > 0.5625f) {//横屏  9:16=0.5625
                            targetH = (int) (ScreenDimenUtil.getInstance().getScreenWdith() / w * h);
                        } else {
                            targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                        }
                        if (targetH != params.height) {
                            params.height = targetH;
                            params.gravity = Gravity.CENTER;
                            mCover.requestLayout();
                        }
                        mCover.setImageDrawable(drawable);
                    }
                }

                @Override
                public void onLoadFailed() {

                }
            });
            UserHomeWallVideoViewHolder vh = new UserHomeWallVideoViewHolder(mContext, (ViewGroup) findViewById(R.id.video_container));
            mVideoVh=vh;
            vh.addToParent();
            vh.subscribeActivityLifeCycle();
            vh.setActionListener(this);
            vh.startPlay(wallBean.getHref());
        } else {
            ImgLoader.display(mContext, wallBean.getThumb(), mCover);
        }

    }


    /**
     * 被动暂停播放
     */
    public void passivePause() {
        if (mVideoVh != null) {
            mVideoVh.passivePause();
        }
    }


    /**
     * 被动恢复播放
     */
    public void passiveResume() {
        if (mVideoVh != null) {
            mVideoVh.passiveResume();
        }
    }



    @Override
    public void onFirstFrame() {
        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
            mCover.setVisibility(View.INVISIBLE);
        }
    }
}
