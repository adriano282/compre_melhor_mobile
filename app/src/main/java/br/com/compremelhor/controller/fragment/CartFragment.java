package br.com.compremelhor.controller.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import br.com.compremelhor.controller.activity.ProductActivity;
import br.com.compremelhor.controller.adapter.ExpandableListAdapter;
import br.com.compremelhor.dao.DAOCart;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.PurchaseLine;

import static br.com.compremelhor.useful.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.useful.Constants.OTHERS_CODES;
import static br.com.compremelhor.useful.Constants.PRODUCT_MODE;
import static br.com.compremelhor.useful.Constants.QR_CODE_MODE;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_CART_ITEM_ADDED;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_SCANNED_CODE;
import static br.com.compremelhor.useful.Constants.SCAN_MODE;

public class CartFragment extends android.support.v4.app.Fragment {
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
    }

    @Override
    public void onStart() {
        super.onStart();
        setWidgets();
        registerViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_SCANNED_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    String code = intent.getStringExtra("SCAN_RESULT");
                    Intent intent1 = new Intent(getActivity(), ProductActivity.class);
                    intent1.putExtra("codeResult", code);
                    startActivityForResult(intent1, REQUEST_CODE_CART_ITEM_ADDED);
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("SCRIP", "Press a button to start a scan.");
                    Log.v("SCRIP", "Scan cancelled");
                }
                break;

            case REQUEST_CODE_CART_ITEM_ADDED:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("RESULT", "SALVO CARRINHO");

                    addItemToList();
                }
                break;
        }
    }

    private void addItemToList() {
        Cart c = new DAOCart(getActivity()).getCart();

        List<PurchaseLine> items = c.getItens();


        listDataHeader.

    }

    private void registerViews() {
        optionsListener = new OptionsDialogOnClickListener();
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createReadingAlertDialog().show();
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
        explicitView = (ExpandableListView) getView().findViewById(R.id.lv_shopping_list);
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

    }

    private String formattedItem(PurchaseLine pl) {
        return pl.getProduct().getName() + " / " + pl.getQuantity() + " / " + pl.getSubTotal();
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
            startActivityForResult(intent, REQUEST_CODE_SCANNED_CODE);
        }
    }
}
