package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.useful.Constants.KEEP_CONNECT;
import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.USER_ID;

public class LoginActivity extends Activity {
    private final String TAG = "LoginActivity";

    private LoginButton lgbFacebook;
    private CallbackManager callbackManager;

    private SharedPreferences preferences;
    private EditText edUser, edPassword;
    private CheckBox cbKeepConnected;
    private DAOUser dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);

        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        dao = new DAOUser(LoginActivity.this);

        edUser = (EditText) findViewById(R.id.user);
        edPassword = (EditText) findViewById(R.id.password);
        cbKeepConnected = (CheckBox) findViewById(R.id.keep_connected);

        cbKeepConnected.setOnCheckedChangeListener(new CheckButtonListener());
        lgbFacebook = (LoginButton) findViewById(R.id.login_button_facebook);

        lgbFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();

        lgbFacebook.registerCallback(callbackManager, new FaceCallback());
        initDashboard();
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

    public void onLogin() {
        if (identification()) {
            initDashboard();
        }
        Toast.makeText(this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
    }

    public void onRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private boolean identification() {
        String username = edUser.getText().toString();
        String password = edPassword.getText().toString();

        if (username.isEmpty())
            return false;

        User user = dao.getUserByEmail(username);
        putUserId(user);
        return user != null && user.getPassword().equals(password);
    }

    private void putUserId(User user) {
        if (user != null)
            preferences.edit().putLong(USER_ID, user.getId()).commit();
    }

    private void initDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private class CheckButtonListener implements  CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                preferences.edit().putBoolean(KEEP_CONNECT, true).commit();
                return;
            }
            preferences.edit().putBoolean(KEEP_CONNECT, false).commit();
        }
    }

    private class FaceCallback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            User user = new User();

                            try {
                                user.setName(object.getString("name"));
                                user.setEmail(object.getString("email"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            user.setId(dao.insertOrUpdate(user));
                            putUserId(user);
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
        public void onCancel() {}

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "Occurred a error attempting to login on facebook");
        }
    }
}