package com.example.pbank.shakealarmclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by pbank on 19.01.15.
 */
public class ActivityAlarm extends Activity {

    private TextView tv_name_alarm;
    private TextView tv_time_alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Intent intentIn = getIntent();

        tv_name_alarm = (TextView) findViewById(R.id.tv_name_alarm);
        tv_time_alarm = (TextView) findViewById(R.id.tv_time_alarm);

        tv_name_alarm.setText(intentIn.getStringExtra("name"));
        tv_time_alarm.setText(intentIn.getStringExtra("time"));

    }

}
