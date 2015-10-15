package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.User;
import br.com.compremelhor.useful.Constants;

/**
 * Created by adriano on 18/08/15.
 */
public class LoginActivity extends Activity implements Constants {
    private LoginButton lgbFacebook;
    private final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private SharedPreferences preferences;
    private EditText user, password;
    private CheckBox keepConnected;
    private SignInButton signGooglePlus;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);

        signGooglePlus = (SignInButton) findViewById(R.id.login_teste);
        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        keepConnected = (CheckBox) findViewById(R.id.keep_connected);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);


        keepConnected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean(Constants.KEEP_CONNECT, true);
                    edit.commit();
                } else {
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean(Constants.KEEP_CONNECT, false);
                    edit.commit();
                }
            }
        });


        lgbFacebook = (LoginButton) findViewById(R.id.login_button_facebook);

        lgbFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();

        lgbFacebook.registerCallback(callbackManager,mFacebookCallback);

        if (preferences.getBoolean(KEEP_CONNECT, false) && AccessToken.getCurrentAccessToken() != null) {
            initDashboard();
        }

    }

    private FacebookCallback<LoginResult> mFacebookCallback =  new FacebookCallback<LoginResult>() {
        DAOUser dao = new DAOUser(LoginActivity.this);

        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {
                            Log.v(TAG, response.toString());
                            try {
                                userId = dao.insertOrUpdate(
                                        new User(object.getString("name"), object.getString("email"))
                                );

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putLong("USER_ID", userId);
                                editor.commit();
                                Log.v(TAG, "User id: " + userId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email");
            request.setParameters(parameters);
            request.executeAsync();

            initDashboard();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "An error occur during login of user.");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, "An error occur during login of user.");
        }
    };


    private void initDashboard() {
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
