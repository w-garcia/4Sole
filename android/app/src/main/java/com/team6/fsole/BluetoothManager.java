package com.team6.fsole;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.ArraySet;
import android.util.Log;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Owner on 2/5/2017.
 */

public class BluetoothManager extends Service
{
    // Binder given to clients
    private final IBinder _binder = new BluetoothManagerBinder();

    static final String TAG = "BluetoothManager";
    static final String DIRECTION = "direction";
    static final String LEFT = "left";
    static final String RIGHT = "right";
    static final String DEVICE = "device";

    private BluetoothAdapter mBluetoothAdapter;
    private Boolean BluetoothOK;
    Set<BluetoothDevice> pairedSoles;
    private List<BluetoothDevice> foundDevices;

    LeftSoleBluetoothService leftSoleService;
    RightSoleBluetoothService rightSoleService;
    Intent leftIntent;
    Intent rightIntent;
    Boolean leftBound = false;
    Boolean rightBound = false;
    ServiceConnection mLeftSoleConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            //Get an instance of left sole service by casting service.
            LeftSoleBluetoothService.LeftSoleBluetoothServiceBinder binder =
                    (LeftSoleBluetoothService.LeftSoleBluetoothServiceBinder) service;
            leftSoleService = binder.getService();
            Log.i(TAG, "Left sole service connected!");
            leftBound = true;
            onLeftServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.i(TAG, "Left sole service disconnected.");
            leftBound = false;
            leftSoleService = null;
        }
    };

    ServiceConnection mRightSoleConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            //Get an instance of left sole service by casting service.
            RightSoleBluetoothService.RightSoleBluetoothServiceBinder binder =
                    (RightSoleBluetoothService.RightSoleBluetoothServiceBinder) service;
            rightSoleService = binder.getService();
            Log.i(TAG, "Right sole service connected!");
            rightBound = true;
            onRightServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.i(TAG, "Right sole service disconnected.");
            rightBound = false;
            rightSoleService = null;
        }
    };

    Boolean receiverRegistered = false;
    IntentFilter filter;
    // BEGIN mGATTUpdateReceiver ----------------------------------------------------->
    // Create a BroadcastReceiver to listen for GATT actions.
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a result of read or notification operations.
    private final BroadcastReceiver mGATTUpdateReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            final Bundle extras = intent.getExtras();
            final String direction = (String) extras.get(DIRECTION);
            SoleBluetoothService service;

            if (direction.equals(RIGHT))
            {
                service = rightSoleService;
            }
            else
            {
                service = leftSoleService;
            }

            if (SoleBluetoothService.ACTION_GATT_CONNECTED.equals(action))
            {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
            }
            else if (SoleBluetoothService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
            }
            else if (SoleBluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                service.getGattServices();
            }
            else if (SoleBluetoothService.ACTION_DATA_AVAILABLE.equals(action))
            {
                // Data received from BLE device.
                // Perform handshaking or re-broadcast to relevant activities.
                Log.i(TAG, "ACTION_DATA_AVAILABLE");
                if (!service.matchModelCharacteristic())
                {
                    String pingResult = intent.getStringExtra(SoleBluetoothService.EXTRA_DATA);
                    broadcastUpdate(SoleBluetoothService.PING_RESULT, direction, pingResult);
                    //Log.i(TAG, intent.getStringExtra(SoleBluetoothService.EXTRA_DATA));
                }

            }
        }

        private void broadcastUpdate(final String action, final String direction, final String result)
        {
            final Intent intent = new Intent(action);
            intent.putExtra(DIRECTION, direction);
            intent.putExtra(SoleBluetoothService.EXTRA_DATA, result);

            sendBroadcast(intent);
        }

    };

    public BluetoothManager()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothOK = mBluetoothAdapter != null;

        if (!BluetoothOK)
        {
            return;
        }

        pairedSoles = getPairedDevices();
        foundDevices = new ArrayList<>();

        // ACTION_GATT_CONNECTED: connected to a GATT server.
        // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
        // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
        // ACTION_DATA_AVAILABLE: received data from the device.
        // This can be a result of read or notification operations.
        filter = new IntentFilter();
        filter.addAction(SoleBluetoothService.ACTION_GATT_CONNECTED);
        filter.addAction(SoleBluetoothService.ACTION_GATT_DISCONNECTED);
        filter.addAction(SoleBluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(SoleBluetoothService.ACTION_DATA_AVAILABLE);

    }

    private void onRightServiceReady()
    {
        if (!receiverRegistered)
        {
            Log.i(TAG, "Receiver registered with mGattUpdateReceiver.");
            registerReceiver(mGATTUpdateReceiver, filter);
            receiverRegistered = true;
        }
        //Initiate data management.
        //rightSoleService.serialSend("PING\n");

    }

    private void onLeftServiceReady()
    {
        if (!receiverRegistered)
        {
            Log.i(TAG, "Receiver registered with mGattUpdateReceiver.");
            registerReceiver(mGATTUpdateReceiver, filter);
            receiverRegistered = true;
        }
        //Initiate data management.
        //leftSoleService.serialSend("PING\n");
    }

    Set<BluetoothDevice> getPairedSoles()
    {
        return pairedSoles;
    }

    void addPairedSole(BluetoothDevice device)
    {
        pairedSoles.add(device);
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

    void initiateDeviceConnection(BluetoothDevice device, String dir)
    {
        Log.v(TAG, "Connecting to: " + device.getName());
        switch(dir)
        {
            case LEFT:
                // start connection here
                leftIntent = new Intent(this, LeftSoleBluetoothService.class);
                leftIntent.putExtra(DEVICE, device);
                leftIntent.putExtra(DIRECTION, LEFT);
                startService(leftIntent);
                bindService(leftIntent, mLeftSoleConnection, Context.BIND_AUTO_CREATE);
                break;
            case RIGHT:
                // start connection here
                rightIntent = new Intent(this, RightSoleBluetoothService.class);
                rightIntent.putExtra(DEVICE, device);
                rightIntent.putExtra(DIRECTION, RIGHT);
                startService(rightIntent);
                bindService(rightIntent, mRightSoleConnection, Context.BIND_AUTO_CREATE);
                break;
        }
    }

    void closeDeviceConnection(String dir)
    {
        switch (dir)
        {
            case LEFT:
                stopService(leftIntent);
                unbindService(mLeftSoleConnection);
                leftBound = false;
                break;
            case RIGHT:
                stopService(rightIntent);
                unbindService(mRightSoleConnection);
                rightBound = false;
                break;
        }
        Log.v(TAG, "Closed connection to " + dir);
    }
/*
    void setLeftSoleSocket(BluetoothSocket leftSoleSocket)
    {
        LeftSoleSocket = leftSoleSocket;
    }

    void setRightSoleSocket(BluetoothSocket rightSoleSocket)
    {
        RightSoleSocket = rightSoleSocket;
    }
*/
    List<BluetoothDevice> getFoundDevices()
    {
        return foundDevices;
    }

    void setFoundDevices(List<BluetoothDevice> newList)
    {
        foundDevices = newList;
    }

    /*
    void initiateSocketManagement(String tag, Handler dataHandler)
    {
        if (tag.equals(RIGHT) && RightSoleSocket.isConnected())
        {
            RightSoleService = new SoleBluetoothService(RightSoleSocket, dataHandler);
            RightSoleService.getConnectedThread().start();
        }
        else
        {
            LeftSoleService = new SoleBluetoothService(LeftSoleSocket, dataHandler);
            LeftSoleService.getConnectedThread().start();
        }
    }

    void startSession()
    {
        if (RightSoleService != null)
        {
            RightSoleService.getConnectedThread().write("START\r\n".getBytes());
        }

        if (LeftSoleService != null)
        {
            LeftSoleService.getConnectedThread().write("START\r\n".getBytes());
        }
    }

    void endSession()
    {
        if (RightSoleService != null)
        {
            RightSoleService.getConnectedThread().write("END\r\n".getBytes());
        }
        if (LeftSoleService != null)
        {
            LeftSoleService.getConnectedThread().write("END\r\n".getBytes());
        }
    }
    */

    public class BluetoothManagerBinder extends Binder
    {
        BluetoothManager getService()
        {
            return BluetoothManager.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return _binder;
    }
}
