package br.com.compremelhor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import br.com.compremelhor.R;

public class DashboardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        findViewById(R.id.edit_profile).setOnClickListener(this);
        findViewById(R.id.start_purchase).setOnClickListener(this);
        findViewById(R.id.list_purchases).setOnClickListener(this);
        findViewById(R.id.manager_addresses).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.edit_profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.list_purchases:
                break;

            case R.id.start_purchase:
                intent = new Intent(this, PartnerListActivity.class);
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