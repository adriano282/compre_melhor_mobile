package br.com.compremelhor.controller.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.useful.Constants.ADDRESS_ID_EXTRA;
import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.USER_ID_SHARED_PREFERENCE;

public class AddressActivity extends AppCompatActivity {

    private SharedPreferences preferences;

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

    private ProgressDialog dialog;

    private String result;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_address);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        setToolbar();
        setWidgets();
        registerWidgets();
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

    private void registerWidgets() {
        btnReset.setOnClickListener(new OnClickListener());
        btnSubmit.setOnClickListener(new OnClickListener());
        etZipcode.setOnKeyListener(new OnKeyListener());
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

    private void setWidgets() {
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

    private void fillFields() {
        if (getIntent().hasExtra(ADDRESS_ID_EXTRA)) {

            Long id = getIntent().getLongExtra(ADDRESS_ID_EXTRA, 0);
            Address ad = new DAOAddress(this).getAddressById(id);

            etZipcode.setText(ad.getZipcode());
            etStreet.setText(ad.getStreet());
            etNumber.setText(ad.getNumber());
            etQuarter.setText(ad.getQuarter());
            etCity.setText(ad.getCity());
            etState.setText(ad.getState());
            etNameAddress.setText(ad.getAddressName());
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
                getIntent().getLongExtra(ADDRESS_ID_EXTRA, 0), etStreet.getText().toString(),
                etNumber.getText().toString(),
                etQuarter.getText().toString(),
                etCity.getText().toString(),
                null, etZipcode.getText().toString(),
                etNameAddress.getText().toString(),
                preferences.getLong(USER_ID_SHARED_PREFERENCE, 0)
        );

    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class HttpGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            if (AddressActivity.this.dialog != null) {
                AddressActivity.this.dialog.dismiss();
            }
            AddressActivity.this.dialog = ProgressDialog.show(AddressActivity.this, "Aguarde", "Processando", true, false);
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            AddressActivity.this.dialog.dismiss();
            if (result.equals("success")) {
                etStreet.setText(street);
                etCity.setText(city);
                etQuarter.setText(quarter);
                etState.setText(state);
            } else {
                resetFields(true);
                etZipcode.setFocusable(true);
                showMessage(getString(R.string.err_cep_not_found));
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
                    DAOAddress dao = new DAOAddress(AddressActivity.this);
                    if (dao.insertOrUpdate(getAddressView()) == -1) {
                        showMessage(getString(R.string.err_ocurred_attempting_save_address));
                    } else {
                        showMessage(getString(R.string.address_saved_successfully));
                    }
                    finish();
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
