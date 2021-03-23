package cn.xihan.age.activitys;

import static cn.xihan.age.Config.*;
import static cn.xihan.age.utils.Utils.*;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;


import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import cn.xihan.age.MyApp;
import cn.xihan.age.R;
import cn.xihan.age.base.BaseActivity;
import cn.xihan.age.services.ClearVideoCacheService;
import cn.xihan.age.utils.ClearUtil;
import cn.xihan.age.utils.MySingleton;
import cn.xihan.age.utils.SharedPreferencesUtils;
import cn.xihan.age.utils.Utils;
import cn.xihan.age.webview.X5WebView;
import cn.xihan.mysniffing.DefaultFilter;
import cn.xihan.mysniffing.SniffingUICallback;
import cn.xihan.mysniffing.SniffingVideo;


public class MainActivity extends BaseActivity {

    @BindView(R.id.nav_view)
    BottomNavigationView navView;
    @BindView(R.id.myWeb)
    X5WebView            myWeb;
    @BindView(R.id.topbar)
    QMUITopBarLayout     topbar;

    public ArrayList     tabData;
    public LinkedTreeMap launchUrl;
    public String        aboutUrl = "https://xihan.rthe.net/about.html";

    /**
     * 页面是否加载错误
     * */
    private boolean isError;

    /**
     * 页面是否加载成功
     * */
    private boolean isSuccess;
    static String[] adUrls;
    static String[] wUrls;
    String VideoUrl,VideoTitle;

    RequestQueue  queue;
    Context       ctx;
    Context ctx2;

