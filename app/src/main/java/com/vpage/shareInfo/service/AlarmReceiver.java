package com.vpage.shareInfo.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.vpage.shareInfo.R;
import com.vpage.shareInfo.activity.ShareActivity;
import com.vpage.shareInfo.tools.Preferences;
import java.util.List;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            long currenttimestamp = System.currentTimeMillis() / 1000;
            Long lastVisitedTime= Preferences.getTimeForNotification("currentShareInfoTime");
            if(currenttimestamp>(lastVisitedTime+(172000))) {
                Random ran = new Random();
                int sdk = Build.VERSION.SDK_INT;
                List<String> alarmMsg=Preferences.getLoacalNotificationMsgs("comebackMsg");
                if(alarmMsg.size()>0) {
                    int x = ran.nextInt(alarmMsg.size());
                    String content = alarmMsg.get(0);
                    if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                        notification.setContentTitle("ShareInfo");
                        notification.setContentText(content);
                        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                        notification.setAutoCancel(true);
                        notification.setTicker(content);
                        notification.setSmallIcon(R.drawable.app_icon);
                        Intent resultIntent = new Intent(context, ShareActivity.class);
                        String userdata = Preferences.get("userdata");
                        resultIntent.putExtra("ActiveUser", userdata);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        //stackBuilder.addNextIntent(resultIntent);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pIntent);
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (isAppIsInBackground(context))
                        {
                            manager.notify(0, notification.build());
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean isAppIsInBackground(Context context) {
        if(MyLifeCycleHandler.stoppedActivities==MyLifeCycleHandler.startedActivities){
            return true;
        }else{
            return false;
        }
    }
}
