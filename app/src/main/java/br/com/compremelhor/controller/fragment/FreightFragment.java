package br.com.compremelhor.controller.fragment;


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

import java.util.Calendar;
import java.util.Date;
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
    private Button btnDateShip;
    private Button btnTimeStartShip;

    private RadioGroup radioGroup;

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
    public void onViewCreated(View view, Bundle state) {
        btnDateShip = (Button) view.findViewById(R.id.btn_picker_day_of_ship);
        btnTimeStartShip = (Button) view.findViewById(R.id.btn_picker_start_hour_range);

        radioGroup = (RadioGroup) view.findViewById(R.id.rg_type_freight);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener());

        RadioGroup rgAddresses = new RadioGroup(getActivity());

        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Long userId = preferences.getLong(USER_ID_SHARED_PREFERENCE, 0);
        List<Address> addresses = new DAOAddress(getActivity()).getAddressesByUserId(userId);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.my_addresses);
        ll.removeViews(1, ll.getChildCount() -1);

        for (Address ad : addresses) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(ad.getAddressName() + " / " + ad.getZipcode());
            rgAddresses.addView(rb);
        }

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

    private class OnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_scheduled_freight:
                    getView().findViewById(R.id.scheduled_freight_data).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.my_addresses).setVisibility(View.VISIBLE);
                    break;

                case R.id.rb_without_freight:
                    getView().findViewById(R.id.scheduled_freight_data).setVisibility(View.GONE);
                    getView().findViewById(R.id.my_addresses).setVisibility(View.GONE);
                    break;

                case R.id.rb_express_freight:
                    getView().findViewById(R.id.scheduled_freight_data).setVisibility(View.GONE);
                    getView().findViewById(R.id.my_addresses).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}

