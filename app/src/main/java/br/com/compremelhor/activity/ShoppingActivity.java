package br.com.compremelhor.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.fragment.CartFragment;
import br.com.compremelhor.fragment.FreightFragment;
import br.com.compremelhor.fragment.PaymentFragment;
import br.com.compremelhor.service.CartService;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_PARTNER_NAME;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.FREIGHT_FRAGMENT;
import static br.com.compremelhor.util.Constants.CART_FRAGMENT;
import static br.com.compremelhor.util.Constants.PAYMENT_FRAGMENT;

public class ShoppingActivity extends ActionBarActivity {

    private static final String TAG = "shoppingActivity";

    private final String FREIGHT_FRAGMENT_TAG = "freight_fragment_tag";

    private String currentFragment;

    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);
        setActionBar();
        handler = new Handler();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        int userId = preferences.getInt(SP_USER_ID, 0);
        int partnerId = preferences.getInt(SP_PARTNER_ID, 0);

        preferences
                .edit()
                .putString(SP_PARTNER_NAME, DAOEstablishment.getInstance(this).find(partnerId).getName())
                .apply();

        getIntent().putExtra("cartService", CartService.getInstance(this, userId, partnerId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    public void onClickButtonClosePurchase(View v) {
        Log.d(TAG, "onClickButtonClosePurchase");
        ((PaymentFragment)getSupportFragmentManager().getFragments().get(0))
                .onClickButtonClosePurchase(v);
    }

    public void onClickedDayOfShip(View v) {
        Log.d(TAG, "onClickedDayOfShip");
        FragmentManager fm = getSupportFragmentManager();
        FreightFragment freightFragment = (FreightFragment) fm.getFragments().get(0);
        freightFragment.onClickedDayOfShip(v);
    }

    public void onClickedStartHourRangeShip(View v) {
        Log.d(TAG, "onClickedStartHourRangeShip");
        ((FreightFragment) getSupportFragmentManager().getFragments().get(0))
                .onClickedStartHourRangeShip(v);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
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
        Log.d(TAG, "setActionBar");
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
            Log.d("MyTabsListener", "onTabSelected");
            ft.replace(R.id.placeholder, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Log.d("MyTabsListener", "onTabUnselected");
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Log.d("MyTabsListener", "onTabReselected");
//            ft.remove(fragment);
        }
    }

}
