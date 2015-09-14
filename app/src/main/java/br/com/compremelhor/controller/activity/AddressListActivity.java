package br.com.compremelhor.controller.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.AddressDAO;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.useful.Constants;

/**
 * Created by adriano on 09/09/15.
 */
public class AddressListActivity extends ListActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener,
        Constants, View.OnClickListener {

    private List<Map<String, Object>> addresses;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogConfirmation;
    private int addressSelect;

    private Button btnAddAddress;
    private Button btnBack;
    private final String TAG = "AddressActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_address);

        getListView().setOnItemClickListener(this);
        addresses = listAddress();
        alertDialog = createAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();

        String[] from = {ZIPCODE, STREET};
        int[] to = {R.id.zipcode, R.id.street};

        SimpleAdapter adapter = new SimpleAdapter(AddressListActivity.this,
                listAddress(), R.layout.address_row, from, to);

        setListAdapter(adapter);

        setWidgets();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.addressSelect = position;
        alertDialog.show();
    }

    private AlertDialog createDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete);
        builder.setPositiveButton(getString(R.string.yes), this);
        builder.setNegativeButton(getString(R.string.no), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Log.d(TAG, "Item clicked " + item);
        Intent intent;

        switch (item) {
            case 0:
                intent = new Intent(this, AddressActivity.class);
                intent.putExtra(ADDRESS_ID, Long.parseLong(addresses.get(addressSelect).get("id").toString()));
                startActivity(intent);
                break;

            case 1:
                alertDialogConfirmation.show();
                break;

            case 2:
                intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                break;

            case DialogInterface.BUTTON_POSITIVE:
                new AddressDAO(this).delete(Long.parseLong(addresses.get(addressSelect).get("id").toString()));
                addresses.remove(addressSelect);
                getListView().invalidateViews();
                setWidgets();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                alertDialogConfirmation.dismiss();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.btn_list_address_back:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_list_address_new:
                intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setWidgets() {
        btnAddAddress = (Button) findViewById(R.id.btn_list_address_new);
        btnBack = (Button) findViewById(R.id.btn_list_address_back);

        btnBack.setOnClickListener(this);
        btnAddAddress.setOnClickListener(this);

        if (addresses.size() >= 3) {
            btnAddAddress.setEnabled(false);
        } else {
            btnAddAddress.setEnabled(true);
        }
    }

    private AlertDialog createAlertDialog() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.options);
        builder.setItems(items, this);

        return builder.create();
    }

    private List<Map<String, Object>> listAddress() {
        addresses = new ArrayList<Map<String, Object>>();

        List<Address> listAddress = new AddressDAO(this).listAddresses();
        Map<String, Object> item;

        for (Address d: listAddress) {
            item = new HashMap<>();

            item.put("city", d.getCity());
            item.put("number", d.getNumber());
            item.put("quarter", d.getQuarter());
            item.put("state", d.getState());
            item.put("street", d.getStreet());
            item.put("zipcode", d.getZipcode());
            item.put("id", d.getId());

            addresses.add(item);
        }

        return addresses;
    }
}
