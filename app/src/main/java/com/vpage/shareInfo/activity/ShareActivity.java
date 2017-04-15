package com.vpage.shareInfo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.vpage.shareInfo.R;
import com.vpage.shareInfo.rootTools.RootCommands;
import com.vpage.shareInfo.rootTools.Shell;
import com.vpage.shareInfo.rootTools.command.SimpleCommand;
import com.vpage.shareInfo.service.AlarmReceiver;
import com.vpage.shareInfo.service.AlarmReceiverOnBoot;
import com.vpage.shareInfo.service.DailyAlarmReceiver;
import com.vpage.shareInfo.tools.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ShareActivity.class.getName();
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    List<String> phoneContacts = new ArrayList<>();
    List<String> WhatsAppContacts = new ArrayList<>();

    private static final int FILE_SELECT_CODE = 0;

    Button shareButton,chooseFileButton,messageButton;
    LinearLayout messageLayout;
    EditText userMessage;

    String userMessageInput = "";
    String selectedFilePath ="";

    Intent launchIntent;

    String shareType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shareButton = (Button)findViewById(R.id.shareButton);
        chooseFileButton = (Button)findViewById(R.id.chooseFileButton);
        messageButton = (Button)findViewById(R.id.messageButton);
        userMessage = (EditText)findViewById(R.id.userMessage);

        messageLayout = (LinearLayout)findViewById(R.id.messageLayout);


        chooseFileButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        if(Preferences.getAppInstallVariable("GetDaliyNotfi")){
           // TO DO based on the message sent delete it daily
           // deleteFileShared();
        }


       /* showContacts();
        RootCommands.DEBUG = true;
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");

        if (isWhatsappInstalled) {

            List<String> myWhatsappContacts = getWhatsAppContact();

            for(int i =0 ;i<=phoneContacts.size();i++){

                if(WhatsAppContacts.contains(phoneContacts.get(i))){
                    String[] namesArr = myWhatsappContacts.toArray(new String[myWhatsappContacts.size()]);

                    whatsAppSendMessage(namesArr, "Hello, Message from Whatsapp Background");

                }else {
                    Toast.makeText(this, "The contact number "+phoneContacts.get(i)+" not in whatsapp", Toast.LENGTH_SHORT).show();
                }

            }

        } else {
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            startActivity(goToMarket);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void commandsTestOnClick(String command) {
        Log.d(TAG,"Command :- " + command);
        try {
            // start root shell
            Shell shell = Shell.startRootShell();

            // simple commands
            SimpleCommand command2 = new SimpleCommand(command);

            shell.add(command2).waitForFinish();

            // close root shell
            shell.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
    }


    protected void whatsAppSendMessage(String[] paramArrayOfString, String paramString) {

        try {
            Shell shell = Shell.startRootShell();
            int j = paramArrayOfString.length;
            for (int i = 0; i < j; i++) {
                String str3;
                long l1;
                long l2;
                int k;
                String str1;
                String str2;
                Random localRandom = new Random(20L);

                Log.d(TAG,"AUTO: "+
                        "ps | grep -w 'com.whatsapp' | awk '{print $2}' | xargs kill");
                commandsTestOnClick("ps | grep -w 'com.whatsapp' | awk '{print $2}' | xargs kill");

                str3 = paramArrayOfString[i] + "@s.whatsapp.net";
                l1 = System.currentTimeMillis();
                l2 = l1 / 1000L;
                k = localRandom.nextInt();

                str1 = "sqlite3 /data/data/com.whatsapp/databases/msgstore.db \"INSERT INTO messages (key_remote_jid, key_from_me, key_id, status, needs_push, data, timestamp, MEDIA_URL, media_mime_type, media_wa_type, MEDIA_SIZE, media_name , latitude, longitude, thumb_image, remote_resource, received_timestamp, send_timestamp, receipt_server_timestamp, receipt_device_timestamp, raw_data, media_hash, recipient_count, media_duration, origin)VALUES ('"
                        + str3
                        + "', 1,'"
                        + l2
                        + "-"
                        + k
                        + "', 0,0, '"
                        + paramString
                        + "',"
                        + l1
                        + ",'','', '0', 0,'', 0.0,0.0,'','',"
                        + l1
                        + ", -1, -1, -1,0 ,'',0,0,0); \"";

                str2 = "sqlite3 /data/data/com.whatsapp/databases/msgstore.db \"insert into chat_list (key_remote_jid) select '"
                        + str3
                        + "' where not exists (select 1 from chat_list where key_remote_jid='"
                        + str3 + "');\"";

                str3 = "sqlite3 /data/data/com.whatsapp/databases/msgstore.db \"update chat_list set message_table_id = (select max(messages._id) from messages) where chat_list.key_remote_jid='"
                        + str3 + "';\"";

                Log.d(TAG,"AUTO: "+ str1);
                Log.d(TAG,"AUTO: "+ str2);
                Log.d(TAG,"AUTO: "+ str3);

                shell.add(
                        new SimpleCommand(
                                "chmod 777 /data/data/com.whatsapp/databases/msgstore.db"))
                        .waitForFinish();
                shell.add(new SimpleCommand(str1)).waitForFinish();
                shell.add(new SimpleCommand(str2)).waitForFinish();
                shell.add(new SimpleCommand(str3)).waitForFinish();
            }
            shell.close();
            startWhatsApp();
            reStartApp();
            setNotificationMessage();

        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
    }


    void startWhatsApp(){

        launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        startActivity(launchIntent);
    }

    void reStartApp(){
       onBackPressed();
      this.onRestart();
    }

    void setNotificationMessage(){
        Map<String, String> membersMap = new HashMap<>();
        membersMap.put("Message ","Delete "+shareType);
        List<String> massages=new ArrayList<>(membersMap.values());
        Preferences.saveLocalNotificationMsg("dailyMsg",massages);
    }

    void stopWhatsApp(){
        if (isIntentAvailable(this, "com.whatsapp")){
            getPackageManager().setApplicationEnabledSetting("com.whatsapp", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            getPackageManager().setApplicationEnabledSetting("com.whatsapp",PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
        }
    }

    public boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }


    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

 /*   List<String> getWhatsAppContact(){
        Cursor whatsAppContactCursor = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[] {ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY },
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" },
                null);

        List<String> myWhatsappContacts = new ArrayList<>();
        int contactNameColumn = whatsAppContactCursor.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
        int contactNumberColumn = whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        Log.d(TAG,"contactNumberColumn: "+contactNumberColumn);

        while (whatsAppContactCursor.moveToNext())
        {
            // You can also read RawContacts.CONTACT_ID to read the
            // ContactsContract.Contacts table or any of the other related ones.
            String number = whatsAppContactCursor.getString(contactNumberColumn);
            Log.d(TAG,"myWhatsappContactsNumber: "+number);
            myWhatsappContacts.add("91"+number);
            WhatsAppContacts.add(whatsAppContactCursor.getString(contactNameColumn));
        }
        Log.d(TAG,"myWhatsappContacts: "+myWhatsappContacts.toString());
        return myWhatsappContacts;
    }*/


    List<String> getWhatsAppContact(){

        //This class provides applications access to the content model.
        ContentResolver cr = getApplicationContext().getContentResolver();

//RowContacts for filter Account Types
        Cursor contactCursor = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID,
                        ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[]{"com.whatsapp"},
                null);

