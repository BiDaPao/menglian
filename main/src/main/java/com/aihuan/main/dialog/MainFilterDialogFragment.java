package com.aihuan.main.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.aihuan.common.Constants;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.main.R;

/**
 * Created by cxf on 2019/3/30.
 */

public class MainFilterDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private byte mSex;
    private byte mChatType;
    private ActionListener mActionListener;
    private RadioButton mBtnSexAll;
    private RadioButton mBtnSexMale;
    private RadioButton mBtnSexFamale;
    private RadioButton mBtnTypeAll;
    private RadioButton mBtnTypeVideo;
    private RadioButton mBtnTypeVoice;

    private View group_call_type;
    private boolean mShowCallType;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_main_filter;
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
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBtnSexAll = (RadioButton) findViewById(R.id.btn_sex_all);
        mBtnSexMale = (RadioButton) findViewById(R.id.btn_sex_male);
        mBtnSexFamale = (RadioButton) findViewById(R.id.btn_sex_famale);
        mBtnTypeAll = (RadioButton) findViewById(R.id.btn_type_all);
        mBtnTypeVideo = (RadioButton) findViewById(R.id.btn_type_video);
        mBtnTypeVoice = (RadioButton) findViewById(R.id.btn_type_voice);
        group_call_type = findViewById(R.id.group_call_type);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mShowCallType = bundle.getBoolean("showCallType");
        if (mShowCallType){
            if (group_call_type.getVisibility() != View.VISIBLE){
                group_call_type.setVisibility(View.VISIBLE);
            }
        }else {
            if (group_call_type.getVisibility() == View.VISIBLE){
                group_call_type.setVisibility(View.GONE);
            }
        }
        mSex = bundle.getByte(Constants.MAIN_SEX);
        mChatType = bundle.getByte(Constants.CHAT_TYPE);
        if (mSex == Constants.MAIN_SEX_NONE) {
            mBtnSexAll.setChecked(true);
        } else if (mSex == Constants.MAIN_SEX_MALE) {
            mBtnSexMale.setChecked(true);
        } else if (mSex == Constants.MAIN_SEX_FAMALE) {
            mBtnSexFamale.setChecked(true);
        }
        if (mChatType == Constants.CHAT_TYPE_NONE) {
            mBtnTypeAll.setChecked(true);
        } else if (mChatType == Constants.CHAT_TYPE_VIDEO) {
            mBtnTypeVideo.setChecked(true);
        } else if (mChatType == Constants.CHAT_TYPE_VOICE) {
            mBtnTypeVoice.setChecked(true);
        }
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        }
        else if (i == R.id.btn_confirm) {
            confirmClick();
        }
    }

    private void confirmClick() {
        byte sex = mSex;
        byte chatType = mChatType;
        if (mBtnSexAll.isChecked()) {
            sex = Constants.MAIN_SEX_NONE;
        } else if (mBtnSexMale.isChecked()) {
            sex = Constants.MAIN_SEX_MALE;
        } else if (mBtnSexFamale.isChecked()) {
            sex = Constants.MAIN_SEX_FAMALE;
        }
        if (mBtnTypeAll.isChecked()) {
            chatType = Constants.CHAT_TYPE_NONE;
        } else if (mBtnTypeVideo.isChecked()) {
            chatType = Constants.CHAT_TYPE_VIDEO;
        } else if (mBtnTypeVoice.isChecked()) {
            chatType = Constants.CHAT_TYPE_VOICE;
        }
        if (mShowCallType){
            if (sex != mSex || chatType != mChatType) {
                if (mActionListener != null) {
                    mActionListener.onFilter(sex, chatType);
                }
            }
        }else {
            if (mActionListener != null) {
                mActionListener.onFilter(sex, Constants.CHAT_TYPE_VIDEO);
            }
        }

        dismiss();
    }


    @Override
    public void onDestroy() {
        mActionListener = null;
        super.onDestroy();
    }

    public interface ActionListener {
        void onFilter(byte sex, byte chatType);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
