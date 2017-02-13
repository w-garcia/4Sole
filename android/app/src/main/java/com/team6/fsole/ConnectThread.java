package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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

    // Constants for trasmitting messages between ConnectThread and UI
    private interface MessageConstants {
        public static final int CONNECTION_GOOD = 0;
        public static final int CONNECTION_BAD = 1;
        public static final int CONNECTION_CLOSE = 2;
    }

    private MapActivity mapActivityRef;
    private String sole_tag;
    private Handler imageHandler;

    private final BluetoothSocket mmSocket;
    private final BluetoothManager mManager;

    ConnectThread(BluetoothManager bluetoothManager, BluetoothDevice device, String tag, Handler imgHandler)
    {
        BluetoothSocket temp = null;
        mManager = bluetoothManager;
        sole_tag = tag;
        imageHandler = imgHandler;

        try
        {
            UUID myUUID = device.getUuids()[0].getUuid();
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
                imageHandler.sendEmptyMessage(MessageConstants.CONNECTION_BAD);

            }
            catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            return;

        }

        // Havr connected socket here.
        if (sole_tag.equals(RIGHT))
        {
            mManager.setRightSoleSocket(mmSocket);
            imageHandler.sendEmptyMessage(MessageConstants.CONNECTION_GOOD);

        }
        else
        {
            mManager.setLeftSoleSocket(mmSocket);
            imageHandler.sendEmptyMessage(MessageConstants.CONNECTION_GOOD);
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
