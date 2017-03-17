package com.team6.fsole;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BLEBootActivity
{
    Integer REQUEST_ENABLE_BT = 0;
    FSoleApplication myFSoleApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button _btnMap = (Button) findViewById(R.id._btnMap);
        Button _btnSession = (Button) findViewById(R.id._btnSession);

        _btnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //Intent activityChangeIntent = new Intent(MainActivity.this, MapActivity.class);
                Intent activityChangeIntent = new Intent(MainActivity.this, MapActivity.class);

                startActivity(activityChangeIntent);
            }
        });

        _btnSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sessionChangeIntent = new Intent(MainActivity.this, SessionActivity.class);

                startActivity(sessionChangeIntent);
            }
        });
    }


    @Override
    protected void onServiceReady()
    {
        super.onServiceReady();

        if (mBluetoothManager.getBluetoothOK())
        {
            //TODO: Handle case where service is not ready and user has bluetooth off. (disable buttons)
            if (!mBluetoothManager.getmBluetoothAdapter().isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else
        {
            // bluetooth not supported on this device.
            myFSoleApplication = (FSoleApplication) getApplication();
            Toast.makeText(myFSoleApplication, "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_CANCELED)
        {
            // User declined to turn on bluetooth.
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Bluetooth is not supported on this device.")
                    .setTitle("Warning");

            AlertDialog dialog = builder.create();
            dialog.show();
            */
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
