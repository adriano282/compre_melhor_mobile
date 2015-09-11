package br.com.compremelhor.controller.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import br.com.compremelhor.model.Address;
import br.com.compremelhor.useful.Constants;

/**
 * Created by adriano on 09/09/15.
 */
public class AddressListActivity extends ListActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener,
        Constants {

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
        alertDialog = createAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();

        new Task().execute();
    }

    private class Task extends AsyncTask<Void, Void, List<Map<String, Object>>> {
        @Override
        protected List<Map<String,Object>> doInBackground(Void... params) {
            return listAddress();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            String[] from = {"zipcode", "street"};
            int[] to = {R.id.zipcode, R.id.street};

            SimpleAdapter adapter = new SimpleAdapter(AddressListActivity.this,
                    listAddress(), R.layout.address_row, from, to);

            setListAdapter(adapter);
        }
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
        Log.d(TAG, "Item clicked ");
        Intent intent;

        switch (item) {
            case 0:
                Log.d(TAG, "EDIT ADDRESS CLICKED");
                intent = new Intent(this, AddressActivity.class);
                intent.putExtra(CITY, getAddress().get(addressSelect).getCity());
                intent.putExtra(NUMBER, getAddress().get(addressSelect).getNumber());
                intent.putExtra(QUARTER, getAddress().get(addressSelect).getQuarter());
                intent.putExtra(STREET, getAddress().get(addressSelect).getStreet());
                intent.putExtra(ZIPCODE, getAddress().get(addressSelect).getZipcode());
                intent.putExtra(ADDRESS_ID, getAddress().get(addressSelect).getId());

                startActivity(intent);
                break;

            case 1:
                intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                break;

            case 2:
                alertDialogConfirmation.show();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                getAddress().remove(this.addressSelect);
                getListView().invalidateViews();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                alertDialogConfirmation.dismiss();
                break;
        }
    }

    private AlertDialog createAlertDialog() {
        final CharSequence[] items = {
                getString(R.string.edit),
                getString(R.string.new_address),
                getString(R.string.remove)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.options);
        builder.setItems(items, this);

        return builder.create();
    }

    private List<Map<String, Object>> listAddress() {
        addresses = new ArrayList<Map<String, Object>>();

        Map<String, Object> item;

        for (Address d: getAddress()) {
            item = new HashMap<>();

            item.put("city", d.getCity());
            item.put("number", d.getNumber());
            item.put("quarter", d.getQuarter());
            item.put("state", d.getState());
            item.put("street", d.getStreet());
            item.put("zipcode", d.getZipcode());

            addresses.add(item);
        }

        return addresses;
    }

    private List<Address> getAddress() {
        List<Address> addresses = new ArrayList<Address>();

        Address ad1 = new Address();
        ad1.setCity("Mogi das Cruzes");
        ad1.setNumber("49");
        ad1.setQuarter("Vila Brasileira");
        ad1.setState("Sao Paulo");
        ad1.setStreet("Rua Alfredo Gomes Loureiro");
        ad1.setZipcode("08738250");

        addresses.add(ad1);

        Address ad2 = new Address();
        ad2.setCity("Mogi das Cruzes");
        ad2.setNumber("49");
        ad2.setQuarter("Vila Brasileira");
        ad2.setState("Sao Paulo");
        ad2.setStreet("Rua Alfredo Gomes Loureiro");
        ad2.setZipcode("08888888");

        addresses.add(ad2);

        Address ad3 = new Address();
        ad3.setCity("Mogi das Cruzes");
        ad3.setNumber("49");
        ad3.setQuarter("Vila Brasileira");
        ad3.setState("Sao Paulo");
        ad3.setStreet("Rua Coronel Gomes Loureiro");
        ad3.setZipcode("087394-490");

        addresses.add(ad3);

        return addresses;
    }
}
