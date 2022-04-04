package com.aihuan.main.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpUtil;

/**
 * Created by cxf on 2019/5/14.
 */

public class AgentDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private boolean mCancelable;
    private EditText mInput;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_agent;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return mCancelable;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(220);
        params.height = DpUtil.dp2px(140);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInput = (EditText) findViewById(R.id.input);
        if (mCancelable) {
            findViewById(R.id.btn_close).setOnClickListener(this);
        } else {
            findViewById(R.id.btn_close).setVisibility(View.INVISIBLE);
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            confirm();
        }
    }

    private void confirm() {
        if (mInput == null) {
            return;
        }
        String code = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(R.string.main_input_invatation_code);
            return;
        }
        MainHttpUtil.setAgent(code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    dismiss();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }


    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();
    }
}
