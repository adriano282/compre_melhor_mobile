package br.com.compremelhor.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import br.com.compremelhor.R;
import br.com.compremelhor.fragment.PurchaseLinesFragment;

/**
 * Created by adriano on 21/04/16.
 */
public class PurchaseActivity extends ActionBarActivity  {
    private final String CART_FRAGMENT = "cart_fragment";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setActionBar();

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

    private void setActionBar() {
        ActionBar ab = getSupportActionBar();

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setIcon(R.mipmap.icon);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setElevation(4);

        Fragment purchaseLinesFragment = new PurchaseLinesFragment();

        ab.addTab(ab.newTab().setText(R.string.action_bar_tab_list_items_purchase).setTabListener(new MyTabsListener(purchaseLinesFragment)));
        ab.addTab(ab.newTab().setText(R.string.action_bar_tab_items_purchase_graphic).setTabListener(new MyTabsListener(purchaseLinesFragment)));

    }

    private class MyTabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.purchase_placeholder, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//            ft.remove(fragment);
        }
    }


}
