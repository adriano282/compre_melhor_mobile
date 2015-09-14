package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.AddressDAO;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.useful.Constants;

/**
 * Created by adriano on 11/09/15.
 */
public class AddressActivity extends Activity implements OnClickListener, Constants {

    /* Resources from address.xml's page */
    private EditText etZipcode;
    private EditText etStreet;
    private EditText etNumber;
    private EditText etQuarter;
    private EditText etCity;

    private Button btnSubmit;
    private Button btnCancel;
    private Button btnReset;

    private long id;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.address);

        setWidgets();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.btn_address_cancel:
                showMessage("Operação cancelada");
                intent = new Intent(this, AddressListActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_address_reset:
                resetFields();
                break;

            case R.id.btn_address_submit:
                AddressDAO dao = new AddressDAO(this);

                int result = dao.insertOrUpdate(getAddressView());

                intent = new Intent(this, AddressListActivity.class);
                startActivity(intent);

                if (result == -1) {
                    showMessage("The address wasn't saved");
                } else {
                    showMessage("The address have been saved successufully");
                }
                break;
        }
    }


    private void setWidgets() {
        etZipcode = (EditText) findViewById(R.id.et_address_zipcode);
        etStreet = (EditText) findViewById(R.id.et_address_street);
        etNumber = (EditText) findViewById(R.id.et_address_number);
        etQuarter = (EditText) findViewById(R.id.et_address_quarter);
        etCity = (EditText) findViewById(R.id.et_address_city);

        btnCancel = (Button) findViewById(R.id.btn_address_cancel);
        btnReset = (Button) findViewById(R.id.btn_address_reset);
        btnSubmit = (Button) findViewById(R.id.btn_address_submit);

        btnCancel.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        fillFields();
    }

    private void fillFields() {
        if (getIntent().hasExtra(ADDRESS_ID)) {

            id = getIntent().getLongExtra(ADDRESS_ID, 0);
            Address ad = new AddressDAO(this).getAddressById(id);

            etZipcode.setText(ad.getZipcode());
            etStreet.setText(ad.getStreet());
            etNumber.setText(ad.getNumber());
            etQuarter.setText(ad.getQuarter());
            etCity.setText(ad.getCity());
        }
    }

    private void resetFields() {
        etZipcode.setText("");
        etStreet.setText("");
        etNumber.setText("");
        etQuarter.setText("");
        etCity.setText("");
    }

    private Address getAddressView() {
        return new Address(
                getIntent().getLongExtra(ADDRESS_ID, 0), etStreet.getText().toString(),
                etNumber.getText().toString(),
                etQuarter.getText().toString(),
                etCity.getText().toString(),
                null, etZipcode.getText().toString()
        );

    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
