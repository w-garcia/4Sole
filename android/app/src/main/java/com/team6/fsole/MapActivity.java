package com.team6.fsole;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team6.fsole.BluetoothManager.BluetoothManagerBinder;

public class MapActivity extends BLEBoundActivity
{
    static final String TAG = "MapActivity";

    private ImageButton _imgBtnLeft;
    private ImageButton _imgBtnRight;
    private Boolean leftBtnActive = false;
    private Boolean rightBtnActive = false;

    static final String DIRECTION = "direction";
    static final String LEFT = "left";
    static final String RIGHT = "right";
    static final String DEVICE = "device";
    static final Integer DEVICE_PICK = 1;

    Pattern dataPattern = Pattern.compile("[^A\\d][0-9]+");

    private TextView _txt0R;
    private TextView _txt1R;
    private TextView _txt2R;
    private TextView _txt3R;

    private TextView _txt0L;
    private TextView _txt1L;
    private TextView _txt2L;
    private TextView _txt3L;

    private ImageButton _ib0R;
    private ImageButton _ib1R;
    private ImageButton _ib2R;
    private ImageButton _ib3R;

    private ImageButton _ib0L;
    private ImageButton _ib1L;
    private ImageButton _ib2L;
    private ImageButton _ib3L;

    private final String LEFT_IMG_HANDLER = "leftImgHandler";
    private final String RIGHT_IMG_HANDLER = "rightImgHandler";
    private final String LEFT_DATA_HANDLER = "leftDataHandler";
    private final String RIGHT_DATA_HANDLER = "rightDataHandler";
    private static final long SCAN_PERIOD = 10000;

    private ColorHelper colorHelper;

    // Constants for trasmitting messages between ConnectThread and UI
    private interface MessageConstants {
        public static final int CONNECTION_GOOD = 0;
        public static final int CONNECTION_BAD = 1;
        public static final int CONNECTION_CLOSE = 2;
        public static final int MESSAGE_READ = 100;
        public static final int MESSAGE_WRITE = 200;
        public static final int MESSAGE_TOAST = 300;
    }

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

