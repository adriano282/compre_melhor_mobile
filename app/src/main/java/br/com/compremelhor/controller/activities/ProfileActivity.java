package br.com.compremelhor.controller.activities;

/**
 * Created by adriano on 05/09/15.
 */

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.model.Address;

public class ProfileActivity extends ListActivity implements View.OnClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private Button btnSave;
    private Button btnUndone;
    private Button btnChangePassword;
    private Button btnAddNewAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        edName = (EditText) findViewById(R.id.profile_name);
        edEmail = (EditText) findViewById(R.id.profile_email);
        edDocument = (EditText) findViewById(R.id.profile_name);

        btnSave = (Button) findViewById(R.id.profile_button_save);
        btnUndone = (Button) findViewById(R.id.profile_button_undone);
        btnChangePassword = (Button) findViewById(R.id.profile_button_change_password);
        btnAddNewAddress = (Button) findViewById(R.id.profile_button_add_new_address);

        btnChangePassword.setOnClickListener(this);
        btnUndone.setOnClickListener(this);
        btnAddNewAddress.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        setListAdapter(mListAdapter);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

        }
    }

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_addresses, container, false);
            }

            convertView.findViewById(R.id.primary_target).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ProfileActivity.this, "TESTE",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            return convertView;
        }

    };

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

        return addresses;
    }
}
