package br.com.compremelhor.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.com.compremelhor.R;

/**
 * Created by adriano on 21/08/15.
 */
public class DashboardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        findViewById(R.id.edit_profile).setOnClickListener(this);
        findViewById(R.id.start_purchase).setOnClickListener(this);
        findViewById(R.id.list_purchases).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.edit_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.list_purchases:

                break;

            case R.id.start_purchase:
                break;
        }

    }
}