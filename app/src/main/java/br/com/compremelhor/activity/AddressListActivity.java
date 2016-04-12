package br.com.compremelhor.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.AddressResource;
import br.com.compremelhor.dao.impl.DAOAddress;
import br.com.compremelhor.util.DatabaseHelper;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.util.Constants.EXTRA_ADDRESS_ID;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class AddressListActivity extends ActivityTemplate<Address> {
    private List<Map<String, Object>> addresses;

    private AlertDialog alertDialog;
    private AlertDialog alertDialogConfirmation;

    private Button btnAddAddress;

    private ListAdapter adapter;
    private ListView listView;

    private int addressSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(R.id.address_toolbar,
                getSharedPreferences(PREFERENCES, MODE_PRIVATE),
                new Handler(),
                DAOAddress.getInstance(AddressListActivity.this),
                new AddressResource(this, preferences.getInt(SP_USER_ID, 0)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        setWidgets();
        registerWidgets();
        setToolbar();
    }

    @Override
    public void onActivityResult(int requestCode, int requestResult, Intent data) {
        setWidgets();
    }

    @Override
    protected void setWidgets() {
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

    @Override
    protected void registerWidgets() {
        listView.setOnItemClickListener(new OnItemClickListener());
        btnAddAddress.setOnClickListener(new ViewOnClickListener());
    }

    @Override
    protected void fillFields() {}

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
                            setWidgets();
                            showMessage(R.string.address_removed_successful_message);
                        }
                    });

                }
                else {
                    Log.w("REST_API", response.getErrors().toString());
                    dismissProgressDialog();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showMessage(R.string.address_remove_error_message);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressDialog(R.string.dialog_header_wait, R.string.dialog_content_text_removing_address);
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
                    intent.putExtra(EXTRA_ADDRESS_ID, currentAddressId);

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
