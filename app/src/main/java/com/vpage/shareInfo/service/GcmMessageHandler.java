package com.vpage.shareInfo.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vpage.shareInfo.R;
import com.vpage.shareInfo.activity.LoginActivity;
import com.vpage.shareInfo.activity.ShareActivity;
import com.vpage.shareInfo.tools.ShareApplication;
import com.vpage.shareInfo.tools.Preferences;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class GcmMessageHandler extends IntentService {

    private static final String TAG = GcmMessageHandler.class.getName();

    public static final int notifyID = 9001;

    Intent resultIntent;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String gcmMessage, title, gcmType,imgUrl;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG,"messageType:"+messageType);
        gcmMessage = extras.getString("message");
        gcmType = extras.getString("type");
        imgUrl=extras.getString("imageUrl");
        title = extras.getString("title");
        Log.d(TAG, "GCM Received : ");
        Log.d(TAG,"GCM  : (" + gcmType + ")  " + gcmMessage);
        sendNotification(title, gcmMessage, gcmType,imgUrl);

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String title, String gcmMessage, String gcmType, String imgUrl) {
        try {
            String userdata = Preferences.get("userdata");
            String isLoggedIn = Preferences.get("isLoggedIn");

            if (isLoggedIn == null || isLoggedIn.isEmpty() || null == userdata || userdata.isEmpty()) {
                resultIntent = new Intent(this, LoginActivity.class);
            } else {
                if (gcmType != null) {

                    if(gcmType.equalsIgnoreCase(" ")){
                        if((imgUrl==null)||(imgUrl.isEmpty())){
                            resultIntent = new Intent(this, ShareActivity.class);
                        }else{
                            new sendNotification(this).execute(gcmMessage,imgUrl);
                            return;
                        }
                    }
                    else {
                        resultIntent = new Intent(this, ShareActivity.class);
                    }
                }
            }
            resultIntent.putExtra("ActiveUser", userdata);
            resultIntent.putExtra("gcmMessage", gcmMessage);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(gcmMessage)
                    .setSmallIcon(R.drawable.app_icon);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            mNotifyBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(gcmMessage));

            // Set Vibrate, Sound and Light
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification
            mNotifyBuilder.setContentText(gcmMessage);
            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            if (isAppIsInBackground()) {

                    mNotificationManager.notify(notifyID, mNotifyBuilder.build());

            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    private boolean isAppIsInBackground() {
        if(MyLifeCycleHandler.stoppedActivities==MyLifeCycleHandler.startedActivities){
            return true;
        }else{
            return false;
        }
    }

    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;

        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            message = params[0];
            try {

                URL url = new URL(params[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(ShareApplication.getContext());
                notification.setAutoCancel(true);
                notification.setTicker("nothing");
                notification.setSmallIcon(R.drawable.app_icon);
                Intent resultIntent = new Intent(ShareApplication.getContext(),ShareActivity.class);
                String userdata = Preferences.get("userdata");
                resultIntent.putExtra("ActiveUser", userdata);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(ShareApplication.getContext());
                stackBuilder.addNextIntentWithParentStack(resultIntent);
                PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pIntent);
                NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
                bigPicStyle.bigPicture(result);
                bigPicStyle.setBuilder(notification);
                bigPicStyle.setBigContentTitle("ShareInfo");
                bigPicStyle.setSummaryText(message);
                notification.setStyle(bigPicStyle);
                NotificationManager manager = (NotificationManager) ShareApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (isAppIsInBackground()) {
                    manager.notify(0, notification.build());
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

}
