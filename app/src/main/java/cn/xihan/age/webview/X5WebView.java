package cn.xihan.age.webview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;


import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.IWindowInsetLayout;
import com.tencent.smtt.sdk.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/19 18:00
 * @介绍 :
 */
public class X5WebView extends WebView implements IWindowInsetLayout {

    private static final String TAG = "X5WebView";
    private static boolean sIsReflectionOccurError = false;

    private Object mAwContents;
    private Object mWebContents;
    private Method mSetDisplayCutoutSafeAreaMethod;
    private Rect mSafeAreaRectCache;

    /**
     * if true, the web content may be located under status bar
     */
    private boolean                                                                   mNeedDispatchSafeAreaInset = false;
    private       com.qmuiteam.qmui.widget.webview.QMUIWebView.Callback                     mCallback;
    private final List<com.qmuiteam.qmui.widget.webview.QMUIWebView.OnScrollChangeListener> mOnScrollChangeListeners = new ArrayList<>();

    public X5WebView(Context context) {
        super(context);
        init();
        setBackgroundColor(85621);
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public X5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void addJavascriptInterface(Object object, String name) {

    }

    @Deprecated
    public void setCustomOnScrollChangeListener(com.qmuiteam.qmui.widget.webview.QMUIWebView.OnScrollChangeListener onScrollChangeListener) {
        addCustomOnScrollChangeListener(onScrollChangeListener);
    }

    public void addCustomOnScrollChangeListener(com.qmuiteam.qmui.widget.webview.QMUIWebView.OnScrollChangeListener listener) {
        if (!mOnScrollChangeListeners.contains(listener)) {
            mOnScrollChangeListeners.add(listener);
        }
    }

    public void removeOnScrollChangeListener(com.qmuiteam.qmui.widget.webview.QMUIWebView.OnScrollChangeListener listener) {
        mOnScrollChangeListeners.remove(listener);
    }

    public void removeAllOnScrollChangeListener(){
        mOnScrollChangeListeners.clear();
    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public void setNeedDispatchSafeAreaInset(boolean needDispatchSafeAreaInset) {
        if (mNeedDispatchSafeAreaInset != needDispatchSafeAreaInset) {
            mNeedDispatchSafeAreaInset = needDispatchSafeAreaInset;
            if (ViewCompat.isAttachedToWindow(this)) {
                if (needDispatchSafeAreaInset) {
                    ViewCompat.requestApplyInsets(this);
                } else {
                    // clear insets
                    setStyleDisplayCutoutSafeArea(new Rect());
                }
            }
        }
    }

    public boolean isNeedDispatchSafeAreaInset() {
        return mNeedDispatchSafeAreaInset;
    }

    public void setCallback(com.qmuiteam.qmui.widget.webview.QMUIWebView.Callback callback) {
        mCallback = callback;
    }

    private void doNotSupportChangeCssEnv() {
        sIsReflectionOccurError = true;
        if (mCallback != null) {
            mCallback.onSureNotSupportChangeCssEnv();
        }
    }

    boolean isNotSupportChangeCssEnv() {
        return sIsReflectionOccurError;
    }

    @Override
    public boolean applySystemWindowInsets19(Rect insets) {
        return false;
    }

    @Override
    public WindowInsetsCompat applySystemWindowInsets21(WindowInsetsCompat insets) {
        if (!mNeedDispatchSafeAreaInset) {
            return insets;
        }
        float density = QMUIDisplayHelper.getDensity(getContext());
        int left = insets.getSystemWindowInsetLeft();
        int top = insets.getSystemWindowInsetTop();
        int right = insets.getSystemWindowInsetRight();
        int bottom = insets.getSystemWindowInsetBottom();
        Rect rect = new Rect(
                (int) (left / density + getExtraInsetLeft(density)),
                (int) (top / density + getExtraInsetTop(density)),
                (int) (right / density + getExtraInsetRight(density)),
                (int) (bottom / density + getExtraInsetBottom(density))
        );
        setStyleDisplayCutoutSafeArea(rect);
        return insets.consumeSystemWindowInsets();
    }

    protected int getExtraInsetTop(float density) {
        return 0;
    }

    protected int getExtraInsetLeft(float density) {
        return 0;
    }

    protected int getExtraInsetRight(float density) {
        return 0;
    }

    protected int getExtraInsetBottom(float density) {
        return 0;
    }

    @Override
    public void destroy() {
        mAwContents = null;
        mWebContents = null;
        mSetDisplayCutoutSafeAreaMethod = null;
        stopLoading();
        super.destroy();
    }

    private void setStyleDisplayCutoutSafeArea(@NonNull Rect rect) {
        if (sIsReflectionOccurError || Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            return;
        }

        if (rect == mSafeAreaRectCache) {
            return;
        }

        if (mSafeAreaRectCache == null) {
            mSafeAreaRectCache = new Rect(rect);
        } else {
            mSafeAreaRectCache.set(rect);
        }

        long start = System.currentTimeMillis();
        if (mAwContents == null || mWebContents == null || mSetDisplayCutoutSafeAreaMethod == null) {
            try {
                Field providerField = android.webkit.WebView.class.getDeclaredField("mProvider");
                providerField.setAccessible(true);
                Object provider = providerField.get(this);

                mAwContents = getAwContentsFieldValueInProvider(Objects.requireNonNull(provider));
                if (mAwContents == null) {
                    return;
                }

                mWebContents = getWebContentsFieldValueInAwContents(mAwContents);
                if (mWebContents == null) {
                    return;
                }

                mSetDisplayCutoutSafeAreaMethod = getSetDisplayCutoutSafeAreaMethodInWebContents(mWebContents);
                if (mSetDisplayCutoutSafeAreaMethod == null) {
                    // no such method, maybe the old version
                    doNotSupportChangeCssEnv();
                    return;
                }
            } catch (Exception e) {
                doNotSupportChangeCssEnv();
                Log.i(TAG, "setStyleDisplayCutoutSafeArea error: " + e);
            }
        }

        try {
            mSetDisplayCutoutSafeAreaMethod.setAccessible(true);
            mSetDisplayCutoutSafeAreaMethod.invoke(mWebContents, rect);
        } catch (Exception e) {
            sIsReflectionOccurError = true;
            Log.i(TAG, "setStyleDisplayCutoutSafeArea error: " + e);
        }

        Log.i(TAG, "setDisplayCutoutSafeArea speed time: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewCompat.requestApplyInsets(this);
    }

    private Object getAwContentsFieldValueInProvider(Object provider) throws IllegalAccessException {
        try {
            Field awContentsField = provider.getClass().getDeclaredField("mAwContents");
            if (awContentsField != null) {
                awContentsField.setAccessible(true);
                return awContentsField.get(provider);
            }
        } catch (NoSuchFieldException ignored) {

        }
        // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        for (Field field : provider.getClass().getDeclaredFields()) {
            // 1. get field mAwContents
            field.setAccessible(true);
            Object awContents = field.get(provider);
            if (awContents == null) {
                continue;
            }
            if (awContents.getClass().getSimpleName().equals("AwContents")) {
                return awContents;
            }
        }
        return null;
    }

    private Object getWebContentsFieldValueInAwContents(Object awContents) throws IllegalAccessException {
        try {
            Field webContentsField = awContents.getClass().getDeclaredField("mWebContents");
            if (webContentsField != null) {
                webContentsField.setAccessible(true);
                return webContentsField.get(awContents);
            }
        } catch (NoSuchFieldException ignored) {

        }
        // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        for (Field innerField : awContents.getClass().getDeclaredFields()) {
            innerField.setAccessible(true);
            Object webContents = innerField.get(awContents);
            if (webContents == null) {
                continue;
            }
            if (webContents.getClass().getSimpleName().equals("WebContentsImpl")) {
                return webContents;
            }
        }
        return null;
    }

    private Method getSetDisplayCutoutSafeAreaMethodInWebContents(Object webContents) {
        try {
            return webContents.getClass()
                    .getDeclaredMethod("setDisplayCutoutSafeArea", Rect.class);
        } catch (NoSuchMethodException ignored) {

        }
        // Unfortunately, the source code is ugly in some roms, so we can not reflect the field/method by name
        // not very safe in future
        for (Method method : webContents.getClass().getDeclaredMethods()) {
            if (method.getReturnType() == void.class && method.getParameterTypes().length == 1 &&
                    method.getParameterTypes()[0] == Rect.class) {
                return method;
            }
        }
        return null;
    }


    public interface Callback {
        void onSureNotSupportChangeCssEnv();
    }

    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param webView    The view whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(WebView webView, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}

