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

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.address);

        setWidgets();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_address_cancel:
                showMessage("Operação cancelada");
                Intent intent = new Intent(this, AddressListActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_address_reset:
                resetFields();
                break;

            case R.id.btn_address_submit:
                // Persist on the database
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
        if (getIntent().hasExtra(ZIPCODE)) {
            Bundle b = getIntent().getExtras();

            etZipcode.setText(String.valueOf(b.get(ZIPCODE)));
            etStreet.setText(String.valueOf(b.get(STREET)));
            etNumber.setText(String.valueOf(b.get(NUMBER)));
            etQuarter.setText(String.valueOf(b.get(QUARTER)));
            etCity.setText(String.valueOf(b.get(CITY)));
        }
    }

    private void resetFields() {
        etZipcode.setText("");
        etStreet.setText("");
        etNumber.setText("");
        etQuarter.setText("");
        etCity.setText("");
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
