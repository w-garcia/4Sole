package com.team6.fsole;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import android.os.Handler;

/**
 * Created by Owner on 2/6/2017.
 */

class SoleBluetoothService
{
    private static final String TAG = "SoleBluetoothService";
    private Handler dataHandler; //gets info from Bluetooth service

    private ConnectedThread connectedThread;

    // Constants for trasmitting messages between service and UI
    private interface MessageConstants {
        public static final int MESSAGE_READ = 100;
        public static final int MESSAGE_WRITE = 200;
        public static final int MESSAGE_TOAST = 300;
    }

    SoleBluetoothService(BluetoothSocket socket, Handler handler)
    {
        dataHandler = handler;
        connectedThread = new ConnectedThread(socket);
    }

    ConnectedThread getConnectedThread()
    {
        return connectedThread;
    }

    public class ConnectedThread extends Thread
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
                write("PING\r\n".getBytes());


                try
                {
                    // Read
                    numBytes = mmInStream.read(mmBuffer);
                    // Send bytes to UI
                    String s = new String(mmBuffer);

                    Message readMsg = dataHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1, s);
                    dataHandler.sendMessageDelayed(readMsg, 500);
                }
                catch (IOException e)
                {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }

                //Message readMsg = dataHandler.obtainMessage(
                 //       MessageConstants.MESSAGE_READ, 0, -1, "Successful management loop");
                //dataHandler.sendMessage(readMsg);
                SystemClock.sleep(2000);
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
                Message writeErrorMsg = dataHandler.obtainMessage(
                        MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the device");
                writeErrorMsg.setData(bundle);
                dataHandler.sendMessage(writeErrorMsg);
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
