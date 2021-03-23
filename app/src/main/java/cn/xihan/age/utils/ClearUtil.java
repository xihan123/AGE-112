package cn.xihan.age.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/19 19:56
 * @介绍 :
 */
public class ClearUtil {

    /**
     * 清除所有缓存
     * @param context
     */
    public static void clearAllCache(Context context) {
        Runtime runtime = Runtime.getRuntime();
        //清除内部缓存
        deleteDir(context.getCacheDir());
        try {
            runtime.exec("rm -rf /sdcard/cn.xihan.age/" );
            runtime.exec("rm -r /sdcard/Android/files/cn.xihan.age/" );
            runtime.exec("rm -r /sdcard/Android/data/cn.xihan.age/" );
            runtime.exec("rm -r /storage/emulated/0/Android/data/cn.xihan.age/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/cache/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/code_cache/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/app_webview/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/app_textures/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/files/data/" );
            runtime.exec("rm -r /data/data/cn.xihan.age/files/live_log/" );
//            runtime.exec("rm -r /data/data/cn.xihan.age/app_tbs/" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //清除外部缓存
            deleteDir(context.getExternalCacheDir());

        }
    }


    /**
     * 清除视频缓存
     */
    public static void clearVideoCacheCache() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("rm -r /storage/emulated/0/Android/data/cn.xihan.age/files/VideoCache/" );
            runtime.exec("rm -r /storage/emulated/0/Android/files/cn.xihan.age/files/VideoCache/" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取指定文件的大小
     *
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {

        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);//使用FileInputStream读入file的数据流
                size = fis.available();//文件的大小
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    Objects.requireNonNull(fis).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }
    /**
     * 递归删除文件
     * @param dir
     * @return
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : Objects.requireNonNull(children)) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return Objects.requireNonNull(dir).delete();
    }
    /**
     *获取文件
     *Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
     *目录，一般放一些长时间保存的数据
     Context.getExternalCacheDir() -->
     SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File value : Objects.requireNonNull(fileList)) {
                // 如果下面还有文件
                if (value.isDirectory()) {
                    size = size + getFolderSize(value);
                } else {
                    size = size + value.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}

