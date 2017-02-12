package com.team6.fsole;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class MapActivity extends AppCompatActivity
{
    private FSoleApplication myFSoleApplication;
    private ImageButton _imgBtnLeft;
    private ImageButton _imgBtnRight;
    static String LEFT = "left";
    static String RIGHT = "right";

    // Constants for trasmitting messages between ConnectThread and UI
    private interface MessageConstants {
        public static final int CONNECTION_GOOD = 0;
        public static final int CONNECTION_BAD = 1;
        public static final int CONNECTION_CLOSE = 2;
    }

    private Boolean quit = false;

    private Handler leftImageHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            if (msg.what == MessageConstants.CONNECTION_GOOD)
            {
                _imgBtnLeft.setImageResource(R.drawable.foot_outline_l_small);
            }
        }
    };

    private Handler rightImageHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            if (msg.what == MessageConstants.CONNECTION_GOOD)
            {
                _imgBtnRight.setImageResource(R.drawable.foot_outline_r_small);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        myFSoleApplication = (FSoleApplication) getApplication();
        final BluetoothManager mBluetoothManager = myFSoleApplication.getmBluetoothManager();

        _imgBtnLeft = (ImageButton) findViewById(R.id._imgBtnLeft);
        _imgBtnRight = (ImageButton) findViewById(R.id._imgBtnRight);

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
    public void onBackPressed()
    {
        quit = true;
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
