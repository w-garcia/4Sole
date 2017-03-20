package com.team6.fsole;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Locale;

public class SessionActivity extends BLEBoundActivity
{

    private TextView _chronometer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Button _startBtn = (Button) findViewById(R.id._btnStart);
        Button _endBtn = (Button) findViewById(R.id._btnEnd);
        _chronometer = (TextView) findViewById(R.id._txtChronometer);

        countDownTimer = new CountDownTimer(900000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                String minutes = String.valueOf((int) millisUntilFinished / (1000 * 60));
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String seconds_formatted = "";
                if (seconds < 10)
                {
                    seconds_formatted = "0";
                }
                seconds_formatted += seconds;

                _chronometer.setText(String.format(Locale.US, "%s:%s", minutes, seconds_formatted));
            }

            @Override
            public void onFinish()
            {
                stopSession();
            }
        };

        _startBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                countDownTimer.start();
                mBluetoothManager.startSession();
                //_mBlueToothManager.startSession();
            }
        });

        _endBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopSession();
            }
        });
    }

    private void stopSession()
    {
        countDownTimer.cancel();
        mBluetoothManager.endSession();
        //_mBlueToothManager.endSession();
    }
}
