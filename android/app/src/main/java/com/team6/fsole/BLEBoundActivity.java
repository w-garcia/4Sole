package com.team6.fsole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Owner on 3/16/2017.
 */

public class BLEBoundActivity extends BLEBaseActivity
{
    @Override
    protected void onStart()
    {
        super.onStart();
        // Bind to bluetooth manager service
        bindService(intent, mBLEConnection, Context.BIND_AUTO_CREATE);
    }
}
