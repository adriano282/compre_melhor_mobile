package br.com.compremelhor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import br.com.compremelhor.api.integration.resource.Resource;
import br.com.compremelhor.api.integration.resource.impl.FreightTypeResource;
import br.com.compremelhor.api.integration.resource.impl.PartnerResource;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOFreightType;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.model.FreightType;
import br.com.compremelhor.util.helper.DatabaseHelper;

import static br.com.compremelhor.util.Constants.REQUEST_CODE_PURCHASE_FINISHED;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;

public class PartnerListActivity extends ActivityTemplate<Establishment> {
    private List<Map<String, Object>> partners;

    private ListAdapter adapter;
    private ListView listView;

    private int selectedPartner;
    private Resource<FreightType> freightTypeResource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(
                DAOEstablishment.getInstance(this),
                new PartnerResource("partners", this)
        );


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_list);
        setToolbar();
        setWidgets();
        registerWidgets();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE_FINISHED && resultCode == Activity.RESULT_OK) {
            showMessage(R.string.purchase_concluded_successful_message);
            finish();
        }
    }

    @Override
    protected void setWidgets() {
        partners = listPartners();
        String[] from = { DatabaseHelper.Establishment.NAME };

        int[] to = {R.id.tv_partner_name};

        adapter = new SimpleAdapter(PartnerListActivity.this,
                partners, R.layout.partner_row, from, to);

        listView = (ListView) findViewById(R.id.partner_list_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void registerWidgets() {
        listView.setOnItemClickListener(new OnItemClickListener());
    }

    @Override
    protected void fillFields() {}

    private void getAndPersistFreightTypes() {
        showProgressDialog(getString(R.string.looking_for_freight_types));
        freightTypeResource = new FreightTypeResource(this, selectedPartner);
        final DAOFreightType daoFreightType = DAOFreightType.getInstance(this);
        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<FreightType> freightTypeList = new ArrayList<>();
                freightTypeList = freightTypeResource.getAllResources(0, 30);

                                for (FreightType ft : freightTypeList) {
                        FreightType fromDb;

                        if ((fromDb = daoFreightType.find(ft.getId())) == null
                                || fromDb.getLastUpdated().before(ft.getLastUpdated())) {

                            boolean insert = false;
                            if (fromDb == null) {
                                fromDb = new FreightType();
                                insert = true;
                            }

                            fromDb.setId(ft.getId());
                            fromDb.setDescription(ft.getDescription());
                            fromDb.setEstablishmentId(selectedPartner);
                            fromDb.setDateCreated(ft.getDateCreated());
                            fromDb.setScheduled(ft.isScheduled());
                            fromDb.setDelayInWorkdays(ft.getDelayInWorkdays());
                            fromDb.setTypeName(ft.getTypeName());
                            fromDb.setRideValue(ft.getRideValue());
                            fromDb.setAvailabilityScheduleWorkDays(ft.getAvailabilityScheduleWorkDays());
                            fromDb.setLastUpdated(Calendar.getInstance());

                            if (insert)
                                daoFreightType.insert(fromDb);
                            else
                                daoFreightType.insertOrUpdate(fromDb);
                        }
                }

                List<FreightType> freightTypesDb = daoFreightType.findAllByForeignId(DatabaseHelper.FreightType._ESTABLISHMENT_ID, preferences.getInt(SP_PARTNER_ID, 0));

                for (FreightType ft : freightTypesDb) {
                    if (!freightTypeList.contains(ft)) {
                        daoFreightType.delete(ft.getId());
                    }
                }

                progressDialog.dismiss();
                return null;
            }
        };
        request.execute();
    }

    private List<Map<String, Object>> listPartners() {
        showProgressDialog(getString(R.string.dialog_content_text_loading_places));

        AsyncTask<Void, Void, List<Map<String, Object>>> request = new AsyncTask<Void, Void, List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> doInBackground(Void... params) {
                partners = new ArrayList<>();
                List<Establishment> partnersList = resource.getAllResources(0, 5);
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
            throw new RuntimeException("Error occurred during loading partner: " + e);
        }
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


            Establishment est = dao
                    .findByAttribute("name", partner.get(DatabaseHelper.Establishment.NAME).toString());


            if (est == null) {
                Establishment establishment = new Establishment();
                establishment.setId((int) partner.get(DatabaseHelper.Establishment._ID));
                establishment.setName(partner.get(DatabaseHelper.Establishment.NAME).toString());
                establishment.setDateCreated(Calendar.getInstance());
                establishment.setLastUpdated(Calendar.getInstance());
                dao.insert(establishment);
            } else if (est.getId() != (int) partner.get(DatabaseHelper.Establishment._ID)) {
                est.setId((int) partner.get(DatabaseHelper.Establishment._ID));
                ((DAOEstablishment)dao).updateByName(est);
            }

            selectedPartner = (int) partner.get(DatabaseHelper.Establishment._ID);
            preferences.edit()
                    .putInt(SP_PARTNER_ID, selectedPartner)
                    .apply();


            getAndPersistFreightTypes();
            Intent intent = new Intent(PartnerListActivity.this, ShoppingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
