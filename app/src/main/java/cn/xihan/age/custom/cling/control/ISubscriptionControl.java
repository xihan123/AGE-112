package cn.xihan.age.custom.cling.control;

import android.content.Context;

import cn.xihan.age.custom.cling.entity.IDevice;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:14
 * @介绍 :
 */
public interface ISubscriptionControl<T> {

    /**
     * 监听投屏端 AVTransport 回调
     */
    void registerAVTransport(IDevice<T> device, Context context);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    void registerRenderingControl(IDevice<T> device, Context context);

    /**
     * 销毁: 释放资源
     */
    void destroy();
}

