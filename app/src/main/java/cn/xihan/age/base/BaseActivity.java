package cn.xihan.age.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import cn.xihan.age.MyApp;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/19 20:56
 * @介绍 :
 */
public class BaseActivity extends QMUIActivity {

    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(MyApp.getInstance(), 100);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
    }

    /**
     * Android 9 异形屏适配
     */
    protected void hideGap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    /**
     * 隐藏虚拟导航按键
     */
    protected void hideNavBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public boolean gtSdk23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public boolean gtSdk26() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
