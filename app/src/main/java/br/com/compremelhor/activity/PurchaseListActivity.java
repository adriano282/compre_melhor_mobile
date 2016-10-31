package br.com.compremelhor.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.Resource;
import br.com.compremelhor.api.integration.resource.impl.PurchaseResource;
import br.com.compremelhor.api.integration.resource.impl.SyncResource;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.fragment.purchase.PurchaseListChartFragment;
import br.com.compremelhor.fragment.purchase.PurchaseListFragment;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.Sync;

import static br.com.compremelhor.util.Constants.MENU_OPTION_ID_LIST_BY_MONTH;
import static br.com.compremelhor.util.Constants.MENU_OPTION_ID_LIST_BY_YEAR;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.SP_VIEW_PURCHASE_BY_YEAR;

/**
 * Created by adriano on 21/04/16.
 */
public class PurchaseListActivity extends ActionBarActivity {
    private final String TAG = "purchaseListActivity";
    private ProgressDialog progressDialog;
    private DAOPurchase dao;
    private SharedPreferences preferences;
    private Resource<Purchase> resource;

    PurchaseListFragment listFragment;
    PurchaseListChartFragment chartFragment;
    private ActionBar ab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        setActionBar();

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        dao = DAOPurchase.getInstance(this);
        resource = new PurchaseResource("purchases", this);
        sync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        if (ab!= null && ab.getSelectedTab().getPosition() == 0) {
//            if (preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false)) {
                menu.add(0, MENU_OPTION_ID_LIST_BY_MONTH, 0, R.string.setting_view_purchases_by_month)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
  //          }
    //        else {
                menu.add(0, MENU_OPTION_ID_LIST_BY_YEAR, 0, R.string.setting_view_purchases_by_year)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
      //      }
        }
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    private void refreshPurchaseList() {
        if (listFragment.isVisible()) {
            listFragment.fillFields();
            listFragment.setWidgets();
        }
        else if (chartFragment.isVisible()) {
            chartFragment.refreshFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                return true;

            case android.R.id.home:
                finish();
                return true;

            case MENU_OPTION_ID_LIST_BY_YEAR:
                preferences.edit().putBoolean(SP_VIEW_PURCHASE_BY_YEAR, true).apply();
                refreshPurchaseList();

                return true;

            case MENU_OPTION_ID_LIST_BY_MONTH:
                preferences.edit().putBoolean(SP_VIEW_PURCHASE_BY_YEAR, false).apply();
                refreshPurchaseList();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                        if (c.getEntityName().equals("purchase") && c.getAction().equals("EDITED")) {
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

    protected void showProgressDialog(int headerTextId, int contentTextId) {
        this.progressDialog = ProgressDialog
                .show(this, getString(headerTextId), getString(contentTextId), true, false);
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setActionBar() {
        ab = getSupportActionBar();
        Log.d(TAG, "setActionBar");
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setIcon(R.mipmap.icon);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setElevation(4);

        listFragment = PurchaseListFragment.newInstance("PurchaseListFragment");
        chartFragment = PurchaseListChartFragment.newInstance("purchaseListCharFragment");

        ab.addTab(
                ab.newTab()
                        .setText("Listagem")
                        .setTabListener(new MyTabsListener(listFragment))
        );
        ab.addTab(ab.newTab().setText("Gráfico").setTabListener(new MyTabsListener(chartFragment)));
    }


    private class MyTabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;
            Log.d("MyTabsListener", "Constructor");

        }


        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Log.d("MyTabsListener", "onTabSelected");
            ft.replace(R.id.purchase_list_placeholder, fragment);

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Log.d("MyTabsListener", "onTabUnselected");
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Log.d("MyTabsListener", "onTabReselected");
        }
    }
}
