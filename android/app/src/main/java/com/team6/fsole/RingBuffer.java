package com.team6.fsole;

import android.util.Log;

import java.io.PrintStream;

/**
 * Created by Owner on 3/19/2017.
 *
 * Taken from DFROBOT blunobasicdemo apk
 * https://github.com/DFRobot/BlunoBasicDemo
 */

public class RingBuffer<T>
{
    private T[] buffer;
    private int count = 0;
    private int indexIn = 0;
    private int indexOut = 0;

    public RingBuffer(int capacity)
    {
        buffer = (T[]) new Object[capacity];
    }

    public boolean isEmpty()
    {
        return count == 0;
    }

    public boolean isFull()
    {
        return count == buffer.length;
    }

    public int size()
    {
        return count;
    }

    public void clear()
    {
        count = 0;
    }

    /*
    Pushes an item to the buffer
     */
    public void push(T item)
    {
        if (count == buffer.length)
        {
            overflow();
        }

        buffer[indexIn] = item;
        indexIn = (indexIn + 1) % buffer.length; //wrap-around
        if (count++ == buffer.length)
        {
            count = buffer.length;
        }
    }

    /*
    Gets the last item in the buffer;
     */
    public T pop()
    {
        if (isEmpty())
        {
            underflow();
        }

        T item = buffer[indexOut];
        buffer[indexOut] = null;
        if (count-- == 0)
        {
            count = 0;
        }
        indexOut = (indexOut + 1) % buffer.length;
        return item;
    }

    public T next()
    {
        if (isEmpty())
        {
            underflow();
        }

        return buffer[indexOut];
    }

    private void underflow()
    {
        Log.e("RingBuffer", "Ring buffer pop underflow");
    }

    private void overflow()
    {
        Log.e("RingBuffer", "Ring buffer overflow");
    }
}
