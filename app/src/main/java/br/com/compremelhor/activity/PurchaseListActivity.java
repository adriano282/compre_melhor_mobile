package br.com.compremelhor.activity;

import android.content.Intent;
import android.os.Bundle;
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

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.util.DatabaseHelper;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_PURCHASE_VIEWED;

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
                DatabaseHelper.Purchase.LAST_UPDATED,
                DatabaseHelper.Purchase.STATUS,
                DatabaseHelper.Purchase.TOTAL_VALUE
        };

        int [] to = {R.id.tv_purchase_row_establishment_name,
                R.id.tv_purchase_row_date,
                R.id.tv_purchase_row_status,
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

    private List<Map<String, Object>> listObjects() {
        objects = new ArrayList<>();

        int userId = preferences.getInt(SP_USER_ID, 0);

        List<Purchase> listPurchases =
                DAOPurchase.getInstance(this)
                        .findAllByAttribute(DatabaseHelper.Purchase._USER_ID,
                                String.valueOf(userId));

        Map<String, Object> item;

        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        for (Purchase p : listPurchases) {
            item = new HashMap<>();

            item.put(DatabaseHelper.Purchase._ESTABLISHMENT_ID, p.getEstablishment().getId());
            item.put(DatabaseHelper.Establishment.NAME, p.getEstablishment().getName());
            item.put(DatabaseHelper.Purchase.LAST_UPDATED, dt.format(p.getLastUpdated().getTime()));
            item.put(DatabaseHelper.Purchase.STATUS, p.getStatus().getTranslatedValued());

            Double totalValue = p.getTotalValue().doubleValue() +
                    (p.getFreight() != null ? p.getFreight().getValueRide().doubleValue() : 0.0);

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
