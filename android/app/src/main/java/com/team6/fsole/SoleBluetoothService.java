package com.team6.fsole;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Owner on 2/6/2017.
 */

class SoleBluetoothService
{
    private static final String TAG = "SoleBluetoothService";
    private Handler mHandler; //gets info from Bluetooth service

    private ConnectedThread connectedThread;

    // Constants for trasmitting messages between service and UI
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    SoleBluetoothService(BluetoothSocket socket, Handler handler)
    {
        mHandler = handler;
        connectedThread = new ConnectedThread(socket);
        connectedThread.run();
    }

    public void write(byte[] bytes)
    {
        connectedThread.write(bytes);
    }

    public void cancel()
    {
        connectedThread.cancel();
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
            while (true)
            {
                try
                {
                    // Read
                    numBytes = mmInStream.read(mmBuffer);
                    // Send bytes to UI
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();
                }
                catch (IOException e)
                {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error occured when sending data", e);

                //Send failure message back to activity
                Message writeErrorMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
