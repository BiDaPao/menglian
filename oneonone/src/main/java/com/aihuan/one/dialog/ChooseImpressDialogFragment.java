package com.aihuan.one.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.one.R;
import com.aihuan.one.adapter.ImpressAdapter;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/12.
 * 选择
 */

public class ChooseImpressDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ImpressAdapter mAdapter;
    private ActionListener mActionListener;
    private List<ImpressBean> mCheckImpressList;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_choose_impress;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        OneHttpUtil.getImpressList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mRecyclerView != null) {
                        List<ImpressBean> list = JSON.parseArray(Arrays.toString(info), ImpressBean.class);
                        if (list != null && list.size() > 0) {
                            for (ImpressBean bean : list) {
                                bean.setChecked(hasChecked(bean));
                            }
                            mAdapter = new ImpressAdapter(mContext, list);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    public void setCheckImpressList(List<ImpressBean> checkImpressList) {
        mCheckImpressList = checkImpressList;
    }

    private boolean hasChecked(ImpressBean bean) {
        if (mCheckImpressList != null && bean != null) {
            for (ImpressBean b : mCheckImpressList) {
                if (b != null && b.getId() == bean.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mActionListener = null;
        OneHttpUtil.cancel(OneHttpConsts.GET_IMPRESS_LIST);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            showImpress();
            dismiss();
        }
    }

    private void showImpress() {
        if (mAdapter == null || mActionListener == null) {
            return;
        }
        mActionListener.onChooseImpress(mAdapter.getChooseImpressList());
    }

    public interface ActionListener {
        void onChooseImpress(List<ImpressBean> list);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
