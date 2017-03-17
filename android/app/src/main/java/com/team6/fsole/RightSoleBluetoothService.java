package com.team6.fsole;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Julissa on 3/13/2017.
 */

public class RightSoleBluetoothService extends SoleBluetoothService {

    private final IBinder _rBinder = new RightSoleBluetoothServiceBinder();

    public RightSoleBluetoothService()
    {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return _rBinder;
    }

    public class RightSoleBluetoothServiceBinder extends Binder
    {
        RightSoleBluetoothService getService()
        {
            return RightSoleBluetoothService.this;
        }
    }
}
