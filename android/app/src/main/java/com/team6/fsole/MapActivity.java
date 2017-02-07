package com.team6.fsole;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
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
                            mBluetoothManager.initiateDeviceConnection(pDevice, LEFT);
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
                            mBluetoothManager.initiateDeviceConnection(pDevice, RIGHT);
                        }
                    });

                    alertDialog.show();
                }
            }
        });
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
