package com.team6.fsole;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;

/**
 * Created by Owner on 2/6/2017.
 */

public class SoleBluetoothService
{
    private static final String TAG = "SoleBluetoothService";
    private Handler mHandler; //gets info from Bluetooth service

    // Constants for trasmitting messages between service and UI
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occured when creating input stream", e);
            }


            try
            {
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occured when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            mmBuffer = new byte[1024];
            int numBytes;

            // Keep listening to input stream until exception
        }
    }
}
