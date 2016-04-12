package br.com.compremelhor.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.Resource;
import br.com.compremelhor.dao.IDAO;


public abstract class ActivityTemplate<T> extends AppCompatActivity {
    private boolean configured = false;

    protected int toolbarId;
    protected SharedPreferences preferences;
    protected Handler handler;
    protected IDAO<T> dao;
    protected Resource<T> resource;

    protected ProgressDialog progressDialog;

    protected abstract void setWidgets();
    protected abstract void registerWidgets();
    protected abstract void fillFields();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!configured) throw new RuntimeException("Activity resources weren't configured!");
        super.onCreate(savedInstanceState);
    }

    protected void setupOnCreateActivity(int toolbarId,
                                      SharedPreferences preferences,
                                      Handler handler,
                                      IDAO<T> dao,
                                      Resource<T> resource) {
        this.toolbarId = toolbarId;
        this.preferences = preferences;
        this.handler = handler;
        this.dao = dao;
        this.resource = resource;
        configured = true;
    }

    protected void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(toolbarId);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void showMessage(int messageId) {
        Toast toast = Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void showProgressDialog(int headerTextId, int contentTextId) {
        this.progressDialog = ProgressDialog
                .show(this, getString(headerTextId), getString(contentTextId), true, false);
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected AlertDialog createDialogErrorWithoutNetwork(int messageId) {
        return new AlertDialog.Builder(this)
                .setMessage(getString(messageId))
                .setTitle(R.string.header_dialog_error_message_without_internet)
                .create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
