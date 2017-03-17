package com.team6.fsole;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Owner on 3/16/2017.
 * Designed for MainActivity, operates as boot sequence for BluetoothManager.
 */

public class BLEBootActivity extends BLEBaseActivity
{
    @Override
    protected void onStart()
    {
        super.onStart();
        // Start bluetooth manager during boot sequence to override bindService life cycle.
        startService(intent);
        Log.i(getApplicationContext().getPackageName(), "BluetoothManager service initiated!");

        // Now bind to service.
        bindService(new Intent(BLEBootActivity.this, BluetoothManager.class), mBLEConnection, Context.BIND_AUTO_CREATE);
    }
}
