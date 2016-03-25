package br.com.compremelhor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseAPI;
import br.com.compremelhor.api.integration.resource.UserResource;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.useful.Constants.KEEP_CONNECT_SP;
import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.SP_FACEBOOK_USER_ID;
import static br.com.compremelhor.useful.Constants.SP_LOGGED_ON_FACEBOOK;
import static br.com.compremelhor.useful.Constants.SP_USER_ID;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    private SharedPreferences preferences;
    private EditText edUser, edPassword;
    private DAOUser dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);

        /*try {
            PackageInfo info =     getPackageManager().getPackageInfo("br.com.compremelhor",     PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/

        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (isAlreadyLogged())
            initDashboard();

        dao = new DAOUser(LoginActivity.this);

        setViews();
        setLoginOnFacebook();
        registerViews();
    }

    private boolean isAlreadyLogged() {
        if (preferences.getBoolean(SP_LOGGED_ON_FACEBOOK, false)
                && preferences.getBoolean(KEEP_CONNECT_SP, false))
            return true;

        if (!preferences.getBoolean(KEEP_CONNECT_SP, false) &&
                preferences.getBoolean(SP_LOGGED_ON_FACEBOOK, false))
            LoginManager.getInstance().logOut();

        return false;
    }

    private void setViews() {
        edUser = (EditText) findViewById(R.id.user);
        edPassword = (EditText) findViewById(R.id.password);
    }

    private void setLoginOnFacebook() {
        LoginButton lgbFacebook = (LoginButton) findViewById(R.id.login_button_facebook);
        lgbFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        lgbFacebook.registerCallback(callbackManager, new FaceCallback());
    }

    private void registerViews() {
        ((CheckBox) findViewById(R.id.keep_connected))
                .setOnCheckedChangeListener(new CheckButtonListener());
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

    public void onLogin(View view) {
        if (identification())
            initDashboard();
        else
            Toast.makeText(this, R.string.err_wrong_credentials, Toast.LENGTH_SHORT).show();
    }

    public void onRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private boolean identification() {
        String username = edUser.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (username.isEmpty())
            return false;

        User user = dao.getUserByEmail(username);
        putUserId(user);
        return user != null && user.getPassword() != null && user.getPassword().equals(password);
    }

    private void putUserId(User user) {
        if (user != null)
            preferences.edit().putLong(SP_USER_ID, user.getId()).apply();
    }

    private void initDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
    }


    private class CheckButtonListener implements  CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                preferences.edit().putBoolean(KEEP_CONNECT_SP, true).apply();
                return;
            }
            preferences.edit().putBoolean(KEEP_CONNECT_SP, false).apply();
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
                            String userId = "";
                            try {
                                user.setName(object.getString("name"));
                                user.setEmail(object.getString("email"));
                                userId = object.getString("id");
                                user.setPassword(userId);
                                preferences.edit().putString(SP_FACEBOOK_USER_ID, userId).apply();
                                preferences.edit().putBoolean(SP_LOGGED_ON_FACEBOOK, true).apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new HttpRequestTask().execute(user);
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
            Toast.makeText(LoginActivity.this, R.string.err_login_facebook, Toast.LENGTH_LONG).show();
            preferences.edit().putBoolean(SP_LOGGED_ON_FACEBOOK, false).apply();
        }
    }

    private class HttpRequestTask extends AsyncTask<User, Void, User> {
        @Override
        protected User doInBackground(User... params) {
            User user = params[0];
            UserResource userResource = new UserResource();

            User userFromServer = userResource.findUserByUsername(user.getEmail());

            if (userFromServer == null) {
                ResponseAPI<User> response = userResource.createUserOnWebServer(user);

                if (response.hasErrors()) return null;

                userFromServer = response.getEntity();
            }

            if (dao.getUserByEmail(user.getEmail()) == null ) {

                user.setId(userFromServer.getId());
                dao.insert(user);

            } else if ((user = dao.getUserByEmail(user.getEmail())).getId() !=
                    userFromServer.getId()) {
                System.out.println("ID divergent: \nAPP: " + user.getId());
                System.out.println("API: " + userFromServer.getId());
                System.out.println("Data merged");
                user.setId(userFromServer.getId());
                dao.insertOrUpdate(user);
            }

            putUserId(user);
            return user;
        }
    }



 /*   private static byte[] getFacebookProfilePicture(String userId) {
        try {
            URL imageURL = new URL("https://graph.facebook.com/"+userId+"/picture?type=large");
            Bitmap image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
}