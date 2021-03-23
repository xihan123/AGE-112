package cn.xihan.mysniffing;

import android.view.View;

import java.util.List;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/21 22:34
 * @介绍 :
 */
public class DefaultUICallback implements SniffingUICallback {

    @Override
    public void onSniffingStart(View webView, String url) {
    }

    @Override
    public void onSniffingFinish(View webView, String url) {
    }

    @Override
    public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
    }

}
