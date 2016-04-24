package br.com.compremelhor.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import br.com.compremelhor.R;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Dashboard";
    private OptionsDialogOnClickListener optionsListener;
    private AlertDialog alertDialogConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        optionsListener = new OptionsDialogOnClickListener();

        findViewById(R.id.edit_profile).setOnClickListener(this);
        findViewById(R.id.start_purchase).setOnClickListener(this);
        findViewById(R.id.list_purchases).setOnClickListener(this);
        findViewById(R.id.manager_addresses).setOnClickListener(this);
        setToolbar();

    }

    private AlertDialog createDialogConfirmation() {
        return new AlertDialog.Builder(this)
                .setMessage(R.string.logout_action_confirmation)
                .setPositiveButton(R.string.yes, optionsListener)
                .setNegativeButton(R.string.no, optionsListener)
                .create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            alertDialogConfirmation = createDialogConfirmation();
            alertDialogConfirmation.show();
        }
        return true;
    }


    protected void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.drawable.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
                intent = new Intent(this, PurchaseListActivity.class);
                startActivity(intent);
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

    private class OptionsDialogOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            Intent intent;

            switch (item) {
                case DialogInterface.BUTTON_POSITIVE:
                    LoginManager.getInstance().logOut();
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }
}