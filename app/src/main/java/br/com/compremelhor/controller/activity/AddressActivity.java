package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
    private EditText etState;

    private String zipcode;
    private String street;
    private String quarter;
    private String city;
    private String state;

    private Button btnSubmit;
    private Button btnCancel;
    private Button btnReset;

    private ProgressDialog dialog;

    private String result;

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
                resetFields(true);
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
        etState = (EditText) findViewById(R.id.et_address_state);

        etState.setEnabled(false);
        etStreet.setEnabled(false);
        etQuarter.setEnabled(false);
        etCity.setEnabled(false);

        btnCancel = (Button) findViewById(R.id.btn_address_cancel);
        btnReset = (Button) findViewById(R.id.btn_address_reset);
        btnSubmit = (Button) findViewById(R.id.btn_address_submit);

        btnCancel.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        etZipcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (etZipcode.getText().length() == 8) {
                    HttpGetter get = new HttpGetter();

                    zipcode = etZipcode.getText().toString();
                    if (zipcode.matches("[0-9]{8}")) {
                        get.execute();
                    } else {
                        showMessage("Cep inválido. Por favor digite um cep válido.");
                    }
                } else {
                    resetFields(false);
                }
                return false;
            }
        });

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
            etState.setText(ad.getState());
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
                    Log.e("Sucesso", "Sucesso.");
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
                showMessage("CEP não encontrado");
            }
        }

    }
}
