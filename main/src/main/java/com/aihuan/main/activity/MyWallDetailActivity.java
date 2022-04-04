package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.WallBean;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.views.PhotoDetailViewHolder;
import com.aihuan.main.views.WallVideoPlayViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/5/13.
 */

public class MyWallDetailActivity extends AbsActivity implements PhotoDetailViewHolder.ActionLister, WallVideoPlayViewHolder.ActionLister {

    public static void forward(Context context, WallBean bean, int wallImageSize, boolean isVideo) {
        Intent intent = new Intent(context, MyWallDetailActivity.class);
        intent.putExtra(Constants.WALL_BEAN, bean);
        intent.putExtra(Constants.WALL_IMAGE_SIZE, wallImageSize);
        intent.putExtra(Constants.WALL_IS_VIDEO, isVideo);
        context.startActivity(intent);
    }

    private WallBean mWallBean;
    private int mImageSize;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mWallBean = intent.getParcelableExtra(Constants.WALL_BEAN);
        if (mWallBean == null) {
            return;
        }
        mImageSize = intent.getIntExtra(Constants.WALL_IMAGE_SIZE, 1);
        boolean isVideo = intent.getBooleanExtra(Constants.WALL_IS_VIDEO, false);
        if (isVideo) {
            WallVideoPlayViewHolder vh = new WallVideoPlayViewHolder(mContext, (ViewGroup) findViewById(R.id.container), mWallBean.getHref(), mWallBean.getThumb());
            vh.setActionLister(this);
            vh.subscribeActivityLifeCycle();
            vh.addToParent();
            vh.loadData();
        }else{
            PhotoDetailViewHolder vh = new PhotoDetailViewHolder(mContext, (ViewGroup) findViewById(R.id.container), mWallBean.getThumb(), true);
            vh.setActionLister(this);
            vh.subscribeActivityLifeCycle();
            vh.addToParent();
        }
    }

    @Override
    public void onMoreClick() {
        if (mWallBean == null) {
            return;
        }
        List<Integer> list = new ArrayList<>();
        list.add(R.string.wall_replace_img);
        list.add(R.string.wall_replace_video);
        if (mImageSize > 1) {
            list.add(R.string.delete);
        }
        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), true, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.wall_replace_img) {
                    if (mWallBean != null) {
                        MyWallChooseImageActivity.forward(mContext, 1, mWallBean.getId());
                    }
                } else if (tag == R.string.wall_replace_video) {
                    if (mWallBean != null) {
                        MyWallChooseVideoActivity.forward(mContext, 1, mWallBean.getId());
                    }
                } else if (tag == R.string.delete) {
                    delete();
                }
            }
        });
    }


    /**
     * 删除自己的背景墙中的照片
     */
    private void delete() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.wall_delete_tip), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (mWallBean == null) {
                    return;
                }
                MainHttpUtil.deleteWall(mWallBean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            finish();
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }


}
