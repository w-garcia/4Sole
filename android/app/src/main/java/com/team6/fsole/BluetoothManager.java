package com.team6.fsole;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.Set;

/**
 * Created by Owner on 2/5/2017.
 */

class BluetoothManager
{
    static String LEFT = "left";
    static String RIGHT = "right";

    private BluetoothAdapter mBluetoothAdapter;
    private Boolean BluetoothOK;
    Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket LeftSole;
    private BluetoothSocket RightSole;

    BluetoothManager()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothOK = mBluetoothAdapter != null;

        if (!BluetoothOK)
        {
            return;
        }

        pairedDevices = mBluetoothAdapter.getBondedDevices();
    }

    Set<BluetoothDevice> getPairedDevices()
    {
        return mBluetoothAdapter.getBondedDevices();
    }

    BluetoothAdapter getmBluetoothAdapter()
    {
        return mBluetoothAdapter;
    }

    Boolean getBluetoothOK()
    {
        return BluetoothOK;
    }

    void initiateDeviceConnection(BluetoothDevice device, String tag)
    {
        ConnectThread t = new ConnectThread(this, device, tag);
        t.start();
    }

    void setLeftSole(BluetoothSocket leftSole)
    {
        LeftSole = leftSole;
    }

    void setRightSole(BluetoothSocket rightSole)
    {
        RightSole = rightSole;
    }

    void initiateSocketManagement(String tag)
    {
        if (tag.equals(RIGHT))
        {

        }
    }
}
