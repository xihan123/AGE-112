package cn.xihan.age.custom.cling.control;

import android.content.Context;

import androidx.annotation.NonNull;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

import cn.xihan.age.custom.cling.entity.IDevice;
import cn.xihan.age.custom.cling.service.callback.AVTransportSubscriptionCallback;
import cn.xihan.age.custom.cling.service.callback.RenderingControlSubscriptionCallback;
import cn.xihan.age.custom.cling.service.manager.ClingManager;
import cn.xihan.age.custom.cling.util.ClingUtils;
import cn.xihan.age.custom.cling.util.OtherUtils;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:13
 * @介绍 :
 */
public class SubscriptionControl implements ISubscriptionControl<Device> {

    private AVTransportSubscriptionCallback      mAVTransportSubscriptionCallback;
    private RenderingControlSubscriptionCallback mRenderingControlSubscriptionCallback;

    public SubscriptionControl() {
    }

    @Override
    public void registerAVTransport(@NonNull IDevice<Device> device, @NonNull Context context) {
        if (OtherUtils.isNotNull(mAVTransportSubscriptionCallback)) {
            mAVTransportSubscriptionCallback.end();
        }
        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (OtherUtils.isNull(controlPointImpl)) {
            return;
        }

        mAVTransportSubscriptionCallback = new AVTransportSubscriptionCallback(device.getDevice().findService(ClingManager.AV_TRANSPORT_SERVICE), context);
        controlPointImpl.execute(mAVTransportSubscriptionCallback);
    }

    @Override
    public void registerRenderingControl(@NonNull IDevice<Device> device, @NonNull Context context) {
        if (OtherUtils.isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback.end();
        }
        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (OtherUtils.isNull(controlPointImpl)) {
            return;
        }
        mRenderingControlSubscriptionCallback = new RenderingControlSubscriptionCallback(device.getDevice().findService(ClingManager
                .RENDERING_CONTROL_SERVICE), context);
        controlPointImpl.execute(mRenderingControlSubscriptionCallback);
    }

    @Override
    public void destroy() {
        if (OtherUtils.isNotNull(mAVTransportSubscriptionCallback)) {
            mAVTransportSubscriptionCallback.end();
        }
        if (OtherUtils.isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback.end();
        }
    }
}

