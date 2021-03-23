package cn.xihan.age.custom.cling.service.manager;


import android.content.Context;
import android.util.Log;

import java.util.Collection;

import cn.xihan.age.custom.cling.Config;
import cn.xihan.age.custom.cling.control.SubscriptionControl;
import cn.xihan.age.custom.cling.entity.ClingDevice;
import cn.xihan.age.custom.cling.entity.ClingDeviceList;
import cn.xihan.age.custom.cling.entity.IDevice;
import cn.xihan.age.custom.cling.util.OtherUtils;


/**
 * 说明：
 * 作者：zhouzhan
 * 日期：17/7/21 16:33
 */

public class DeviceManager implements IDeviceManager {
    private static final String              TAG = DeviceManager.class.getSimpleName();
    /**
     * 已选中的设备, 它也是 ClingDeviceList 中的一员
     */
    private       ClingDevice         mSelectedDevice;
    private final SubscriptionControl mSubscriptionControl;

    public DeviceManager() {
        mSubscriptionControl = new SubscriptionControl();
    }

    @Override
    public IDevice getSelectedDevice() {
        return mSelectedDevice;
    }

    @Override
    public void setSelectedDevice(IDevice selectedDevice) {

        Log.i(TAG, "Change selected device.");
        mSelectedDevice = (ClingDevice) selectedDevice;

        // 重置选中状态
        Collection<ClingDevice> clingDeviceList = ClingDeviceList.getInstance().getClingDeviceList();
        if (OtherUtils.isNotNull(clingDeviceList)){
            for (ClingDevice device : clingDeviceList){
                device.setSelected(false);
            }
        }
        // 设置选中状态
        mSelectedDevice.setSelected(true);
        // 清空状态
        Config.getInstance().setHasRelTimePosCallback(false);
    }

    @Override
    public void cleanSelectedDevice() {
        if (OtherUtils.isNull(mSelectedDevice))
            return;

        mSelectedDevice.setSelected(false);
    }

    @Override
    public void registerAVTransport(Context context) {
        if (OtherUtils.isNull(mSelectedDevice))
            return;

        mSubscriptionControl.registerAVTransport(mSelectedDevice, context);
    }

    @Override
    public void registerRenderingControl(Context context) {
        if (OtherUtils.isNull(mSelectedDevice))
            return;

        mSubscriptionControl.registerRenderingControl(mSelectedDevice, context);
    }

    @Override
    public void destroy() {
        if (OtherUtils.isNotNull(mSubscriptionControl)){
            mSubscriptionControl.destroy();
        }
    }
}
