package cn.xihan.age.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.json.JSONArray;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import cn.xihan.age.Config;
import cn.xihan.age.MyApp;
import cn.xihan.age.R;
import cn.xihan.age.base.BaseActivity;
import cn.xihan.age.utils.MySingleton;
import cn.xihan.age.utils.PermissionUtils;
import cn.xihan.age.utils.SharedPreferencesUtils;

public class SplashActivity extends BaseActivity {

    private static final int           PERMISSION_REQUEST_CODE = 1001;
    final                String        yes= "true";
    final  String[]      permission              = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public LinkedTreeMap launchUrl;

    Context     ctx;
    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;
    private       boolean hashandle;

    RequestQueue queue;

    private final int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ctx = getApplicationContext();
        queue = MySingleton.getInstance(ctx).getRequestQueue();
        Check();
        hide();
    }

    private void Check() {
        JsonObjectRequest jsonObject = new JsonObjectRequest(Config.Main, null, response -> {
            if (!TextUtils.isEmpty(String.valueOf(response))) {
                HashMap hashMap = new Gson().fromJson(String.valueOf(response), HashMap.class);
                launchUrl = (LinkedTreeMap) hashMap.get("launch");
                String appversion = (String) hashMap.get("appversion");
                String versioncode = (String) hashMap.get("versioncode");
                String DownloadApkUrl = (String) hashMap.get("DownloadApkUrl");
                String MyAge = (String) hashMap.get("MyAge");
                String GGRZ = (String) hashMap.get("GG");
                String isUpdate = (String) hashMap.get("isUpdate");
                String Api = (String) hashMap.get("Api");
                String Api2 = (String) hashMap.get("Api2");
                try {
                    SharedPreferencesUtils.setParam(ctx, "SplashImage", new JSONArray(((ArrayList) Objects.requireNonNull(launchUrl.get("pic"))).toArray()).toString());
                    SharedPreferencesUtils.setParam(ctx, "BlackListDevicesId", new JSONArray(((ArrayList) Objects.requireNonNull(launchUrl.get("devicesId"))).toArray()).toString());
                    SharedPreferencesUtils.setParam(ctx, "AdUrls", new JSONArray(((ArrayList) Objects.requireNonNull(launchUrl.get("adUrls"))).toArray()).toString());
                    SharedPreferencesUtils.setParam(ctx, "WhiteUrls", new JSONArray(((ArrayList) Objects.requireNonNull(launchUrl.get("wUrls"))).toArray()).toString());
                    SharedPreferencesUtils.setParam(ctx, "versionCode", Objects.requireNonNull(versioncode));
                    SharedPreferencesUtils.setParam(ctx, "versionName", Objects.requireNonNull(appversion));
                    SharedPreferencesUtils.setParam(ctx, "MyAge", Objects.requireNonNull(MyAge));
                    SharedPreferencesUtils.setParam(ctx, "GGRZ", Objects.requireNonNull(GGRZ));
                    SharedPreferencesUtils.setParam(ctx,"Api", Objects.requireNonNull(Api));
                    SharedPreferencesUtils.setParam(ctx,"Api2", Objects.requireNonNull(Api2));
                    SharedPreferencesUtils.setParam(ctx, "isUpdate", Objects.requireNonNull(isUpdate));
                    SharedPreferencesUtils.setParam(ctx, "DownloadApkUrl", Objects.requireNonNull(DownloadApkUrl));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (!TextUtils.isEmpty(MyAge)) {
                    runOnUiThread(() -> CheckTrue(MyAge));
                }
            }
        }, error -> {
            showErrorDialog(String.valueOf(error));
            Log.d(Config.TAG,"错误:"+error);
        });
        queue.add(jsonObject);
    }

    String errorMessage;
    /**
     * 展示网络错误提示框
     * */
    public void showErrorDialog(String str) {

        if (str.contains("No address associated with hostname")){
            errorMessage = str + "\n\n\n" + "请检查是否能访问\"Gitee.com\"";
        } else {
            errorMessage= str;
        }
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("提示")
                .setMessage(errorMessage)
                .setSkinManager(QMUISkinManager.defaultInstance(this))
                .addAction("退出", (dialog, index) -> {
                    dialog.dismiss();
                    MyApp.getInstance().exit();
                })
                .addAction(0, "重试", (dialog, index) -> {
                    dialog.dismiss();
                    Check();
                })
                .create(mCurrentDialogStyle).show();

    }

    private void CheckTrue(String myAge) {
        int bdversionCode = 0;
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        if (yes.equals(myAge)) {
            runOnUiThread(() -> {
                boolean checkResult = PermissionUtils.checkPermissionsGroup(SplashActivity.this, permission);
                if (!checkResult) {
                    PermissionUtils.requestPermissions(SplashActivity.this, permission,PERMISSION_REQUEST_CODE);
                }else{
                    Goto();
                }
            });
            isNetwork();
        } else {
            showNormalDialog();
        }
    }

    /**
     * 判断网络
     * */
    private void isNetwork(){
        ImageView imageView = findViewById(R.id.ImageView);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
            String str = sharedPreferences.getString("SplashImage", null);
            String str1 = null;
            if (str != null) {
                try {
                    final JSONArray jsonArray = new JSONArray(str);
                    str1 = jsonArray.getString(new Random().nextInt(jsonArray.length()));
                    //Log.v("xihan", "String" + str1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    str1 = null;
                }
            }
            if (str1 != null) { Glide.with(this).load(Uri.parse(str1)).into(imageView);}
        }

        else {
            Glide.with(this).load(R.drawable.splash).into(imageView);
        }
    }

    /**
     * 前往
     * */
    private void Goto() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        },3000);
    }

    /**
     * 展示错误提示框
     * */
    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(SplashActivity.this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("远程服务器已更换，请等待后续更新...");
        normalDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    //...To-do
                    finish();
                });
        // 显示
        normalDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hashandle){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            hashandle= false;
            finish();
        }
    }

    /**
     * 隐藏
     * */
    private void hide(){
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.hide();
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE//状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
                    | View.SYSTEM_UI_FLAG_FULLSCREEN//Activity全屏显示，且状态栏被隐藏覆盖掉。
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE//这个标志来帮助你的应用维持一个稳定的布局。
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//全屏沉浸模式，
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
        }
    }

    /**
     **  申请权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                Goto();
                //Toast.makeText(this, "get All Permisison", Toast.LENGTH_SHORT).show();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_name) + "需要 \"外部存储器读写权限\", 请到 \"应用信息 -> 权限\" 中设置！");
        builder.setPositiveButton("去设置", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setNegativeButton("暂不设置", (dialog, which) -> {
            //finish();
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (!openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }


    /**
     * 沉浸模式
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}