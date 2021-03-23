package cn.xihan.age.custom.cling.util;

import java.util.Collection;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:12
 * @介绍 :
 */
public class ListUtils {

    public static boolean isEmpty(Collection list){
        return !(list != null && list.size() != 0);
    }

}
