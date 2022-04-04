package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.custom.XEditText;

public class AuthBasicActivity extends AbsActivity {

    public static void forward(Context context, int authStauts) {
        Intent intent = new Intent(context, AuthBasicActivity.class);
        intent.putExtra(Constants.AUTH_STATUS, authStauts);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_basic;
    }

    @Override
    protected void main() {
        setTitle("认证中心");
        setOnTextListener();

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            ToastUtil.show(realName+":"+cardNumber);
            if (this.verifyInfo()){
                AuthActivity.forward(this,);
            }
        });


    }

    private boolean verifyInfo(){
        if(TextUtils.isEmpty(cardNumber)||cardNumber.length()<18){
            ToastUtil.show("证件格式不符合要求");
            return  false ;
        }

        return  true ;
    }





    private String realName ;
    private String cardNumber ;

    private void setOnTextListener(){
        XEditText realNameInput = findViewById(R.id.real_name_input);
        XEditText  cardNumberInput = findViewById(R.id.card_number_input);
        cardNumberInput.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  cardNumber =s.toString();
            }
        });
        realNameInput.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                   realName = s.toString();
            }
        });


    }
}
