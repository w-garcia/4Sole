package com.team6.fsole;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

/**
 * Created by Owner on 2/6/2017.
 */

public class SoleBluetoothService extends Service
{
    //Binder given to clients
    private final IBinder _binder = new SoleBluetoothServiceBinder();

    private static final String TAG = "SoleBluetoothService";

    private int mBaudrate=115200;	//set the default baud rate to 115200
    private String mPassword="AT+PASSWOR=DFRobot\r\n";

    private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private String mySoleDirection;

    public static BluetoothGattCharacteristic mSCharacteristic;
    public static BluetoothGattCharacteristic mModelNumberCharacteristic;
    public static BluetoothGattCharacteristic mSerialPortCharacteristic;
    public static BluetoothGattCharacteristic mCommandCharacteristic;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private BluetoothDevice mBluetoothDevice;

    private int mConnectionState = STATE_DISCONNECTED;

    //To tell the onCharacteristicWrite call back function that this is a new characteristic,
    //not the Write Characteristic to the device successfully.
    private static final int WRITE_NEW_CHARACTERISTIC = -1;
    //define the limited length of the characteristic.
    private static final int MAX_CHARACTERISTIC_LENGTH = 17;
    //Show that Characteristic is writing or not.
    private boolean mIsWritingCharacteristic=false;

    private boolean isPinging = false;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> pingerHandle;
    private Runnable pingingRunnable;

    static final String DIRECTION = "direction";
    static final String LEFT = "left";
    static final String RIGHT = "right";
    static final String DEVICE = "device";

    public static final String SerialPortUUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID = "00002a24-0000-1000-8000-00805f9b34fb";

    private RingBuffer<GattCharacteristicHelper> mRingBuffer = new RingBuffer<>(8);
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String PING_RESULT =
            "com.example.bluetooth.le.PING_RESULT";
    public final static String SESSION_RESULT =
            "com.example.bluetooth.le.SESSION_RESULT";

    public final static String US_ASCII = "ISO-8859-1";

