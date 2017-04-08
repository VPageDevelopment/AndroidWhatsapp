package org.sufficientlysecure.rootcommands.testbackgroundwhatsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static android.Manifest.permission.READ_CONTACTS;
import org.sufficientlysecure.rootcommands.testbackgroundwhatsapp.command.SimpleCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    List<String> phoneContacts = new ArrayList<>();
    List<String> WhatsAppContacts = new ArrayList<>();

    Button sentMessage;
    EditText userMessage;

    String userMessageInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sentMessage = (Button)findViewById(R.id.sentMessage);
        userMessage = (EditText)findViewById(R.id.userMessage);

        sentMessage.setOnClickListener(this);


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
        } catch (Exception e) {
            Log.e(TAG, "Exception!", e);
        }
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
        ArrayList<String> myWhatsappContacts = new ArrayList<>();

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

                                myWhatsappContacts.add("91"+number);
                                WhatsAppContacts.add(name);

                             //   Log.d(TAG, " WhatsApp contact id  :  " + id);
                            //    Log.d(TAG, " WhatsApp contact name :  " + name);
                             //   Log.d(TAG, " WhatsApp contact number :  " + number);
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

            case R.id.sentMessage:
                userMessageInput = userMessage.getText().toString();
                RootCommands.DEBUG = true;
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");

                 if (isWhatsappInstalled) {

                        List<String> myWhatsappContacts = getWhatsAppContact();

                        String[] namesArr = myWhatsappContacts.toArray(new String[myWhatsappContacts.size()]);
                        if(!userMessageInput.isEmpty()){

                      //  whatsAppSendMessage(new String[]{"919600291325", "918124236660", "919659869830"}, userMessageInput);
                         whatsAppSendMessage(namesArr, userMessageInput);
                        }else
                        {
                            Toast.makeText(this, "Fill the message to share", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Uri uri = Uri.parse("market://details?id=com.whatsapp");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                        startActivity(goToMarket);
                    }

                break;
        }
    }
}
