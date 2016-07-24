package br.com.compremelhor.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.api.integration.resource.impl.SyncResource;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.Sync;
import br.com.compremelhor.util.helper.DatabaseHelper;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_PURCHASE_VIEWED;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

/**
 * Created by adriano on 21/04/16.
 */
public class PurchaseListActivity extends ActivityListTemplate<Purchase> {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE_VIEWED) {}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(R.layout.activity_purchase_list,
                DAOPurchase.getInstance(this),
                new PurchaseResource("purchases", this));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setWidgets() {
        objects = listObjects();

        String[] from = {
                DatabaseHelper.Establishment.NAME,
                DatabaseHelper.Purchase.DATE_CREATED,
                DatabaseHelper.Purchase.STATUS,
                DatabaseHelper.Purchase.LAST_UPDATED,
                DatabaseHelper.Purchase.TOTAL_VALUE
        };

        int [] to = {R.id.tv_purchase_row_establishment_name,
                R.id.tv_purchase_row_date,
                R.id.tv_purchase_row_status,
                R.id.tv_purchase_row_last_updated,
                R.id.tv_purchase_row_value_total};

        adapter = new SimpleAdapter(this, objects,
                R.layout.purchase_row, from, to);
        listView = (ListView) findViewById(R.id.purchase_list_view);
        listView.setAdapter(adapter);


        TextView tv = (TextView) findViewById(R.id.purchase_list_empty);
        if (objects.size() > 0) {
            tv.setVisibility(TextView.GONE);
        } else {
            tv.setVisibility(TextView.VISIBLE);
        }

    }

    private void sync() {
        final SyncResource syncResource = new SyncResource(this);
        final Map<String, String> params = new HashMap<>();
        params.put("mobileUserIdRef", String.valueOf(preferences.getInt(SP_USER_ID, 0)));
        AsyncTask<Map<String, String>, Void, Void> request = new AsyncTask<Map<String, String>, Void, Void>() {
            @Override
            protected Void doInBackground(Map<String, String>... params) {
                List<Sync> changes = syncResource.getAllResources(params[0]);

                try {
                    for (Sync c : changes) {
                        if (c.getEntityName().equals("purchase") && c.getAction().equals("edit")) {
                            Purchase p;

                            if ((p = dao.find(c.getEntityId())) == null) {
                                syncResource.deleteResource(c);
                                continue;
                            }

                            Purchase pS = resource.getResource(c.getEntityId());
                            p.setStatus(pS.getStatus());

                            dao.insertOrUpdate(p);

                            syncResource.deleteResource(c);
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", "Error while trying sync purchases from server");
                }
                return null;
            }
        };
        try {
            showProgressDialog(R.string.dialog_header_wait, R.string.dialog_content_sync_purchases);
            request.execute(params).get();
            dismissProgressDialog();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> listObjects() {
        sync();

        objects = new ArrayList<>();
        int userId = preferences.getInt(SP_USER_ID, 0);

        List<Purchase> listPurchases =
                dao.findAllByAttribute(DatabaseHelper.Purchase._USER_ID,
                                String.valueOf(userId));

        Map<String, Object> item;

        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        for (Purchase p : listPurchases) {
            item = new HashMap<>();

            item.put(DatabaseHelper.Purchase._ESTABLISHMENT_ID, p.getEstablishment().getId());
            item.put(DatabaseHelper.Establishment.NAME, p.getEstablishment().getName());
            item.put(DatabaseHelper.Purchase.DATE_CREATED, p.getDateCreated().getTime());
            item.put(DatabaseHelper.Purchase.LAST_UPDATED, dt.format(p.getLastUpdated().getTime()));
            item.put(DatabaseHelper.Purchase.STATUS, p.getStatus().getTranslatedValued());

            Double totalValue = p.getTotalValue().doubleValue() +
                    (p.getFreight() != null ? p.getFreight().getRideValue().doubleValue() : 0.0);

            item.put(DatabaseHelper.Purchase.TOTAL_VALUE,
                    String.format("R$ %,.2f", totalValue));
            item.put(DatabaseHelper.Purchase._ID, p.getId());
            objects.add(item);
        }
        return objects;
    }

    @Override
    protected void registerWidgets() {
        listView.setOnItemClickListener(new OnItemClickListener());
    }

    @Override
    protected void setDialogs() {}

    private class OnItemClickListener implements  AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PurchaseListActivity.this.objectSelected = position;
            int purchaseId = (int) objects.get(position).get(DatabaseHelper.Purchase._ID);
            int partnerId = (int) objects.get(position).get(DatabaseHelper.Purchase._ESTABLISHMENT_ID);

            Intent intent = new Intent(PurchaseListActivity.this, PurchaseActivity.class);
            intent.putExtra(EXTRA_PURCHASE_ID, purchaseId);
            intent.putExtra(EXTRA_PARTNER_ID, partnerId);
            startActivityForResult(intent, REQUEST_CODE_PURCHASE_VIEWED);
        }
    }

}
