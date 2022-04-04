package com.aihuan.main.views;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.main.R;

/**
 * Created by cxf on 2019/4/11.
 */

public abstract class AbsWalletDetailViewHolder extends AbsViewHolder {

    private boolean mLoadData;
    private WebView mWebView;

    public AbsWalletDetailViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_wallet_detail;
    }

    @Override
    public void init() {
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        mWebView = new WebView(mContext);
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        container.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public void loadData() {
        if (mLoadData) {
            return;
        }
        mLoadData = true;
        if (mWebView != null) {
            CommonAppConfig appConfig = CommonAppConfig.getInstance();
            String url = StringUtil.contact(getHtmlUrl(), "?uid=", appConfig.getUid(), "&token=", appConfig.getToken());
            mWebView.loadUrl(url);
        }
    }


    @Override
    public void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }

    public abstract String getHtmlUrl();
}
