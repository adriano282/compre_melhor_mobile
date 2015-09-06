package br.com.compremelhor.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import br.com.compremelhor.R;
import br.com.compremelhor.useful.Constants;

/**
 * Created by adriano on 18/08/15.
 */
public class LoginActivity extends Activity implements Constants {
    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;
    private SharedPreferences preferences;
    private EditText user, password;
    private CheckBox keepConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        keepConnected = (CheckBox) findViewById(R.id.keep_connected);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button_facebook);

        LoginManager loginManager = LoginManager.getInstance();

        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                initDashboard();
            }

            @Override
            public void onCancel() {
                System.out.println("logout");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Login: ", "An error occur during login of user.");
            }
        });

        if (preferences.getBoolean(KEEP_CONNECT, false) && AccessToken.getCurrentAccessToken() != null) {
            initDashboard();
        }

        setContentView(R.layout.login);
    }

    public void initDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
