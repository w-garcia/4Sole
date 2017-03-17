package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Julissa on 3/13/2017.
 */

public class LeftSoleBluetoothService extends SoleBluetoothService{

    private final IBinder _lBinder = new LeftSoleBluetoothServiceBinder();

    public LeftSoleBluetoothService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return _lBinder;
    }

    public class LeftSoleBluetoothServiceBinder extends Binder
    {
        LeftSoleBluetoothService getService()
        {
            return LeftSoleBluetoothService.this;
        }
    }
}
