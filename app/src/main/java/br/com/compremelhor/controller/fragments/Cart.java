package br.com.compremelhor.controller.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.controller.adapter.ExpandableListAdapter;

import static br.com.compremelhor.useful.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.useful.Constants.OTHERS_CODES;
import static br.com.compremelhor.useful.Constants.PRODUCT_MODE;
import static br.com.compremelhor.useful.Constants.QR_CODE_MODE;
import static br.com.compremelhor.useful.Constants.SCAN_MODE;

public class Cart extends Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    
    private AlertDialog actionsProduct;
    private AlertDialog alertDialogConfirmation;

    private Button btnAddProduct;

    private OptionsDialogOnClickListener optionsListener;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prepareListData();
        setWidgets();
        registerViews();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shopping_list, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != 0)
            return;

        if (resultCode == Activity.RESULT_OK) {
            Log.v("SCRIP", intent.getStringExtra("SCAN_RESULT_FORMAT"));
            Log.v("SCRIP", intent.getStringExtra("SCAN_RESULT"));
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.v("SCRIP", "Press a button to start a scan.");
            Log.v("SCRIP", "Scan cancelled");
        }
    }


    private void registerViews() {
        optionsListener = new OptionsDialogOnClickListener();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createReadingAlertDialog();
            }
        });

        explicitView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                actionsProduct.show();
                return false;
            }
        });
    }

    private void setWidgets() {
        explicitView = (ExpandableListView) getActivity().findViewById(R.id.lv_shopping_list);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        explicitView.setAdapter(listAdapter);

        btnAddProduct = (Button) getActivity().findViewById(R.id.btn_shopping_list_add);
        actionsProduct = createProductAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();

    }

    private AlertDialog createReadingAlertDialog() {
        CharSequence[] items;

        items = new CharSequence[] {
                getString(R.string.QR_CODE),
                getString(R.string.BARCODE),
                getString(R.string.OTHER)
        };

        return new Builder(getActivity())
                .setTitle(getString(R.string.read_type))
                .setItems(items, new ScannerOnClickListener())
                .create();
    }

    private AlertDialog createDialogConfirmation() {
        return new Builder(getActivity())
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, optionsListener)
                .setNegativeButton(R.string.no, optionsListener)
                .create();
    }

    private AlertDialog createProductAlertDialog() {
        final CharSequence[] items;

        items = new CharSequence[] {
                getString(R.string.alter_quantity),
                getString(R.string.remove_product)
        };

        return new Builder(getActivity())
                .setTitle(getString(R.string.options))
                .setItems(items, optionsListener)
                .create();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("CATEGORIA CARNES/R$ 30,00");
        listDataHeader.add("CATEGORIA BEBIDAS/R$ 30,00");
        listDataHeader.add("CATEGORIA GULOSEIMAS/R$ 30,00");

        List<String> carnes = new ArrayList<>();
        carnes.add("Passarinho - /2 und - /R$ 10,00");
        carnes.add("Frango - /2 und - /R$ 10,00");
        carnes.add("hamburguer - /2 und - /R$ 10,00");


        List<String> drinks = new ArrayList<String>();
        drinks.add("Coca-cola - /2 gar - /R$ 10,00");
        drinks.add("Dell-Vale - /2 und - /R$ 10,00");
        drinks.add("Guarana Antertica - /2 lt - /R$ 10,00");

        List<String> sweets = new ArrayList<String>();
        sweets.add("passa-tempo - /10 pct - /R$ 10,00");
        sweets.add("passa-tempo - /10 pct - /R$ 10,00");
        sweets.add("passa-tempo - /10 pct - /R$ 10,00");
        sweets.add("passa-tempo - /10 pct - /R$ 10,00");
        sweets.add("chocolate nestle - /2 und - /R$ 10,00");
        sweets.add("sorvete - /2 und - /R$ 10,00");

        listDataChild.put(listDataHeader.get(0), carnes);
        listDataChild.put(listDataHeader.get(1), drinks);
        listDataChild.put(listDataHeader.get(2), sweets);
    }


    private class OptionsDialogOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            Intent intent;

            switch (item) {
                case 0:
                    // Here, implement change quantity product
                    break;

                case 1:
                    alertDialogConfirmation.show();
                    break;

                case DialogInterface.BUTTON_POSITIVE:
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }

    private class ScannerOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            Intent intent = new Intent(CLIENT_SCANNER);
            switch(item) {
                case 0:
                    intent.putExtra(SCAN_MODE, QR_CODE_MODE);
                    break;
                case 1:
                    intent.putExtra(SCAN_MODE, PRODUCT_MODE);
                    break;
                case 2:
                    intent.putExtra(SCAN_MODE, OTHERS_CODES);
                    break;
            }
            startActivityForResult(intent, 0);
        }
    }
}
