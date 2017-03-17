package com.team6.fsole;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class DeviceScanActivity extends BLEBoundActivity
{
    private final String TAG = "DeviceScanActivity";
    private final String DEVICE = "device";

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private FSoleApplication myFSoleApplication;
    private ArrayAdapter<BluetoothDevice>  mLeDeviceListAdapter;
    private List<BluetoothDevice> _foundDevices;
    private static final long SCAN_PERIOD = 10000;

    private ListView lvLEDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mLeDeviceListAdapter.add(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mScanning) { scanLEDevice(false);}
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mScanning) { scanLEDevice(false);}
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mScanning) { scanLEDevice(false);}
    }

    private void scanLEDevice(final boolean enable)
    {
        if (enable)
        {
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLEScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mLeDeviceListAdapter.clear();
            mBluetoothAdapter.startLeScan(mLEScanCallback);
        }
        else
        {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLEScanCallback);
        }
    }

    @Override
    protected void onServiceReady()
    {
        super.onServiceReady();
        mBluetoothAdapter = mBluetoothManager.getmBluetoothAdapter();

        _foundDevices = mBluetoothManager.getFoundDevices();

        mHandler = new Handler();

        lvLEDevices = (ListView) findViewById(R.id._lvBLEDevices);
        lvLEDevices.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BluetoothDevice device = mLeDeviceListAdapter.getItem(position);
                scanLEDevice(false);

                // return object here
                Intent returnDeviceIntent = getIntent();
                returnDeviceIntent.putExtra(DEVICE, device);
                setResult(RESULT_OK, returnDeviceIntent);
                finish();
            }
        });
        mLeDeviceListAdapter = new BluetoothListAdapter(getApplicationContext(), R.layout.bluetooth_list_item, _foundDevices);

        lvLEDevices.setAdapter(mLeDeviceListAdapter);

        // start scanning immediately
        scanLEDevice(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.scan)
        {
            if (mScanning)
            {
                scanLEDevice(false);
            }
            else
            {
                scanLEDevice(true);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
