package br.com.compremelhor.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.impl.PartnerResource;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.util.DatabaseHelper;
import br.com.compremelhor.model.Establishment;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;

/**
 * Created by adriano on 10/04/16.
 */
public class PartnerListActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private List<Map<String, Object>> partners;

    private ListAdapter adapter;
    private ListView listView;

    private PartnerResource partnerResource;
    private DAOEstablishment daoEstablishment;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_list);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        partnerResource = new PartnerResource("partners", this);

        daoEstablishment = DAOEstablishment.getInstance(this);

        setToolbar();
        setViews();
        registerViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.partner_list_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setViews() {
        partners = listPartners();

        String[] from = {
                DatabaseHelper.Establishment.NAME
        };

        int[] to = {R.id.tv_partner_name};

        adapter = new SimpleAdapter(PartnerListActivity.this,
                partners, R.layout.partner_row, from, to);

        listView = (ListView) findViewById(R.id.partner_list_view);
        listView.setAdapter(adapter);
    }

    private List<Map<String, Object>> listPartners() {
        showProgressDialog(getString(R.string.dialog_content_text_loading_places));

        AsyncTask<Void, Void, List<Map<String, Object>>> request = new AsyncTask<Void, Void, List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> doInBackground(Void... params) {
                partners = new ArrayList<>();
                List<Establishment> partnersList = partnerResource.getAllResources(0, 5);
                Map<String, Object> partner;

                for (Establishment item : partnersList) {

                    partner = new HashMap<>();
                    partner.put(DatabaseHelper.Establishment._ID, item.getId());
                    partner.put(DatabaseHelper.Establishment.NAME, item.getName());

                    partners.add(partner);
                }
                progressDialog.dismiss();
                return partners;
            }
        };
        try {
            return request.execute().get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Error occurred during editing an item on cart: " + e);
        }
    }

    private void registerViews() {
        listView.setOnItemClickListener(new OnItemClickListener());
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(PartnerListActivity.this,
                        getString(R.string.dialog_header_wait), message, true, false);
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, Object> partner = partners.get(position);

            Establishment est = daoEstablishment
                    .findByAttribute("name", partner.get(DatabaseHelper.Establishment.NAME).toString());
            if (est == null) {
                Establishment establishment = new Establishment();
                establishment.setId((int) partner.get(DatabaseHelper.Establishment._ID));
                establishment.setName(partner.get(DatabaseHelper.Establishment.NAME).toString());
                establishment.setDateCreated(Calendar.getInstance());
                establishment.setLastUpdated(Calendar.getInstance());
                daoEstablishment.insert(establishment);
            } else if (est.getId() != (int) partner.get(DatabaseHelper.Establishment._ID)) {
                est.setId((int) partner.get(DatabaseHelper.Establishment._ID));
                daoEstablishment.updateByName(est);
            }

            preferences.edit()
                    .putInt(SP_PARTNER_ID, (int) partner.get(DatabaseHelper.Establishment._ID))
                    .apply();

            Intent intent = new Intent(PartnerListActivity.this, ShoppingActivity.class);
            startActivity(intent);
        }
    }
}
