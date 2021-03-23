package cn.xihan.age.utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import com.tencent.smtt.sdk.WebView;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 1:44
 * @介绍 :
 */
public class Utils {
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    /**
     * 获取string.xml文本
     * @param id
     * @return
     */
    public static String getString(@StringRes int id) {
        return getContext().getResources().getString(id);
    }

    public static String[] getArray(@ArrayRes int id) {
        return getContext().getResources().getStringArray(id);
    }

    public static void goBrowser(String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    /**
     * 展示错误页面
     * */
    public static void showError(WebView webView) {
        webView.loadUrl("file:///android_asset/html/Error.html");
    }

    /**
     * 选择视频播放器
     *
     * @param url
     */
    public static void selectVideoPlayer(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse(url), "video/*");
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择视频播放器"));
        } else {
            Toast.makeText(context,"没有找到匹配的程序",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 复制到剪切板
     * @param context
     * @param string
     */
    public static void copyString(Context context, String string) {
        ClipboardManager clipManager;
        clipManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy text", string);//将数据放到clip对象
        clipManager.setPrimaryClip(clipData);//将clip对象放到剪切板

        //判断剪贴板里是否有内容
        if (!clipManager.hasPrimaryClip()) {
            return;
        }
        ClipData clip = clipManager.getPrimaryClip();
        //获取 ClipDescription
        ClipDescription description = clip.getDescription();
        //获取 label
        String label = description.getLabel().toString();
        //获取 text
        String copyText = clip.getItemAt(0).getText().toString();

    }

    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
