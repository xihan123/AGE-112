package com.xihan.age.paylibrary;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import java.net.URISyntaxException;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/2/15 17:26
 * @介绍 :
 */
public class AlipayDonate {
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{payCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    public AlipayDonate() {
    }

    public static boolean startAlipayClient(Activity activity, String payCode) {
        return startIntentUrl(activity,INTENT_URL_FORMAT.replace("{payCode}", payCode));
    }

    public static boolean startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            Intent e = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME);
            activity.startActivity(e);
            return true;
        } catch (URISyntaxException | ActivityNotFoundException var3) {
            var3.printStackTrace();
            return false;
        }
    }
}


