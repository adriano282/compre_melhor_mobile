package br.com.compremelhor.fragment.shopping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.ProductActivity;
import br.com.compremelhor.adapter.ExpandableListAdapter;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.service.CartService;
import br.com.compremelhor.util.helper.ComputePurchaseLinesHelper;
import br.com.compremelhor.util.helper.dialog.ProgressDialogHelper;
import br.com.compremelhor.view.SkuListView;

import static br.com.compremelhor.util.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.util.Constants.EXTRA_CURRENT_QUANTITY_OF_ITEM;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.EXTRA_SER_PRODUCT;
import static br.com.compremelhor.util.Constants.OTHERS_CODES;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.PRODUCT_MODE;
import static br.com.compremelhor.util.Constants.QR_CODE_MODE;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_CART_ITEM_ADDED;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_CART_ITEM_EDITED;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_SCANNED_CODE;
import static br.com.compremelhor.util.Constants.SCAN_MODE;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_PARTNER_NAME;
import static br.com.compremelhor.util.Constants.SP_USER_ID;


public class CartFragment extends android.support.v4.app.Fragment {
    private static CartFragment instance;
    private static final String TAG = "CartFragment";
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private List<String> listDataHeader = new ArrayList<>();
    private TreeMap<String, List<String>> listDataChild = new TreeMap<>();

    private AlertDialog actionsProduct;
    private AlertDialog alertDialogConfirmation;

    private TextView tvPartnerName;
    private TextView tvValueTotal;
    private Button btnAddProduct;

    private OptionsDialogOnClickListener optionsListener;
    private String itemIdSelected;
    private String currentQuantityOfItemSelected;

    private SharedPreferences preferences;

    private CartService cartService;
    private Handler handler;

    private String partnerName;

    public static CartFragment getCurrentInstance() {
        return instance;
    }

    public static CartFragment newInstance(String mTag){
        Log.d(TAG, "newInstance");
        if (instance == null)
            instance = new CartFragment();

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        partnerName = preferences.getString(SP_PARTNER_NAME, "");

        int userId = preferences.getInt(SP_USER_ID, 0);
        int partnerId = preferences.getInt(SP_PARTNER_ID, 0);

        cartService = CartService.getInstance(getActivity(), userId, partnerId);
        handler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        new LoadCurrentCart().execute();

        handler = new Handler();
        setWidgets();
        registerViews();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        Log.d(TAG, "onActivityResult");
        cartService = CartService.getInstance(getActivity(),
                preferences.getInt(SP_USER_ID, 0),
                preferences.getInt(SP_PARTNER_ID, 0));
        switch (requestCode) {
            case REQUEST_CODE_SCANNED_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    final String code = intent.getStringExtra("SCAN_RESULT");

                    ProgressDialogHelper.getInstance(getActivity())
                            .setMessage("Buscando produto...")
                            .showWaitProgressDialog();

                    AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Product p = cartService.getProduct(code);

                            if (cartService.containsProduct(code)) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),
                                                "Produto já inserido em seu carrinho.", Toast.LENGTH_SHORT).show();
                                        ProgressDialogHelper.dismissProgressDialog();
                                    }
                                });
                                return null;
                            }

                            if (p == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),
                                                "Produto de código " + code + " não encontrado.", Toast.LENGTH_SHORT).show();
                                        ProgressDialogHelper.dismissProgressDialog();
                                    }
                                });
                                return null;
                            }

                            ProgressDialogHelper.dismissProgressDialog();
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
        Log.d(TAG, "registerViews");
        optionsListener = new OptionsDialogOnClickListener();
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createReadingAlertDialog().show();
            }
        });

        explicitView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                itemIdSelected = ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString().trim();
                currentQuantityOfItemSelected = ((TextView)((LinearLayout) v).getChildAt(2)).getText().toString().trim();

                if (currentQuantityOfItemSelected.contains(":")) {
                    currentQuantityOfItemSelected = currentQuantityOfItemSelected.substring(
                            currentQuantityOfItemSelected.indexOf(":") + 1, currentQuantityOfItemSelected.indexOf(".")
                    ).trim();
                }

                Log.d("ID", "ITEM ID " + itemIdSelected);
                Log.d("ID", "QUANTITY " + currentQuantityOfItemSelected);
                actionsProduct.show();
                return false;
            }
        });
    }

    private void setWidgets() {
        Log.d(TAG, "setWidgets");
        explicitView = (ExpandableListView) getView().findViewById(R.id.lv_shopping_list);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        explicitView.setAdapter(listAdapter);
        tvPartnerName = (TextView) getView().findViewById(R.id.fragment_tv_partner_name);
        tvPartnerName.setText(partnerName);
        tvValueTotal = (TextView) getView().findViewById(R.id.tv_value_total_purchase);
        btnAddProduct = (Button) getView().findViewById(R.id.btn_shopping_list_add);
        btnAddProduct.setVisibility(View.VISIBLE);

        actionsProduct = createProductAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();
    }

    private AlertDialog createReadingAlertDialog() {
        Log.d(TAG, "createReadingAlertDialog");
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
        Log.d(TAG, "createDialogConfirmation");
        return new Builder(getActivity())
                .setMessage(R.string.delete_confirmation_cart_item)
                .setPositiveButton(R.string.yes, optionsListener)
                .setNegativeButton(R.string.no, optionsListener)
                .create();
    }

    private AlertDialog createProductAlertDialog() {
        Log.d(TAG, "createProductAlertDialog");
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
                            Product p = cartService.getProduct(Integer.valueOf(itemIdSelected.trim()));
                            if (p == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),
                                                "Desculpe, ocorreu um erro inesperado ao tentar editar o item", Toast.LENGTH_SHORT).show();
                                        ProgressDialogHelper.dismissProgressDialog();
                                    }
                                });
                                return null;
                            }
                            ProgressDialogHelper.dismissProgressDialog();
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
                    alertDialogConfirmation.dismiss();
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
        Log.d(TAG, "removeItemFromCart");
        boolean result = cartService.removeItem(Integer.valueOf(itemIdSelected));
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

    private class LoadCurrentCart extends AsyncTask<Void, Void, Double> {
        public void onPreExecute() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    ProgressDialogHelper.getInstance(getActivity())
                            .setMessage(getString(R.string.loading_current_cart_dialog))
                            .showWaitProgressDialog();
                }
            });

        }

        @Override
        protected Double doInBackground(Void... params) {

            return refreshListOnCurrentCart();
        }

        public void onPostExecute(Double valueTotal) {
            setWidgets();
            tvValueTotal.setText(String.format("R$ %,.2f",valueTotal));
            ProgressDialogHelper.dismissProgressDialog();
        }

        private double refreshListOnCurrentCart() {
            Log.d(TAG, "refreshListOnCurrentCart");
            final TreeSet<PurchaseLine> items = cartService.getItems();
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

            SkuListView slv = ComputePurchaseLinesHelper.getSkuListView(items);

            listDataChild = slv.getChildDataList();
            listDataHeader = slv.getHeaderDataList();
            return slv.getTotalValue();
        }
    }
}
