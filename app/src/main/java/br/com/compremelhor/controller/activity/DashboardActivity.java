package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.USER_ID;
/**
 * Created by adriano on 21/08/15.
 */
public class DashboardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Dashboard";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        findViewById(R.id.edit_profile).setOnClickListener(this);
        findViewById(R.id.start_purchase).setOnClickListener(this);
        findViewById(R.id.list_purchases).setOnClickListener(this);
        findViewById(R.id.manager_addresses).setOnClickListener(this);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.edit_profile:
                intent = new Intent(this, ProfileActivity.class);

                User user = new DAOUser(this).getUserById(new Long(1));
                SharedPreferences.Editor edit = preferences.edit();
                edit.putLong(USER_ID, user != null? user.getId() : 0);
                edit.commit();

                startActivity(intent);
                break;

            case R.id.list_purchases:
                break;

            case R.id.start_purchase:
                intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                break;

            case R.id.manager_addresses:
                Log.d(TAG, "Manager Address have been clicked");
                intent = new Intent(this, AddressListActivity.class);
                startActivity(intent);
                break;
        }
    }
}