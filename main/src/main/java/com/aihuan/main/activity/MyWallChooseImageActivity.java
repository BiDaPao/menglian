package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.MyWallChooseImageAdapter;
import com.aihuan.main.bean.PhotoBean;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/5/13.
 */

public class MyWallChooseImageActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, int action, String oldId) {
        Intent intent = new Intent(context, MyWallChooseImageActivity.class);
        intent.putExtra(Constants.WALL_ACTION, action);
        intent.putExtra(Constants.WALL_OLD_ID, oldId);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private MyWallChooseImageAdapter mAdapter;
    private View mBtnConfirm;
    private int mAction;
    private String mOldId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wall_choose_image;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_my_album));
        Intent intent = getIntent();
        mAction = intent.getIntExtra(Constants.WALL_ACTION, 0);
        mOldId = intent.getStringExtra(Constants.WALL_OLD_ID);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_album_home);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<PhotoBean>() {
            @Override
            public RefreshAdapter<PhotoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MyWallChooseImageAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getMyAlbum(p, callback);
            }

            @Override
            public List<PhotoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), PhotoBean.class);
            }

            @Override
            public void onRefreshSuccess(List<PhotoBean> list, int listCount) {
                if (mBtnConfirm != null) {
                    mBtnConfirm.setClickable(listCount > 0);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<PhotoBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_confirm) {
            confirmClick();
        }
    }

    private void confirmClick() {
        if (mAdapter != null) {
            final PhotoBean bean = mAdapter.getCheckedPhoto();
            if (bean != null) {
                if(bean.isPrivate()){
                    DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.wall_img_tip), new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            setWall(bean.getId());
                        }
                    });
                }else{
                    setWall(bean.getId());
                }
            }
        }
    }


    /**
     * 设置背景墙中的照片
     */
    private void setWall(String newId) {
        if (mBtnConfirm != null) {
            mBtnConfirm.setClickable(false);
        }
        MainHttpUtil.setWall(mAction, 0, mOldId, newId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    MyWallActivity.forward(mContext);
                }
                ToastUtil.show(msg);
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }

            @Override
            public void onFinish() {
                if (mBtnConfirm != null) {
                    mBtnConfirm.setClickable(true);
                }
                super.onFinish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_ALBUM);
        MainHttpUtil.cancel(MainHttpConsts.SET_WALL);
        super.onDestroy();
    }
}
