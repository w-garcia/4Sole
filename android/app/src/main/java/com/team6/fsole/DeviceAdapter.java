package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Owner on 3/9/2017.
 */

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice>
{
    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> foundDevices)
    {
        super(context, android.R.layout.simple_list_item_1, foundDevices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BluetoothDevice device = getItem(position);


        return convertView;
    }



}
