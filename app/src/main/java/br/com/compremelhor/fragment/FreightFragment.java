package br.com.compremelhor.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.AddressListActivity;
import br.com.compremelhor.dao.impl.DAOAddress;
import br.com.compremelhor.dao.impl.DAOFreight;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.service.CartService;
import br.com.compremelhor.util.DatabaseHelper;

import static br.com.compremelhor.util.Constants.MENU_OPTION_ID_MANAGE_ADDRESS;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_ADDRESS_EDITED_OR_ADDED;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_SELECTED_ADDRESS_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class FreightFragment extends Fragment {
    private static FreightFragment instance;
    private SharedPreferences preferences;
    private Button btnDateShip;
    private Button btnTimeStartShip;

    private TextView tvTotalValue;
    private final String TAG = "freight_fragment";
    private RadioGroup radioGroup;

    private CartService cartService;

    private DAOAddress daoAddress;
    private DAOFreight daoFreight;

    private Address currentAddress;
    private boolean hasAddresses = false;

    public static FreightFragment newInstance(String mTag){
        if (instance == null)
            instance = new FreightFragment();

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_freight, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onViewCreated(View view, Bundle state) {
        Log.d(TAG, "onViewCreated");
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        daoAddress = DAOAddress.getInstance(getActivity());
        daoFreight = DAOFreight.getInstance(getActivity());

        int userId = preferences.getInt(SP_USER_ID, 0);
        int partnerId = preferences.getInt(SP_PARTNER_ID, 0);
        cartService = CartService.getInstance(getActivity(), userId, partnerId);

        btnDateShip = (Button) view.findViewById(R.id.btn_picker_day_of_ship);
        btnTimeStartShip = (Button) view.findViewById(R.id.btn_picker_start_hour_range);

        tvTotalValue = (TextView) view.findViewById(R.id.tv_value_total_freight);

        radioGroup = (RadioGroup) view.findViewById(R.id.rg_type_freight);
        radioGroup.setOnCheckedChangeListener(new FreightTypeOnCheckedChangeListener());

        RadioGroup rgAddresses = new RadioGroup(getActivity());
        rgAddresses.setOnCheckedChangeListener(new AddressOnCheckedChangeListener());

        List<Address> addresses = DAOAddress
                .getInstance(getActivity())
                .findAllByForeignId(DatabaseHelper.Address._USER_ID, userId);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.my_addresses);
        ll.removeViews(1, ll.getChildCount() -1);

        hasAddresses = !addresses.isEmpty();

        if (cartService.getFreight() != null) {
            currentAddress = cartService.getFreight().getshipAddress();

            if (cartService.getFreightSetup() != null) {
                FreightSetup freightSetup = cartService.getFreightSetup();
                btnDateShip.setText(freightSetup.getDayOfMonth() + "/" + freightSetup.getMonth() + "/" + freightSetup.getYear());
                btnTimeStartShip.setText(freightSetup.getHour() + ":" + freightSetup.getMinute());
            }

            if (cartService.getFreight().getType() == Freight.FreightType.SCHEDULED) {
                radioGroup.check(R.id.rb_scheduled_freight);
            } else if (cartService.getFreight().getType() == Freight.FreightType.EXPRESS) {
                radioGroup.check(R.id.rb_express_freight);
            }

            if (cartService.getFreight().getValueRide() == null) {
                tvTotalValue.setText("R$ 0.00");
            } else {
                tvTotalValue.setText("R$ " + cartService.getFreight().getValueRide().toString());
            }
        }

        for (Address ad : addresses) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setId(ad.getId());
            rb.setText(ad.getAddressName() + " / " + ad.getZipcode());

            if (currentAddress != null && currentAddress.getId() == ad.getId()) {
                rb.setChecked(true);

                cartService.loadCurrentFreight();

                if (cartService.getFreight().getShipAddress() != null &&
                        cartService.getFreight().getShipAddress() != currentAddress) {
                    cartService.getFreight().setAshipAddress(currentAddress);
                    cartService.getFreight().setVersion(1);
                }
            }

            rgAddresses.addView(rb);
        }

        ll.addView(rgAddresses);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");


        if (cartService.getFreight() != null && cartService.getFreight().getShipAddress() == null) {
            Toast.makeText(getActivity(), "Endereço não selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartService.getFreight() != null &&
                cartService.getFreight().getType() == Freight.FreightType.SCHEDULED &&
                cartService.getFreightSetup() == null) {
            Toast.makeText(getActivity(), "Horário não configurado para o frete agendado.", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (cartService.getFreight() != null && cartService.getFreight().getVersion() == 0) return null;

                if (cartService.getFreight() != null) {
                    cartService.persistFreight();
                    cartService.getFreight().setVersion(0);

                } else if (daoFreight.findByAttribute(DatabaseHelper.Freight._PURCHASE_ID,
                        String.valueOf(cartService.getPurchase().getId())) != null) {
                    cartService.removeFreight();
                }
                return null;
            }
        };

        try {
            request.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
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
            btnTimeStartShip.setText(hour + ":" + sMinute);

            if (cartService.getFreightSetup() == null) cartService.setFreightSetup(new FreightSetup());
            cartService.getFreightSetup().setHour(hourOfDay);
            cartService.getFreightSetup().setMinute(minute);

            cartService.getFreight().setVersion(1);
        }
    }

    private class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            String month = String.valueOf(monthOfYear).length() == 1 ? "0" + monthOfYear : String.valueOf(monthOfYear);
            btnDateShip.setText(dayOfMonth + "/" + month + "/" + year);

            if (cartService.getFreightSetup() == null) cartService.setFreightSetup(new FreightSetup());

            cartService.getFreightSetup().setYear(year);
            cartService.getFreightSetup().setMonth(Integer.valueOf(month));
            cartService.getFreightSetup().setDayOfMonth(dayOfMonth);

            cartService.getFreight().setVersion(1);
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

            cartService.loadCurrentFreight();
            cartService.getFreight().setAshipAddress(daoAddress.find(checkedId));
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

                    cartService.loadCurrentFreight();
                    if (cartService.getFreight().getType() != Freight.FreightType.SCHEDULED) {
                        cartService.getFreight().setAshipAddress(currentAddress);
                        cartService.getFreight().setType(Freight.FreightType.SCHEDULED);
                        cartService.getFreight().setValueRide(BigDecimal.valueOf(35.00));

                        cartService.getFreight().setVersion(1);
                    }

                    tvTotalValue.setText("R$ " + cartService.getFreight().getValueRide().toString());
                    break;

                case R.id.rb_without_freight:
                    getView().findViewById(R.id.scheduled_freight_data).setVisibility(View.GONE);
                    getView().findViewById(R.id.my_addresses).setVisibility(View.GONE);
                    preferences.edit().putLong(SP_SELECTED_ADDRESS_ID, 0).apply();

                    cartService.setFreight(null);
                    cartService.setFreightSetup(null);
                    tvTotalValue.setText("R$ 0.0");
                    break;

                case R.id.rb_express_freight:
                    map = new HashMap<>();
                    map.put(R.id.scheduled_freight_data, View.GONE);
                    map.put(R.id.my_addresses, View.VISIBLE);
                    showViewsIfHasAddresses(map);

                    cartService.loadCurrentFreight();

                    if (cartService.getFreight().getType() != Freight.FreightType.EXPRESS) {
                        cartService.getFreight().setType(Freight.FreightType.EXPRESS);
                        cartService.getFreight().setValueRide(BigDecimal.valueOf(20.00));
                        cartService.setFreightSetup(null);
                        cartService.getFreight().setVersion(1);
                    }
                    tvTotalValue.setText("R$ " + cartService.getFreight().getValueRide().toString());
                    break;
            }
        }
    }
}