    @BindView(R.id.fl_main)
    FrameLayout flMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ctx = getApplicationContext();
        ctx2 = this;
        queue = MySingleton.getInstance(ctx).getRequestQueue();
        initWebView();
        initTopBar();
        initHardwareAccelerate();
        startrequst();
    }

    QMUITipDialog tipDialog;
    /**
     * 初始化标题栏
     */
    private void initTopBar() {
        topbar.addLeftBackImageButton().setOnClickListener(v -> {
            if (myWeb.canGoBack()) {
                myWeb.goBack();
            } else {
                Snackbar.make(topbar, "已经没法返回啦~", Snackbar.LENGTH_LONG).show();
            }
        });
        injectEntrance(topbar);
        topbar.setTitle(getString(R.string.app_name));
        topbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (myWeb.getUrl().contains("https://web.age-spa.com:8443/#/play/")) {
                    tipDialog = new QMUITipDialog.Builder(ctx2)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord("正在加载")
                            .create();
                    String url = myWeb.getUrl();
                    cn.xihan.mysniffing.x5.SniffingUtil
                            .get()
                            .activity(MainActivity.this)
                            .referer(url)
                            .callback(new SniffingUICallback() {
                                @Override
                                public void onSniffingStart(View webView, String url) {

                                    tipDialog.show();
//                                    new Handler().postDelayed(tipDialog::dismiss,1000);
                                }

                                @Override
                                public void onSniffingFinish(View webView, String url) {
                                    tipDialog.dismiss();
                                }

                                @Override
                                public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
                                    //Toast.makeText(ctx2,"视频地址:" + videos.toString(),Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i < videos.size(); i++){
                                        VideoUrl =videos.get(i).getUrl();
                                        //Log.d(TAG,"视频地址:" +VideoUrl);
                                    }
                                    if (!TextUtils.isEmpty(VideoUrl)){
                                        ShowDiglog();
                                    }
                                }

                                @Override
                                public void onSniffingError(View webView, String url, int errorCode) {
                                    tipDialog = new QMUITipDialog.Builder(ctx2)
                                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                            .setTipWord("解析失败..")
                                            .create();
                                    tipDialog.show();
                                    new Handler().postDelayed(tipDialog::dismiss,1000);
//                                    Toast.makeText(ctx2,"解析失败...",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .connTimeOut(5 *1000)
                            .readTimeOut(5 *1000)
                            .filter(new DefaultFilter())
                            .url(url)
                            .start();
                }else{
                    Toast.makeText(ctx2,"请在播放页面进行此操作",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            private void ShowDiglog() {
                String[] listItems = new String[]{
                        "内部播放器播放",
                        "外部播放器播放",
                        "复制视频地址",
                        "下载"
                };
                List<String> data = new ArrayList<>();
                Collections.addAll(data, listItems);
                ArrayAdapter adapter = new ArrayAdapter<>(ctx2, R.layout.simple_list_item, data);
                AdapterView.OnItemClickListener onItemClickListener = (adapterView, view, i, l) -> {
                    //Toast.makeText(ctx, "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                    switch(i){
                        case 0:
                            Bundle bundle =new Bundle();
                            bundle.putString("url",VideoUrl);
                            bundle.putString("title", myWeb.getTitle());
                            startActivity(new Intent(MainActivity.this,PlayerActivity.class).putExtras(bundle));
                            break;
                        case 1:
                            selectVideoPlayer(ctx2,VideoUrl);
                            break;
                        case 2:
                            Toast.makeText(ctx,"已复制:" +VideoUrl,Toast.LENGTH_SHORT).show();
                            copyString(ctx,VideoUrl);
                            break;
                        case 3:
                            Uri uri = Uri.parse(VideoUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            break;
                    }
                    if (mNormalPopup != null) {
                        mNormalPopup.dismiss();
                    }
                };
                //registerForContextMenu(myWeb);
                QMUIPopups.listPopup(ctx2,
                        QMUIDisplayHelper.dp2px(ctx, 250),
                        QMUIDisplayHelper.dp2px(ctx, 300),
                        adapter,
                        onItemClickListener)
                        .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                        .preferredDirection(QMUIPopup.DIRECTION_TOP)
                        .shadow(true)
                        .offsetYIfTop(QMUIDisplayHelper.dp2px(ctx2, 5))
                        .skinManager(QMUISkinManager.defaultInstance(ctx2))
                        .onDismiss(() -> {
                            //Toast.makeText(getContext(), "onDismiss", Toast.LENGTH_SHORT).show();
                        })
                        .show(navView);

            }

        });


    }

    public void injectEntrance(QMUITopBarLayout topbar) {
        topbar.addRightTextButton("···", QMUIViewHelper.generateViewId())
                .setOnClickListener(v -> showBottomSheetList(topbar.getContext()));
    }

    private QMUIPopup mNormalPopup;
    /**
     * 菜单按钮事件
     *
     * @param context
     */
    private void showBottomSheetList(Context context) {
        String[] listItems = new String[]{
                "刷新",
                "清理缓存",
                "关于",
                "退出"

        };
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(context, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = (adapterView, view, i, l) -> {
            switch (i) {
                        case 0:
                            if (myWeb.getUrl().equals("file:///android_asset/html/Error.html")) {
                                showNormalDialog();
                            } else {
                                myWeb.reload();
                            }
                            break;
                        case 1:
                            showCleanDialog();
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this,AboutActivity.class));
                            break;
                        case 3:
                            new Handler().postDelayed(() -> MyApp.getInstance().exit(),100);
                            break;
                    }
            if (mNormalPopup != null) {
                mNormalPopup.dismiss();
            }
        };
        mNormalPopup = QMUIPopups.listPopup(context,
                QMUIDisplayHelper.dp2px(context, 250),
                QMUIDisplayHelper.dp2px(context, 300),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_AUTO)
                .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                .shadow(true)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(ctx, 5))
                .skinManager(QMUISkinManager.defaultInstance(context))
                .onDismiss(() -> {
                    //Toast.makeText(context, "onDismiss", Toast.LENGTH_SHORT).show();
                })
                .show(navView);
    }

    /**
     * 开始请求
     */
    private void startrequst() {
        try {
            requstData(0);
        } catch (Exception e) {
            updateUI(true);
            e.printStackTrace();
        }
    }

    /**
     * 请求数据
     */
    public void requstData(Integer num) {
        int intValue = num;
        if (intValue >= urlArray.length) {
            updateUI(true);
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlArray[num], null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG,String.valueOf(response));
                if (!TextUtils.isEmpty(String.valueOf(response))) {
                    HashMap hashMap = new Gson().fromJson(String.valueOf(response), HashMap.class);
                    launchUrl = (LinkedTreeMap) hashMap.get("launch");
                    tabData = (ArrayList) hashMap.get("nav");
                    aboutUrl = (String) hashMap.get("about");
                    SharedPreferences.Editor edit = getSharedPreferences("data", 0).edit();
                    try {
                        edit.putString("adImage", new JSONArray(((ArrayList) Objects.requireNonNull(launchUrl.get("pic"))).toArray()).toString());
                        edit.putString("adUrl", (String) launchUrl.get("url"));
                        edit.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    two();
                }
            }

            private void two() {
                updateUI(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    one(num + 1);
                } catch (Exception exception) {
                    two();
                    exception.printStackTrace();
                }
            }

            private void two() {
                updateUI(true);
            }

            private void one(int i) {
                requstData(i);
            }
        });
        queue.add(jsonObjectRequest);

    }

    /**
     * 更新UI
     */
    public void updateUI(boolean isError2) {
        runOnUiThread(() -> {
            if (isError2) {
                showNormalDialog();
            } else {
                success();
            }
        });
    }

    /**
     * 请求成功
     */
    private void success() {
        Menu menu = navView.getMenu();
        menu.clear();
        Iterator it = tabData.iterator();
        int i = 0;
        while (it.hasNext()) {
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) it.next();
            menu.add(0, i, i, (String) linkedTreeMap.get("title"));

            int finalI = i;
            Glide.with(navView).load(Uri.parse((String) linkedTreeMap.get("icon"))).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                    menu.findItem(finalI).setIcon(resource);
                }
            });
            i++;
        }
        navView.setOnNavigationItemSelectedListener(item -> {
            //item.getTitle().toString();
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) tabData.get(item.getItemId());
            myWeb.loadUrl((String) linkedTreeMap.get("url"));
            return true;
        });
        if (i > 0) {
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) tabData.get(0);
            myWeb.loadUrl((String) linkedTreeMap.get("url"));
            //Log.d(TAG,"i:"+i);
        }
    }

    private final int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    /**
     * 展示网络错误提示框
     */
    public void showNormalDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("提示")
                .setMessage("网络异常，请检查后重试")
                .setSkinManager(QMUISkinManager.defaultInstance(this))
                .addAction("退出", (dialog, index) -> {
                    dialog.dismiss();
                    MyApp.getInstance().exit();
                })
                .addAction(0, "重试", (dialog, index) -> {
                    dialog.dismiss();
                    startrequst();
                })
                .create(mCurrentDialogStyle).show();

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

        if ((Boolean) SharedPreferencesUtils.getParam(ctx,"X5State",false)){
            if ((Boolean) SharedPreferencesUtils.getParam(ctx,"自动全屏播放",false)){
                Bundle bundle = new Bundle();
                Bundle bundle2 = new Bundle();
                bundle.putBoolean("require", true);
                bundle2.putBoolean("standardFullScreen", false);
                bundle2.putInt("DefaultVideoScreen", 2);
                myWeb.getX5WebViewExtension().invokeMiscMethod("setVideoParams", bundle2);
                myWeb.getX5WebViewExtension().invokeMiscMethod("setVideoPlaybackRequiresUserGesture", bundle);
            }
        }

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
                return false;
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
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                String adUrl = sharedPreferences.getString("AdUrls", null);
                String wUrl = sharedPreferences.getString("WhiteUrls", null);
                boolean isOpenDiyApi = sharedPreferences.getBoolean("自定义API", false);
                String Api = sharedPreferences.getString("Api",null);
                String Api2 = sharedPreferences.getString("Api2",null);
                adUrls = new Gson().fromJson(adUrl, String[].class);
                wUrls = new Gson().fromJson(wUrl, String[].class);
