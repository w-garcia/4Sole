package com.team6.fsole;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
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
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapActivity extends AppCompatActivity
{
    private FSoleApplication myFSoleApplication;
    private ImageButton _imgBtnLeft;
    private ImageButton _imgBtnRight;
    static String LEFT = "left";
    static String RIGHT = "right";

    Pattern dataPattern = Pattern.compile("[^A\\d][0-9]+");

    private TextView _txt0R;
    private TextView _txt1R;
    private TextView _txt2R;
    private TextView _txt3R;

    private TextView _txt0L;
    private TextView _txt1L;
    private TextView _txt2L;
    private TextView _txt3L;

    private final String LEFT_IMG_HANDLER = "leftImgHandler";
    private final String RIGHT_IMG_HANDLER = "rightImgHandler";
    private final String LEFT_DATA_HANDLER = "leftDataHandler";
    private final String RIGHT_DATA_HANDLER = "rightDataHandler";

    private final LeftImageHandler leftImageHandler = new LeftImageHandler(this);
    private final RightImageHandler rightImageHandler = new RightImageHandler(this);

    private final LeftDataHandler leftDataHandler = new LeftDataHandler(this);
    private final RightDataHandler rightDataHandler = new RightDataHandler(this);

    // Constants for trasmitting messages between ConnectThread and UI
    private interface MessageConstants {
        public static final int CONNECTION_GOOD = 0;
        public static final int CONNECTION_BAD = 1;
        public static final int CONNECTION_CLOSE = 2;
        public static final int MESSAGE_READ = 100;
        public static final int MESSAGE_WRITE = 200;
        public static final int MESSAGE_TOAST = 300;
    }

    private Boolean quit = false;


    private static class LeftImageHandler extends Handler
    {
        private final WeakReference<MapActivity> mapActivityWeakReference;

        public LeftImageHandler(MapActivity activity)
        {
            mapActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MapActivity mapActivity = mapActivityWeakReference.get();

            if (mapActivity == null)
            {
                return;
            }

            mapActivity.onConnectionMessageRecieved(msg, "left");
        }
    }

    private static class RightImageHandler extends Handler
    {
        private final WeakReference<MapActivity> mapActivityWeakReference;

        public RightImageHandler(MapActivity activity)
        {
            mapActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MapActivity mapActivity = mapActivityWeakReference.get();

            if (mapActivity == null)
            {
                return;
            }

            mapActivity.onConnectionMessageRecieved(msg, "right");
        }
    }

    public void onConnectionMessageRecieved(Message msg, String tag)
    {
        if (msg.what == MessageConstants.CONNECTION_GOOD)
        {
            if (tag.equals(LEFT))
            {
                _imgBtnLeft.setImageResource(R.drawable.foot_outline_l);
                final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();
                mBluetoothManager.initiateSocketManagement(LEFT, leftDataHandler);

                _txt0L.setVisibility(View.VISIBLE);
                _txt1L.setVisibility(View.VISIBLE);
                _txt2L.setVisibility(View.VISIBLE);
                _txt3L.setVisibility(View.VISIBLE);
            }
            else
            {
                _imgBtnRight.setImageResource(R.drawable.foot_outline_r);
                final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();
                mBluetoothManager.initiateSocketManagement(RIGHT, rightDataHandler);

                _txt0R.setVisibility(View.VISIBLE);
                _txt1R.setVisibility(View.VISIBLE);
                _txt2R.setVisibility(View.VISIBLE);
                _txt3R.setVisibility(View.VISIBLE);
            }
        }
        else if (msg.what == MessageConstants.CONNECTION_BAD)
        {
            Toast.makeText(MapActivity.this, "Failed to create connection.", Toast.LENGTH_SHORT).show();
        }
    }


    private static class LeftDataHandler extends Handler
    {
        private final WeakReference<MapActivity> mapActivityWeakReference;

        public LeftDataHandler(MapActivity activity)
        {
            mapActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MapActivity activity = mapActivityWeakReference.get();
            activity.onDataMessageRecieved(msg, "left");
        }
    }

    private static class RightDataHandler extends Handler
    {
        private final WeakReference<MapActivity> mapActivityWeakReference;

        public RightDataHandler(MapActivity activity)
        {
            mapActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MapActivity activity = mapActivityWeakReference.get();
            activity.onDataMessageRecieved(msg, "right");
        }
    }

    public void onDataMessageRecieved(Message msg, String tag)
    {
        if (msg.what == MessageConstants.MESSAGE_READ)
        {
            String payload = msg.obj.toString();
            Matcher matcher = dataPattern.matcher(payload);

            String matches = "";
            ArrayList<Integer> sensorValues = new ArrayList<>();
            while(matcher.find())
            {
                matches += matcher.group().replace(" ", "") + " ";
                sensorValues.add(Integer.parseInt(matcher.group().replace(" ", "")));
            }

            Log.v("MapActivity", "dataPattern matches: " + matches);

            if (sensorValues.size() == 4)
            {
                if (tag.equals(RIGHT))
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

            // Parse message for sensor values
            //Toast.makeText(this, payload, Toast.LENGTH_SHORT).show();
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

        myFSoleApplication = (FSoleApplication) getApplication();
        final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();

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

        _imgBtnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!myFSoleApplication.getLeftSoleConnected())
                {
                    ArrayList<BluetoothDevice> pDevices = new ArrayList<BluetoothDevice>();
                    pDevices.addAll(mBluetoothManager.getPairedDevices());

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                    alertDialog.setTitle("Paired Devices");

                    final ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, pDevices);

                    alertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            BluetoothDevice pDevice = arrayAdapter.getItem(which);
                            //Connect here
                            mBluetoothManager.initiateDeviceConnection(pDevice, LEFT, leftImageHandler);
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        _imgBtnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!myFSoleApplication.getRightSoleConnected())
                {
                    ArrayList<BluetoothDevice> pDevices = new ArrayList<BluetoothDevice>();
                    pDevices.addAll(mBluetoothManager.getPairedDevices());

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
                    alertDialog.setTitle("Paired Devices");

                    final ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, pDevices);

                    alertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            BluetoothDevice pDevice = arrayAdapter.getItem(which);
                            //Connect here
                            mBluetoothManager.initiateDeviceConnection(pDevice, RIGHT, rightImageHandler);
                        }
                    });

                    alertDialog.show();
                }
            }
        });
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
    protected void onStop()
    {
        super.onStop();

    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        quit = true;
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

    public ImageButton getImgBtnLeft()
    {
        return _imgBtnLeft;
    }

    public ImageButton getImgBtnRight()
    {
        return _imgBtnRight;
    }
}
