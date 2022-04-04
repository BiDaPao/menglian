package com.aihuan.main.presenter

import android.app.Activity
import android.content.Context
import com.aihuan.common.CommonAppConfig
import com.aihuan.common.http.CommonHttpUtil
import com.aihuan.common.http.HttpCallback
import com.aihuan.common.presenter.ServiceP
import com.aihuan.common.utils.DialogUitl
import com.aihuan.common.utils.L
import com.aihuan.common.utils.ToastUtil
import com.aihuan.im.utils.ImMessageUtil
import com.aihuan.main.activity.LoginActivity
import com.alibaba.fastjson.JSON
import com.umeng.analytics.MobclickAgent

/**
 * @author Saint  2022/3/15 11:49
 * @DESC:
 */
class UserStatusP {
    fun checkStatus(context: Context, checkBack: OnCheckBack) {
        val config = CommonAppConfig.getInstance()
        CommonHttpUtil.getUserStatus(config.uid, config.token,
            object : HttpCallback() {
                override fun onSuccess(code: Int, msg: String, info: Array<String>) {
                    if (code == 701 && info.size > 0) {
                        val obj = JSON.parseObject(info[0])
                        val isDisable = obj.getIntValue("is_disable")
                        val disableText = obj.getString("disable_text")
                        L.e("封禁状态： $isDisable  $disableText")
                        if (isDisable == 1) {
                            //退出IM
                            ImMessageUtil.getInstance().logoutImClient()
                            //友盟统计登出
                            MobclickAgent.onProfileSignOff()

                            DialogUitl.showCongelation(context, disableText,
                                { dialog, content ->
                                    ServiceP(context).openWXService()
                                }
                            ) { dialog, content ->
                                CommonAppConfig.getInstance().clearLoginInfo()
                                dialog.dismiss()
                                LoginActivity.forward()
                                if (context is Activity) context.finish()
                            }
                        } else {
                            checkBack.onCheckNormal()
                        }
                    } else if (code == 0) {
                        checkBack.onCheckNormal()
                    } else {
                        ToastUtil.show(msg)
                    }
                }
            })
    }

    fun checkStatus(context: Context, uid: String, token: String, checkBack: OnCheckBack) {
        CommonHttpUtil.getUserStatus(uid, token,
            object : HttpCallback() {
                override fun onSuccess(code: Int, msg: String, info: Array<String>) {
                    if (code == 701 && info.size > 0) {
                        val obj = JSON.parseObject(info[0])
                        val isDisable = obj.getIntValue("is_disable")
                        val disableText = obj.getString("disable_text")
                        L.e("封禁状态： $isDisable  $disableText")
                        if (isDisable == 1) {
                            //退出IM
                            ImMessageUtil.getInstance().logoutImClient()
                            //友盟统计登出
                            MobclickAgent.onProfileSignOff()

                            DialogUitl.showCongelation(context, disableText,
                                { dialog, content ->
                                    ServiceP(context).openWXService()
                                }
                            ) { dialog, content ->
                                CommonAppConfig.getInstance().clearLoginInfo()
                                dialog.dismiss()
                            }
                        } else {
                            checkBack.onCheckNormal()
                        }
                    } else if (code == 0) {
                        checkBack.onCheckNormal()
                    } else {
                        ToastUtil.show(msg)
                    }
                }
            })
    }
}

interface OnCheckBack {
    fun onCheckNormal()
}