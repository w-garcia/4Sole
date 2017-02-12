package com.team6.fsole;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.ImageButton;

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
    private BluetoothSocket LeftSoleSocket;
    private BluetoothSocket RightSoleSocket;
    private SoleBluetoothService LeftSoleService;
    private SoleBluetoothService RightSoleService;

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

    void initiateDeviceConnection(BluetoothDevice device, String tag, Handler imgHandler)
    {
        ConnectThread t = new ConnectThread(this, device, tag, imgHandler);
        t.start();
    }

    void setLeftSoleSocket(BluetoothSocket leftSoleSocket)
    {
        LeftSoleSocket = leftSoleSocket;
    }

    void setRightSoleSocket(BluetoothSocket rightSoleSocket)
    {
        RightSoleSocket = rightSoleSocket;
    }

    void initiateSocketManagement(String tag, Handler handler)
    {
        if (tag.equals(RIGHT) && RightSoleSocket.isConnected())
        {
            RightSoleService = new SoleBluetoothService(RightSoleSocket, handler);
        }
        else
        {
            LeftSoleService = new SoleBluetoothService(LeftSoleSocket, handler);
        }
    }
}
