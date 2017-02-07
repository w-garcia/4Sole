package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Owner on 2/6/2017.
 */

class ConnectThread extends Thread
{
    static String LEFT = "left";
    static String RIGHT = "right";

    private String sole_tag;

    private final BluetoothSocket mmSocket;
    private final BluetoothManager mManager;

    ConnectThread(BluetoothManager bluetoothManager, BluetoothDevice device, String tag)
    {
        BluetoothSocket temp = null;
        mManager = bluetoothManager;
        sole_tag = tag;

        try
        {
            UUID myUUID = UUID.fromString("1157511d-6235-4d83-9515-5c8e8a7ad124");
            temp = device.createRfcommSocketToServiceRecord(myUUID);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Socket's create() method failed", e);
        }

        mmSocket = temp;
    }

    public void run()
    {
        mManager.getmBluetoothAdapter().cancelDiscovery();

        try
        {
            mmSocket.connect();
        }
        catch (IOException connectException)
        {
            try
            {
                Log.e(TAG, "Could not connect the client socket");
                mmSocket.close();

            }
            catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            return;

        }

        // Havr connected socket here.
        if (sole_tag.equals(RIGHT))
        {
            mManager.setRightSole(mmSocket);
        }
        else
        {
            mManager.setLeftSole(mmSocket);
        }

    }

    public void cancel()
    {
        try {
            mmSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