            if (SoleBluetoothService.ACTION_GATT_CONNECTED.equals(action))
            {
                Log.i(TAG, "ACTION_GATT_CONNECTED");
                onConnectionMessageRecieved(direction);
            }
            else if (SoleBluetoothService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
                onDisconnectMessageRecieved(direction);
            }
            else if (SoleBluetoothService.PING_RESULT.equals(action))
            {
                // Data received from BLE device
                Log.i(TAG, "PING_RESULT");
                String receivedText = intent.getStringExtra(SoleBluetoothService.EXTRA_DATA);
                //Log.i(TAG, "Received: " + receivedText);
                //updatePressureValues(direction, receivedText); // Perform only when debugging
                updateMapColors(direction, receivedText);
            }
        }
    };

    // END mGATTUpdateReceiver <-----------------------------------------------------

    private void onDisconnectMessageRecieved(String tag)
    {
        if (tag.equals(LEFT))
        {
            _imgBtnLeft.setImageResource(R.mipmap.plus);

            _txt0L.setVisibility(View.INVISIBLE);
            _txt1L.setVisibility(View.INVISIBLE);
            _txt2L.setVisibility(View.INVISIBLE);
            _txt3L.setVisibility(View.INVISIBLE);
            leftBtnActive = false;

            _ib0L.setVisibility(View.INVISIBLE);
            _ib1L.setVisibility(View.INVISIBLE);
            _ib2L.setVisibility(View.INVISIBLE);
            _ib3L.setVisibility(View.INVISIBLE);
        }
        else
        {
            _imgBtnRight.setImageResource(R.mipmap.plus);

            _txt0R.setVisibility(View.INVISIBLE);
            _txt1R.setVisibility(View.INVISIBLE);
            _txt2R.setVisibility(View.INVISIBLE);
            _txt3R.setVisibility(View.INVISIBLE);
            rightBtnActive = false;

            _ib0R.setVisibility(View.INVISIBLE);
            _ib1R.setVisibility(View.INVISIBLE);
            _ib2R.setVisibility(View.INVISIBLE);
            _ib3R.setVisibility(View.INVISIBLE);
        }
    }

    public void onConnectionMessageRecieved(String tag)
    {
        if (tag.equals(LEFT))
        {
            _imgBtnLeft.setImageResource(R.drawable.foot_outline_l);
            //final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();
            //mBluetoothManager.initiateSocketManagement(LEFT, leftDataHandler);

            /* debug mode
            _txt0L.setVisibility(View.VISIBLE);
            _txt1L.setVisibility(View.VISIBLE);
            _txt2L.setVisibility(View.VISIBLE);
            _txt3L.setVisibility(View.VISIBLE);
            leftBtnActive = true;
            */

            _ib0L.setVisibility(View.VISIBLE);
            _ib1L.setVisibility(View.VISIBLE);
            _ib2L.setVisibility(View.VISIBLE);
            _ib3L.setVisibility(View.VISIBLE);
        }
        else
        {
            _imgBtnRight.setImageResource(R.drawable.foot_outline_r);
            //final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();
            //mBluetoothManager.initiateSocketManagement(RIGHT, rightDataHandler);

            /* debug mode
            _txt0R.setVisibility(View.VISIBLE);
            _txt1R.setVisibility(View.VISIBLE);
            _txt2R.setVisibility(View.VISIBLE);
            _txt3R.setVisibility(View.VISIBLE);
            rightBtnActive = true;
            */

            _ib0R.setVisibility(View.VISIBLE);
            _ib1R.setVisibility(View.VISIBLE);
            _ib2R.setVisibility(View.VISIBLE);
            _ib3R.setVisibility(View.VISIBLE);
        }
    }

    private void updatePressureValues(String direction, String receivedText)
    {
        String[] stringValues = receivedText.split(" ");
        ArrayList<Integer> sensorValues = new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            try
            {
                sensorValues.add(Integer.parseInt(stringValues[i]));
            }
            catch (NumberFormatException e)
            {
                Log.e(TAG, e.getMessage());
                return;
            }
        }

        if (direction.equals(RIGHT))
        {
            _txt0R.setText(String.format(Locale.US, "%d", sensorValues.get(0)));
            _txt1R.setText(String.format(Locale.US, "%d", sensorValues.get(1)));
            _txt2R.setText(String.format(Locale.US, "%d", sensorValues.get(2)));
            _txt3R.setText(String.format(Locale.US, "%d", sensorValues.get(3)));
        }
        else
        {
            _txt0L.setText(String.format(Locale.US, "%d", sensorValues.get(0)));
            _txt1L.setText(String.format(Locale.US, "%d", sensorValues.get(1)));
            _txt2L.setText(String.format(Locale.US, "%d", sensorValues.get(2)));
            _txt3L.setText(String.format(Locale.US, "%d", sensorValues.get(3)));
        }
    }

    private void updateMapColors(String direction, String receivedText)
    {
        String[] stringValues = receivedText.split(" ");
        ArrayList<Integer> sensorValues = new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            try
            {
                sensorValues.add(Integer.parseInt(stringValues[i]));
            }
            catch (NumberFormatException e)
            {
                Log.e(TAG, e.getMessage());
                return;
            }
        }

        // Get value of respective (value, position)
        int bottom_outter = colorHelper.getColorFromRawSensorValue(sensorValues.get(ColorHelper.B_OUTTER), ColorHelper.B_OUTTER); // 0
        int top_inner = colorHelper.getColorFromRawSensorValue(sensorValues.get(ColorHelper.T_INNER), ColorHelper.T_INNER); // 1
        int top_outter = colorHelper.getColorFromRawSensorValue(sensorValues.get(ColorHelper.T_OUTTER), ColorHelper.T_OUTTER); // 2
        int bottom_inner = colorHelper.getColorFromRawSensorValue(sensorValues.get(ColorHelper.B_INNER), ColorHelper.B_INNER); // 3

        if (direction.equals(RIGHT))
        {
            // Change file based on ColorHelper output
            _ib0R.setImageResource(bottom_outter);
            _ib1R.setImageResource(top_inner);
            _ib2R.setImageResource(top_outter);
            _ib3R.setImageResource(bottom_inner);
        }
        else
        {
            _ib0L.setImageResource(bottom_outter);
            _ib1L.setImageResource(top_inner);
            _ib2L.setImageResource(top_outter);
            _ib3L.setImageResource(bottom_inner);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (savedInstanceState == null)
        {
            Log.v("MapActivity", "savedInstanceState is NULL");
        }

        _imgBtnLeft = (ImageButton) findViewById(R.id._imgBtnLeft);
        _imgBtnRight = (ImageButton) findViewById(R.id._imgBtnRight);

        _txt0R = (TextView) findViewById(R.id._txt0R);
        _txt1R = (TextView) findViewById(R.id._txt1R);
        _txt2R = (TextView) findViewById(R.id._txt2R);
        _txt3R = (TextView) findViewById(R.id._txt3R);
        _txt0L = (TextView) findViewById(R.id._txt0L);
        _txt1L = (TextView) findViewById(R.id._txt1L);
        _txt2L = (TextView) findViewById(R.id._txt2L);
        _txt3L = (TextView) findViewById(R.id._txt3L);

        _ib0R = (ImageButton) findViewById(R.id._colorView0R);
        _ib1R = (ImageButton) findViewById(R.id._colorView1R);
        _ib2R = (ImageButton) findViewById(R.id._colorView2R);
        _ib3R = (ImageButton) findViewById(R.id._colorView3R);

        _ib0L = (ImageButton) findViewById(R.id._colorView0L);
        _ib1L = (ImageButton) findViewById(R.id._colorView1L);
        _ib2L = (ImageButton) findViewById(R.id._colorView2L);
        _ib3L = (ImageButton) findViewById(R.id._colorView3L);

        _imgBtnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!leftBtnActive)
                {
                    Intent pickDeviceIntent = new Intent(MapActivity.this, DeviceScanActivity.class);
                    pickDeviceIntent.putExtra(DIRECTION, LEFT);
                    startActivityForResult(pickDeviceIntent, DEVICE_PICK);
                }
                else
                {
                    //TODO: Open dialog instead
                    mBluetoothManager.closeDeviceConnection(LEFT);
                }

            }
        });

        _imgBtnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!rightBtnActive)
                {
                    Intent pickDeviceIntent = new Intent(MapActivity.this, DeviceScanActivity.class);
                    pickDeviceIntent.putExtra(DIRECTION, RIGHT);
                    startActivityForResult(pickDeviceIntent, DEVICE_PICK);
                }
                else
                {
                    //TODO: Open dialog instead
                    mBluetoothManager.closeDeviceConnection(RIGHT);
                }

            }
        });

        // ACTION_GATT_CONNECTED: connected to a GATT server.
        // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
        // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
        // PING_RESULT: received sole data from the device after PING event.
        IntentFilter filter = new IntentFilter();
        filter.addAction(SoleBluetoothService.ACTION_GATT_CONNECTED);
        filter.addAction(SoleBluetoothService.ACTION_GATT_DISCONNECTED);
        filter.addAction(SoleBluetoothService.PING_RESULT);

        registerReceiver(mGATTUpdateReceiver, filter);

        colorHelper = new ColorHelper();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == DEVICE_PICK)
        {
            if (resultCode != RESULT_OK)
            {
                return;
            }

            String dir = data.getStringExtra(DIRECTION);
            BluetoothDevice device = data.getParcelableExtra(DEVICE);
            if (!mBound)
            {
                Log.e("MapActivity", "BluetoothManager is not bound!");
                return;
            }

            mBluetoothManager.initiateDeviceConnection(device, dir);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Bundle viewHierarchy = savedInstanceState.getBundle("android:viewHierarchyState");
        if (viewHierarchy != null)
        {
            SparseArray views = viewHierarchy.getSparseParcelableArray("android:views");
            if (views != null)
            {
                for (int i = 0; i < views.size(); i++)
                {
                    Log.v("MapActivity", "key-->" + views.get(i));
                    Log.v("MapActivity", "value-->" + views.valueAt(i));
                }
            }
        }
        else
        {
            Log.v("MapActivity", "No view data");
        }

        super.onRestoreInstanceState(savedInstanceState);
        Log.v("MapActivity", "Inside of onRestoreInstanceState");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mBluetoothManager != null)
        {
            mBluetoothManager.stopSolePinging();
        }
        unregisterReceiver(mGATTUpdateReceiver);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //Intent mainIntent = new Intent(this, MainActivity.class);
        //#startActivity(mainIntent);
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

        return super.onOptionsItemSelected(item);
    }
}