    public UUID UUID_SOLE;
    private Boolean readyToDestroy = false;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);

                if (readyToDestroy)
                {
                    close();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
            else
            {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "onCharacteristicRead " + characteristic.getUuid().toString());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            //super.onCharacteristicWrite(gatt, characteristic, status);

            //this block should be synchronized to prevent the function overloading
            synchronized (this)
            {
                //CharacteristicWrite success
                if (status == BluetoothGatt.GATT_SUCCESS)
                {
                    Log.i(TAG, "onCharacteristicWrite success: " + new String(characteristic.getValue()));
                    if (mRingBuffer.isEmpty())
                    {
                        mIsWritingCharacteristic = false;
                    }
                    else
                    {
                        GattCharacteristicHelper nextHelper = mRingBuffer.next();

                        // Too long
                        if (nextHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {
                            try
                            {
                                byte[] newVal = nextHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes(US_ASCII);
                                nextHelper.mCharacteristic.setValue(newVal);
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(nextHelper.mCharacteristic))
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":success");
                            }
                            else
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":failure");
                            }

                            nextHelper.mCharacteristicValue = nextHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        // Shorter
                        else
                        {
                            try
                            {
                                nextHelper.mCharacteristic.setValue(nextHelper.mCharacteristicValue.getBytes(US_ASCII));
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(nextHelper.mCharacteristic))
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":success");
                            }
                            else
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            nextHelper.mCharacteristicValue = "";

                            mRingBuffer.pop();
                        }
                    }
                }
                // Write a NEW characteristic
                else if (status == WRITE_NEW_CHARACTERISTIC)
                {
                    if (!mRingBuffer.isEmpty() && !mIsWritingCharacteristic)
                    {
                        GattCharacteristicHelper nextHelper = mRingBuffer.next();
                        if (nextHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {
                            try
                            {
                                byte[] newVal = nextHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes(US_ASCII);
                                nextHelper.mCharacteristic.setValue(newVal);
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(nextHelper.mCharacteristic))
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":success");
                            }
                            else
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            nextHelper.mCharacteristicValue = nextHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        else
                        {
                            try
                            {
                                byte[] newVal = nextHelper.mCharacteristicValue.getBytes(US_ASCII);
                                nextHelper.mCharacteristic.setValue(newVal);
                            }
                            catch (UnsupportedEncodingException e)
                            {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(nextHelper.mCharacteristic))
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":success");
                            }
                            else
                            {
                                Log.i(TAG, "writeCharacteristic init " + new String(nextHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            nextHelper.mCharacteristicValue = "";

                            mRingBuffer.pop();
                        }
                    }

                    mIsWritingCharacteristic = true;

                    //clear the buffer to prevent the lock of mIsWritingCharacteristic
                    if (mRingBuffer.isFull())
                    {
                        mRingBuffer.clear();
                        mIsWritingCharacteristic = false;
                    }
                }
                // Failure
                else
                {
                    mRingBuffer.clear();
                    Log.i(TAG, "onCharacteristicWrite fail: " + new String(characteristic.getValue()) + ", status: " + status);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        private void broadcastUpdate(final String action)
        {
            final Intent intent = new Intent(action);
            intent.putExtra(DIRECTION, mySoleDirection);

            sendBroadcast(intent);
        }

        private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
        {
            final Intent intent = new Intent(action);
            intent.putExtra(DIRECTION, mySoleDirection);

            // Writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0)
            {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }

            sendBroadcast(intent);
        }
    };

    public SoleBluetoothService()
    {

    }

    public void getGattServices()
    {
        List<BluetoothGattService> gattServices = getSupportedGattServices();
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic = null;
        mSerialPortCharacteristic = null;
        mCommandCharacteristic = null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for (BluetoothGattService gattService : gattServices)
        {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Build 2d array of characteristics for this service
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                switch (uuid)
                {
                    case ModelNumberStringUUID:
                        mModelNumberCharacteristic = gattCharacteristic;
                        Log.i(TAG, "mModelNumberCharacteristic  " + mModelNumberCharacteristic.getUuid().toString());
                        break;
                    case SerialPortUUID:
                        mSerialPortCharacteristic = gattCharacteristic;
                        Log.i(TAG, "mSerialPortCharacteristic  " + mSerialPortCharacteristic.getUuid().toString());
                        break;
                    case CommandUUID:
                        mCommandCharacteristic = gattCharacteristic;
                        Log.i(TAG, "mCommandCharacteristic  " + mCommandCharacteristic.getUuid().toString());
                        break;
                }
            }
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
            Log.e(TAG, "Please select DFRobot devices");
            mConnectionState = BluetoothGatt.STATE_CONNECTING;
        }
        else
        {
            mSCharacteristic = mModelNumberCharacteristic;
            setCharacteristicNotification(mSCharacteristic, true);
            readCharacteristic(mSCharacteristic);
        }

    }

    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.w(TAG, "Bluetooth service started!");
        Bundle extras = intent.getExtras();
        mBluetoothDevice = (BluetoothDevice) extras.get(DEVICE);
        mySoleDirection = (String) extras.get(DIRECTION);

        mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return _binder;
    }


    @Override
    public void onDestroy()
    {
        // Let device disconnect, which will trigger callback and call close()
        readyToDestroy = true;
        mBluetoothGatt.disconnect();
    }

    public void close()
    {
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            stopContinuousPinging();
        }

        Log.v(TAG, "Service destroyed!");
        super.onDestroy();
    }

    /*
     * Request a read on a given BluetoothGattCharacteristic. The read result is reported
     * asynchronously through the onCharacteristicRead callback.
     */
    public void readCharacteristic(BluetoothGattCharacteristic gattChar)
    {
        if (mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        mBluetoothGatt.readCharacteristic(gattChar);
    }

    /*
     * Write information to the device on a given {@code BluetoothGattCharacteristic}.
     * The content string and characteristic is only pushed into a ring buffer.
     * All the transmission is based on the {onCharacteristicWrite} call back function,
     * which is called directly in this function
    Taken from DFROBOT blunobasicdemo apk
    https://github.com/DFRobot/BlunoBasicDemo
     */
    public void writeCharacteristic(BluetoothGattCharacteristic gattChar)
    {
        if (mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        //The character size of TI CC2540 is limited to 17 bytes, otherwise characteristic can not be sent properly,
        //so String should be cut to comply this restriction. And something should be done here:
        String writeString = "";

        try
        {
            writeString = new String(gattChar.getValue(), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e)
        {
            // this should never happen because "US-ASCII" is hard-coded.
            throw new IllegalStateException(e);
        }

        Log.i(TAG, "writeCharacteristic: " + writeString);
        //As the communication is asynchronous content string and characteristic should be
        // pushed into an ring buffer for further transmission
        mRingBuffer.push(new GattCharacteristicHelper(gattChar, writeString));
        Log.i(TAG, "Ring buffer length: " + mRingBuffer.size());

        //The progress of onCharacteristicWrite and writeCharacteristic is almost the same.
        // So callback function is called directly here. for details see the onCharacteristicWrite function
        mGattCallback.onCharacteristicWrite(mBluetoothGatt, gattChar, WRITE_NEW_CHARACTERISTIC);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic gattChar, boolean ENABLED)
    {
        if (mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(gattChar, ENABLED);
    }

    private void logGattServices()
    {
        List<BluetoothGattService> gattServices;


        gattServices = mBluetoothGatt.getServices();


        if (gattServices == null) return;

        for (BluetoothGattService i : gattServices)
        {
            Log.i(TAG, "Available service:" + i.toString());
        }
    }

    public BluetoothGatt getBluetoothGatt()
    {
        return mBluetoothGatt;
    }

    /*
     * No idea what this is for.
     */
    public boolean matchModelCharacteristic()
    {
        if (mSCharacteristic == mModelNumberCharacteristic)
        {
            setCharacteristicNotification(mSCharacteristic, false);

            mSCharacteristic = mCommandCharacteristic;
            mSCharacteristic.setValue(mPassword);
            writeCharacteristic(mSCharacteristic);
            mSCharacteristic.setValue(mBaudrateBuffer);
            writeCharacteristic(mSCharacteristic);

            mSCharacteristic = mSerialPortCharacteristic;
            setCharacteristicNotification(mSCharacteristic, true);
            mConnectionState = BluetoothGatt.STATE_CONNECTED;
            //onConnectionStateChange(mConnectionState);

            if (!isPinging) startContinuousPinging();

            return true;
        }

        return false;
    }

    private void startContinuousPinging()
    {
        int delay = 500;
        pingingRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                serialSend("PING\n");

            }
        };

        pingerHandle = scheduler.scheduleAtFixedRate(pingingRunnable, 500, delay, TimeUnit.MILLISECONDS);
        isPinging = true;
    }

    public void stopContinuousPinging()
    {
        if (pingerHandle != null)
        {
            pingerHandle.cancel(true);
            isPinging = false;
        }
    }

    public void serialSend(String s)
    {
        mSCharacteristic.setValue(s);
        writeCharacteristic(mSCharacteristic);
    }

    public class SoleBluetoothServiceBinder extends Binder
    {
        SoleBluetoothService getService()
        {
            return SoleBluetoothService.this;
        }
    }

    public class GattCharacteristicHelper
    {
        BluetoothGattCharacteristic mCharacteristic;
        String mCharacteristicValue;

        GattCharacteristicHelper(BluetoothGattCharacteristic characteristic, String value)
        {
            mCharacteristic = characteristic;
            mCharacteristicValue = value;
        }
    }
}
