package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.model.Address;

/**
 * Created by adriano on 09/09/15.
 */
public class AddressListActivity extends Activity
        implements DialogInterface.OnClickListener {

    private AlertDialog alertDialog;
    private int addressSelect;
    private ListView listView;
    private Button btnAddAddress;
    private Button btnBack;
    private final String TAG = "AddressActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_address);

        this.listView = (ListView) findViewById(R.id.list_address);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked " + id);
                addressSelect = position;
                alertDialog.show();
            }
        });

        this.alertDialog = createAlertDialog();

        this.listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return getAddress().size();
            }

            @Override
            public Object getItem(int position) {
                return getAddress().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater =
                        (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.address_row, null);

                TextView tvZipcode = (TextView) view.findViewById(R.id.zipcode);
                tvZipcode.setText(getAddress().get(position).getZipcode());

                TextView tvStreet = (TextView) view.findViewById(R.id.street);
                tvStreet.setText(getAddress().get(position).getStreet());

                return view;
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "Item clicked ");
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
