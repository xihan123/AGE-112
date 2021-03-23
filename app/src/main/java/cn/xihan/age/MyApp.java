package cn.xihan.age;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;


import androidx.multidex.MultiDex;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.xihan.age.utils.Utils;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/19 17:39
 * @介绍 :
 */
public class MyApp extends Application {


    public final List<Activity> activityList = new LinkedList<>();

    private static MyApp appContext;

    public static MyApp getInstance() {
        return appContext;
    }

    public static final int PLAN_ID_EXO = 2;
    public static boolean ignoreMobile;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWebView();
        initTBS();
        initBugly();
        initQMUI();
        //initPlayer();
        Utils.init(this);
        appContext = this;
    }

    private void initQMUI() {
        QMUISwipeBackActivityManager.init(this);
    }

    /**
     * 初始化Bugly
     */
    private void initBugly() {
        Beta.initDelay = 10 * 1000;
        Beta.enableHotfix = true;
        Bugly.init(getApplicationContext(), "a826f523b3", false);
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    /**
     * 初始化x5内核
     */
    private void initTBS() {
        if (!android.os.Build.MODEL.contains("Pixel C")) {
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
            QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
                @Override
                public void onViewInitFinished(boolean arg0) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    try {
                        editor.putBoolean("X5State",arg0);
                        editor.apply();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                @Override
                public void onCoreInitFinished() {
                }
            };
            //x5内核初始化接口
            QbSdk.initX5Environment(getApplicationContext(), cb);
            HashMap map = new HashMap();
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
            QbSdk.initTbsSettings(map);
        }
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void exit(){
        for (Activity activity : activityList){
            activity.finish();
        }
        activityList.clear();
        System.exit(0);
    }
}
