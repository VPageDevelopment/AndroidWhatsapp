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


public class DailyAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = DailyAlarmReceiver.class.getName();

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
                int sdk = Build.VERSION.SDK_INT;
                if (sdk > Build.VERSION_CODES.JELLY_BEAN) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    List<String> alarmMsg= Preferences.getLoacalNotificationMsgs("dailyMsg");
                    if(alarmMsg.size()>0) {
                        notification.setContentTitle("VPage");
                        notification.setContentText(alarmMsg.get(0));
                        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(alarmMsg.get(0)));
                        notification.setTicker(alarmMsg.get(0));
                        notification.setAutoCancel(true);
                        notification.setSmallIcon(R.drawable.app_icon);
                        Preferences.saveAppInstallVariable("GetDaliyNotfi",true);
                        Intent resultIntent = new Intent(context, ShareActivity.class);
                        String userdata = Preferences.get("userdata");
                        resultIntent.putExtra("ActiveUser", userdata);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pIntent);
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (isAppIsInBackground(context)) {
                            manager.notify(0, notification.build());
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
