package br.com.compremelhor.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.dao.DatabaseHelper;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.useful.Constants.ADDRESS_ID_EXTRA;
import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.USER_ID_SHARED_PREFERENCE;

public class AddressListActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private List<Map<String, Object>> addresses;

    private AlertDialog alertDialog;
    private AlertDialog alertDialogConfirmation;

    private Button btnAddAddress;

    private ListAdapter adapter;
    private ListView listView;

    private int addressSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        setViews();
        registerViews();
        setToolbar();
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
        if (!addresses.isEmpty()) {
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
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(getString(R.string.yes), new DialogOnClickListener())
                .setNegativeButton(getString(R.string.no), new DialogOnClickListener())
                .create();
    }

    private AlertDialog dialogOptions() {
        final CharSequence[] items;

        if (addresses.size() >= 3) {
            items = new CharSequence[]{
                    getString(R.string.edit),
                    getString(R.string.remove)
            };
        } else {
            items = new CharSequence[]{
                    getString(R.string.edit),
                    getString(R.string.remove),
                    getString(R.string.new_address)
            };
        }

        return new AlertDialog.Builder(this)
                .setTitle(R.string.options)
                .setItems(items, new DialogOnClickListener())
                .create();
    }

    private List<Map<String, Object>> listAddress() {
        addresses = new ArrayList<>();

        Long userId = preferences.getLong(USER_ID_SHARED_PREFERENCE, 0);
        List<Address> listAddress = new DAOAddress(this).getAddressesByUserId(userId);
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
        Long currentAddressId = (Long) attributes.get(DatabaseHelper.Address._ID);

        new DAOAddress(AddressListActivity.this).delete(currentAddressId, DatabaseHelper.Address.TABLE);

        addresses.remove(addressSelect);
        listView.invalidateViews();
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
        public void onClick(DialogInterface dialog, int item) {
            Map<String, Object> attributes = addresses.get(addressSelect);
            Long currentAddressId = (Long) attributes.get(DatabaseHelper.Address._ID);

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
                    setViews();
                    break;

                // Cancelled delete action of selected address:
                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }
}
