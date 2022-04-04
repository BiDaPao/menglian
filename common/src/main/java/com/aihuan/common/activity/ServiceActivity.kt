package com.aihuan.common.activity

import android.widget.ProgressBar
import android.webkit.WebView
import android.webkit.ValueCallback
import com.aihuan.common.mob.MobShareUtil
import com.aihuan.common.R
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.content.Intent
import android.webkit.WebSettings
import android.provider.MediaStore
import com.aihuan.common.utils.WordUtil
import android.view.ViewGroup
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import com.aihuan.common.dialog.CommonShareDialogFragment
import com.aihuan.common.CommonAppConfig
import com.aihuan.common.Constants
import com.aihuan.common.mob.ShareData
import com.aihuan.common.HtmlConfig
import com.aihuan.common.utils.L
import com.aihuan.common.utils.ToastUtil

/**
 * Created by cxf on 2018/9/25.
 */
class ServiceActivity : AbsCstActivity() {
    private var mProgressBar: ProgressBar? = null
    private var mWebView: WebView? = null
    private val CHOOSE = 100 //Android 5.0以下的
    private val CHOOSE_ANDROID_5 = 200 //Android 5.0以上的
    private var mValueCallback: ValueCallback<Uri?>? = null
    private var mValueCallback2: ValueCallback<Array<Uri>>? = null
    private var mShareCode: String? = null
    private var mMobShareUtil: MobShareUtil? = null
    override fun getLayoutId(): Int {
        //设置状态栏颜色
        window.statusBarColor = Color.WHITE
        return R.layout.activity_service
    }

