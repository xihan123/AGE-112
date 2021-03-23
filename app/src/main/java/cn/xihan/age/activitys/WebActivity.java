package cn.xihan.age.activitys;

import static cn.xihan.age.Config.PHONE_USER_AGENT;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xihan.age.R;
import cn.xihan.age.base.BaseActivity;
import cn.xihan.age.webview.X5WebView;

public class WebActivity extends BaseActivity {

    @BindView(R.id.myWeb2)
    X5WebView myWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            initWebView();
            myWeb.loadUrl(bundle.getString("url"));
        }
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        myWeb.getSettings().setJavaScriptEnabled(true);//支持js
        myWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //设置Javascript是否可以自动打开Windows
        myWeb.getSettings().setAllowFileAccess(false);
        myWeb.getSettings().setSupportZoom(false); //设置支持缩放
        myWeb.getSettings().setBuiltInZoomControls(false); //设置内置缩放控件
        myWeb.getSettings().setUseWideViewPort(true); //设置使用广角端口
        myWeb.getSettings().setSupportMultipleWindows(true); //设置支持多个Windows
        myWeb.getSettings().setAppCacheEnabled(true); //设置应用程序缓存
        myWeb.getSettings().setLoadWithOverviewMode(true); //设置总览模式的加载
        // webSetting.setDatabaseEnabled(true); //设置数据库
        myWeb.getSettings().setDomStorageEnabled(true); //设置Dom存储
        myWeb.getSettings().setGeolocationEnabled(false); //设置地理位置
        myWeb.getSettings().setAppCacheMaxSize(Long.MAX_VALUE); //设置应用缓存最大大小
        myWeb.getSettings().setDefaultTextEncodingName("UTF-8"); //设置默认文本编码名称
        myWeb.getSettings().setDatabaseEnabled(true); //设置数据库
        myWeb.getSettings().setSavePassword(true); //设置保存密码
        myWeb.getSettings().setMixedContentMode(0); //设置混合内容模式
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH); //设置渲染优先级
        myWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); //设置缓存模式
        //myWeb.getSettings().setUserAgent(PHONE_USER_AGENT);
        myWeb.getSettings().setUserAgentString(myWeb.getSettings().getUserAgentString() + PHONE_USER_AGENT); //设置用户代理字符串

        myWeb.getSettings().setDisplayZoomControls(false); //设置显示缩放控件
        IX5WebViewExtension ix5 = myWeb.getX5WebViewExtension();
        if (null != ix5) {
            ix5.setScrollBarFadingEnabled(false); //设置滚动条淡入
        }

        myWeb.requestFocus(130); //请求重点
        myWeb.setEnabled(true); //设置启用
        myWeb.setFocusable(true); //设置焦点


        myWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final JsResult finalRes = result;
                new AlertDialog.Builder(view.getContext())
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                (dialog, which) -> finalRes.confirm())
                        .setCancelable(false)
                        .create()
                        .show();
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                result.cancel();
                return true;
            }
        });
        myWeb.setWebViewClient(new WebViewClient(){
            // 防止加载网页时调起系统浏览器
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });


    }
}