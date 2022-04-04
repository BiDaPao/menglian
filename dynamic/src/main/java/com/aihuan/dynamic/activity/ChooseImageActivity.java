package com.aihuan.dynamic.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.adapter.ChooseImageAdapter;
import com.aihuan.dynamic.event.ImgEvent;
import com.aihuan.dynamic.upload.UploadBean;
import com.aihuan.dynamic.util.ImageUtil;
import com.aihuan.im.R;

import org.greenrobot.eventbus.EventBus;
import java.util.List;

/**
 * Created by cxf on 2018/7/16.
 * 动态选择图片
 */

public class ChooseImageActivity extends AbsActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ChooseImageAdapter mAdapter;
    private ImageUtil mImageUtil;
    private View mNoData;
    private int mAlSelectNum;

    public static void forward(Context context,int alSelectNum){
        Intent intent=new Intent(context,ChooseImageActivity.class);
        intent.putExtra(Constants.DYNAMIC_IMG_SELECT,alSelectNum);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_choose_img;
    }

    @Override
    protected void main() {
        mAlSelectNum=getIntent().getIntExtra(Constants.DYNAMIC_IMG_SELECT,0);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mRecyclerView =  findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 1, 1);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mNoData = findViewById(R.id.no_data);
        mImageUtil = new ImageUtil();
        mImageUtil.getLocalImageList(new CommonCallback<List<UploadBean>>() {
            @Override
            public void callback(List<UploadBean> list) {
                if (list.size() == 0) {
                    if (mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    mAdapter = new ChooseImageAdapter(mContext, list,mAlSelectNum);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            onBackPressed();
        } else if (i == R.id.btn_send) {
            sendImage();
        }
    }

    private void sendImage() {
        if (mAdapter != null) {
            List<UploadBean> list = mAdapter.getSelectedFile();
            EventBus.getDefault().post(new ImgEvent(list));
            finish();
        } else {
            ToastUtil.show(WordUtil.getString(R.string.im_no_image));
        }
    }


    @Override
    protected void onDestroy() {
        mImageUtil.release();
        super.onDestroy();
    }


}
