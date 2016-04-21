package br.com.compremelhor.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.ProductActivity;
import br.com.compremelhor.adapter.ExpandableListAdapter;
import br.com.compremelhor.api.integration.resource.impl.SKUResource;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.service.CartService;

import static br.com.compremelhor.util.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.util.Constants.EXTRA_CURRENT_QUANTITY_OF_ITEM;
import static br.com.compremelhor.util.Constants.EXTRA_SER_PRODUCT;
import static br.com.compremelhor.util.Constants.OTHERS_CODES;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.PRODUCT_MODE;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.QR_CODE_MODE;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_CART_ITEM_ADDED;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_CART_ITEM_EDITED;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_SCANNED_CODE;
import static br.com.compremelhor.util.Constants.SCAN_MODE;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;


public class CartFragment extends android.support.v4.app.Fragment {
    private static CartFragment instance;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<String>> listDataChild = new HashMap<>();
    
    private AlertDialog actionsProduct;
    private AlertDialog alertDialogConfirmation;

    private TextView tvPartnerName;
    private TextView tvValueTotal;
    private Button btnAddProduct;

    private ProgressDialog progressDialog;

    private OptionsDialogOnClickListener optionsListener;
    private String itemIdSelected;
    private String currentQuantityOfItemSelected;

    private SharedPreferences preferences;

    private DAOPurchaseLine daoPurchaseLine;
    private DAOEstablishment daoEstablishment;
    private SKUResource skuResource;
    private CartService cartService;
    private Handler handler;

    public static CartFragment newInstance(String mTag){
        if (instance == null)
            instance = new CartFragment();

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (progressDialog == null || !progressDialog.isShowing())
            new LoadCurrentCart().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        int userId = preferences.getInt(SP_USER_ID, 0);
        int partnerId = preferences.getInt(SP_PARTNER_ID, 0);

        daoEstablishment = DAOEstablishment.getInstance(getActivity());
        daoPurchaseLine = DAOPurchaseLine.getInstance(getActivity());
        cartService = CartService.getInstance(getActivity(), userId, partnerId);
        skuResource = new SKUResource(getActivity());
        handler = new Handler();
        setWidgets();
        registerViews();
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_SCANNED_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    final String code = intent.getStringExtra("SCAN_RESULT");

                    showProgressDialog("Buscando produto...");

                    AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Product p = skuResource.getResource("code.code", code);
                            if (p == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),
                                                "Produto de código " + code + " não encontrado.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                return null;
                            }
                            progressDialog.dismiss();
                            Intent intent1 = new Intent(getActivity(), ProductActivity.class);
                            intent1.putExtra(EXTRA_SER_PRODUCT, p);
                            startActivityForResult(intent1, REQUEST_CODE_CART_ITEM_ADDED);
                            return null;
                        }
                    };
                    request.execute();


                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("SCRIP", "Press a button to start a scan.");
                    Log.v("SCRIP", "Scan cancelled");
                }
                break;

            case REQUEST_CODE_CART_ITEM_ADDED:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("ASYNC TASK", "CartFragment.OnActivityResult");
                    new LoadCurrentCart().execute();
                }
                break;

            case REQUEST_CODE_CART_ITEM_EDITED:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("ASYNC TASK", "CartFragment.OnActivityResult");
                    new LoadCurrentCart().execute();
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
        tvPartnerName = (TextView) getView().findViewById(R.id.fragment_tv_partner_name);
        tvPartnerName.setText(daoEstablishment.find(preferences.getInt(SP_PARTNER_ID, 0)).getName());
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
                // For to do changes on item
                case 0:
                    AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            PurchaseLine line = daoPurchaseLine.find(Integer.valueOf(itemIdSelected));
                            if (line == null) throw new RuntimeException("CANNOT BE NULL");
                            Product p = skuResource.getResource(line.getProduct().getId());
                            if (p == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),
                                                "Desculpe, ocorreu um erro inesperado ao tentar editar o item", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                return null;
                            }
                            progressDialog.dismiss();
                            Intent intent = new Intent(getActivity(), ProductActivity.class);
                            intent.putExtra(EXTRA_SER_PRODUCT, p);
                            intent.putExtra(EXTRA_PURCHASE_ID, itemIdSelected);
                            intent.putExtra(EXTRA_CURRENT_QUANTITY_OF_ITEM, currentQuantityOfItemSelected);
                            startActivityForResult(intent, REQUEST_CODE_CART_ITEM_EDITED);
                            return null;
                        }
                    };
                    request.execute();
                    break;

                case 1:
                    alertDialogConfirmation.show();
                    break;

                // For exclusion of item from cart
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
        boolean result = cartService.removeItem(daoPurchaseLine.find(Integer.valueOf(itemIdSelected)));
        new LoadCurrentCart().execute();
        return result;
    }

    private class ScannerOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            Intent intent = new Intent(CLIENT_SCANNER);
            //startActivityForResult(new Intent(getActivity(), ProductActivity.class), REQUEST_CODE_CART_ITEM_ADDED);
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

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(getActivity(),
                        getString(R.string.dialog_header_wait), message, true, false);
    }

    private class LoadCurrentCart extends AsyncTask<Void, Void, Double> {
        public void onPreExecute() {
            progressDialog = ProgressDialog
                    .show(getActivity(), getString(R.string.dialog_header_wait), getString(R.string.loading_current_cart_dialog), true, false);
        }

        @Override
        protected Double doInBackground(Void... params) {
            return refreshListOnCurrentCart();
        }

        public void onPostExecute(Double valueTotal) {
            setWidgets();
            tvValueTotal.setText("R$ " + valueTotal);
            progressDialog.dismiss();
        }

        private double refreshListOnCurrentCart() {
            final TreeSet<PurchaseLine> items = cartService.getItems();

            listDataChild = new HashMap<>();
            listDataHeader = new ArrayList<>();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (items.size() == 0) {
                        getView().findViewById(R.id.tv_empty_cart_message).setVisibility(View.VISIBLE);
                    } else {
                        getView().findViewById(R.id.tv_empty_cart_message).setVisibility(View.GONE);
                    }
                }
            });

            Map<String, Double> sumByCategoryMap = new HashMap<>();
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
