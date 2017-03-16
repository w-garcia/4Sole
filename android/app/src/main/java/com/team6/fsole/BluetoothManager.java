package com.team6.fsole;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.ArraySet;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    Set<BluetoothDevice> pairedSoles;
    private List<BluetoothDevice> foundDevices;
    //private BluetoothSocket LeftSoleSocket;
   // private BluetoothSocket RightSoleSocket;
    private LeftSoleBluetoothService LeftSoleService;
    private RightSoleBluetoothService RightSoleService;

    BluetoothManager()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothOK = mBluetoothAdapter != null;

        if (!BluetoothOK)
        {
            return;
        }

        pairedSoles = getPairedDevices();
        foundDevices = new ArrayList<>();
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

    SoleBluetoothService initiateDeviceConnection(BluetoothDevice device, String tag)
    {
        // Patch possible leaks
        if (tag.equals(RIGHT))
        {
            LeftSoleService = new LeftSoleBluetoothService();
            return LeftSoleService;
        }
        else
        {
            RightSoleService = new RightSoleBluetoothService();
            return RightSoleService;
        }
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
}
