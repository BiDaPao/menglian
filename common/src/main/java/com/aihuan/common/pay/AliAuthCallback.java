package com.aihuan.common.pay;

/**
 * Created by cxf on 2018/10/23.
 */

public interface AliAuthCallback {

    void onSuccess(String authCode);

    void onFailed();
}
