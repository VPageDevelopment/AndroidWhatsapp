package com.vpage.shareInfo.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vpage.shareInfo.R;
import com.vpage.shareInfo.service.GCMClientManager;
import com.vpage.shareInfo.tools.Preferences;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    Button loginButton;
    EditText password,userName;

    String userNameInput = "",userPasswordInput = "";
    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER;


    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        loginButton = (Button)findViewById(R.id.loginButton);
        password = (EditText)findViewById(R.id.password);
        userName = (EditText)findViewById(R.id.userName);

        getGcmDeviceToken();
        Preferences.saveAppInstallVariable("isAppInstalled", true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userNameInput = userName.getText().toString();
                userPasswordInput = password.getText().toString();

                if(!userNameInput.isEmpty() && !userPasswordInput.isEmpty()){
                    // Keep the login
                    Preferences.save("userdata", "true");
                    Preferences.save("isLoggedIn", "true");
                    Preferences.saveAppInstallVariable("isAppInstalled", true);

                    Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                    startActivity(intent);

                }else {
                    Toast.makeText(getApplicationContext(), "Fill the all info", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void getGcmDeviceToken(){
        try {
            PROJECT_NUMBER = getString(R.string.G_PROJECT_NUMBER);
            pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {
                    Preferences.save("gcmToken", registrationId);
                }

                @Override
                public void onFailure(String ex) {
                    Log.d(TAG, "GCM Registration failed");
                    Log.d(TAG, ex);
                    super.onFailure(ex);
                }
            });
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

}
