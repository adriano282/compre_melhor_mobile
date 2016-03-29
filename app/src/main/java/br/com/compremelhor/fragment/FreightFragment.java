package br.com.compremelhor.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.AddressListActivity;
import br.com.compremelhor.dao.DAOAddress;
import br.com.compremelhor.model.Address;

import static br.com.compremelhor.useful.Constants.MENU_OPTION_ID_MANAGE_ADDRESS;
import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_ADDRESS_EDITED_OR_ADDED;
import static br.com.compremelhor.useful.Constants.SP_SELECTED_ADDRESS_ID;
import static br.com.compremelhor.useful.Constants.SP_USER_ID;


public class FreightFragment extends Fragment {
    private SharedPreferences preferences;
    private Button btnDateShip;
    private Button btnTimeStartShip;

    private RadioGroup radioGroup;

    private boolean hasAddresses = false;

    public static FreightFragment newInstance(String mTag){
        FreightFragment freightFragment = new FreightFragment();
        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        freightFragment.setArguments(args);
        return freightFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_freight, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle state) {
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        btnDateShip = (Button) view.findViewById(R.id.btn_picker_day_of_ship);
        btnTimeStartShip = (Button) view.findViewById(R.id.btn_picker_start_hour_range);

        radioGroup = (RadioGroup) view.findViewById(R.id.rg_type_freight);
        radioGroup.setOnCheckedChangeListener(new FreightTypeOnCheckedChangeListener());

        RadioGroup rgAddresses = new RadioGroup(getActivity());
        rgAddresses.setOnCheckedChangeListener(new AddressOnCheckedChangeListener());

        Long userId = preferences.getLong(SP_USER_ID, 0);
        List<Address> addresses = DAOAddress.getInstance(getActivity()).getAddressesByUserId(userId);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.my_addresses);
        ll.removeViews(1, ll.getChildCount() -1);

        hasAddresses = !addresses.isEmpty();

        for (Address ad : addresses) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setId(ad.getId());
            rb.setText(ad.getAddressName() + " / " + ad.getZipcode());
            rgAddresses.addView(rb);
        }

        ll.addView(rgAddresses);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_OPTION_ID_MANAGE_ADDRESS, 0, R.string.manager_addresses)
                .setIcon(R.drawable.address)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        inflater.inflate(R.menu.action_bars_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_OPTION_ID_MANAGE_ADDRESS:
                startActivityForResult(new Intent(getActivity(), AddressListActivity.class), REQUEST_CODE_ADDRESS_EDITED_OR_ADDED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onViewCreated(getView(), null);
        switch(requestCode) {
            case REQUEST_CODE_ADDRESS_EDITED_OR_ADDED:
                radioGroup.clearCheck();
                break;
        }
    }

    public void onClickedStartHourRangeShip(View view) {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(
                getActivity(),
                new OnTimeSetListener(),
                c.get(Calendar.HOUR),
                c.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity())
        ).show();
    }

    public void onClickedDayOfShip(View view) {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new OnDateSetListener(),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog
                .getDatePicker()
                .setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    private class OnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hour = String.valueOf(hourOfDay).length() == 1 ?
                            "0" + hourOfDay :
                            "" + hourOfDay;

            String sMinute = String.valueOf(minute).length() == 1 ?
                            "0" + minute :
                            "" + minute;
            btnTimeStartShip.setText(hour +":" + sMinute);
        }
    }

    private class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            String month = String.valueOf(monthOfYear).length() == 1 ? "0" + monthOfYear : String.valueOf(monthOfYear);
            btnDateShip.setText(dayOfMonth + "/" + month + "/" + year);
        }
    }

    private void showViewsIfHasAddresses(Map<Integer, Integer> mapIdsAndStates) {
        Iterator<Map.Entry<Integer, Integer>> it = mapIdsAndStates.entrySet().iterator();
        if (hasAddresses) {
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> pair = it.next();
                int visibility;
                switch (pair.getValue()) {
                    case View.GONE:
                        visibility = View.GONE;
                        break;

                    case View.VISIBLE:
                        visibility = View.VISIBLE;
                        break;

                    case View.INVISIBLE:
                        visibility = View.INVISIBLE;
                        break;

                    default:
                        throw new IllegalArgumentException("Visibility state invalid. Only View.GONE, View.VISIBLE, View.INVISIBLE");
                }
                getView().findViewById(pair.getKey()).setVisibility(visibility);
            }
        }
        else {
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> pair = it.next();
                getView().findViewById(pair.getKey()).setVisibility(View.GONE);
            }
            Toast.makeText(
                    getActivity(),
                    "Você ainda não possui nenhum endereço cadastrado.",
                    Toast.LENGTH_SHORT)
                    .show();
            Toast.makeText(
                    getActivity(),
                    "Cadastre um endereço clicando na casinha no menu acima.",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class AddressOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            preferences
                    .edit()
                    .putLong(SP_SELECTED_ADDRESS_ID, checkedId)
                    .apply();

            /*
               Here will be implemented the consult on API for
               verify if selected address is on Establishment's ship range
            */
            if (true) {
            /*
                Verify the price of ship for the address selected
            */
            } else {
            /*
                Inform for the user tha unfortunately the address
                selected doesn't is included on ship range
            */
            }
        }
    }

    private class FreightTypeOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Map<Integer, Integer> map;
            switch (checkedId) {
                case R.id.rb_scheduled_freight:
                    map = new HashMap<>();
                    map.put(R.id.scheduled_freight_data, View.VISIBLE);
                    map.put(R.id.my_addresses, View.VISIBLE);
                    showViewsIfHasAddresses(map);
                    break;

                case R.id.rb_without_freight:
                    getView().findViewById(R.id.scheduled_freight_data).setVisibility(View.GONE);
                    getView().findViewById(R.id.my_addresses).setVisibility(View.GONE);
                    preferences.edit().putLong(SP_SELECTED_ADDRESS_ID, 0);
                    break;

                case R.id.rb_express_freight:
                    map = new HashMap<>();
                    map.put(R.id.scheduled_freight_data, View.GONE);
                    map.put(R.id.my_addresses, View.VISIBLE);
                    showViewsIfHasAddresses(map);
                    break;
            }
        }
    }
}

