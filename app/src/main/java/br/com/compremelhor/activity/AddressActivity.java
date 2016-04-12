package br.com.compremelhor.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.AddressResource;
import br.com.compremelhor.dao.impl.DAOAddress;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.util.Constants.EXTRA_ADDRESS_ID;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class AddressActivity extends ActivityTemplate<Address> {
    private EditText etZipcode;
    private EditText etStreet;
    private EditText etNumber;
    private EditText etQuarter;
    private EditText etCity;
    private EditText etState;
    private EditText etNameAddress;

    private Button btnReset;
    private Button btnSubmit;

    private String zipcode;
    private String street;
    private String quarter;
    private String city;
    private String state;

    private String result;
    private boolean update = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(R.id.address_toolbar,
                getSharedPreferences(PREFERENCES, MODE_PRIVATE), new Handler(),
                DAOAddress.getInstance(AddressActivity.this),
                new AddressResource(this, preferences.getInt(SP_USER_ID, 0)));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address);

        if (!resource.isConnectedOnInternet()) {
            createDialogErrorWithoutNetwork(R.string.err_without_connection_register_message);
            finish();
        }

        setToolbar();
        setWidgets();
        registerWidgets();
    }

    protected void registerWidgets() {
        btnReset.setOnClickListener(new OnClickListener());
        btnSubmit.setOnClickListener(new OnClickListener());
        etZipcode.setOnKeyListener(new OnKeyListener());
    }

    @Override
    protected void setWidgets() {
        etZipcode = (EditText) findViewById(R.id.et_address_zipcode);
        etStreet = (EditText) findViewById(R.id.et_address_street);
        etNumber = (EditText) findViewById(R.id.et_address_number);
        etQuarter = (EditText) findViewById(R.id.et_address_quarter);
        etCity = (EditText) findViewById(R.id.et_address_city);
        etState = (EditText) findViewById(R.id.et_address_state);
        etNameAddress = (EditText) findViewById(R.id.et_address_name);

        etState.setEnabled(false);
        etStreet.setEnabled(false);
        etQuarter.setEnabled(false);
        etCity.setEnabled(false);

        btnReset = (Button) findViewById(R.id.btn_address_reset);
        btnSubmit = (Button) findViewById(R.id.btn_address_submit);

        fillFields();
    }

    @Override
    protected void fillFields() {
        if (getIntent().hasExtra(EXTRA_ADDRESS_ID)) {

            int id = getIntent().getIntExtra(EXTRA_ADDRESS_ID, 0);
            Address ad = DAOAddress.getInstance(this).find(id);

            etZipcode.setText(ad.getZipcode());
            etStreet.setText(ad.getStreet());
            etNumber.setText(ad.getNumber());
            etQuarter.setText(ad.getQuarter());
            etCity.setText(ad.getCity());
            etState.setText(ad.getState());
            etNameAddress.setText(ad.getAddressName());
            update = true;
        }
    }

    private void resetFields(boolean includeCep) {
        if (includeCep)
            etZipcode.setText("");

        etStreet.setText("");
        etNumber.setText("");
        etQuarter.setText("");
        etCity.setText("");
        etState.setText("");
    }

    private Address getAddressView() {

        return new Address(
                getIntent().getIntExtra(EXTRA_ADDRESS_ID, 0), etStreet.getText().toString(),
                etNumber.getText().toString(),
                etQuarter.getText().toString(),
                etCity.getText().toString(),
                etState.getText().toString(), etZipcode.getText().toString(),
                etNameAddress.getText().toString(),
                preferences.getInt(SP_USER_ID, 0)
        );
    }

    private class HttpGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dismissProgressDialog();
            showProgressDialog(R.string.dialog_header_wait, R.string.dialog_content_text_running);
        }

        private String converse(String host, int port, String path) throws IOException {
            URL url = new URL("http", host, port, path);
            URLConnection conn = url.openConnection();

            conn.setDoInput(true);
            conn.setAllowUserInteraction(true);
            conn.connect();

            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            return sb.toString();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                result = converse("viacep.com.br", 80,"/ws/" + zipcode +"/json/");
                JSONObject jsonObject = new JSONObject(result);

                if (!result.contains("erro")) {
                    result = "success";
                    city = jsonObject.getString("localidade");
                    quarter = jsonObject.getString("bairro");
                    street = jsonObject.getString("logradouro");
                    state = jsonObject.getString("uf");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            dismissProgressDialog();
            if (result.equals("success")) {
                etStreet.setText(street);
                etCity.setText(city);
                etQuarter.setText(quarter);
                etState.setText(state);
            } else {
                resetFields(true);
                etZipcode.setFocusable(true);
                showMessage(R.string.err_cep_not_found);
            }
        }

    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_address_reset:
                    resetFields(true);
                    break;

                case R.id.btn_address_submit:
                    if (!resource.isConnectedOnInternet()) {
                        createDialogErrorWithoutNetwork(R.string.err_without_connection_register_message);
                        return;
                    }

                    AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Address ad = getAddressView();
                            ResponseServer<Address> response;

                            if (update)
                                response = resource.updateResource(ad);
                            else
                                response = resource.createResource(ad);

                            if (!response.hasErrors()) {

                                if (update) {
                                    if (dao.insertOrUpdate(ad) == -1) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showMessage(R.string.err_ocurred_attempting_save_address);
                                            }
                                        });

                                    }
                                } else {
                                    ad.setId(response.getEntity().getId());
                                    if (dao.insert(ad) == -1) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showMessage(R.string.err_ocurred_attempting_save_address);
                                            }
                                        });
                                    }
                                }

                                dismissProgressDialog();

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMessage(R.string.data_registered_successful_message);
                                        finish();
                                    }
                                });
                            }
                            else {
                                for (String s : response.getErrors()) {
                                    Log.d("REST API", "Error in creation Address: " + s);
                                }
                                throw new RuntimeException("An Error occurred during try of update resource");
                            }
                            return null;
                        }

                        @Override
                        protected void onPreExecute() {
                            showProgressDialog(R.string.dialog_header_wait,
                                    R.string.dialog_content_text_registering_on_server);
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            dismissProgressDialog();
                        }
                    };
                    request.execute();
                    break;
            }
        }
    }

    private class OnKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            zipcode = etZipcode.getText().toString();
            if (zipcode.length() == 8 && isValidCep(etZipcode))
                new HttpGetter().execute();
            else
                resetFields(false);

            return false;
        }

        private boolean isValidCep(EditText cep) {
            if (cep.getText().toString().matches("[0-9]{8}"))
                return true;

            cep.setError(getString(R.string.err_invalid_cep));
            return false;
        }
    }
}
