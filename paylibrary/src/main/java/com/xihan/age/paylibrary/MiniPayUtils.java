package com.xihan.age.paylibrary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/2/15 17:19
 * @介绍 :
 */
public class MiniPayUtils {
    static final String EXTRA_KEY_PAY_CONFIG = "pay_config";

    /*package*/
    static void closeIO(Closeable target) {
        try {
            if (target != null)
                target.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*package*/
    static boolean isActivityAvailable(Context cxt, Intent intent) {
        PackageManager pm = cxt.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<ResolveInfo> list = pm.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    public static void setupPay(Context cxt, Config config) {
        Intent i = new Intent(cxt, ZhiActivity.class);
        i.putExtra(EXTRA_KEY_PAY_CONFIG, config);
        cxt.startActivity(i);
    }
}

