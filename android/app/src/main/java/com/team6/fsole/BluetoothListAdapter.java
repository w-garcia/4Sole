package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julissa on 3/13/2017.
 */

public class BluetoothListAdapter extends ArrayAdapter<BluetoothDevice>
{

    public BluetoothListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public BluetoothListAdapter(Context context, int resource, List<BluetoothDevice> items)
    {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.bluetooth_list_item, null);
        }

        BluetoothDevice device = getItem(position);

        if (device != null)
        {
            TextView _txtName = (TextView) v.findViewById(R.id._txtName);
            TextView _txtAddr = (TextView) v.findViewById(R.id._txtAddress);

            _txtName.setText(device.getName());
            _txtAddr.setText(device.getAddress());
        }

        return v;
    }
}