//                if (url.contains(".mp4")){
//                    VideoUrl=url;
//                    Log.d(Config.TAG,"mp4链接:" + url);
//                }else if (url.contains(".m3u8")){
//                    VideoUrl=url;
//                    Log.d(Config.TAG,"m3u8链接:" + url);
//                }

                //判断是否是广告相关的资源链接
                if (!isAd(url)) {
                    //这里是不做处理的数据
                    if (url.contains(".mp4")){
                        VideoUrl=url;
                        Log.d(TAG,"mp4链接:" + url);
                    }else if (url.contains(".m3u8")){
                        VideoUrl=url;
                        Log.d(TAG,"m3u8链接:" + url);
                    }
                    return super.shouldInterceptRequest(webView, url);
                } else if (isW(url)){
                    //Log.d(Config.TAG,"白名单:"+url);
                    return super.shouldInterceptRequest(webView, url);
                } else  {
                    //有广告的请求数据，我们直接返回空数据，注：不能直接返回null
                    //Log.d(Config.TAG,"广告:"+url);
                    return new WebResourceResponse(null, null, null);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!isError) {
                    // 在访问失败的时候会首先回调onReceivedError，然后再回调onPageFinished
                    isSuccess = true;
                }
                // 还原变量
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                isSuccess = false;
                Utils.showError(view);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                isSuccess = false;
            }
        });

        myWeb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(VideoUrl)){
                    ShowDiglog();
                }
                return false;
            }
            private void ShowDiglog() {
                String[] listItems = new String[]{
                        "内部播放器播放",
                        "外部播放器播放",
                        "复制视频地址",
                        "下载"
                };
                List<String> data = new ArrayList<>();
                Collections.addAll(data, listItems);
                ArrayAdapter adapter = new ArrayAdapter<>(ctx2, R.layout.simple_list_item, data);
                AdapterView.OnItemClickListener onItemClickListener = (adapterView, view, i, l) -> {
                    //Toast.makeText(ctx, "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                    switch(i){
                        case 0:
                            Bundle bundle =new Bundle();
                            bundle.putString("url",VideoUrl);
                            bundle.putString("title", myWeb.getTitle());
                            startActivity(new Intent(MainActivity.this,PlayerActivity.class).putExtras(bundle));
                            break;
                        case 1:
                            selectVideoPlayer(ctx2,VideoUrl);
                            break;
                        case 2:
                            Toast.makeText(ctx,"已复制:" +VideoUrl,Toast.LENGTH_SHORT).show();
                            copyString(ctx,VideoUrl);
                            break;
                        case 3:
                            Uri uri = Uri.parse(VideoUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            break;
                    }
                    if (mNormalPopup != null) {
                        mNormalPopup.dismiss();
                    }
                };
                //registerForContextMenu(myWeb);
                QMUIPopups.listPopup(ctx2,
                        QMUIDisplayHelper.dp2px(ctx, 250),
                        QMUIDisplayHelper.dp2px(ctx, 300),
                        adapter,
                        onItemClickListener)
                        .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                        .preferredDirection(QMUIPopup.DIRECTION_TOP)
                        .shadow(true)
                        .offsetYIfTop(QMUIDisplayHelper.dp2px(ctx2, 5))
                        .skinManager(QMUISkinManager.defaultInstance(ctx2))
                        .onDismiss(() -> {
                            //Toast.makeText(getContext(), "onDismiss", Toast.LENGTH_SHORT).show();
                        })
                        .show(topbar);

            }
        });
    }

    /**
     * 判断是否广告url
     * @param url
     * @return
     */
    public static boolean isAd(String url) {
        for (String adUrl : adUrls ) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否白名单Url
     * @param url
     * @return
     */
    public static boolean isW(String url){
        for (String adUrl : wUrls ) {
            if (url.equals(adUrl)) {
                return true;
            }
        }
        return false;
    }

    long exitTime;

    /**
     * 定义返回键事件
     */
    public void doOnBackPressed() {
        if (myWeb.canGoBack()) {
            myWeb.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //连击在两秒之内退出
            {
                Snackbar.make(myWeb, "再按一次退出程序~", Snackbar.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                MyApp.getInstance().exit();
            }
//        moveTaskToBack(false);
        }
    }

    @Override
    protected void onDestroy() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //释放资源
        if (myWeb != null){
            myWeb.destroy();
        }
        cn.xihan.mysniffing.x5.SniffingUtil.get().releaseAll();
        boolean isAutoDelete = (boolean) SharedPreferencesUtils.getParam(ctx,"自动删除视频缓存",false);
        if (isAutoDelete){
            startService(new Intent(this, ClearVideoCacheService.class));
            ClearUtil.clearVideoCacheCache();
        }
        super.onDestroy();
    }

    /**
     * 展示清理缓存弹窗
     */
    private void showCleanDialog() {
        QMUIDialog.MessageDialogBuilder dialog = new QMUIDialog.MessageDialogBuilder(this);

        dialog.setTitle("退出后是否清理缓存?");
        dialog.setMessage(getString(R.string.setting_cleansMessage));

        dialog.setSkinManager(QMUISkinManager.defaultInstance(this));
        dialog.addAction("清理视频缓存", (dialog1, index) -> {
            dialog1.dismiss();
            ClearUtil.clearVideoCacheCache();
        });
        dialog.addAction("清除全部缓存", (dialog12, index) -> {
            dialog12.dismiss();
            ClearUtil.clearAllCache(ctx);
        });
        dialog.create(mCurrentDialogStyle).show();
    }

    /**
     * 启用硬件加速
     */
    private void initHardwareAccelerate() {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}