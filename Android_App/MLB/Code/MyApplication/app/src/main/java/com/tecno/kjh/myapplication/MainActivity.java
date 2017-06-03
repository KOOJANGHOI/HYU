package com.tecno.kjh.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView m10MsCount = null;
    TextView m60000MsCount = null;

    Timer mTimer = new Timer();

    TimerTask m10MsCountTimerTask = null;
    TimerTask m60000MsCountTimerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m10MsCount = (TextView)findViewById(R.id.m_10Ms_countdown);
        m60000MsCount = (TextView)findViewById(R.id.m_60000Ms_countdown);
    }

    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.start_countdown: {
                startTimerTask();
                break;
            }
            case R.id.stop_countdown: {
                stopTimerTask();
                break;
            }
        }
    }

    private void startTimerTask() {
        stopTimerTask();

        m10MsCountTimerTask = new TimerTask() {
            int mCount = 0;
            @Override
            public void run() {
                mCount++;
                m10MsCount.post(new Runnable() {
                    @Override
                    public void run() {
                        m10MsCount.setText(" " + mCount);
                    }
                });
            }
        };

        m60000MsCountTimerTask = new TimerTask() {
            int mCount = -1;
            @Override
            public void run() {
                mCount++;
                m60000MsCount.post(new Runnable() {
                    @Override
                    public void run() {
                        m60000MsCount.setText(" " + mCount);
                    }
                });
            }
        };

        mTimer.schedule( m10MsCountTimerTask , 0 , 10);
        mTimer.schedule( m60000MsCountTimerTask , 0 , 60000);
      }

    private void stopTimerTask() {
        if(m10MsCountTimerTask != null || m60000MsCountTimerTask != null) {
            m10MsCountTimerTask.cancel();
            m10MsCountTimerTask = null;
            m60000MsCountTimerTask.cancel();
            m60000MsCountTimerTask = null;
        }

        m10MsCount.setText(" 00 : 00 ");
        m60000MsCount.setText(" 00 ");
    }
}
