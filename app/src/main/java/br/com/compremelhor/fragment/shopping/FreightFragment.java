package br.com.compremelhor.fragment.shopping;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.AddressListActivity;
import br.com.compremelhor.dao.impl.DAOAddress;
import br.com.compremelhor.dao.impl.DAOFreightType;
import br.com.compremelhor.model.Address;
import br.com.compremelhor.model.Freight;
import br.com.compremelhor.model.FreightSetup;
import br.com.compremelhor.model.FreightType;
import br.com.compremelhor.service.CartService;
import br.com.compremelhor.util.helper.DatabaseHelper;

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

    private RadioGroup rgFreightTypes;
    private RadioGroup rgAddresses;

    private CartService cartService;

    private DAOAddress daoAddress;
    private DAOFreightType daoFreightType;

    private LinearLayout llAddressView;
    private LinearLayout llFreightTypeView;

    private Address currentAddress;

    private List<Address> addresses;
    private List<FreightType> freightTypes;


    private int userId;
    private int partnerId;

    public static FreightFragment newInstance(String mTag){
        if (instance == null)
            instance = new FreightFragment();

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setPreferences();
        setDAOS();

        userId = preferences.getInt(SP_USER_ID, 0);
        partnerId = preferences.getInt(SP_PARTNER_ID, 0);

        setCartService(userId, partnerId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_freight, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle state) {
        Log.d(TAG, "onViewCreated");

        setWidgets(view);
        setAddresses();
        setFreightTypes();
        registerWidgets();

        llAddressView.removeViews(1, llAddressView.getChildCount() - 1);
        llFreightTypeView.removeViews(1, llFreightTypeView.getChildCount() - 1);

        setUpFreightSetupView();
        setUpTotalValueView();

        if (cartService.getFreight() != null)
        currentAddress = cartService.getFreight().getshipAddress();

        addRadioButtonForFreightTypes();
        setAddressView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        if (cartService.getFreight() == null || cartService.getFreightType() == null) {
            Toast.makeText(getActivity(), "Frete não selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartService.getFreight().getShipAddress() == null) {
            Toast.makeText(getActivity(), "Endereço não selecionado.", Toast.LENGTH_SHORT).show();
            cartService.getFreight().setComplete(false);
            return; }

        if (cartService.getFreightType().isScheduled() &&
                cartService.getFreightSetup(true) == null) {
            cartService.getFreight().setComplete(false);
            Toast.makeText(getActivity(), "Horário não configurado para o frete agendado.", Toast.LENGTH_SHORT).show();
            return; }

        if (!cartService.getFreight().getComplete()) {
            cartService.getFreight().setComplete(true);
            cartService.persistFreightInBackground(); }

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
                rgFreightTypes.clearCheck();
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

        if (cartService.getFreightType() == null) throw new IllegalArgumentException("FreightFragment: CartService.getFreightType() can not return null here");

        int sumOfDays = cartService.getFreightType().getAvailabilityScheduleWorkDays();

        if (c.get(Calendar.DAY_OF_WEEK) == 1) sumOfDays++;
        else if (c.get(Calendar.DAY_OF_WEEK) == 7) sumOfDays = sumOfDays + 2;

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, sumOfDays);

        datePickerDialog
                .getDatePicker()
                .setMinDate(minDate.getTime().getTime());
        datePickerDialog.show();

        Toast.makeText(getActivity(),"Agendamento a partir de " + cartService.getFreightType().getAvailabilityScheduleWorkDays() + " dias últeis.", Toast.LENGTH_SHORT).show();

    }

    private class OnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTimeStartShip.setText(getHourFormatted(hourOfDay, minute));

            if (cartService.getFreightSetup() == null) cartService.setFreightSetup(new FreightSetup());
            cartService.getFreightSetup().setHour(hourOfDay);
            cartService.getFreightSetup().setMinute(minute);

            cartService.getFreight().setVersion(1);
        }
    }

    private String getFormattedDate(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String month = String.valueOf(monthOfYear).length() == 1 ? "0" + monthOfYear : String.valueOf(monthOfYear);
        return dayOfMonth + "/" + month + "/" + year;
    }

    private String getHourFormatted(int hourOfDay, int minute) {
        String hour = String.valueOf(hourOfDay).length() == 1 ?
                "0" + hourOfDay :
                "" + hourOfDay;

        String sMinute = String.valueOf(minute).length() == 1 ?
                "0" + minute :
                "" + minute;
        return hour + ":" + sMinute;
    }

    private class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            btnDateShip.setText(getFormattedDate(year, monthOfYear, dayOfMonth));
            if (cartService.getFreightSetup() == null) cartService.setFreightSetup(new FreightSetup());

            cartService.getFreightSetup().setYear(year);
            cartService.getFreightSetup().setMonth(Integer.valueOf(++monthOfYear));
            cartService.getFreightSetup().setDayOfMonth(dayOfMonth);

            cartService.getFreight().setVersion(1);
        }
    }

    private void showViewsIfHasAddresses(Map<Integer, Integer> mapIdsAndStates) {
        Iterator<Map.Entry<Integer, Integer>> it = mapIdsAndStates.entrySet().iterator();
        if (!addresses.isEmpty()) {
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

            if (cartService.getFreight() != null)
                cartService.getFreight().setAshipAddress(daoAddress.find(checkedId));
        }
    }

    private void setAddressView() {
        for (Address ad : addresses) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setId(ad.getId());

            String street = ad.getStreet();
            street = street.length() > 15 ? street.substring(0, 15).concat("...") : street;

            rb.setText(street + " / " + ad.getZipcode());

            if (currentAddress != null && currentAddress.getId() == ad.getId()) {
                rb.setChecked(true);

                if (cartService.getFreight() != null &&
                        cartService.getFreight().getShipAddress() != null &&
                        cartService.getFreight().getShipAddress() != currentAddress) {
                    cartService.getFreight().setAshipAddress(currentAddress);
                    cartService.getFreight().setVersion(1);
                }
            }

            rgAddresses.addView(rb);
        }

        llAddressView.addView(rgAddresses);
    }

    private void setUpFreightSetupView() {
        FreightSetup freightSetup;
        if (cartService.getFreightSetup() != null) {
            freightSetup = cartService.getFreightSetup(); }
        else {
            freightSetup = cartService.getFreightSetupFromDB(); }

        if (freightSetup != null) {
            btnDateShip.setText(getFormattedDate(freightSetup.getYear(), freightSetup.getMonth(), freightSetup.getDayOfMonth()));
            btnTimeStartShip.setText(getHourFormatted(freightSetup.getHour(), freightSetup.getMinute())); }
    }

    private void addRadioButtonForFreightTypes() {
        for (FreightType ft : freightTypes) {
            RadioButton rb = new RadioButton(getActivity());
            rb.setId(ft.getId());
            rb.setText(ft.getTypeName() + " / R$ " + ft.getRideValue());

            if (cartService.getFreightType() != null && cartService.getFreightType().getTypeName().equals(ft.getTypeName())) {
                rb.setChecked(true);

                cartService.getFreight().setRideValue(ft.getRideValue());
                cartService.getFreight().setType(ft.getTypeName());
            }
            rgFreightTypes.addView(rb);
        }
        llFreightTypeView.addView(rgFreightTypes);
    }

    private void setUpTotalValueView() {
        Freight f = cartService.getFreight();
        if (f == null || f.getRideValue() == null) {
            tvTotalValue.setText("R$ 0,00");
        } else {
            tvTotalValue.setText(String.format("R$ %,.2f", f.getRideValue().doubleValue()));
        }
    }

    private void setAddresses() {
        addresses = DAOAddress.getInstance(getActivity())
                .findAllByForeignId(DatabaseHelper.Address._USER_ID, preferences.getInt(SP_USER_ID, 0));
    }

    private void registerWidgets() {
        rgFreightTypes.setOnCheckedChangeListener(new FreightTypeOnCheckedChangeListener());
        rgAddresses.setOnCheckedChangeListener(new AddressOnCheckedChangeListener());
    }

    private void setFreightTypes() {
        freightTypes =
                daoFreightType.findAllByForeignId(DatabaseHelper.FreightType._ESTABLISHMENT_ID, preferences.getInt(SP_PARTNER_ID, 0));
    }

    private void setWidgets(View view) {
        btnDateShip = (Button) view.findViewById(R.id.btn_picker_day_of_ship);
        btnTimeStartShip = (Button) view.findViewById(R.id.btn_picker_start_hour_range);

        tvTotalValue = (TextView) view.findViewById(R.id.tv_value_total_freight);

        rgFreightTypes = new RadioGroup(getActivity());
        llFreightTypeView = (LinearLayout) view.findViewById(R.id.ll_freight_types);

        rgAddresses = new RadioGroup(getActivity());
        llAddressView = (LinearLayout) view.findViewById(R.id.my_addresses);

    }

    private void setPreferences() {
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
    private void setDAOS() {
        daoAddress = DAOAddress.getInstance(getActivity());
        daoFreightType = DAOFreightType.getInstance(getActivity());
    }

    private void setCartService(int userId, int partnerId) {
        cartService =  CartService.getInstance(getActivity(), userId, partnerId);

    }

    private void upgradeTotalValueView() {
        if (cartService.getFreight() == null) {
            tvTotalValue.setText("R$ 0,00");
            return;
        }
        tvTotalValue.setText(String.format("R$ %,.2f", cartService.getFreight().getRideValue().doubleValue()));
    }

    private class FreightTypeOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Map<Integer, Integer> map = new HashMap<>();

            FreightType ft = daoFreightType.find(checkedId);
            if (ft == null) return;

            if (ft.isScheduled()) {
                map.put(R.id.scheduled_freight_data, View.VISIBLE);
            } else {
                map.put(R.id.scheduled_freight_data, View.GONE);
            }

            map.put(R.id.my_addresses, View.VISIBLE);
            showViewsIfHasAddresses(map);

            Freight f = cartService.getFreight();

            if (f == null) f = new Freight();

            f.setAshipAddress(currentAddress);
            f.setType(ft.getTypeName());
            f.setRideValue(ft.getRideValue());
            f.setVersion(1);

            cartService.setFreight(f);
            cartService.setFreightType(ft);
            upgradeTotalValueView();
        }
    }
}

