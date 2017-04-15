package com.vpage.shareInfo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;

public class AlarmReceiverOnBoot extends BroadcastReceiver {

    private static final String TAG = AlarmReceiverOnBoot.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                // Set the alarm to start at 10:00 AM
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE, 4);
                calendar.set(Calendar.SECOND, 50);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pendingIntent);
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
