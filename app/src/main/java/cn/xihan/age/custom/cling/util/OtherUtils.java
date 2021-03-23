package cn.xihan.age.custom.cling.util;

import java.util.Formatter;
import java.util.Locale;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:12
 * @介绍 :
 */
public class OtherUtils {

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 把时间戳转换成 00:00:00 格式
     * @param timeMs    时间戳
     * @return 00:00:00 时间格式
     */
    public static String getStringTime(int timeMs) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    /**
     * 把 00:00:00 格式转成时间戳
     * @param formatTime    00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    public static int getIntTime(String formatTime) {
        if (isNull(formatTime)) {
            return 0;
        }

        String[] tmp = formatTime.split(":");
        if (tmp.length < 3) {
            return 0;
        }
        int second = Integer.parseInt(tmp[0]) * 3600 + Integer.parseInt(tmp[1]) * 60 + Integer.parseInt(tmp[2]);

        return second * 1000;
    }
}


