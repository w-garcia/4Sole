package com.team6.fsole;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Owner on 3/16/2017.
 */

public class BLEBaseActivity extends AppCompatActivity
{
    public static final String SerialPortUUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID = "00002a24-0000-1000-8000-00805f9b34fb";

    Intent intent;
    BluetoothManager mBluetoothManager;
    Boolean mBound = false;
    ServiceConnection mBLEConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // Bound to BluetoothManager, now get an instance
            BluetoothManager.BluetoothManagerBinder binder = (BluetoothManager.BluetoothManagerBinder) service;
            mBluetoothManager = binder.getService();
            Log.i(getApplicationContext().getPackageName(), "BluetoothManager service connected!");
            mBound = true;
            onServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.i(getApplicationContext().getPackageName(), "BluetoothManager service disconnected.");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, BluetoothManager.class);
    }

    @Override
    protected void onStop()
    {
        // Might not be called if killed by os?

        super.onStop();
        // Unbind service
        if (mBound)
        {
            //stopService(intent); // Might cause problem for BluetoothManager...
            unbindService(mBLEConnection);
            mBound = false;
        }
    }

    protected void onServiceReady()
    {

    }
}
