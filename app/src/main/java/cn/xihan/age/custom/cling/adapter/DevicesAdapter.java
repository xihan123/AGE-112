package cn.xihan.age.custom.cling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import org.fourthline.cling.model.meta.Device;

import cn.xihan.age.R;
import cn.xihan.age.custom.cling.entity.ClingDevice;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/20 22:12
 * @介绍 :
 */
public class DevicesAdapter extends ArrayAdapter<ClingDevice> {
    private final LayoutInflater mInflater;

    public DevicesAdapter(Context context) {
        super(context, 0);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.item_device, null);

        ClingDevice item = getItem(position);
        if (item == null || item.getDevice() == null) {
            return convertView;
        }

        Device device = item.getDevice();

        TextView textView = convertView.findViewById(R.id.title);
        textView.setText(device.getDetails().getFriendlyName());
        return convertView;
    }
}
