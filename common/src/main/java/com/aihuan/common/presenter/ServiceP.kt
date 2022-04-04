package com.aihuan.common.presenter

import android.content.Context
import com.aihuan.common.utils.ToastUtil
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * @author Saint  2022/3/21 11:50
 * @DESC:
 */
class ServiceP(val context: Context) {
    fun openWXService() {
        val appId = "wx48c3027dd2fa5fbe" // 填移动应用(App)的 AppId

        val api = WXAPIFactory.createWXAPI(context, appId)

// 判断当前版本是否支持拉起客服会话
        if (api.wxAppSupportAPI >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            val req: WXOpenCustomerServiceChat.Req = WXOpenCustomerServiceChat.Req()
            req.corpId = "wwf78b1f0a088b417e" // 企业ID
            req.url = "https://work.weixin.qq.com/kfid/kfc4702d1e0f4c9d718" // 客服URL
            api.sendReq(req)
        } else {
            ToastUtil.show("当前不支持微信客服")
        }
    }
}