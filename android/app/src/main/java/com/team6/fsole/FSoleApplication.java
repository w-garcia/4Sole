package com.team6.fsole;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Owner on 2/4/2017.
 */
public class FSoleApplication extends Application
{
    public Integer numPairedSoles = 0;
    public Integer numConnectedSoles = 0;
    private Boolean leftSoleConnected = false;
    private Boolean rightSoleConnected = false;

    private BluetoothManager mBluetoothManager = new BluetoothManager();


    private static final String CLASS_NAME = FSoleApplication.class.getSimpleName();

    public FSoleApplication()
    {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(CLASS_NAME, "Activity created: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(CLASS_NAME, "Activity started: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(CLASS_NAME, "Activity resumed: " + activity.getLocalClassName());
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity,Bundle outState) {
                Log.d(CLASS_NAME, "Activity saved instance state: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(CLASS_NAME, "Activity paused: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(CLASS_NAME, "Activity stopped: " + activity.getLocalClassName());
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(CLASS_NAME, "Activity destroyed: " + activity.getLocalClassName());
            }
        });
    }

    public void incrementPairedSoles()
    {
        if (numPairedSoles < 2)
        {
            numPairedSoles += 1;
        }
        else
        {
            Log.d(CLASS_NAME, "Tried to increment number of paired devices past 2.");
        }
    }

    public void decrementPairedSoles()
    {
        if (numPairedSoles > 0)
        {
            numPairedSoles -= 1;
        }
        else
        {
            Log.d(CLASS_NAME, "Tried to decrement a non-positive number of paired devices.");
        }
    }

    public void incrementConnectedSoles()
    {
        if (numConnectedSoles < 2)
        {
            numConnectedSoles += 1;
        }
        else
        {
            Log.d(CLASS_NAME, "Tried to increment number of connected devices past 2.");
        }
    }

    public void decrementConnectedSoles()
    {
        if (numConnectedSoles > 0)
        {
            numConnectedSoles -= 1;
        }
        else
        {
            Log.d(CLASS_NAME, "Tried to decrement a non-positive number of connected devices.");
        }
    }

    public BluetoothManager getmBluetoothManager()
    {
        return mBluetoothManager;
    }

    public Boolean getLeftSoleConnected()
    {
        return leftSoleConnected;
    }

    public void setLeftSoleConnected(Boolean leftSoleConnected)
    {
        this.leftSoleConnected = leftSoleConnected;
    }

    public Boolean getRightSoleConnected()
    {
        return rightSoleConnected;
    }

    public void setRightSoleConnected(Boolean rightSoleConnected)
    {
        this.rightSoleConnected = rightSoleConnected;
    }
}
