package br.com.compremelhor.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.AddressResource;
import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.util.Constants.ADDRESS_ID_EXTRA;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class AddressListActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private List<Map<String, Object>> addresses;

    private AlertDialog alertDialog;
    private AlertDialog alertDialogConfirmation;
    private ProgressDialog progressDialog;

    private Button btnAddAddress;

    private ListAdapter adapter;
    private ListView listView;

    private Handler handler;
    private AddressResource resource;
    private DAOAddress dao;
    private int addressSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        int userId = preferences.getInt(SP_USER_ID, 0);
        resource = new AddressResource(this, userId);

        handler = new Handler();
        dao = DAOAddress.getInstance(AddressListActivity.this);

        setViews();
        registerViews();
        setToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(AddressListActivity.this,
                        getString(R.string.wait_header_dialog), message, true, false);
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

    @Override
    public void onActivityResult(int requestCode, int requestResult, Intent data) {
        setViews();
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.address_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setViews() {
        addresses = listAddress();

        String[] from = {
                DatabaseHelper.Address.ADDRESS_NAME,
                DatabaseHelper.Address.ZIPCODE,
                DatabaseHelper.Address.STREET
        };

        int[] to = {R.id.tv_address_name, R.id.zipcode, R.id.street};

        adapter = new SimpleAdapter(AddressListActivity.this,
                listAddress(), R.layout.address_row, from, to);

        listView = (ListView) findViewById(R.id.address_list_view);
        listView.setAdapter(adapter);

        btnAddAddress = (Button) findViewById(R.id.btn_list_address_new);

        TextView tv = (TextView) findViewById(R.id.empty);
        if (addresses.size() > 0) {
            tv.setVisibility(TextView.GONE);
        } else {
            tv.setVisibility(TextView.VISIBLE);
        }

        if (addresses.size() >= 3) {
            btnAddAddress.setEnabled(false);
        } else {
            btnAddAddress.setEnabled(true);
        }
        setDialogs();
    }

    private void registerViews() {
        listView.setOnItemClickListener(new OnItemClickListener());
        btnAddAddress.setOnClickListener(new ViewOnClickListener());
    }

    private void setDialogs() {
        alertDialog = dialogOptions();
        alertDialogConfirmation = deleteActionConfirmation();
    }

    private AlertDialog deleteActionConfirmation() {
        return new AlertDialog.Builder(this)
                .setMessage(R.string.delete_confirmation_address)
                .setPositiveButton(getString(R.string.yes), new DialogOnClickListener())
                .setNegativeButton(getString(R.string.no), new DialogOnClickListener())
                .create();
    }

    private AlertDialog dialogOptions() {
        final CharSequence[] items;

        if (addresses.size() >= 3) {
            items = new CharSequence[]{
                    getString(R.string.edit),
                    getString(R.string.dialog_option_text_address)
            };
        } else {
            items = new CharSequence[]{
                    getString(R.string.edit),
                    getString(R.string.dialog_option_text_address),
                    getString(R.string.dialog_option_text_new_address)
            };
        }

        return new AlertDialog.Builder(this)
                .setTitle(R.string.options)
                .setItems(items, new DialogOnClickListener())
                .create();
    }

    private List<Map<String, Object>> listAddress() {
        addresses = new ArrayList<>();

        int userId = preferences.getInt(SP_USER_ID, 0);
        List<Address> listAddress = DAOAddress
                .getInstance(this)
                .findAllByForeignId(DatabaseHelper.Address._USER_ID, userId);

        Map<String, Object> item;

        for (Address d: listAddress) {
            item = new HashMap<>();

            item.put(DatabaseHelper.Address.CITY, d.getCity());
            item.put(DatabaseHelper.Address.NUMBER, d.getNumber());
            item.put(DatabaseHelper.Address.QUARTER, d.getQuarter());
            item.put(DatabaseHelper.Address.STATE, d.getState());
            item.put(DatabaseHelper.Address.STREET, d.getStreet());
            item.put(DatabaseHelper.Address.ZIPCODE, d.getZipcode());
            item.put(DatabaseHelper.Address.ADDRESS_NAME, d.getAddressName());
            item.put(DatabaseHelper.Address._ID, d.getId());

            addresses.add(item);
        }
        return addresses;
    }

    private void deleteAddress() {
        Map<String, Object> attributes = addresses.get(addressSelect);
        final int currentAddressId = (int) attributes.get(DatabaseHelper.Address._ID);

        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ResponseServer<Address> response = resource.deleteResource(dao.find(currentAddressId));

                if (!response.hasErrors()) {
                    dao.delete(currentAddressId);
                    progressDialog.dismiss();
                    addresses.remove(addressSelect);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.invalidateViews();
                            setViews();
                            Toast.makeText(AddressListActivity.this,
                                    getString(R.string.address_removed_successuful_message), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else {
                    Log.w("REST_API", response.getErrors().toString());
                    progressDialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddressListActivity.this, getString(R.string.address_remove_error_message), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressDialog("Excluindo endereço...");
            }
        };
        request.execute();
    }

    private class OnItemClickListener implements  AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AddressListActivity.this.addressSelect = position;
            alertDialog.show();
        }
    }

    // Listener for Button`s Actions
    private class ViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_list_address_new:
                    startActivityForResult(new Intent(AddressListActivity.this, AddressActivity.class), 0);
                    break;
            }
        }
    }

    // Listener for Dialog's actions
    private class DialogOnClickListener implements  DialogInterface.OnClickListener {
        @Override
        public void onClick(final DialogInterface dialog, int item) {
            Map<String, Object> attributes = addresses.get(addressSelect);
            int currentAddressId = (int) attributes.get(DatabaseHelper.Address._ID);

            switch (item) {
                // Edit Action address:
                case 0:
                    Intent intent = new Intent(AddressListActivity.this, AddressActivity.class);
                    intent.putExtra(ADDRESS_ID_EXTRA, currentAddressId);

                    startActivityForResult(intent, 0);
                    break;

                // Show confirmation dialog delete action:
                case 1:
                    alertDialogConfirmation.show();
                    break;

                // Create Action Address
                case 2:
                    startActivityForResult(new Intent(AddressListActivity.this, AddressActivity.class), 1);
                    break;

                // Confirmed delete action of selected address:
                case DialogInterface.BUTTON_POSITIVE:
                    deleteAddress();
                    break;

                // Cancelled delete action of selected address:
                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }
}
