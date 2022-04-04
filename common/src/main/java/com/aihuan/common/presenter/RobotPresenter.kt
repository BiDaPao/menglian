package com.aihuan.common.presenter

import com.aihuan.common.CommonAppConfig
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import java.lang.reflect.UndeclaredThrowableException

/**
 * @author Saint  2020/8/15
 */
object RobotPresenter {
    private val robotUrl =
        "https://oapi.dingtalk.com/robot/send?access_token=60b8f3b34be658aed44ffe3791e8f6f2ec359f5f5f34529321f26db327004039"

    private fun postInfo(title: String, info: String) {
        OkGo.post<String>(robotUrl)
            .upJson(Gson().toJson(ErrorInfo(title, info)))
            .tag("robotPresenter")
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {

                }
            })
    }

    fun postError(title: String, extra: String) {
        val sb = StringBuilder()
        sb.append("版本信息：")
            .append(CommonAppConfig.getInstance().versionName)
            .append("—")
            .append(CommonAppConfig.getInstance().versionCode)
            .append("### UID：")
            .append(CommonAppConfig.getInstance().uid)
            .append("\n")
        val user = CommonAppConfig.getInstance().userBean
        if (user != null) {
            sb.append("### 昵称：")
                .append(user.userNiceName)
                .append("\n")
        }
        sb.append("上报信息：")
            .append(extra)
            .append("\n")
        postInfo(title, sb.toString())
    }

    fun postError(extra: String, e: Throwable) {
        val sb = StringBuilder(extra)
        sb.append("\n")
            .append(e.toString())
            .append("\n")
            .append(e.message)
        val case = e.cause
        while (case != null) {
            sb.append("\n")
                .append(case.toString())
                .append("\n")
                .append(case.message)

        }
        postInfo(title = "错误告警", sb.toString())
    }


}

fun getMessage(e: Throwable): String? {
    var msg: String? = null
    if (e is UndeclaredThrowableException) {
        val targetEx: Throwable = e.undeclaredThrowable
        if (targetEx != null) {
            msg = targetEx.message
        }
    } else {
        msg = e.message
    }
    return msg
}

class ErrorInfo(title: String, content: String) {
    val msgtype = "markdown"
    var markdown: Markdown? = null

    init {
        markdown = Markdown(title, "ERROR_$title: \n$content")
    }
}

data class Markdown(
    val title: String,
    val text: String
)