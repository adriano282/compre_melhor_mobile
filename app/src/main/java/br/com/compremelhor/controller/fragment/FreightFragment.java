package br.com.compremelhor.controller.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.controller.activity.AddressListActivity;
import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.USER_ID_SHARED_PREFERENCE;

public class FreightFragment extends Fragment {
    private final int manage_address_id = 1;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_freight, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle state) {
        RadioGroup rgAddresses = new RadioGroup(getActivity());

        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Long userId = preferences.getLong(USER_ID_SHARED_PREFERENCE, 0);
        List<Address> addresses = new DAOAddress(getActivity()).getAddressesByUserId(userId);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.my_addresses);
        ll.removeViews(1, ll.getChildCount() -1);

        if (addresses.isEmpty()) {
            ll.setVisibility(View.GONE);
            return;
        }

        for (Address ad : addresses) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(ad.getAddressName() + " / " + ad.getZipcode());
            rgAddresses.addView(rb);
        }

        ll.setVisibility(View.VISIBLE);
        ll.addView(rgAddresses);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, manage_address_id, 0, R.string.manager_addresses)
                .setIcon(R.drawable.address)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        inflater.inflate(R.menu.action_bars_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case manage_address_id:
                startActivityForResult(new Intent(getActivity(), AddressListActivity.class), 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onViewCreated(getView(), null);
    }
}

