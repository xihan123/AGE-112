package cn.xihan.mysniffing;

import android.view.View;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/21 22:34
 * @介绍 :
 */
public interface SniffingFilter {

    String[] DEFAULT_TYPE = {".m3u8",".mp4",".3gp",".wmv",".avi",".rm"};

    /**
     * 用来过滤视频连接，m3u8,mp4等
     * @param webView
     * @param url
     * @return
     */
    SniffingVideo onFilter(View webView, String url);

}
