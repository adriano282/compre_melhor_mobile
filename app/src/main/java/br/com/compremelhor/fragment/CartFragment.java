package br.com.compremelhor.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.ProductActivity;
import br.com.compremelhor.adapter.ExpandableListAdapter;
import br.com.compremelhor.dao.DAOCart;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.PurchaseLine;

import static br.com.compremelhor.useful.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_CART_ITEM_ADDED;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_CART_ITEM_EDITED;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_SCANNED_CODE;
import static br.com.compremelhor.useful.Constants.PURCHASE_ID_EXTRA;
import static br.com.compremelhor.useful.Constants.CURRENT_QUANTITY_OF_ITEM_EXTRA;


public class CartFragment extends android.support.v4.app.Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<String>> listDataChild = new HashMap<>();
    
    private AlertDialog actionsProduct;
    private AlertDialog alertDialogConfirmation;

    private TextView tvValueTotal;
    private Button btnAddProduct;

    private ProgressDialog progressDialog;

    private OptionsDialogOnClickListener optionsListener;
    private String itemIdSelected;
    private String currentQuantityOfItemSelected;

    @Override
    public void onStart() {
        super.onStart();
        setWidgets();
        registerViews();
        Log.d("ASYNC TASK", "CartFragment.OnStart");

        if (progressDialog == null || !progressDialog.isShowing())
            new LoadCurrentCart().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("ASYNC TASK", "CartFragment.OnCreateView");
        if (getView() == null) {
            Log.d("ASYNC TASK", "NULL VIEW - CartFragment.OnCreateView ");
        }
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("ASYNC TASK", "CartFragment.OnViewCreated");
        if (getView() == null) {
            Log.d("ASYNC TASK", "NULL VIEW - CartFragment.OnViewCreated ");
        }
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
                    Log.d("ASYNC TASK", "CartFragment.OnActivityResult");
                 //   new LoadCurrentCart().execute();
                }
                break;
        }
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
                itemIdSelected = ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString();
                currentQuantityOfItemSelected = ((TextView)((LinearLayout) v).getChildAt(2)).getText().toString();
                Log.d("ID", "ITEM ID " + itemIdSelected);
                Log.d("ID", "QUANTITY " + currentQuantityOfItemSelected);
                actionsProduct.show();
                return false;
            }
        });
    }

    private void setWidgets() {
        explicitView = (ExpandableListView) getView().findViewById(R.id.lv_shopping_list);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        explicitView.setAdapter(listAdapter);

        tvValueTotal = (TextView) getView().findViewById(R.id.tv_value_total_purchase);
        btnAddProduct = (Button) getView().findViewById(R.id.btn_shopping_list_add);

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
                .setMessage(R.string.delete_confirmation_address)
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

    private class OptionsDialogOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            Intent intent;

            switch (item) {
                case 0:
                    intent = new Intent(getActivity(), ProductActivity.class);
                    intent.putExtra(PURCHASE_ID_EXTRA, itemIdSelected);
                    intent.putExtra(CURRENT_QUANTITY_OF_ITEM_EXTRA, currentQuantityOfItemSelected);
                    startActivityForResult(intent, REQUEST_CODE_CART_ITEM_EDITED);
                    break;

                case 1:
                    alertDialogConfirmation.show();
                    break;

                case DialogInterface.BUTTON_POSITIVE:
                    if (removeItemFromCart())
                        Toast.makeText(getActivity(), getString(R.string.removed_item_success_msg), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), getString(R.string.removed_item_error_msg), Toast.LENGTH_SHORT).show();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }

    private boolean removeItemFromCart() {
        PurchaseLine line = new PurchaseLine();
        line.setId(Integer.valueOf(itemIdSelected));
        if (DAOCart.getInstance(getActivity()).removeItem(line) == -1)
            return false;

        new LoadCurrentCart().execute();
        return true;
    }

    private class ScannerOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            Intent intent = new Intent(CLIENT_SCANNER);
            startActivityForResult(new Intent(getActivity(), ProductActivity.class), REQUEST_CODE_CART_ITEM_ADDED);
            /*switch(item) {
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
            startActivityForResult(intent, REQUEST_CODE_SCANNED_CODE);*/
        }
    }



    private class LoadCurrentCart extends AsyncTask<Void, Void, Double> {
        public void onPreExecute() {
            Log.d("ASYNC TASK", "onPreExecute");
            progressDialog = ProgressDialog
                    .show(getActivity(), getString(R.string.wait_header_dialog), getString(R.string.loading_current_cart_dialog), true, false);

        }

        @Override
        protected Double doInBackground(Void... params) {
            Log.d("ASYNC TASK", "doInBackground(Void... params)");
            return refreshListOnCurrentCart();
        }

        public void onPostExecute(Double valueTotal) {
            Log.d("ASYNC TASK", "onPostExecute(Double valueTotal)");
            setWidgets();
            tvValueTotal.setText("R$ " + valueTotal);
            progressDialog.dismiss();
        }

        private double refreshListOnCurrentCart() {
            Cart c = DAOCart.getInstance(getActivity()).getCart();

            List<PurchaseLine> items = c.getItems();
            Collections.sort(items, new Comparator<PurchaseLine>() {
                @Override
                public int compare(PurchaseLine lhs, PurchaseLine rhs) {
                    String category1 = lhs.getCategory();
                    String category2 = rhs.getCategory();
                    return category1.compareTo(category2);
                }
            });

            listDataChild = new HashMap<>();
            listDataHeader = new ArrayList<>();

            if (items.size() == 0) {
                getView().findViewById(R.id.tv_empty_cart_message).setVisibility(View.VISIBLE);
            } else {
                getView().findViewById(R.id.tv_empty_cart_message).setVisibility(View.GONE);
            }

            Map<String, Double> sumByCategoryMap = new HashMap<>();
            List<String> listChild = new ArrayList<>();

            for (PurchaseLine line : items) {
                String currentCategory = line.getCategory();

                if (!sumByCategoryMap.containsKey(line.getCategory())) {
                    sumByCategoryMap.put(line.getCategory(), 0.0);
                    listDataHeader.add(currentCategory);
                    listDataChild.put(currentCategory, new ArrayList<String>());
                }

                sumByCategoryMap.put(currentCategory, sumByCategoryMap.get(currentCategory) + line.getSubTotal().doubleValue());
                listDataChild
                        .get(currentCategory)
                        .add(line.getProductName() + "/" + line.getQuantity() + "/ R$" + line.getSubTotal() + "/" + line.getId());
            }

            Iterator<Map.Entry<String, Double>> iterator = sumByCategoryMap.entrySet().iterator();
            double valueTotal = 0.0;
            int i = 0;
            while(iterator.hasNext()) {
                Map.Entry<String, Double> pair = iterator.next();
                String newHeader = pair.getKey() + "/R$ " + pair.getValue();

                valueTotal += pair.getValue();

                listDataChild.put(newHeader, listDataChild.get(listDataHeader.get(i)));
                listDataChild.remove(listDataHeader.get(i));
                listDataHeader.set(i, newHeader);

                i++;
            }
            return valueTotal;
        }
    }
}