    override fun main() {
        val url = intent.getStringExtra(Constants.URL)
        L.e("H5--->$url")
        mProgressBar = findViewById(R.id.progressbar)
        mWebView = findViewById(R.id.webView)
        mWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                L.e("H5-------->$url")
                if (url.startsWith(Constants.COPY_PREFIX)) {
                    val content = url.substring(Constants.COPY_PREFIX.length)
                    if (!TextUtils.isEmpty(content)) {
                        copy(content)
                    }
                } else if (url.startsWith(Constants.SHARE_PREFIX)) {
                    val content = url.substring(Constants.SHARE_PREFIX.length)
                    if (!TextUtils.isEmpty(content)) {
                        mShareCode = content
                        openShareWindow()
                    }
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                setTitle(view.title)
            }
        }
        mWebView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    mProgressBar?.visibility = View.GONE
                } else {
                    mProgressBar?.setProgress(newProgress)
                }
            }

            //以下是在各个Android版本中 WebView调用文件选择器的方法
            // For Android < 3.0
            fun openFileChooser(valueCallback: ValueCallback<Uri?>) {
                openImageChooserActivity(valueCallback)
            }

            // For Android  >= 3.0
            fun openFileChooser(valueCallback: ValueCallback<Uri?>, acceptType: String?) {
                openImageChooserActivity(valueCallback)
            }

            //For Android  >= 4.1
            fun openFileChooser(
                valueCallback: ValueCallback<Uri?>,
                acceptType: String?, capture: String?
            ) {
                openImageChooserActivity(valueCallback)
            }

            // For Android >= 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mValueCallback2 = filePathCallback
                val intent = fileChooserParams.createIntent()
                startActivityForResult(intent, CHOOSE_ANDROID_5)
                return true
            }
        }
        mWebView?.settings?.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mWebView?.loadUrl(url)
    }

    private fun openImageChooserActivity(valueCallback: ValueCallback<Uri?>) {
        mValueCallback = valueCallback
        val intent = Intent()
        if (Build.VERSION.SDK_INT < 19) {
            intent.action = Intent.ACTION_GET_CONTENT
        } else {
            intent.action = Intent.ACTION_PICK
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(
                intent,
                WordUtil.getString(R.string.choose_flie)
            ), CHOOSE
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            CHOOSE -> processResult(resultCode, intent)
            CHOOSE_ANDROID_5 -> processResultAndroid5(resultCode, intent)
        }
    }

    private fun processResult(resultCode: Int, intent: Intent?) {
        if (mValueCallback == null) {
            return
        }
        if (resultCode == RESULT_OK && intent != null) {
            val result = intent.data
            mValueCallback!!.onReceiveValue(result)
        } else {
            mValueCallback!!.onReceiveValue(null)
        }
        mValueCallback = null
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun processResultAndroid5(resultCode: Int, intent: Intent?) {
        if (mValueCallback2 == null) {
            return
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2!!.onReceiveValue(FileChooserParams.parseResult(resultCode, intent))
        } else {
            mValueCallback2!!.onReceiveValue(null)
        }
        mValueCallback2 = null
    }

    protected fun canGoBack(): Boolean {
        return mWebView != null && mWebView!!.canGoBack()
    }

    override fun onBackPressed() {
        if (isNeedExitActivity) {
            finish()
        } else {
            if (canGoBack()) {
                mWebView!!.goBack()
            } else {
                finish()
            }
        }
    }

    //身份认证成功页面
    //家族申请提交成功页面
    private val isNeedExitActivity: Boolean
        private get() {
            if (mWebView != null) {
                val url = mWebView!!.url
                if (!TextUtils.isEmpty(url)) {
                    return (url.contains("g=Appapi&m=Auth&a=success") //身份认证成功页面
                            || url.contains("g=Appapi&m=Family&a=home") //家族申请提交成功页面
                            )
                }
            }
            return false
        }

    override fun onDestroy() {
        if (mWebView != null) {
            val parent = mWebView!!.parent as ViewGroup
            parent?.removeView(mWebView)
            mWebView!!.destroy()
        }
        super.onDestroy()
    }

    /**
     * 复制到剪贴板
     */
    private fun copy(content: String) {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", content)
        cm.primaryClip = clipData
        ToastUtil.show(getString(R.string.copy_success))
    }

    /**
     * 分享
     */
    private fun openShareWindow() {
        val fragment = CommonShareDialogFragment()
        fragment.setActionListener { type ->
            val configBean = CommonAppConfig.getInstance().config
            val data = ShareData()
            data.title = configBean.agentShareTitle
            data.des = configBean.agentShareDes
            data.imgUrl = CommonAppConfig.getInstance().userBean.avatarThumb
            val webUrl = HtmlConfig.MAKE_MONEY + mShareCode
            data.webUrl = webUrl
            if (mMobShareUtil == null) {
                mMobShareUtil = MobShareUtil()
            }
            mMobShareUtil!!.execute(type, data, null)
        }
        fragment.show(supportFragmentManager, "CommonShareDialogFragment")
    }

    companion object {
        fun forward(context: Context, url: String?, addArgs: Boolean) {
//            var url = url
//            if (addArgs) {
//                url += "&uid=" + CommonAppConfig.getInstance().uid + "&token=" + CommonAppConfig.getInstance().token
//            }
            val intent = Intent(context, ServiceActivity::class.java)
            intent.putExtra(Constants.URL, url)
            context.startActivity(intent)
        }

        @JvmStatic
        fun forward(context: Context) {
            val user = CommonAppConfig.getInstance().userBean
            val url = if (user != null) {
                HtmlConfig.SERVICE_URL + "&visiter_id=${user.id}&visiter_name=${user.userNiceName}&avatar=${user.avatar}"
            } else {
                HtmlConfig.SERVICE_URL + "&visiter_id=&visiter_name=&avatar="
            }
            forward(context, url, true)
        }

        @JvmStatic
        fun forward(context: Context, uid: String, nickName: String, avatar: String) {
            val url =
                HtmlConfig.SERVICE_URL + "&visiter_id=$uid&visiter_name=$nickName&avatar=$avatar"
            forward(context, url, true)
        }
    }
}