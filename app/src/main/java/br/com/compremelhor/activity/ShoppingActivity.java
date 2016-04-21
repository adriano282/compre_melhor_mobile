package br.com.compremelhor.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.com.compremelhor.R;
import br.com.compremelhor.fragment.CartFragment;
import br.com.compremelhor.fragment.PaymentFragment;
import br.com.compremelhor.fragment.FreightFragment;

public class ShoppingActivity extends ActionBarActivity {

    private final String CART_FRAGMENT = "cart_fragment";
    private final String FREIGHT_FRAGMENT = "freight_fragment";
    private final String PAYMENT_FRAGMENT = "payment_fragment";

    private final String FREIGHT_FRAGMENT_TAG = "freight_fragment_tag";

    private String currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);
        setActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    public void onClickButtonClosePurchase(View v) {
        ((PaymentFragment)getSupportFragmentManager().getFragments().get(0))
                .onClickButtonClosePurchase(v);
    }

    public void onClickedDayOfShip(View v) {
        FragmentManager fm = getSupportFragmentManager();
        FreightFragment freightFragment = (FreightFragment) fm.getFragments().get(0);
        freightFragment.onClickedDayOfShip(v);
    }

    public void onClickedStartHourRangeShip(View v) {
        ((FreightFragment) getSupportFragmentManager().getFragments().get(0))
                .onClickedStartHourRangeShip(v);
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

        Fragment cartFragment = CartFragment.newInstance(CART_FRAGMENT);
        Fragment freightFragment = FreightFragment.newInstance(FREIGHT_FRAGMENT);
        Fragment closePurchaseFragment = PaymentFragment.newInstance(PAYMENT_FRAGMENT);

        ab.addTab(ab.newTab().setText(R.string.cart).setTabListener(new MyTabsListener(cartFragment)));
        ab.addTab(ab.newTab().setText(R.string.freight).setTabListener(new MyTabsListener(freightFragment)));
        ab.addTab(ab.newTab().setText(R.string.finish).setTabListener(new MyTabsListener(closePurchaseFragment)));
    }

    private class MyTabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;

            if (fragment instanceof CartFragment) {
                currentFragment = CART_FRAGMENT;
            } else if (fragment instanceof  FreightFragment) {
                currentFragment = FREIGHT_FRAGMENT;
            } else {
                currentFragment = PAYMENT_FRAGMENT;
            }
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.placeholder, fragment);
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
