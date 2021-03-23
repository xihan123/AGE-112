package cn.xihan.age.custom.cling.control.callback;

import cn.xihan.age.custom.cling.entity.IResponse;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:14
 * @介绍 :
 */
public interface ControlCallback<T> {

    void success(IResponse<T> response);

    void fail(IResponse<T> response);
}

