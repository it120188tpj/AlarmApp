package com.example.pbank.shakealarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String nameForStartActivity = intent.getStringExtra("startClass");

        if(nameForStartActivity.equals("ActivityAlarm")) {
            Intent scheduledIntent = new Intent(context, ActivityAlarm.class);
            scheduledIntent.putExtra("name", intent.getStringExtra("name"));
            scheduledIntent.putExtra("time", intent.getStringExtra("time"));
            scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(scheduledIntent);
        }

        if(nameForStartActivity.equals("Activity")) {

        }

    }


}
