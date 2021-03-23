package cn.xihan.mysniffing;

import android.view.View;

import java.util.List;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/21 22:33
 * @介绍 :
 */
public class DefaultCallback implements SniffingCallback {

    @Override
    public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {

    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
    }

}
