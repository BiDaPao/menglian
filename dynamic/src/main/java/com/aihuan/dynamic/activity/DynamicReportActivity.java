package com.aihuan.dynamic.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.KeyBoardHeightChangeListener;
import com.aihuan.common.utils.KeyBoardHeightUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.adapter.DynamicReportAdapter;
import com.aihuan.dynamic.bean.DynamicReportBean;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;
import com.aihuan.video.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by debug on 2019/7/25.
 * 动态举报
 */

public class DynamicReportActivity extends AbsActivity implements DynamicReportAdapter.ActionListener, KeyBoardHeightChangeListener {
    public static void forward(Context context, String dynamicId) {
        Intent intent = new Intent(context, DynamicReportActivity.class);
        intent.putExtra(Constants.DYNAMIC_ID, dynamicId);
        context.startActivity(intent);
    }
    private String mDynamicId;
    private RecyclerView mRecyclerView;
    private DynamicReportAdapter mAdapter;
    private KeyBoardHeightUtil mKeyBoardHeightUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_report;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.report));
        mDynamicId = getIntent().getStringExtra(Constants.DYNAMIC_ID);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        DynamicHttpUtil.getDynamicReportList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<DynamicReportBean> list = JSON.parseArray(Arrays.toString(info), DynamicReportBean.class);
                    mAdapter = new DynamicReportAdapter(mContext, list);
                    mAdapter.setActionListener(DynamicReportActivity.this);
                    if (mRecyclerView != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    if (mKeyBoardHeightUtil != null) {
                        mKeyBoardHeightUtil.start();
                    }
                }
            }
        });
    }

    @Override
    public void onReportClick(DynamicReportBean bean, String text) {
        if (TextUtils.isEmpty(mDynamicId)) {
            return;
        }
        if (bean == null) {
            ToastUtil.show(R.string.video_report_tip_3);
            return;
        }
        String content = bean.getName();
        if (!TextUtils.isEmpty(text)) {
            content += " " + text;
        }
        DynamicHttpUtil.dynamicReport(mDynamicId, content, mReportCallback);
    }

    private HttpCallback mReportCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                onBackPressed();
            }
            ToastUtil.show(msg);
        }
    };

    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mRecyclerView != null) {
            mRecyclerView.setTranslationY(-keyboardHeight);
        }
        if (keyboardHeight > 0 && mAdapter != null) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        return false;
    }


    private void release() {
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_REPORT_LIST);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_SET_REPORT);
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        mKeyBoardHeightUtil = null;
        if (mAdapter != null) {
            mAdapter.setActionListener(null);
        }
        mAdapter = null;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }
}