//ArrayList for Store Whatsapp Contact
        List<String> myWhatsappContacts = new ArrayList<>();

        if (contactCursor != null) {
            if (contactCursor.getCount() > 0) {
                if (contactCursor.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                            Cursor whatsAppContactCursor = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{whatsappContactId}, null);

                            if (whatsAppContactCursor != null) {
                                whatsAppContactCursor.moveToFirst();
                                String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                whatsAppContactCursor.close();

                                //Add Number to ArrayList

                                myWhatsappContacts.add(number.replace("+",""));
                                WhatsAppContacts.add(name);

                             //   Log.d(TAG, " WhatsApp contact id  :  " + id);
                            //    Log.d(TAG, " WhatsApp contact name :  " + name);
                           //     Log.d(TAG, " WhatsApp contact number :  " + number.replace("+",""));
                            }
                        }
                    } while (contactCursor.moveToNext());
                    contactCursor.close();
                }
            }
        }
        Log.d(TAG,"myWhatsappContacts: "+myWhatsappContacts.toString());
        Log.d(TAG, "myWhatsappContactsSize :  " + myWhatsappContacts.size());
        return myWhatsappContacts;
    }


    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phoneContacts = getContactNames();
            Log.d(TAG,"phoneContacts: "+phoneContacts.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                phoneContacts = getContactNames();
                Log.d(TAG,"phoneContacts: "+phoneContacts.toString());
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contacts.add(name);
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

        return contacts;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.shareButton:

                userMessageInput = userMessage.getText().toString();

                RootCommands.DEBUG = true;
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");

                 if (isWhatsappInstalled) {

                        List<String> myWhatsappContacts = getWhatsAppContact();

                        String[] namesArr = myWhatsappContacts.toArray(new String[myWhatsappContacts.size()]);
                        if(!userMessageInput.isEmpty()){

                      //  whatsAppSendMessage(new String[]{"919600291325", "918124236660", "919659869830"}, userMessageInput);
                            shareType = "Text Message";
                         whatsAppSendMessage(namesArr, userMessageInput);
                        }else  if(!selectedFilePath.isEmpty()){

                            //  whatsAppSendMessage(new String[]{"919600291325", "918124236660", "919659869830"}, selectedFilePath);
                            shareType = "Media File";
                            whatsAppSendMessage(namesArr, selectedFilePath);
                        }else
                        {
                            Toast.makeText(this, "Set the Content to Share", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Uri uri = Uri.parse("market://details?id=com.whatsapp");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                        startActivity(goToMarket);
                    }

                break;

            case R.id.chooseFileButton:
                messageButton.setVisibility(View.GONE);
                messageLayout.setVisibility(View.GONE);
                showFileChooser();
                break;

            case R.id.messageButton:
                chooseFileButton.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    selectedFilePath = getPath(this, uri);
                    Log.d(TAG, "File Path: " + selectedFilePath);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        showNotification();
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static void deleteFileShared() {

        String rootPath = Environment.getExternalStorageDirectory().toString();
        String VIDEO_FOLDER = "/ShareInfoVideo";
        String path = rootPath + VIDEO_FOLDER;

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    private void showNotification() {
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            Preferences.saveTimeForNotification("currentShareInfoTime", timestamp);
            if (!Preferences.getAppInstallVariable("initalarm")) {
                showDailyNotification();
                Intent alarmIntent = new Intent(ShareActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ShareActivity.this, 0, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                // Set the alarm to start at 10:00 AM
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 1);
                calendar.set(Calendar.SECOND, 00);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20000*1000, pendingIntent);
                ComponentName receiver = new ComponentName(ShareActivity.this, AlarmReceiverOnBoot.class);
                PackageManager pm = ShareActivity.this.getPackageManager();
                pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                Preferences.saveAppInstallVariable("initalarm",true);
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }



    //show local message(daily at 8.00 am)

    private void showDailyNotification() {
        try {
            Intent alarmIntent = new Intent(ShareActivity.this, DailyAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ShareActivity.this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,8);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86000 * 1000, pendingIntent);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

}
